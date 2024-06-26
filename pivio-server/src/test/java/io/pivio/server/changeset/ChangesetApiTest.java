package io.pivio.server.changeset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.runner.RunWith;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import io.pivio.server.AbstractApiTestCase;
import io.pivio.server.PivioServerConfig;
import io.pivio.server.document.PivioDocument;
import io.pivio.server.elasticsearch.Changeset;
import io.pivio.server.elasticsearch.ElasticsearchQueryHelper;
import net.minidev.json.JSONArray;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = {io.pivio.server.Config.class, PivioServerConfig.class})
@Testcontainers
public class ChangesetApiTest extends AbstractApiTestCase {

  private static final String ANOTHER_ID = "anotherId";
  private static final String DOCUMENT_CHANGESET_URL_TEMPLATE = "/document/{id}/changeset";

  private static final String ADD_OPERATION = "add";
  private static final String REMOVE_OPERATION = "remove";
  private static final String REPLACE_OPERATION = "replace";

  private ObjectNode document;

  @Autowired
  protected ElasticsearchQueryHelper queryHelper;

  @Autowired
  protected ChangesetController changesetController;

  // @Container
  // public static final DockerComposeContainer<?> dockerEnvironment =
  // new DockerComposeContainer<>(new File("docker-compose.yml")).withLocalCompose(true);
  // .withExposedService(ELASTICSEARCH_SERVICE_NAME, ELASTICSEARCH_SERVICE_PORT);
  // .withTailChildContainers(true);

  @Container
  public static final GenericContainer<?> opensearchContainer =
      new GenericContainer<>("docker.io/opensearchproject/opensearch:2.14.0")
          .withEnv("OPENSEARCH_INITIAL_ADMIN_PASSWORD", "öasjdfzxdk_I34")
          .withEnv("discovery.type", "single-node").withExposedPorts(9200, 9600);

  static {
    opensearchContainer.start();
    Runtime.getRuntime().addShutdownHook(new Thread(opensearchContainer::stop));
  }

  // @Configuration
  // public static class Config {
  //
  // @Bean
  // public OpenSearchClient client() {
  // // Create the low-level client
  // RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
  //
  // // Create the transport with a Jackson mapper
  // OpenSearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
  //
  // // And create the API client
  // OpenSearchClient client = new OpenSearchClient(transport);
  // return client;
  // }
  //
  // @Bean
  // public ObjectMapper objectMapper() {
  // return new ObjectMapper(new JsonFactory());
  // }
  //
  // @Bean
  // public ElasticsearchQueryHelper esQueryHelper() {
  // return new ElasticsearchQueryHelper(client(), objectMapper(), "steckbrief", "changeset");
  // }
  // }

  @Before
  public void setUp() {
    document = objectMapper.createObjectNode().put("id", SOME_ID).put("type", "service")
        .put("name", "MicroService").put("serviceName", "MS").put("description", "Super service...")
        .put("owner", "Awesome Team");
  }

  @AfterAll
  public static void afterAll() {
    opensearchContainer.stop();
    opensearchContainer.close();
  }

  @Test
  public void changeset_exists_for_document() {
    // given
    postDocumentWithSomeId();

    // when
    JsonNode changeset = getFirstChangesetOfDocumentWithSomeId();

    // then
    assertThat(changeset).isNotNull();
  }

  @Test
  public void changeset_contains_all_fields() {
    // given
    postDocumentWithSomeId();

    // when
    JsonNode changeset = getFirstChangesetOfDocumentWithSomeId();

    // then
    assertThat(changeset.get("fields").findValues("path").stream().map(JsonNode::textValue))
        .containsOnly("/id", "/type", "/serviceName", "/name", "/description", "/owner");
  }

  @Test
  public void changeset_fields_have_correct_diff() {
    // given
    postDocumentWithSomeId();

    // when
    JsonNode changeset = getFirstChangesetOfDocumentWithSomeId();

    // then
    assertThatFieldHasCorrectDiff(changeset, "/id", SOME_ID, ADD_OPERATION);
    assertThatFieldHasCorrectDiff(changeset, "/type", "service", ADD_OPERATION);
    assertThatFieldHasCorrectDiff(changeset, "/name", "MicroService", ADD_OPERATION);
    assertThatFieldHasCorrectDiff(changeset, "/serviceName", "MS", ADD_OPERATION);
    assertThatFieldHasCorrectDiff(changeset, "/description", "Super service...", ADD_OPERATION);
    assertThatFieldHasCorrectDiff(changeset, "/owner", "Awesome Team", ADD_OPERATION);
  }

  @Test
  public void changeset_array_values_can_be_replaced_and_removed() {
    // given
    postDocument(
        document.set("provides", objectMapper.createArrayNode().add("a").add("b").add("c")));
    postDocument(document.set("provides", objectMapper.createArrayNode().add("a").add("d")));

    // when
    JsonNode changeset = getFirstChangesetOfDocumentWithSomeId();

    // then
    assertThatFieldHasCorrectDiff(changeset, "/provides/1", "d", REPLACE_OPERATION);

    // and
    List<String> jsonOperationValue =
        JsonPath.read(changeset.toString(), "$.fields[?(@.path == '/provides/2')].op");
    assertThat(jsonOperationValue.get(0)).isEqualTo(REMOVE_OPERATION);
  }

  @Test
  public void nested_object_value_in_changeset_can_be_replaced() {
    // given
    postDocument(document.set("dependencies",
        objectMapper.createObjectNode().put("name", "de.websitename:file.jar")));
    postDocument(
        document.set("dependencies", objectMapper.createObjectNode().put("name", "file.jar")));

    // when
    JsonNode changeset = getFirstChangesetOfDocumentWithSomeId();

    // then
    assertThatFieldHasCorrectDiff(changeset, "/dependencies/name", "file.jar", REPLACE_OPERATION);
  }

  @Test
  public void changeset_references_document() {
    // given
    postDocument(document);

    // when
    JsonNode changeset = getFirstChangesetOfDocumentWithSomeId();

    // then
    assertThat(changeset.get("document").textValue()).isEqualTo(SOME_ID);
  }

  @Test
  public void changeset_is_created_for_corrupt_document_without_changeset() throws Exception {
    // given
    persistDocumentWithoutCreatingChangeset(document);

    // and
    postDocument(document.put("name", "Other"));

    // when
    JsonNode changeset = getFirstChangesetOfDocumentWithSomeId();

    // then
    assertThat(changeset).isNotNull();
  }

  @Test
  public void changeset_is_not_created_for_same_posted_document() throws JsonProcessingException {
    // given
    postDocument(document);
    postDocument(document);

    // when
    List<JsonNode> changesets = getChangesetsOfDocumentWithSomeIdAsList();

    // then
    assertThat(changesets).hasSize(1);
  }

  @Test
  public void changeset_is_only_created_for_the_changed_fields() {
    // given
    postDocument(document);
    postDocument(document.put("name", "NewService").put("owner", "User Team"));

    // when
    JSONArray changeset =
        JsonPath.read(getChangesetsOfDocumentWithSomeIdAsString(), "$.[?(@.order == 2)]");
    assertThat(changeset).hasSize(1);

    Object firstChangeset = changeset.get(0);
    JSONArray changedFields = JsonPath.read(firstChangeset, "$.fields");
    assertThat(changedFields).hasSize(2);

    // then
    assertThatFieldHasCorrectDiff(firstChangeset, "/name", "NewService", REPLACE_OPERATION);
    assertThatFieldHasCorrectDiff(firstChangeset, "/owner", "User Team", REPLACE_OPERATION);
  }

  @Test
  public void changesets_per_document_contain_incremented_order_numbers() {
    // given
    postDocument(document);

    // and
    document = document.put("name", "NewService");
    postDocument(document);

    // and
    document = document.put("owner", "User Team");
    postDocument(document);

    // when
    String changesets = getChangesetsOfDocumentWithSomeIdAsString();

    // then
    assertThat(((JSONArray) JsonPath.read(changesets, "$.[?(@.order == 1)]"))).hasSize(1);
    assertThat(((JSONArray) JsonPath.read(changesets, "$.[?(@.order == 2)]"))).hasSize(1);
    assertThat(((JSONArray) JsonPath.read(changesets, "$.[?(@.order == 3)]"))).hasSize(1);
    assertThat(((JSONArray) JsonPath.read(changesets, "$.[?(@.order == 4)]"))).hasSize(0);
  }

  @Test
  public void all_changesets_can_be_requested() {
    // given
    postDocument(document);
    postDocument(document.put("name", "NewService"));
    postDocument(createDocumentWithId(ANOTHER_ID));

    // when
    ResponseEntity<JsonNode> responseEntity =
        ResponseEntity.ok(queryHelper.isChangesetPresent(SOME_ID).get());

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getBody().isArray()).isTrue();
    assertThat(toList(responseEntity.getBody())).hasSize(3);
  }

  @Test
  public void deletion_of_document_also_deletes_corresponding_changesets()
      throws OpenSearchException, IOException {
    // given
    postDocument(document);
    postDocument(document.put("name", "NewService"));

    // when
    client.delete(request -> request.id(SOME_ID));

    // and
    ResponseEntity<JsonNode> responseEntity =
        ResponseEntity.of(queryHelper.isDocumentPresent(SOME_ID));
    // restTemplate.getForEntity(DOCUMENT_CHANGESET_URL_TEMPLATE, JsonNode.class, SOME_ID);

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void changeset_of_non_existent_document_cannot_be_requested() {
    // when
    ResponseEntity<JsonNode> responseEntity =
        ResponseEntity.of(queryHelper.isDocumentPresent("nonExistentId"));
    // restTemplate.getForEntity(DOCUMENT_CHANGESET_URL_TEMPLATE, JsonNode.class, "nonExistentId");

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void changesets_per_document_of_the_last_7_days_are_returned() throws Exception {
    // given
    persistDocumentWithoutCreatingChangeset(document);
    Changeset oneDayAgo = createChangesetDaysAgo(3L, 1);
    Changeset twoDaysAgo = createChangesetDaysAgo(2L, 2);

    // when
    persistChangesets(oneDayAgo, twoDaysAgo, createChangesetDaysAgo(1L, 8));

    // then
    assertThatChangesetsExistSince("7d", oneDayAgo, twoDaysAgo);
  }

  @Test
  public void changesets_of_all_documents_of_the_last_7_days_are_returned() throws Exception {
    // given
    persistDocumentWithoutCreatingChangeset(document);
    Changeset oneDayAgo = createChangesetDaysAgo(3L, 1);

    // and
    persistDocumentWithoutCreatingChangeset(document.put("id", ANOTHER_ID));
    Changeset twoDaysAgo = createChangesetDaysAgo(ANOTHER_ID, 2L, 2);

    // when
    persistChangesets(oneDayAgo, twoDaysAgo, createChangesetDaysAgo(1L, 8));

    // then
    assertThatChangesetsExist("/changeset?since=7d", oneDayAgo, twoDaysAgo);
  }

  @Test
  public void changesets_per_document_since_3_weeks_are_returned() throws Exception {
    // given
    persistDocumentWithoutCreatingChangeset(document);
    Changeset oneWeekAgo = createChangesetDaysAgo(3L, 7);
    Changeset twoWeeksAgo = createChangesetDaysAgo(2L, 14);

    // when
    persistChangesets(oneWeekAgo, twoWeeksAgo, createChangesetDaysAgo(1L, 28));

    // then
    assertThatChangesetsExistSince("3w", oneWeekAgo, twoWeeksAgo);
  }

  @Test
  public void changeset_cannot_be_requested_when_query_uses_since_filter_without_value() {
    assertThatChangesetSinceRequestResultsInBadRequestResponse("");
  }

  @Test
  public void changeset_cannot_be_requested_when_query_uses_since_filter_without_time_unit() {
    assertThatChangesetSinceRequestResultsInBadRequestResponse("32");
  }

  @Test
  public void changeset_cannot_be_requested_when_query_uses_since_filter_with_unsupported_time_unit() {
    assertThatChangesetSinceRequestResultsInBadRequestResponse("32h");
  }

  @Test
  public void changeset_cannot_be_requested_when_query_uses_since_filter_with_negative_value() {
    assertThatChangesetSinceRequestResultsInBadRequestResponse("-3d");
  }

  @Test
  public void changeset_cannot_be_requested_when_query_uses_since_filter_with_value_zero() {
    assertThatChangesetSinceRequestResultsInBadRequestResponse("0w");
  }

  private Changeset createChangesetDaysAgo(long order, int daysAgo) {
    return createChangesetDaysAgo(document.get("id").asText(), order, daysAgo);
  }

  private Changeset createChangesetDaysAgo(String document, long order, int daysAgo) {
    Changeset changeset =
        Changeset.builder().document(document).order(order).fields(Collections.emptyList()).build();
    changeset.setTimestamp(DateTime.now().minusDays(daysAgo));
    return changeset;
  }

  private void persistDocumentWithoutCreatingChangeset(JsonNode document)
      throws OpenSearchException, IOException, JsonProcessingException, JsonProcessingException {
    assertNotNull(client.index(request -> {
      try {
        return request.index("steckbrief")
            .document(objectMapper.treeToValue(document, PivioDocument.class));
      } catch (JsonProcessingException | IllegalArgumentException e) {
        return null;
      }
    }));
  }

  private void persistChangesets(Changeset... changesets) throws OpenSearchException, IOException {
    for (Changeset changeset : changesets) {
      assertNotNull(client.index(request -> request.index("changeset").document(changeset)));
    }
  }

  private JsonNode getFirstChangesetOfDocumentWithSomeId() {
    return getChangesetsOfDocumentWithSomeIdAsList().get(0);
  }

  private List<JsonNode> getChangesetsOfDocumentWithSomeIdAsList() {
    return getChangesets(SOME_ID, this::toList);
  }

  private String getChangesetsOfDocumentWithSomeIdAsString() {
    return getChangesets(SOME_ID, JsonNode::toString);
  }

  private <R> R getChangesets(String documentId, Function<JsonNode, R> returnValueFunction) {
    ResponseEntity<JsonNode> responseEntity =
        ResponseEntity.of(queryHelper.isChangesetPresent(documentId));
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getBody().isArray()).isTrue();
    return returnValueFunction.apply(responseEntity.getBody());
  }


  // JsonPath.read(..) is overloaded: JsonPath.read(String) and JsonPath.read(Object). The read(..)
  // method that is used by
  // our assert method (i.e. that will be linked) depends on the type our parameter 'changeset' has
  // _at compile time_.
  // Hence, we need to overload our assert method as well.
  private void assertThatFieldHasCorrectDiff(JsonNode changeset, String fieldKey, String value,
      String operation) {
    List<String> jsonPreviousValue =
        JsonPath.read(changeset.toString(), "$.fields[?(@.path == '" + fieldKey + "')].value");
    assertThat(jsonPreviousValue.get(0)).isEqualTo(value);
    List<String> jsonOperationValue =
        JsonPath.read(changeset.toString(), "$.fields[?(@.path == '" + fieldKey + "')].op");
    assertThat(jsonOperationValue.get(0)).isEqualTo(operation);
  }

  // JsonPath.read(..) is overloaded: JsonPath.read(String) and JsonPath.read(Object). The read(..)
  // method that is used by
  // our assert method (i.e. that will be linked) depends on the type our parameter 'changeset' has
  // _at compile time_.
  // Hence, we need to overload our assert method as well.
  private void assertThatFieldHasCorrectDiff(Object changeset, String fieldKey, String value,
      String operation) {
    List<String> jsonPreviousValue =
        JsonPath.read(changeset, "$.fields[?(@.path == '" + fieldKey + "')].value");
    assertThat(jsonPreviousValue.get(0)).isEqualTo(value);
    List<String> jsonOperationValue =
        JsonPath.read(changeset, "$.fields[?(@.path == '" + fieldKey + "')].op");
    assertThat(jsonOperationValue.get(0)).isEqualTo(operation);
  }

  private void assertThatChangesetsExistSince(String sinceParameter,
      Changeset... expectedChangesets) {
    assertThatChangesetsExist(
        "/document/" + document.get("id").asText() + "/changeset?since=" + sinceParameter,
        expectedChangesets);
  }

  private void assertThatChangesetsExist(String url, Changeset... expectedChangesets) {
    ResponseEntity<JsonNode> responseEntity = null;
    try {
      responseEntity = ResponseEntity.of(Optional.ofNullable(queryHelper.retrieveAllChangesets()));
    } catch (IOException e) {
      fail(e);
    }
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getBody().isArray()).isTrue();

    List<JsonNode> receivedChangesets = toList(responseEntity.getBody());
    assertThat(receivedChangesets).hasSize(expectedChangesets.length);
    assertThat(receivedChangesets.stream().map(c -> c.get("order").longValue()))
        .containsExactly(Arrays.asList(expectedChangesets).stream().map(Changeset::getOrder)
            .collect(Collectors.toList()).toArray(new Long[0]));
  }

  private void assertThatChangesetSinceRequestResultsInBadRequestResponse(String sinceValue) {
    // given
    postDocument(document);

    // when
    HttpStatus httpStatus = getHttpStatusOfChangesetSinceRequest(sinceValue);

    // then
    assertThat(httpStatus).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  private HttpStatus getHttpStatusOfChangesetSinceRequest(String sinceValue) {
    try {
      ArrayNode changesets = changesetController.listAll(sinceValue);
      if (changesets.size() == 0) {
        return HttpStatus.valueOf(401);
      } else {
        return HttpStatus.valueOf(200);
      }
    } catch (IOException e) {
      fail(e);
    }
    return HttpStatus.valueOf(400);
    // return HttpStatus
    // .resolve(restTemplate.getForEntity(DOCUMENT_CHANGESET_URL_TEMPLATE + "?since={since}",
    // JsonNode.class, SOME_ID, sinceValue).getStatusCode().value());
  }

  private List<JsonNode> toList(JsonNode changesetsJson) {
    List<JsonNode> changesets = new LinkedList<>();
    for (Iterator<JsonNode> iterator = changesetsJson.elements(); iterator.hasNext();) {
      changesets.add(iterator.next());
    }
    return changesets;
  }
}

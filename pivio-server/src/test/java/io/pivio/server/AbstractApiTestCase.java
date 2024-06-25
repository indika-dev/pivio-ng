package io.pivio.server;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import java.io.IOException;
import org.junit.Before;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivio.server.document.PivioDocument;
import jakarta.annotation.PostConstruct;

// @RunWith(SpringRunner.class)
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
// @ContextConfiguration(initializers = DockerEnvironmentInitializer.class)
public abstract class AbstractApiTestCase {

  protected static final String ELASTICSEARCH_SERVICE_NAME = "elasticsearch_1";
  protected static final int ELASTICSEARCH_SERVICE_PORT = 9300;
  protected static final String PIVIO_SERVER_BASE_URL = "http://localhost:9123";
  protected static final String SOME_ID = "someId";

  private static final Logger log = LoggerFactory.getLogger(AbstractApiTestCase.class);

  @Autowired
  protected OpenSearchClient client;

  @Autowired
  protected ObjectMapper objectMapper;

  @Before
  public void waitUntilPivioServerIsUpAndCleanUpPersistentData() {
    waitUntilPivioServerIsUp();
    cleanUpPersistentData();
  }

  private void waitUntilPivioServerIsUp() {
    await().atMost(180, SECONDS).until(() -> {
      String documentResponse = "";
      RestTemplate faultSensitiveRestTemplate =
          new RestTemplateBuilder().rootUri(PIVIO_SERVER_BASE_URL).build();
      try {
        documentResponse = faultSensitiveRestTemplate.getForObject("/document", String.class);
      } catch (Exception ignored) {
        log.debug("Pivio Server is not up yet. Exception message: {}", ignored.getMessage());
      }
      return !documentResponse.isEmpty();
    });
  }

  private void cleanUpPersistentData() {
    log.debug(
        "Cleaning up persistent data from Elasticsearch: deleting indices, creating new ones, put mappings, refresh indices");

    try {
      client.indices().delete(builder -> builder.index("steckbrief"));
    } catch (OpenSearchException | IOException e) {
      log.warn("can't delete index steckbrief: " + e.getMessage(), e);
    }
    try {
      client.indices().delete(builder -> builder.index("changeset"));
    } catch (OpenSearchException | IOException e) {
      log.warn("can't delete index changeset: " + e.getMessage(), e);
    }
  }

  @PostConstruct
  public void prepareDb() {
    try {
      client.indices().create(builder -> builder.index("steckbrief"));
    } catch (OpenSearchException | IOException e) {
      log.warn("can't create index steckbrief: " + e.getMessage(), e);
    }
    try {
      client.indices().create(builder -> builder.index("changeset"));
    } catch (OpenSearchException | IOException e) {
      log.warn("can't create index changeset: " + e.getMessage(), e);
    }
  }

  private void refreshIndices() {}

  protected PivioDocument postDocumentWithSomeId() {
    return postDocumentWithId(SOME_ID);
  }

  protected PivioDocument postDocumentWithId(String id) {
    PivioDocument documentWithId = createDocumentWithId(id);
    postDocument(documentWithId);
    return documentWithId;
  }

  protected PivioDocument createDocumentWithId(String id) {
    return PivioDocument.builder().id(id).type("service").name("MicroService").serviceName("MS")
        .description("Super service...").owner("Awesome Team").build();
  }

  protected ResponseEntity<PivioDocument> postDocument(PivioDocument document) {
    return postDocument(document, PivioDocument.class);
  }

  protected ResponseEntity<JsonNode> postDocument(JsonNode document) {
    return postDocument(document, JsonNode.class);
  }

  protected <T> ResponseEntity<T> postDocument(Object document, Class<T> responseType) {
    ResponseEntity<T> responseEntity =
        restTemplate.postForEntity("/document", document, responseType);
    refreshIndices();
    return responseEntity;
  }

  // When provoking responses indicating client or server side HTTP errors (400, 500) we do not want
  // the test to fail.
  private static final class NoOpResponseErrorHandler extends DefaultResponseErrorHandler {
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {}
  }
}

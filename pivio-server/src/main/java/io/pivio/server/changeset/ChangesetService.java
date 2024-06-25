package io.pivio.server.changeset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import io.pivio.server.elasticsearch.Changeset;
import io.pivio.server.elasticsearch.ElasticsearchQueryHelper;
import io.pivio.server.elasticsearch.Fields;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class ChangesetService {

  private final Set<String> EXCLUDED_FIELDS = Set.of("/created", "/lastUpload", "/lastUpdate");

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private ElasticsearchQueryHelper queryHelper;

  public Changeset computeNext(JsonNode document) throws IOException {
    final String documentId = document.get("id").asText();
    final Optional<JsonNode> persistentDocument = queryHelper.isDocumentPresent(documentId);
    final JsonNode patch =
        JsonDiff.asJson(persistentDocument.orElse(mapper.readTree("{}")), document);
    return Changeset.builder().document(documentId).order(retrieveLastOrderNumber(documentId) + 1L)
        .fields(filterExcludedFields(patch)).timestamp(DateTime.now()).build();
  }

  private List<Fields> filterExcludedFields(JsonNode json) {
    List<Fields> filteredJson = new ArrayList<>();
    Iterator<JsonNode> elements = json.elements();
    while (elements.hasNext()) {
      JsonNode current = elements.next();
      if (current.has("path") && !EXCLUDED_FIELDS.contains(current.get("path").textValue())) {
        try {
          filteredJson.add(mapper.treeToValue(current, Fields.class));
        } catch (JsonProcessingException | IllegalArgumentException e) {
          logger.error("can't parse " + current.toPrettyString() + " due to " + e.getMessage(), e);
        }
      }
    }
    return filteredJson;
  }

  private long retrieveLastOrderNumber(String documentId) throws IOException {
    Optional<JsonNode> lastChangeset = queryHelper.isChangesetPresent(documentId);
    return lastChangeset.map(c -> c.get("order").asLong()).orElse(0L);
  }
}

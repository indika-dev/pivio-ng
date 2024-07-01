package io.pivio.schema.annotations.custom;

import java.util.Map;
import java.util.Set;
import org.jsonschema2pojo.AbstractAnnotator;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;

/**
 * CustomJsonMergeAnnotator
 */
public class CustomChangesetAnnotator extends AbstractAnnotator {

  private static final Set<String> idPropertyToAnnotate = Set.of("document");

  private static final Map<String, String> propertiesToAnnotate =
      Map.of("order", "order", "timestamp", "timestamp", "fields", "fields");

  @Override
  public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName,
      JsonNode propertyNode) {
    super.propertyField(field, clazz, propertyName, propertyNode);
    if (clazz.name().equals("Changeset")) {
      if (propertiesToAnnotate.containsKey(propertyName)) {
        switch (propertyName) {
          case "timestamp":
            field.annotate(Field.class).param("name", propertiesToAnnotate.get(propertyName))
                .param("type", FieldType.Date);
            break;
          case "fields":
            field.annotate(Field.class).param("name", propertiesToAnnotate.get(propertyName))
                .param("name", propertiesToAnnotate.get(propertyName))
                .param("type", FieldType.Nested);
            break;
          default:
            field.annotate(Field.class).param("name", propertiesToAnnotate.get(propertyName))
                .param("type", FieldType.Text);
            break;
        }
      } else if (idPropertyToAnnotate.contains(propertyName)) {
        field.annotate(Id.class);
      }
      if (propertyName.equals("document")) { // ensure, that Changeset class is only once
                                             // annotated
        clazz.annotate(Setting.class).param("settingPath", "settings.json");
        clazz.annotate(Document.class).param("indexName", "Changeset").param("createIndex", true);
      }
    }
  }
}

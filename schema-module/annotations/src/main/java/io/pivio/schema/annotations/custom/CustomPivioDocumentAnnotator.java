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
 * CustomPivioPropertyAnnotator
 */
public class CustomPivioDocumentAnnotator extends AbstractAnnotator {

  private static final Set<String> enumsToAnnotate = Set.of("id");

  private static final Map<String, String> textPropertiesToAnnotate = Map.of("type", "type", "name",
      "name", "serviceName", "short_name", "owner", "owner", "description", "description");

  @Override
  public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName,
      JsonNode propertyNode) {
    super.propertyField(field, clazz, propertyName, propertyNode);
    if (clazz.name().equals("PivioDocument")) {
      if (textPropertiesToAnnotate.containsKey(propertyName)) {
        field.annotate(Field.class).param("name", textPropertiesToAnnotate.get(propertyName))
            .param("type", FieldType.Text);
      }
      if (enumsToAnnotate.contains(propertyName)) {
        field.annotate(Id.class);
      }
      if (propertyName.equals("type")) { // ensure, that PivioDocument class is only once annotated
        clazz.annotate(Document.class).param("indexName", "steckbrief").param("createIndex", true);
        clazz.annotate(Setting.class).param("settingPath", "settings.json");
        clazz.annotate(Mapping.class).param("mappingPath", "mapping.json");
      }
    }
  }
}

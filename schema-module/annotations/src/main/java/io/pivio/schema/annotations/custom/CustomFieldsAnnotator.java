package io.pivio.schema.annotations.custom;

import java.util.Map;
import org.jsonschema2pojo.AbstractAnnotator;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import lombok.Builder;
import lombok.Data;

/**
 * CustomFieldsAnnotator
 */
public class CustomFieldsAnnotator extends AbstractAnnotator {

  private static final Map<String, String> propertiesToAnnotate =
      Map.of("path", "path", "op", "op", "value", "value", "from", "from");

  @Override
  public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName,
      JsonNode propertyNode) {
    super.propertyField(field, clazz, propertyName, propertyNode);
    if (clazz.name().equals("Fields")) {
      if (propertiesToAnnotate.containsKey(propertyName)) {
        switch (propertyName) {
          case "path":
            field.annotate(Field.class).param("name", propertiesToAnnotate.get(propertyName))
                .param("type", FieldType.Text).param("analyzer", "simple");
            break;
          default:
            field.annotate(Field.class).param("name", propertiesToAnnotate.get(propertyName))
                .param("type", FieldType.Text);
            break;
        }
      }
      if (propertyName.equals("path")) { // ensure, that Changeset class is only once
                                         // annotated
        clazz.annotate(Data.class);
        clazz.annotate(Builder.class);
      }
    }
  }
}

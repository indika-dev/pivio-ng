package io.pivio.schema.annotations.custom;

import java.util.Map;

import org.jsonschema2pojo.AbstractAnnotator;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;

/**
 * CustomPivioPropertyAnnotator
 */
public class CustomSoftwareDependencyAnnotator extends AbstractAnnotator {

  private static final Map<String, String> textPropertiesToAnnotate = Map.of("name", "name", "version",
      "version");

  private static final Map<String, String> nestedPropertiesToAnnotate = Map.of("licenses",
      "licenses");

  @Override
  public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName,
      JsonNode propertyNode) {
    super.propertyField(field, clazz, propertyName, propertyNode);
    if (clazz.name().equals("SoftwareDependency")) {
      if (textPropertiesToAnnotate.containsKey(propertyName)) {
        if ("name".equals(propertyName)) {
          field.annotate(Field.class).param("name", textPropertiesToAnnotate.get(propertyName))
              .param("type", FieldType.Text).param("analyzer", "simple");
        } else {
          field.annotate(Field.class).param("name", textPropertiesToAnnotate.get(propertyName))
              .param("type", FieldType.Text);
        }
      }
      if (nestedPropertiesToAnnotate.containsKey(propertyName)) {
        field.annotate(Field.class).param("name", nestedPropertiesToAnnotate.get(propertyName)).param("type",
            FieldType.Nested).param("includeInParent", true);
      }
    }
  }
}

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
public class CustomLicenseAnnotator extends AbstractAnnotator {

  private static final Map<String, String> textPropertiesToAnnotate = Map.of("key", "key", "fullName",
      "fullName", "url", "url");

  @Override
  public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName,
      JsonNode propertyNode) {
    super.propertyField(field, clazz, propertyName, propertyNode);
    if (clazz.name().equals("License")) {
      if (textPropertiesToAnnotate.containsKey(propertyName)) {
        field.annotate(Field.class).param("name", textPropertiesToAnnotate.get(propertyName))
            .param("type", FieldType.Text);
      }
    }
  }
}

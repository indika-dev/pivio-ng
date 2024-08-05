package io.pivio.schema.annotations.custom;

import java.util.Set;
import org.jsonschema2pojo.AbstractAnnotator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import io.pivio.schema.annotations.proxy.CustomEnumDeserializer;

public class CustomJsonAnnotator extends AbstractAnnotator {

  private static final Set<String> enumsToAnnotate = Set.of("t-shirt-sizes");

  @Override
  public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName,
      JsonNode propertyNode) {
    super.propertyField(field, clazz, propertyName, propertyNode);
    if (clazz.name().equals("PivioDocument")) {
      if (enumsToAnnotate.contains(propertyName)) {
        field.annotate(JsonDeserialize.class).param("using", CustomEnumDeserializer.class);
      }
    }
  }

}

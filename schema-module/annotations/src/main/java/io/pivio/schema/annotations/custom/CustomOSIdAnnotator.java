package io.pivio.schema.annotations.custom;

import java.util.Set;
import org.jsonschema2pojo.AbstractAnnotator;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;

/**
 * CustomJsonMergeAnnotator
 */
public class CustomOSIdAnnotator extends AbstractAnnotator {

  private static final Set<String> enumsToAnnotate = Set.of("id");

  @Override
  public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName,
      JsonNode propertyNode) {
    super.propertyField(field, clazz, propertyName, propertyNode);
    if (enumsToAnnotate.contains(propertyName)) {
      field.annotate(Id.class);
    }
  }

}

package io.pivio.schema.annotations.custom;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jsonschema2pojo.AbstractAnnotator;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;

/**
 * CustomJsonMergeAnnotator
 */
public class CustomJsonMergeAnnotator extends AbstractAnnotator {

  private static final Set<String> enumsToAnnotate =
      new HashSet<>(Arrays.asList("service", "depends_on", "provides", "internal", "external"));

  @Override
  public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName,
      JsonNode propertyNode) {
    super.propertyField(field, clazz, propertyName, propertyNode);
    if (clazz.name().equals("PivioDocument")) {
      if (enumsToAnnotate.contains(propertyName)) {
        field.annotate(JsonMerge.class);
      }
    }
  }

}

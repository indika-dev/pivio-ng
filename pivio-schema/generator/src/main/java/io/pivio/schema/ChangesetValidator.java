package io.pivio.schema;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChangesetValidator implements Predicate<JsonNode> {

  static {
    try {
      CHANGESETSCHEMA = SchemaModule.provideJsonPatchSchema();
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private static JsonSchema CHANGESETSCHEMA;

  public boolean test(JsonNode node) {
    return ChangesetValidator.validate(node).isValidated();
  }

  public static ValidationResult validate(JsonNode jsonNode) {
    Set<ValidationMessage> validationMessages = CHANGESETSCHEMA.validate(jsonNode).stream().collect(Collectors.toSet());
    return new ValidationResult(validationMessages.isEmpty(), validationMessages);
  }
}

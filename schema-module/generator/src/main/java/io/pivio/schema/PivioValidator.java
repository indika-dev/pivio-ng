package io.pivio.schema;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PivioValidator implements Predicate<JsonNode> {

  static {
    try {
      PIVIOSCHEMA = SchemaModule.providePivioSchema();
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private static JsonSchema PIVIOSCHEMA;

  public boolean test(JsonNode node) {
    return PivioValidator.validate(node).isValidated();
  }

  public static ValidationResult validate(JsonNode jsonNode) {
    Set<ValidationMessage> validationMessages = PIVIOSCHEMA.validate(jsonNode).stream()
        .filter(msg -> !msg.toString().contains("$.created"))
        .filter(msg -> !msg.toString().contains("$.lastUpload"))
        .filter(msg -> !msg.toString().contains("$.lastUpdate"))
        .filter(msg -> !msg.toString().contains("$.service.provides")).collect(Collectors.toSet());
    return new ValidationResult(validationMessages.isEmpty(), validationMessages);
  }
}

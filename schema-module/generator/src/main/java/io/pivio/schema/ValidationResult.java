package io.pivio.schema;

import java.util.Set;

import com.networknt.schema.ValidationMessage;

public record ValidationResult(boolean isValidated, Set<ValidationMessage> validationMessages) {
}

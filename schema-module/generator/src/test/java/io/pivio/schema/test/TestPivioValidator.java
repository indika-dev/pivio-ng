package io.pivio.schema.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.pivio.schema.PivioValidator;
import io.pivio.schema.generated.pivio.DependsOn;
import io.pivio.schema.generated.pivio.Internal;
import io.pivio.schema.generated.pivio.PivioObject;
import io.pivio.schema.generated.pivio.Runtime;
import io.pivio.schema.generated.pivio.Service;

/**
 * TestPivioValidator
 */
public class TestPivioValidator {

  @TempDir
  private Path workingDirectory;

  private JsonMapper jsonMapper = new JsonMapper(new JsonFactory());
  private Supplier<PivioObject> pivioGenerator = () -> new PivioObject();
  private PivioValidator validator;

  @BeforeEach
  public void initialise() throws Throwable {}

  @Test
  public void testEmptyValidation() throws Exception {
    PivioObject testObj = pivioGenerator.get();
    assertFalse(PivioValidator.validate(jsonMapper.valueToTree(testObj)).isValidated());
  }

  @Test
  public void testValidation() throws Exception {
    PivioObject testObj = pivioGenerator.get();
    testObj.id = "test";
    testObj.name = "test";
    testObj.owner = "test";
    testObj.description = "test";
    assertTrue(PivioValidator.validate(jsonMapper.valueToTree(testObj)).isValidated());
  }

  @Test
  public void testMissingValidation() throws Exception {
    PivioObject testObj = pivioGenerator.get();
    testObj.id = "test";
    testObj.name = "test";
    testObj.description = "test";
    assertFalse(PivioValidator.validate(jsonMapper.valueToTree(testObj)).isValidated());
  }

  @Test
  public void testInternalOneOfValidation() throws Exception {
    PivioObject sourceObj = pivioGenerator.get();
    sourceObj.id = "test";
    sourceObj.name = "test";
    sourceObj.owner = "test";
    sourceObj.description = "test";
    sourceObj.service = new Service();
    sourceObj.service.dependsOn = new DependsOn();
    sourceObj.service.dependsOn.internal = new ArrayList<>();
    Internal internal = new Internal();
    sourceObj.service.dependsOn.internal.add(internal);
    JsonNode testObj = jsonMapper.valueToTree(sourceObj);
    assertTrue(PivioValidator.validate(testObj).isValidated());
    internal.serviceName = "test";
    assertTrue(PivioValidator.validate(testObj).isValidated());
    internal.serviceName = null;
    internal.shortName = "test";
    internal.port = 443;
    assertTrue(PivioValidator.validate(testObj).isValidated());
    internal.serviceName = "test";
    internal.shortName = "test";
    internal.port = 0;
    assertTrue(PivioValidator.validate(testObj).isValidated());
    internal.serviceName = null;
    internal.shortName = null;
    internal.port = -1;
    assertTrue(PivioValidator.validate(testObj).isValidated());
  }

  @Test
  public void testRuntimeHosttypeValidation() throws Exception {
    PivioObject testObj = pivioGenerator.get();
    testObj.id = "test";
    testObj.name = "test";
    testObj.owner = "test";
    testObj.description = "test";
    testObj.runtime = new Runtime();
    testObj.runtime.hostType = "VM";
    testObj.runtime.networkZone = "internal";
    assertTrue(PivioValidator.validate(jsonMapper.valueToTree(testObj)).isValidated());
    testObj.runtime.hostType = "k8s";
    assertTrue(PivioValidator.validate(jsonMapper.valueToTree(testObj)).isValidated());
  }
}

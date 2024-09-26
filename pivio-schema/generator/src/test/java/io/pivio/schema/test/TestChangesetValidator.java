package io.pivio.schema.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.pivio.schema.ChangesetValidator;
import io.pivio.schema.PivioValidator;
import io.pivio.schema.generated.Changeset;
import io.pivio.schema.generated.License;
import io.pivio.schema.generated.License.FullName;
import io.pivio.schema.generated.Links;
import io.pivio.schema.generated.PivioDocument;
import io.pivio.schema.generated.PivioObject;
import io.pivio.schema.generated.SoftwareDependency;
import lombok.extern.slf4j.Slf4j;

/**
 * TestPivioValidator
 */
@Slf4j
public class TestChangesetValidator {

  @TempDir
  private Path workingDirectory;

  private JsonMapper jsonMapper = new JsonMapper(new JsonFactory());
  private Supplier<PivioObject> pivioGenerator = () -> new PivioObject();
  private Supplier<Changeset> changesetGenerator = () -> new Changeset();

  @BeforeEach
  public void initialise() throws Throwable {}

  @Test
  public void testEmptyValidation() throws Exception {
    Changeset testSet = changesetGenerator.get();
    assertFalse(ChangesetValidator.validate(jsonMapper.valueToTree(testSet)).isValidated());
  }

  @Test
  public void testValidation() throws Exception {
    Changeset testSet = changesetGenerator.get().withOrder(1l)
        .withDocument(UUID.randomUUID().toString()).withTimestamp(DateTime.now());
    assertTrue(PivioValidator.validate(jsonMapper.valueToTree(testSet)).isValidated());
  }

  @Test
  public void testMissingValidation() throws Exception {
    PivioDocument testObj = pivioGenerator.get();
    testObj.id = "test";
    testObj.name = "test";
    testObj.description = "test";
    assertFalse(PivioValidator.validate(jsonMapper.valueToTree(testObj)).isValidated());
  }

  @Test
  public void testInternalOneOfValidation() throws Exception {
    PivioDocument sourceObj = pivioGenerator.get();
    sourceObj.id = "test";
    sourceObj.name = "test";
    sourceObj.owner = "test";
    sourceObj.description = "test";
    sourceObj.shortName = "test";
    sourceObj.softwareDependencies = new ArrayList<>();
    sourceObj.softwareDependencies.add(new SoftwareDependency().withName("PostgresQL")
        .withLicenses(List.of(new License().withFullName(FullName.GPL_3_0))));
    JsonNode testObj = jsonMapper.valueToTree(sourceObj);
    assertTrue(PivioValidator.validate(testObj).isValidated());
  }

  @Test
  public void testRuntimeHosttypeValidation() throws Exception {
    PivioDocument sourceObj = pivioGenerator.get();
    sourceObj.id = "test";
    sourceObj.name = "test";
    sourceObj.owner = "test";
    sourceObj.description = "test";
    sourceObj.shortName = "test";
    sourceObj.softwareDependencies = new ArrayList<>();
    sourceObj.softwareDependencies.add(new SoftwareDependency());
    sourceObj.type = PivioDocument.Type.SERVICE;
    sourceObj.links = new Links();
    sourceObj.vcsroot = new String();
    JsonNode testObj = jsonMapper.valueToTree(sourceObj);
    assertTrue(PivioValidator.validate(testObj).isValidated());
  }
}

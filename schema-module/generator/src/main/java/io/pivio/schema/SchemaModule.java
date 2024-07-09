package io.pivio.schema;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion.VersionFlag;

/**
 * SchemaModule
 */
public abstract class SchemaModule {

  public static String providePathToPivioSchemaFile() {
    return new String("/schema/pivio/steckbrief-schema.json");
  }

  public static String providePathToJsonPatchSchemaFile() {
    return new String("/schema/changeset-validation-schema.json");
  }

  public static JsonSchema providePivioSchema() {
    return schemaFactory().getSchema(
        SchemaModule.class.getResourceAsStream(providePathToPivioSchemaFile()),
        schemaValidatorsConfig());
  }

  public static JsonSchema provideJsonPatchSchema() {
    return schemaFactory().getSchema(
        SchemaModule.class.getResourceAsStream(providePathToJsonPatchSchemaFile()),
        schemaValidatorsConfig());
  }

  public static JsonSchemaFactory schemaFactory() {
    return JsonSchemaFactory.getInstance(VersionFlag.V202012);
  }

  public static SchemaValidatorsConfig schemaValidatorsConfig() {
    return new SchemaValidatorsConfig.Builder().readOnly(true).build();
  }
}

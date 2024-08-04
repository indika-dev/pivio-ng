package io.pivio.schema.transform;

import java.io.IOException;

/**
 * TransformPivioDefinition
 */
public class TransformPivioDefinition {
  public static void main(String[] args) throws IOException {

    // How to access the test artifacts, i.e. JSON files
    // JsonUtils.classpathToList : assumes you put the test artifacts in your class
    // path
    // JsonUtils.filepathToList : you can use an absolute path to specify the files

    List chainrSpecJSON = JsonUtils.classpathToList("/jolt/OSMappingTransform.json");
    Chainr chainr = Chainr.fromSpec(chainrSpecJSON);

    Object inputJSON = JsonUtils.classpathToObject("/schema/pivio/steckbrief-schema.json");

    Object transformedOutput = chainr.transform(inputJSON);
    System.out.println(JsonUtils.toJsonString(transformedOutput));
  }
}

package io.pivio.schema.transform;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * applies the standard transformations to the included pivio schema
 */
public class JoltTransform {

  public static void main(String[] args) throws IOException {
    if (args == null || args.length == 0 || args.length < 3) {
      System.err.println("<path to source file> <path to jolt spec file> <path to target file>");
      return;
    }
    String SOURCE_TO_TRANSFORM = args[0];
    String TRANSFORMATION_RULES_SOURCE = args[1];
    String TARGETFILE = args[2];

    System.out.println("schema-transform: file to transform -> " + SOURCE_TO_TRANSFORM);
    System.out
        .println("schema-transform: jolt spec to be applied -> " + TRANSFORMATION_RULES_SOURCE);
    System.out.println("schema-transform: file to be written -> " + TARGETFILE);

    Chainr chainr = Chainr.fromSpec(findAndParseJoltSpec(TRANSFORMATION_RULES_SOURCE));
    LinkedHashMap<String, Object> inputJSON = findAndParseJsonFile(SOURCE_TO_TRANSFORM);
    LinkedHashMap<String, Object> transformedOutput = (LinkedHashMap) chainr.transform(inputJSON);
    removeTildeFromKeys(transformedOutput);

    Path fileToGenerate = Paths.get(TARGETFILE);
    if (fileToGenerate.getParent().toFile().mkdirs()) {
      System.out.println("schema-transform: created directories for " + TARGETFILE);
    }
    if (!fileToGenerate.toFile().createNewFile()) {
      if (fileToGenerate.toFile().delete()) {
        if (!fileToGenerate.toFile().createNewFile()) {
          System.err.println("schema-transform: can't create file " + TARGETFILE);
          return;
        }
      } else {
        System.err.println("schema-transform: can't delete existing file " + TARGETFILE);
        return;
      }
    }
    Files.writeString(fileToGenerate, JsonUtils.toPrettyJsonString(transformedOutput));
    System.out.println("schema-transform: transformed successfully");
  }

  private static Object findAndParseJoltSpec(String pathToFile)
      throws IOException, FileNotFoundException {
    // How to access the test artifacts, i.e. JSON files
    // JsonUtils.classpathToList : assumes you put the test artifacts in your class
    // path
    // JsonUtils.filepathToList : you can use an absolute path to specify the files

    URL transformSpecURL = ClassLoader.getSystemClassLoader().getResource(pathToFile);
    if (transformSpecURL != null && "jar".equals(transformSpecURL.getProtocol())) {
      ReadableByteChannel readableByteChannel = Channels.newChannel(transformSpecURL.openStream());
      Path extractedFile = Files.createTempFile("jolt", ".json");
      try (FileOutputStream fos = new FileOutputStream(extractedFile.toFile())) {
        FileChannel fileChannel = fos.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
      }
      return JsonUtils.filepathToList(extractedFile.toString());
    } else if (transformSpecURL != null) {
      return JsonUtils.classpathToList(pathToFile);
    } else {
      return JsonUtils.filepathToList(pathToFile);
    }
  }

  private static LinkedHashMap<String, Object> findAndParseJsonFile(String pathToFile)
      throws IOException, FileNotFoundException {
    // How to access the test artifacts, i.e. JSON files
    // JsonUtils.classpathToList : assumes you put the test artifacts in your class
    // path
    // JsonUtils.filepathToList : you can use an absolute path to specify the files

    URL transformSpecURL = ClassLoader.getSystemClassLoader().getResource(pathToFile);
    if (transformSpecURL != null && "jar".equals(transformSpecURL.getProtocol())) {
      ReadableByteChannel readableByteChannel = Channels.newChannel(transformSpecURL.openStream());
      Path extractedFile = Files.createTempFile("jolt", ".json");
      try (FileOutputStream fos = new FileOutputStream(extractedFile.toFile())) {
        FileChannel fileChannel = fos.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
      }
      return (LinkedHashMap) JsonUtils.filepathToObject(extractedFile.toString());
    } else if (transformSpecURL != null) {
      return (LinkedHashMap) JsonUtils.classpathToObject(pathToFile);
    } else {
      return (LinkedHashMap) JsonUtils.filepathToObject(pathToFile);
    }
  }

  private static void removeTildeFromKeys(LinkedHashMap<String, Object> node) {
    List<String> keysWithTilde = node.keySet().stream().filter(key -> key.startsWith("~")).toList();
    keysWithTilde.stream().forEach(key -> {
      node.putFirst(key.substring(1, key.length()), node.remove(key));
    });
    node.values().stream().filter(value -> value instanceof LinkedHashMap)
        .forEach(value -> removeTildeFromKeys((LinkedHashMap) value));
  }

}

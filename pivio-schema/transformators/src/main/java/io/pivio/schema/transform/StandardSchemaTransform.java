package io.pivio.schema.transform;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * applies the standard transformations to the included pivio schema
 */
public class StandardSchemaTransform {

  private static final String SOURCE_TO_TRANSFORM_FILENAME = "mapping.json";
  private static final String SOURCE_TO_TRANSFORM = "schema/pivio/" + SOURCE_TO_TRANSFORM_FILENAME;
  private static final String TRANSFORMATION_RULES_SOURCE = "jolt/OSMappingTransform.json";

  private static ObjectNode removeTildeFromKeys(ObjectNode node) {
    // A naive depth-first search implementation using recursion. Useful
    // **only** for small object graphs. This will be inefficient
    // (stack overflow) for finding deeply-nested needles or needles
    // toward the end of a forest with deeply-nested branches.
    if (node == null) {
      return null;
    }
    List<Entry<String, JsonNode>> fieldsWithTilde = new ArrayList<>();
    node.fields().forEachRemaining(currentEntry -> {
      if (currentEntry.getKey().startsWith("~")) {
        fieldsWithTilde.add(currentEntry);
      }
    });
    fieldsWithTilde.forEach(entry -> {
      node.put(entry.getKey().substring(1, entry.getKey().length()), node.remove(entry.getKey()));
    });
    for (JsonNode child : node) {
      if (child.isContainerNode()) {
        removeTildeFromKeys((ObjectNode) child);
      }
    }
    return node;
  }

  public static void main(String[] args) throws IOException {
    if (args == null || args.length == 0) {
      System.err.println("schema-transform: path to generated file must be provided");
      return;
    }

    // How to access the test artifacts, i.e. JSON files
    // JsonUtils.classpathToList : assumes you put the test artifacts in your class
    // path
    // JsonUtils.filepathToList : you can use an absolute path to specify the files

    URL transformSpecURL = StandardSchemaTransform.class.getClassLoader().getResource(TRANSFORMATION_RULES_SOURCE);
    List<Object> chainrSpecJSON = new ArrayList<>();
    if (transformSpecURL != null && "jar".equals(transformSpecURL.getProtocol())) {
      ReadableByteChannel readableByteChannel = Channels.newChannel(transformSpecURL.openStream());
      Path extractedFile = Files.createTempFile("jolt", ".json");
      try (FileOutputStream fos = new FileOutputStream(extractedFile.toFile())) {
        FileChannel fileChannel = fos.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
      }
      chainrSpecJSON = JsonUtils.filepathToList(extractedFile.toString());
    } else {
      chainrSpecJSON = JsonUtils.classpathToList(TRANSFORMATION_RULES_SOURCE);
    }

    Chainr chainr = Chainr.fromSpec(chainrSpecJSON);

    URL inputJsonURL = StandardSchemaTransform.class.getClassLoader()
        .getResource(SOURCE_TO_TRANSFORM);
    Object inputJSON = null;
    if (inputJsonURL != null && "jar".equals(inputJsonURL.getProtocol())) {
      Path tmpDirectory = Files.createTempDirectory("tmpTransform");
      tmpDirectory.toFile().deleteOnExit();
      byte[] buffer = new byte[8 * 1024];
      Path extractedFile = Paths.get(tmpDirectory.toString(), SOURCE_TO_TRANSFORM_FILENAME);
      try (
          InputStream is = StandardSchemaTransform.class.getClassLoader()
              .getResourceAsStream(SOURCE_TO_TRANSFORM);
          OutputStream os = new FileOutputStream(extractedFile.toFile());) {
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
          os.write(buffer, 0, bytesRead);
        }
      }
      inputJSON = JsonUtils.filepathToObject(extractedFile.toString());
    } else {
      inputJSON = JsonUtils.filepathToObject(inputJsonURL.getFile());
    }

    Object transformedOutput = removeTildeFromKeys((ObjectNode) chainr.transform(inputJSON));
    Path generatedFile = Paths.get(args[0]);
    if (!generatedFile.toFile().createNewFile()) {
      if (generatedFile.toFile().delete()) {
        if (!generatedFile.toFile().createNewFile()) {
          System.err.println("schema-transform: can't create file " + args[0]);
          return;
        }
      } else {
        System.err.println("schema-transform: can't delete existing file " + args[0]);
        return;
      }
    }
    Files.writeString(generatedFile, JsonUtils.toJsonString(transformedOutput));
  }
}

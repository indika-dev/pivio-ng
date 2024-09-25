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
import java.util.LinkedHashMap;
import java.util.List;
import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;

/**
 * applies the standard transformations to the included pivio schema
 */
public class StandardSchemaTransform {

  private static final String SOURCE_TO_TRANSFORM_FILENAME = "mapping.json";
  private static final String SOURCE_TO_TRANSFORM = "schema/pivio/" + SOURCE_TO_TRANSFORM_FILENAME;
  private static final String TRANSFORMATION_RULES_SOURCE = "jolt/OSMappingTransform.json";

  private static void removeTildeFromKeys(LinkedHashMap<String, Object> node) {
    List<String> keysWithTilde = node.keySet().stream().filter(key -> key.startsWith("~")).toList();
    keysWithTilde.stream().forEach(key -> {
      node.putFirst(key.substring(1, key.length()), node.remove(key));
    });
    node.values().stream().filter(value -> value instanceof LinkedHashMap)
        .forEach(value -> removeTildeFromKeys((LinkedHashMap) value));
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

    URL transformSpecURL =
        StandardSchemaTransform.class.getClassLoader().getResource(TRANSFORMATION_RULES_SOURCE);
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

    URL inputJsonURL =
        StandardSchemaTransform.class.getClassLoader().getResource(SOURCE_TO_TRANSFORM);
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

    LinkedHashMap<String, Object> transformedOutput = (LinkedHashMap) chainr.transform(inputJSON);
    removeTildeFromKeys(transformedOutput);
    System.out.println("writing transformed json to " + args[0]);
    Path generatedFile = Paths.get(args[0]);
    if (generatedFile.getParent().toFile().mkdirs()) {
      System.out.println("schema-transform: created directories for " + args[0]);
    }
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
    Files.writeString(generatedFile, JsonUtils.toPrettyJsonString(transformedOutput));
  }
}

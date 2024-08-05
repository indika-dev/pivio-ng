package io.pivio.schema.transform;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;

// @formatter:off
/**
 * applies the provided transformations to the provided pivio schema
 *
 * args[0] contains the absolute file path or path inside classpath to the transformation rules
 * args[1] contains the absolute file path or path inside classpath to the pivio Schema
 * args[2] contains the absolute file path for the transformed schema
 */
// @formatter:on
public class CustomSchemaTransform {
  public static void main(String[] args) throws IOException {

    URL transformSpecURL = CustomSchemaTransform.class.getClassLoader().getResource(args[0]);
    List<Object> chainrSpecJSON = new ArrayList<>();
    if (transformSpecURL != null && "jar".equals(transformSpecURL.getProtocol())) {
      Path tmpDirectory = Files.createTempDirectory("tmpJolt");
      tmpDirectory.toFile().deleteOnExit();
      byte[] buffer = new byte[8 * 1024];
      Path extractedFile = Paths.get(tmpDirectory.toString(), "CustomTransform.json");
      try (
          InputStream is =
              CustomSchemaTransform.class.getClassLoader().getResourceAsStream(args[0]);
          OutputStream os = new FileOutputStream(extractedFile.toFile());) {
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
          os.write(buffer, 0, bytesRead);
        }
      }
      chainrSpecJSON = JsonUtils.filepathToList(extractedFile.toString());
    } else {
      chainrSpecJSON = JsonUtils.classpathToList(args[0]);
    }

    Chainr chainr = Chainr.fromSpec(chainrSpecJSON);

    URL inputJsonURL = CustomSchemaTransform.class.getClassLoader().getResource(args[1]);
    Object inputJSON = null;
    if (inputJsonURL != null && "jar".equals(inputJsonURL.getProtocol())) {
      Path tmpDirectory = Files.createTempDirectory("tmpTransform");
      tmpDirectory.toFile().deleteOnExit();
      byte[] buffer = new byte[8 * 1024];
      Path extractedFile = Paths.get(tmpDirectory.toString(), "SrcSchema.json");
      try (
          InputStream is =
              CustomSchemaTransform.class.getClassLoader().getResourceAsStream(args[1]);
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

    Object transformedOutput = chainr.transform(inputJSON);

    Path filePath = Paths.get(args[2]);
    Files.deleteIfExists(filePath);
    Files.createFile(filePath);
    Files.writeString(filePath, JsonUtils.toJsonString(transformedOutput),
        StandardOpenOption.APPEND);
  }
}

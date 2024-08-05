package io.pivio.schema.transform;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;

/**
 * TransformPivioDefinition
 */
public class TransformPivioDefinition {
  public static void main(String[] args) throws IOException {

    // How to access the test artifacts, i.e. JSON files
    // JsonUtils.classpathToList : assumes you put the test artifacts in your class
    // path
    // JsonUtils.filepathToList : you can use an absolute path to specify the files

    URL transformSpecURL =
        TransformPivioDefinition.class.getClassLoader().getResource("jolt/OSMappingTransform.json");
    List<Object> chainrSpecJSON = new ArrayList<>();
    if (transformSpecURL != null && "jar".equals(transformSpecURL.getProtocol())) {
      Path tmpDirectory = Files.createTempDirectory("tmpJolt");
      tmpDirectory.toFile().deleteOnExit();
      byte[] buffer = new byte[8 * 1024];
      Path extractedFile = Paths.get(tmpDirectory.toString(), "OSMappingTransform.json");
      try (
          InputStream is = TransformPivioDefinition.class.getClassLoader()
              .getResourceAsStream("jolt/OSMappingTransform.json");
          OutputStream os = new FileOutputStream(extractedFile.toFile());) {
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
          os.write(buffer, 0, bytesRead);
        }
      }
      chainrSpecJSON = JsonUtils.filepathToList(extractedFile.toString());
    } else {
      chainrSpecJSON = JsonUtils.classpathToList("jolt/OSMappingTransform.json");
    }

    Chainr chainr = Chainr.fromSpec(chainrSpecJSON);

    URL inputJsonURL = TransformPivioDefinition.class.getClassLoader()
        .getResource("schema/pivio/steckbrief-schema.json");
    Object inputJSON = null;
    if (inputJsonURL != null && "jar".equals(inputJsonURL.getProtocol())) {
      Path tmpDirectory = Files.createTempDirectory("tmpTransform");
      tmpDirectory.toFile().deleteOnExit();
      byte[] buffer = new byte[8 * 1024];
      Path extractedFile = Paths.get(tmpDirectory.toString(), "steckbrief-schema.json");
      try (
          InputStream is = TransformPivioDefinition.class.getClassLoader()
              .getResourceAsStream("schema/pivio/steckbrief-schema.json");
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
    System.out.println(JsonUtils.toJsonString(transformedOutput));
  }
}

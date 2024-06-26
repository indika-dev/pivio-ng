package io.pivio.schema.annotations.proxy;

import io.pivio.schema.annotations.custom.CustomJsonAnnotator;

/**
 * this is only a dummy class. The real implementation is found on core submodule with exact same
 * package and class name
 */
public class CustomEnumDeserializer {

  public static void main(String[] args) {
    System.out.println();
    for (int i = 0; i < 10; i++) {
      System.out.print(i);
    }
    CustomJsonAnnotator annotator = new CustomJsonAnnotator();
    annotator.propertyField(null, null, null, null);
  }

}

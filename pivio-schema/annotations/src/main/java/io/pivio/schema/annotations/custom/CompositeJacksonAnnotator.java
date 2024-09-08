package io.pivio.schema.annotations.custom;

import org.jsonschema2pojo.CompositeAnnotator;

/**
 * CompositeAnnotator
 */
public class CompositeJacksonAnnotator extends CompositeAnnotator {

  public CompositeJacksonAnnotator() {
    super(new CustomJsonAnnotator(), new CustomJsonMergeAnnotator(), new CustomChangesetAnnotator());
  }

}

package io.pivio.schema.annotations.custom;

/**
 * CompositeAnnotator
 */
public class CompositeAnnotator extends org.jsonschema2pojo.CompositeAnnotator {

  public CompositeAnnotator() {
    super(new CustomJsonAnnotator(), new CustomJsonMergeAnnotator(), new CustomChangesetAnnotator(),
        new CustomPivioDocumentAnnotator(), new CustomFieldsAnnotator());
  }

}

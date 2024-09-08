package io.pivio.schema.annotations.custom;

import org.jsonschema2pojo.CompositeAnnotator;

/**
 * CompositeAnnotator
 */
public class CompositeOpenSearchAnnotator extends CompositeAnnotator {

  public CompositeOpenSearchAnnotator() {
    super(new CustomChangesetAnnotator(),
        new CustomPivioDocumentAnnotator(), new CustomFieldsAnnotator(), new CustomSoftwareDependencyAnnotator(),
        new CustomLicenseAnnotator());
  }

}

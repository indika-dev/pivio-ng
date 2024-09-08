package io.pivio.schema.annotations.custom;

import org.jsonschema2pojo.CompositeAnnotator;

/**
 * CompositeAnnotator
 */
public class CompositeAddAllAnnotator extends CompositeAnnotator {

  public CompositeAddAllAnnotator() {
    super(new CustomJsonAnnotator(), new CustomJsonMergeAnnotator(), new CustomChangesetAnnotator(),
        new CustomPivioDocumentAnnotator(), new CustomFieldsAnnotator(), new CustomSoftwareDependencyAnnotator(),
        new CustomLicenseAnnotator());
  }

}

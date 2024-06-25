package io.pivio.view.app.overview.detail.view;

import io.pivio.view.app.overview.detail.serverresponse.Document;
import io.pivio.view.app.overview.detail.serverresponse.License;
import io.pivio.view.app.overview.detail.serverresponse.SoftwareDependency;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentViewModelTest {

    private DocumentViewModel documentViewModel;

    @Before
    public void setup() {
        Document document = new Document();
        document.short_name = "SHORT";
        document.software_dependencies = generateDependencies();
        documentViewModel = new DocumentViewModel(document);
    }

    @Test
    public void getConsolidatedLicenses() throws Exception {
        List<String> consolidatedLicenses = documentViewModel.getConsolidatedLicenses();
        assertThat(consolidatedLicenses).hasSize(7);
    }

    @Test
    public void testContainsUnknownLicense() throws Exception {
        List<String> consolidatedLicenses = documentViewModel.getConsolidatedLicenses();
        assertThat(consolidatedLicenses).contains(documentViewModel.UNKNOWN_LICENSE);
    }

    @Test
    public void testSortedDependencies() throws Exception {
        List<SoftwareDependency> sortedDependencies = documentViewModel.getSortedDependencies();
        assertThat(sortedDependencies.get(0).name).isEqualTo("A");
        assertThat(sortedDependencies.get(1).name).isEqualTo("B");
        assertThat(sortedDependencies.get(2).name).isEqualTo("C");
    }

    @Test
    public void testGetShortName() throws Exception {
        assertThat(documentViewModel.getShortName()).isEqualTo("(SHORT)");
    }

    @Test
    public void testGetNullShortName() throws Exception {
        Document document = new Document();
        document.short_name = null;
        documentViewModel = new DocumentViewModel(document);

        assertThat(documentViewModel.getShortName()).isEqualTo("");
    }

    private ArrayList<SoftwareDependency> generateDependencies() {
        ArrayList<SoftwareDependency> softwareDependencies = new ArrayList<>();
        softwareDependencies.add(new SoftwareDependency("B", "1", generateLicenses("-B-")));
        softwareDependencies.add(new SoftwareDependency("A", "1", generateLicenses("-A-")));
        softwareDependencies.add(new SoftwareDependency("C", "1", generateLicenses("-A-")));
        return softwareDependencies;
    }

    private ArrayList<License> generateLicenses(String prefix) {
        ArrayList<License> licenses = new ArrayList<>();
        licenses.add(new License(prefix + "L1", prefix + "URL1"));
        licenses.add(new License(prefix + "L2", prefix + "URL2"));
        licenses.add(new License(null, prefix + "URL3"));
        licenses.add(new License(prefix + "L4", null));
        return licenses;
    }

}
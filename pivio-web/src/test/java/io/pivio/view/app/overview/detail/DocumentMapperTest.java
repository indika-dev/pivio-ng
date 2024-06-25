package io.pivio.view.app.overview.detail;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivio.view.app.overview.detail.serverresponse.Document;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


public class DocumentMapperTest {

    @Test
    public void testMapDetail() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Document document = mapper.readValue(new File("src/test/resources/detail.json"), Document.class);

        assertThat(document.id).isEqualTo("next-generation-print-2342-2413-9189-1990");
    }

    @Test
    public void testMapDetailLicenses() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Document document = mapper.readValue(new File("src/test/resources/DetailWithLicenses.json"), Document.class);

        assertThat(document.id).isEqualTo("349534957349857387534");
    }

}

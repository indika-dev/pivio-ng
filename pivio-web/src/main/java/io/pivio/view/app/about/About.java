package io.pivio.view.app.about;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Controller
public class About {

    private final Logger log = LoggerFactory.getLogger(About.class);

    @RequestMapping("/about")
    public String about(Model model) {
        String gitHash = "DEV-VERSION";
        InputStream in = this.getClass().getResourceAsStream("/VERSION");
        if (in != null) {
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(in))) {
                gitHash = buffer.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        model.addAttribute("githash", gitHash);
        return "about";
    }
}

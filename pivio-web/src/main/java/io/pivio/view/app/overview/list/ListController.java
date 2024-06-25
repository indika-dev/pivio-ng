package io.pivio.view.app.overview.list;

import io.pivio.view.configuration.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ListController {

    @Autowired
    ServerConfig serverConfig;

    @Autowired
    ListService listService;

    @RequestMapping("/")
    public String main() {
        return "redirect:/app/overview";
    }

    @RequestMapping("/app/overview")
    public String overview(Model model) {
        model.addAttribute("config", serverConfig);
        model.addAttribute("pageId", "tabOverview");
        List<OverviewModel> overview = new ArrayList<>();
        try {
            overview = listService.getShortInfo();
        } catch (IOException e) {
            model.addAttribute("pivioErrorMessage", "Document server is not reachable. Please try again later. ("+e.getMessage()+")");
        }
        model.addAttribute("pivioOverview", overview);
        return "overview";
    }

    @RequestMapping("/app/overview/list")
    public String similar(@RequestParam("field") String field, @RequestParam("value") String value, Model model) {
        model.addAttribute("config", serverConfig);
        model.addAttribute("pageId", "tabOverview");
        model.addAttribute("field", field);
        model.addAttribute("value", value);
        return "list";
    }
}

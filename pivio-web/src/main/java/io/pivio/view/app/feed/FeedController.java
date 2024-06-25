package io.pivio.view.app.feed;

import io.pivio.view.configuration.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FeedController {

    @Autowired
    ServerConfig serverConfig;

    @Autowired
    FeedService feedService;

    @RequestMapping(value = "/app/feed")
    public String overview(Model model) {
        model.addAttribute("config", serverConfig);
        model.addAttribute("pageId", "tabFeed");
        model.addAttribute("changeset", feedService.getChangeset());
        return "feed";
    }
}

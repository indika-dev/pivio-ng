package io.pivio.view.app.query;

import io.pivio.view.configuration.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class QueryController {

  @Autowired
  ServerConfig serverConfig;

  @RequestMapping(value = "/app/query")
  public String search(Model model) {
    model.addAttribute("config", serverConfig);
    model.addAttribute("pageId", "tabQuery");
    return "query";
  }
}

package com.agussuhardi.simulator.web;

import com.agussuhardi.simulator.service.RestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@AllArgsConstructor
public class IndexController {

  private final RestService restService;

  @GetMapping({"/"})
  public String index(Model model, @RequestParam(value = "title", required = false) String title) {

    model.addAttribute("contents", restService.list(title));
    model.addAttribute("title", title);
    return "index";
  }
}

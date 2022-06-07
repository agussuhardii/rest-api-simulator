package com.agussuhardi.simulator.service;

import com.agussuhardi.simulator.entity.Rest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface RestService {

  ResponseEntity<?> request(
      Map<String, String> headers,
      Map<String, Object> bodies,
      HttpServletRequest httpServletRequest,
      RequestMethod requestMethod)
      throws InterruptedException;

  List<Rest> list(String projectName);

  String save(Rest rest, String id);
}

package com.agussuhardi.simulator.controller;

import com.agussuhardi.simulator.service.RestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("{projectName}")
@AllArgsConstructor
@Slf4j
@Validated
public class ThreePathController {

  private final RestService restService;

  @PostMapping(value = "{first}/{second}/{third}")
  public ResponseEntity<?> post(
      @RequestHeader Map<String, String> headers,
      @RequestBody Map<String, Object> bodies,
      HttpServletRequest httpServletRequest)
      throws InterruptedException {

    return this.restService.request(headers, bodies, httpServletRequest, RequestMethod.POST);
  }

  @GetMapping(value = "{first}/{second}/{third}")
  public ResponseEntity<?> get(
      @RequestHeader Map<String, String> headers,
      @RequestParam Map<String, Object> params,
      HttpServletRequest httpServletRequest)
      throws InterruptedException {

    return this.restService.request(headers, params, httpServletRequest, RequestMethod.GET);
  }

  @PutMapping(value = "{first}/{second}/{third}")
  public ResponseEntity<?> put(
      @RequestHeader Map<String, String> headers,
      @RequestParam Map<String, Object> params,
      HttpServletRequest httpServletRequest)
      throws InterruptedException {

    return this.restService.request(headers, params, httpServletRequest, RequestMethod.PUT);
  }

  @DeleteMapping(value = "{first}/{second}/{third}")
  public ResponseEntity<?> delete(
      @RequestHeader Map<String, String> headers,
      @RequestParam Map<String, Object> params,
      HttpServletRequest httpServletRequest)
      throws InterruptedException {

    return this.restService.request(headers, params, httpServletRequest, RequestMethod.DELETE);
  }
}

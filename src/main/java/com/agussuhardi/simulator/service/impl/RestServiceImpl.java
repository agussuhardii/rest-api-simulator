package com.agussuhardi.simulator.service.impl;

import com.agussuhardi.simulator.repository.RestRepository;
import com.google.common.base.Strings;
import com.agussuhardi.simulator.config.CustomException;
import com.agussuhardi.simulator.entity.Rest;
import com.agussuhardi.simulator.service.RestService;
import com.agussuhardi.simulator.util.ConvertUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class RestServiceImpl implements RestService {

  private final RestRepository restRepository;

  @Override
  public ResponseEntity<?> request(
      Map<String, String> headers,
      Map<String, Object> bodies,
      HttpServletRequest httpServletRequest,
      RequestMethod requestMethod)
      throws InterruptedException {

    var path = httpServletRequest.getRequestURI().split("/");
    var projectName = path[1];

    if (Strings.isNullOrEmpty(projectName))
      throw new CustomException("Invalid ProjectName", HttpStatus.BAD_REQUEST);

    StringBuilder pathUrl = new StringBuilder();
    for (int i = 2; i < path.length; i++) {
      pathUrl.append("/").append(path[i]);
    }

    HttpHeaders responseHeaders = new HttpHeaders();
    var optional = restRepository.findByPathUrl(pathUrl.toString(), requestMethod, projectName);
    log.info("data=>{}", optional.isPresent());
    log.info("{},{},{}", pathUrl, requestMethod, projectName);

    if (optional.isEmpty())
      return new ResponseEntity<>(
          new HashMap<>() {
            {
              put("message", HttpStatus.NOT_FOUND);
            }
          },
          responseHeaders,
          HttpStatus.NOT_FOUND);

    var rest = optional.get();
    Thread.sleep(rest.getResponseInNanoSecond());

    //    validate headers request
    for (var request : ConvertUtil.jsonToMap(rest.getRequestHeaders()).entrySet()) {
      var existHeader = false;
      for (var header : headers.entrySet()) {
        if (header.getKey().equalsIgnoreCase(request.getKey())
            && header.getValue().equals(request.getValue())) {
          existHeader = true;
          break;
        }
      }

      if (!existHeader) {
        ConvertUtil.jsonToMapString(rest.getFailResponseHeader()).forEach(responseHeaders::set);
        return new ResponseEntity<>(
            optional.get().getFailResponseBody(),
            responseHeaders,
            HttpStatus.valueOf(rest.getFailResponseCode()));
      }
    }

    //    validate request body
    for (var request : ConvertUtil.jsonToMap(rest.getBody()).entrySet()) {
      var existBody = false;
      for (var body : bodies.entrySet()) {
        if (body.getKey().equals(request.getKey()) && body.getValue().equals(request.getValue())) {
          existBody = true;
          break;
        }
      }

      if (!existBody) {
        ConvertUtil.jsonToMapString(rest.getFailResponseHeader()).forEach(responseHeaders::set);
        return new ResponseEntity<>(
            optional.get().getFailResponseBody(),
            responseHeaders,
            HttpStatus.valueOf(rest.getFailResponseCode()));
      }
    }

    for (var requestParams : ConvertUtil.jsonToMap(rest.getParams()).entrySet()) {
      var existParam = false;
      for (var param : httpServletRequest.getParameterMap().entrySet()) {
        if (param.getKey().equals(requestParams.getKey())
            && param.getValue().equals(requestParams.getValue())) {
          existParam = true;
          break;
        }
      }

      if (!existParam) {
        ConvertUtil.jsonToMapString(rest.getFailResponseHeader()).forEach(responseHeaders::set);
        return new ResponseEntity<>(
            optional.get().getFailResponseBody(),
            responseHeaders,
            HttpStatus.valueOf(rest.getFailResponseCode()));
      }
    }

    ConvertUtil.jsonToMapString(rest.getSuccessResponseHeader()).forEach(responseHeaders::set);
    return new ResponseEntity<>(
        optional.get().getSuccessResponseBody(),
        responseHeaders,
        HttpStatus.valueOf(rest.getSuccessResponseCode()));
  }

  @Override
  public List<Rest> list(String projectName) {
    if (Strings.isNullOrEmpty(projectName)) return restRepository.findAll();
    return restRepository.findAllByProjectName(projectName);
  }

  @Override
  public String save(Rest rest, String id) {

    if (id != null) {
      restRepository
          .findById(id)
          .orElseThrow(() -> new CustomException("ID not found", HttpStatus.NOT_FOUND));
    }

    isPathValid(rest);

    isJSONValid(Rest.Fields.params, rest.getParams());
    isJSONValid(Rest.Fields.body, rest.getBody());
    isJSONValid(Rest.Fields.successResponseBody, rest.getSuccessResponseBody());
    isJSONValid(Rest.Fields.failResponseBody, rest.getFailResponseBody());
    isJSONValid(Rest.Fields.successResponseHeader, rest.getFailResponseHeader());
    isJSONValid(Rest.Fields.requestHeaders, rest.getRequestHeaders());

    try {

      System.out.println("save");
      rest.setProjectName(formatProjectName(rest.getProjectName()));
      restRepository.save(rest);
      return "success";
    } catch (Exception e) {
      return e.getMessage();
    }
  }

  private String formatProjectName(String projectName) {
    projectName = projectName.replaceAll("[^A-Za-z0-9]", "");
    projectName = projectName.replaceAll("\\s", "");
    return projectName.toLowerCase();
  }

  public void isJSONValid(String key, String value) {
    if (value == null) throw new CustomException(key + " not null", HttpStatus.BAD_REQUEST);

    try {
      var map = ConvertUtil.jsonToMap(value);
      if (map == null)
        throw new CustomException("Invalid json format: " + key, HttpStatus.BAD_REQUEST);
    } catch (JSONException ex) {
      throw new CustomException("Invalid json format: " + key, HttpStatus.BAD_REQUEST);
    }
  }

  public void isPathValid(Rest rest) {
    if (rest.getId() != null) {
      var oldRest = restRepository.findById(rest.getId());
      if (oldRest.isPresent()) {
        System.out.println("is present");
        if (oldRest.get().getPathUrl().equals(rest.getPathUrl())
            && oldRest.get().getMethod().equals(rest.getMethod())) {
          return;
        }
      }
    }

    if (rest.getPathUrl().charAt(0) != '/')
      throw new CustomException("Invalid path url: " + rest.getPathUrl(), HttpStatus.BAD_REQUEST);

    var o =
        restRepository.findByPathUrl(rest.getPathUrl(), rest.getMethod(), rest.getProjectName());
    if (o.isPresent())
      throw new CustomException("Already path url :" + rest.getPathUrl(), HttpStatus.BAD_REQUEST);
  }
}

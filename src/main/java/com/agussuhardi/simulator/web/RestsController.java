package com.agussuhardi.simulator.web;

import com.agussuhardi.simulator.config.GlobalApiResponse;
import com.agussuhardi.simulator.entity.Rest;
import com.agussuhardi.simulator.repository.LogsRepository;
import com.agussuhardi.simulator.repository.RestRepository;
import com.agussuhardi.simulator.service.RestService;
import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@Slf4j
@AllArgsConstructor
@Validated
public class RestsController {

  private final RestRepository restRepository;

  private final RestService restService;

  private final LogsRepository logsRepository;

  @DeleteMapping
  public Map<String, String> delete(@RequestParam(value = "id", required = false) String id) {
    restRepository.deleteById(id);
    return new HashMap<>();
  }

  @RequestMapping(
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public GlobalApiResponse<?> save(@RequestBody @Valid Rest rest) {
    return new GlobalApiResponse<>(HttpStatus.OK.value(), restService.save(rest, null));
  }

  @PutMapping
  public ResponseEntity<?> update(@RequestBody @Valid Rest rest) {

    var o = restRepository.findById(rest.getId());
    if (o.isEmpty())
      return new ResponseEntity<>(
          new HashMap<>() {
            {
              put("message", HttpStatus.NOT_FOUND);
            }
          },
          null,
          HttpStatus.NOT_FOUND);
    try {
      return new ResponseEntity<>(
          new HashMap<>() {
            {
              put("message", restService.save(rest, rest.getId()));
            }
          },
          null,
          HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(
          new HashMap<>() {
            {
              put("message", e.getMessage());
            }
          },
          null,
          HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(
      method = RequestMethod.PATCH,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public GlobalApiResponse<?> logs(
      @RequestParam(value = "projectName", required = false) String projectName) {
    return new GlobalApiResponse<>(
        HttpStatus.OK.value(),
        logsRepository.findAllBy(
            Strings.isNullOrEmpty(projectName) ? null : projectName,
            LocalDateTime.now().minusDays(3),
            LocalDateTime.now()));
  }
}

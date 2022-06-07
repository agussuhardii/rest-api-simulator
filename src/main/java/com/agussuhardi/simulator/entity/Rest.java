package com.agussuhardi.simulator.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "rest")
@FieldNameConstants
public class Rest implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @Column(name = "id", columnDefinition = "char(36)")
  private String id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "path_url", nullable = false)
  private String pathUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "method", nullable = false)
  private RequestMethod method;

  @Column(name = "body", nullable = false)
  private String body;

  @Column(name = "params", nullable = false)
  private String params;

  //  @Type(type = "json")
  @Column(name = "success_response_body", columnDefinition = "json")
  private String successResponseBody;

  //  @Type(type = "json")
  @Column(name = "fail_response_body", columnDefinition = "json")
  private String failResponseBody;

  @Column(name = "success_response_code", nullable = false)
  private int successResponseCode;

  @Column(name = "fail_response_code", nullable = false)
  private int failResponseCode;

  //  @Type(type = "json")
  @Column(name = "success_response_header", columnDefinition = "json")
  private String successResponseHeader;

//  @Type(type = "json")
  @Column(name = "fail_response_header", columnDefinition = "json")
  private String failResponseHeader;

//  @Type(type = "json")
  @Column(name = "request_headers", columnDefinition = "json")
  private String requestHeaders;

  @Column(name = "response_in_nano_second", nullable = false)
  private Long responseInNanoSecond;

  private String projectName;

  private String description;

  @CreationTimestamp
  @Column(nullable = false, updatable = false, name = "created_at")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;
}

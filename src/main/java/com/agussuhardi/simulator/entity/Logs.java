package com.agussuhardi.simulator.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@FieldNameConstants
@Table(name = "logs")
@SuperBuilder(toBuilder = true)
public class Logs implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @Column(name = "id", columnDefinition = "char(36)")
  private String id;

  @Column(name = "path_url", nullable = false)
  private String pathUrl;

  @Column(name = "header", nullable = false)
  private String header;

  @Column(name = "body", nullable = false)
  private String body;

  @Column(name = "params", nullable = false)
  private String params;

  @Column(name = "method", nullable = false)
  private String method;

  @CreationTimestamp
  @Column(nullable = false, updatable = false, name = "created_at")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  private String projectName;
}

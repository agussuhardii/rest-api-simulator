package com.agussuhardi.simulator.config.logger;

import com.agussuhardi.simulator.repository.LogsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.agussuhardi.simulator.entity.Logs;
import com.agussuhardi.simulator.util.ConvertUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * revisi/log_notif 03/09/21
 *
 * @author agussuhardi
 */
@Slf4j
public class CustomHttpRequestWrapper extends HttpServletRequestWrapper {

  private String bodyInStringFormat;

  public CustomHttpRequestWrapper(HttpServletRequest request, LogsRepository logsRepository)
      throws IOException {
    super(request);

    if (Arrays.asList("POST", "PUT", "PATCH").contains(request.getMethod())) {
      bodyInStringFormat =
          readInputStreamInStringFormat(
              request.getInputStream(), Charset.forName(request.getCharacterEncoding()));
    }

    var params = request.getParameterMap();
    final ObjectMapper mapper = new ObjectMapper();
    var paramsObject = mapper.convertValue(params, Object.class);

    log.info(
        "request from => {hostname: {}, port: {}, httpMethod: {}, url: {}, requestDate:{}, Body: {}}",
        request.getRemoteHost(),
        request.getServerPort(),
        request.getMethod(),
        request.getRequestURI(),
        LocalDateTime.now(),
        bodyInStringFormat);

    Map<String, String> headers =
        Collections.list(request.getHeaderNames()).stream()
            .collect(Collectors.toMap(h -> h, request::getHeader));

    var path = request.getRequestURI().split("/");
    var projectName = path[1];

    var v =
        Logs.builder()
            .projectName(projectName)
            .pathUrl(getPathUrl(request.getRequestURL().toString()))
            .header(new Gson().toJson(ConvertUtil.mapToObjectString(headers)))
            .body(bodyInStringFormat == null ? new Gson().toJson(new Object()) : bodyInStringFormat)
            .method(request.getMethod())
            .params(new Gson().toJson(paramsObject))
            .build();

    logsRepository.save(v);
  }

  private String readInputStreamInStringFormat(InputStream stream, Charset charset)
      throws IOException {
    final int MAX_BODY_SIZE = 1024;
    final StringBuilder bodyStringBuilder = new StringBuilder();
    if (!stream.markSupported()) {
      stream = new BufferedInputStream(stream);
    }

    stream.mark(MAX_BODY_SIZE + 1);
    final byte[] entity = new byte[MAX_BODY_SIZE + 1];
    final int bytesRead = stream.read(entity);

    if (bytesRead != -1) {
      bodyStringBuilder.append(new String(entity, 0, Math.min(bytesRead, MAX_BODY_SIZE), charset));
      if (bytesRead > MAX_BODY_SIZE) {
        bodyStringBuilder.append("...");
      }
    }
    stream.reset();

    return bodyStringBuilder.toString();
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    final ByteArrayInputStream byteArrayInputStream =
        new ByteArrayInputStream(bodyInStringFormat.getBytes());

    return new ServletInputStream() {
      private boolean finished = false;

      @Override
      public boolean isFinished() {
        return finished;
      }

      @Override
      public int available() {
        return byteArrayInputStream.available();
      }

      @Override
      public void close() throws IOException {
        super.close();
        byteArrayInputStream.close();
      }

      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setReadListener(ReadListener readListener) {
        throw new UnsupportedOperationException();
      }

      public int read() {
        int data = byteArrayInputStream.read();
        if (data == -1) {
          finished = true;
        }
        return data;
      }
    };
  }

  public static String getBaseUrl(String urlString) {
    if (urlString == null) {
      return null;
    }
    try {
      URL url = URI.create(urlString).toURL();
      return url.getProtocol() + "://" + url.getAuthority() + "/";
    } catch (Exception e) {
      return null;
    }
  }

  public static String getPathUrl(String url) {
    try {
      return new URL(url).getPath();
    } catch (MalformedURLException e) {
      return null;
    }
  }
}

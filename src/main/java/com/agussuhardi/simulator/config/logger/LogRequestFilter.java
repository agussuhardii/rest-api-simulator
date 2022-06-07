package com.agussuhardi.simulator.config.logger;

import com.agussuhardi.simulator.repository.LogsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * revisi/log_notif 03/09/21
 *
 * @author agussuhardi
 */
@Component
@WebFilter(dispatcherTypes = DispatcherType.REQUEST, urlPatterns = "/*")
@AllArgsConstructor
@Slf4j
public class LogRequestFilter implements Filter {

  private final LogsRepository logsRepository;

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

    if (getBaseUrl(httpRequest.getRequestURL().toString())
        .equals(httpRequest.getRequestURL().toString())) {
      filterChain.doFilter(servletRequest, servletResponse);
      return;
    }
    CustomHttpRequestWrapper requestWrapper =
        new CustomHttpRequestWrapper(httpRequest, logsRepository);
    filterChain.doFilter(requestWrapper, servletResponse);
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
}

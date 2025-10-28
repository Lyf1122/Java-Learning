package com.lyf.lib.util;

import com.lyf.lib.dto.ErrorResponseDto;
import com.lyf.lib.exception.HttpIOError;
import com.lyf.lib.exception.InterruptedError;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class HttpUtil {
  
  private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
  
  private final HttpClient client;
  
  public static HttpUtil of() {
    return new HttpUtil(HttpClient.newHttpClient());
  }
  
  public static HttpUtil of(HttpClient client) {
    return new HttpUtil(client);
  }
  
  HttpUtil(HttpClient client) {
    this.client = client;
  }
  
  public <Res> ResponseHolder<Res> get(String url, Class<Res> clazz) throws HttpIOError, InterruptedError {
    return get(url, new HashMap<>(), clazz);
  }
  
  public <Res> ResponseHolder<Res> get(String url, Map<String, String> headers, Class<Res> clazz) throws HttpIOError, InterruptedError {
  
    Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).header("Accept", "application/json");
    headers.forEach(builder::headers);
    HttpRequest request = builder.GET().build();
  
    return execute(url, "GET", request, headers, "N/A", clazz);
  }

  public <Res> ResponseHolder<Res> getPlainText(String url, Map<String, String> headers, Class<Res> clazz) throws HttpIOError, InterruptedError {

    Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).header("Accept", "text/plain");
    headers.forEach(builder::headers);
    HttpRequest request = builder.GET().build();

    return execute(url, "GET", request, headers, "N/A", clazz);
  }

  public <Res, Req> ResponseHolder<Res> postAndReturnPlainText(String url, Req reqObj, Map<String, String> headers, Class<Res> clazz) throws HttpIOError, InterruptedError {

    Builder builder = HttpRequest.newBuilder().uri(URI.create(url))
        .header("Content-Type", "application/json")
        .header("Accept", "text/plain");
    headers.forEach(builder::headers);

    String body = "";
    if(reqObj != null) {
      body = JsonUtil.render(reqObj);
    }
    builder.POST(BodyPublishers.ofString(body));
    return execute(url, "POST", builder.build(), headers, body, clazz);
  }

  public <Req, Res> ResponseHolder<Res> post(String url, Req reqObj, Class<Res> clazz) throws HttpIOError, InterruptedError {
    return post(url, new HashMap<>(), reqObj, clazz);
  }
  
  public <Req, Res> ResponseHolder<Res> post(String url, Map<String, String> headers, Req reqObj, Class<Res> clazz) throws HttpIOError, InterruptedError {
  
    Builder builder = HttpRequest.newBuilder().uri(URI.create(url))
      .header("Content-Type", "application/json")
      .header("Accept", "application/json");
    headers.forEach(builder::headers);

    String body = "";
    if(reqObj instanceof String str) {
      body = str;
    } else if (reqObj != null){
      body = JsonUtil.render(reqObj);
    }
    builder.POST(BodyPublishers.ofString(body));
    return execute(url, "POST", builder.build(), headers, body, clazz);
  }
  
  public <Req, Res> ResponseHolder<Res> put(String url, Req reqObj, Class<Res> clazz) throws HttpIOError, InterruptedError {
    return put(url, new HashMap<>(), reqObj, clazz);
  }
  
  public <Req, Res> ResponseHolder<Res> put(String url, Map<String, String> headers, Req reqObj, Class<Res> clazz) throws HttpIOError, InterruptedError {
    
    Builder builder = HttpRequest.newBuilder().uri(URI.create(url))
      .header("Content-Type", "application/json")
      .header("Accept", "application/json");
    headers.forEach(builder::headers);
  
    String body = JsonUtil.render(reqObj);
    HttpRequest request = builder.PUT(BodyPublishers.ofString(JsonUtil.render(reqObj))).build();
  
    return execute(url, "PUT", request, headers, body, clazz);
  }
  
  public <Res> ResponseHolder<Res> delete(String url, Class<Res> clazz) throws HttpIOError, InterruptedError {
    
    return delete(url, new HashMap<>(), clazz);
  }
  
  public <Res> ResponseHolder<Res> delete(String url, Map<String, String> headers, Class<Res> clazz) throws HttpIOError, InterruptedError {
    
    Builder builder = HttpRequest.newBuilder().uri(URI.create(url));
    headers.forEach(builder::headers);
    HttpRequest request = builder.DELETE().build();
    
    return execute(url, "DELETE", request, headers, "N/A", clazz);
  }

  public <Req, Res> ResponseHolder<Res> patch(String url, Map<String, String> headers, Req reqObj, Class<Res> clazz) throws HttpIOError, InterruptedError {

    Builder builder = HttpRequest.newBuilder().uri(URI.create(url))
      .header("Content-Type", "application/json")
      .header("Accept", "application/json");
    headers.forEach(builder::headers);

    String body = null;
    if (reqObj != null) {
     body = JsonUtil.render(reqObj);
    }

    HttpRequest request = builder.method("PATCH", BodyPublishers.ofString(body)).build();

    return execute(url, "PATCH", request, headers, body, clazz);
  }

  private <Res> ResponseHolder<Res> execute(
    String url, String method, HttpRequest request, Map<String, String> headers, String body, Class<Res> clazz)
    throws HttpIOError, InterruptedError {

    logger.info("== HTTP Request ==> method:[{}], url:[{}], headers:[{}], body:[ {} ]", method, url, headers, filterBody4Log(body));

    StopWatch sw = StopWatch.createStarted();
    HttpResponse<String> response;
    try {
      response = client.send(request, BodyHandlers.ofString());
      sw.stop();
    } catch (IOException e) {
      if(sw.isStarted()) sw.stop();
      logger.error("== HTTP Response (ERROR) ==> method:[{}], url:[{}], escapedTime:[{}]", method, url, sw.getTime(TimeUnit.MILLISECONDS), e);
      throw new HttpIOError("Failed to make HTTP call due to IOError.", e);
    } catch (InterruptedException e) {
      if(sw.isStarted()) sw.stop();
      logger.error("== HTTP Response (ERROR) ==> method:[{}], url:[{}], escapedTime:[{}]", method, url, sw.getTime(TimeUnit.MILLISECONDS), e);
      throw new InterruptedError("Failed to make HTTP call due to unexpected interrupted.", e);
    }
  
    String json = response.body();
    String contentType = response.headers().firstValue("Content-Type").orElse("");

    Res obj = httpStatusIsOk(response.statusCode()) && isJsonContent(contentType) ? JsonUtil.parse(json, clazz) : null;
    ErrorResponseDto err = httpStatusIsError(response.statusCode()) && isJsonContent(contentType) ? JsonUtil.parse(json, ErrorResponseDto.class) : null;

    List<Pair<String, String>> hs = new LinkedList<>();
    response.headers().map().forEach((k, v) -> hs.add(ImmutablePair.of(k, StringUtils.join(v, ","))));
    ResponseHolder<Res> respHolder = ResponseHolder.of(response.statusCode(), Collections.unmodifiableList(hs), json, obj, err);
  
    if(respHolder.httpStatus() / 100 != 2) {
      logger.warn("== HTTP Response (WARN) ==> method:[{}], url:[{}], escapedTime:[{}], response:[{}]", method, url,
        sw.getTime(TimeUnit.MILLISECONDS), respHolder);
    } else {
      logger.info("== HTTP Response ==> method:[{}], url:[{}], escapedTime:[{}], response:[{}]", method, url,
        sw.getTime(TimeUnit.MILLISECONDS), respHolder);
    }
    return respHolder;
  }

  static String filterBody4Log(String body) {
    return StringUtils.isNotBlank(body)
      ? body.replaceAll("\"binary\":\".*\"", "\"binary\":\"-- binary data --\"") : body;
  }

  static boolean httpStatusIsOk(int statusCode) {
    return statusCode / 100 == 2;
  }

  static boolean httpStatusIsError(int statusCode) {
    return httpStatusIsClientError(statusCode) || httpStatusIsServerError(statusCode);
  }

  static boolean httpStatusIsClientError(int statusCode) {
    return statusCode / 100 == 4;
  }

  static boolean httpStatusIsServerError(int statusCode) {
    return statusCode / 100 == 5;
  }

  static boolean isJsonContent(String contentType) {
    return StringUtils.startsWithIgnoreCase(contentType, "application/json");
  }
  
  public record ResponseHolder <Res> (Integer httpStatus, List<Pair<String, String>> headers, String body, Res obj, ErrorResponseDto error) {

    public static <Res> ResponseHolder<Res> of(Integer httpStatus, String body, Res obj) {
      return new ResponseHolder<>(httpStatus, new LinkedList<>(), body, obj, null);
    }

    public static <Res> ResponseHolder<Res> of(Integer httpStatus, List<Pair<String, String>> headers, String body, Res obj, ErrorResponseDto error) {
      return new ResponseHolder<>(httpStatus, headers, body, obj, error);
    }

    @Override
    public String toString() {
      return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
        .append("httpStatus", httpStatus)
        .append("headers", headers)
        .append("body", body)
        .toString();
    }
  }

}

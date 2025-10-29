package lyf.reg.server.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class HealthCheckHandler {

  public static void handle(HttpExchange exchange) throws IOException {
    if("GET".equals(exchange.getRequestMethod())) {
      handleResponse(exchange, 200, "OK");
    } else {
      handleResponse(exchange, 404, "");
    }
  }

  private static void handleResponse(HttpExchange exchange, int status, String body)  throws  IOException {
    OutputStream out = exchange.getResponseBody();
    exchange.sendResponseHeaders(status, body.length());
    out.write(body.getBytes());
    out.flush();
    out.close();
  }
}

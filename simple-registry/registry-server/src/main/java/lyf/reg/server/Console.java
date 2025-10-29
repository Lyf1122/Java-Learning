package lyf.reg.server;

import lyf.reg.server.http.HealthCheckHandler;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Console {

  private static final Logger logger = LoggerFactory.getLogger(Console.class);
  private static final ScheduledExecutorService threadPoolExecutor = Executors.newScheduledThreadPool(10);

  public static void main(String[] args) {

    RegServer server = new RegServer(5757);
    threadPoolExecutor.scheduleAtFixedRate(getTimerTask(server), 1L, 30, TimeUnit.SECONDS);
    threadPoolExecutor.scheduleAtFixedRate(getCleanTask(server), 1L, 5, TimeUnit.MINUTES);
    threadPoolExecutor.execute(getMsgProcessorThread(server));

//    startHealthCheckServer();

    server.up();
  }

  static TimerTask getTimerTask(RegServer server) {
    return new TimerTask() { @Override public void run() { server.pingAll(); }};
  }

  static TimerTask getCleanTask(RegServer server) {
    return new TimerTask() { @Override public void run() { server.cleanup(); }};
  }

  static Thread getMsgProcessorThread(RegServer server) {
    return new Thread(new MsgProcessor(server));
  }

  static void startHealthCheckServer() {

    HttpServer server;
    try {
      server = HttpServer.create(new InetSocketAddress(8080), 0);
      logger.info("HEALTH-CHECK (/reg/health-check) started on port {}.", 8080);
    } catch (IOException ioe) {
      throw new IllegalStateException("Failed to start up HealthCheck Server");
    }

    server.setExecutor(threadPoolExecutor);
    HttpContext context = server.createContext("/reg/health-check");
    context.setHandler(HealthCheckHandler::handle);

    server.start();
  }

}

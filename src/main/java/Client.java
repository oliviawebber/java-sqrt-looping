import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.Thread.sleep;

public class Client {
    public static int count = 0;
    public static void main(String[] args) throws IOException, InterruptedException {
        SqrtLooper sl1 = new SqrtLooper();
        SqrtLooper sl2 = new SqrtLooper();
        HttpServer server = HttpServer.create(new InetSocketAddress(Inet4Address.getLocalHost().getHostAddress(),9999),0);
        server.createContext("/metric", new MetricHandler());
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        server.setExecutor(threadPoolExecutor);
        server.start();
        sl1.start();
        sl2.start();
        while(true) {
            count = sl1.getCount() + sl2.getCount();
            sleep(1000);
        }
    }
}

class SqrtLooper extends Thread {
    Random rng = new Random();
    int count = 0;

    public void run() {
        while(true) {
            double r = rng.nextDouble();
            for(int i = 0; i < Integer.MAX_VALUE; i++) {
                r = Math.sqrt(r) + Math.sqrt(r);
            }
            count++;
        }
    }

    int getCount() {
        return count;
    }
}

class MetricHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if("GET".equals(exchange.getRequestMethod())) {
            String htmlResponse = "<html><body>" + Client.count + "</body></html>";
            OutputStream os = exchange.getResponseBody();
            exchange.sendResponseHeaders(200,htmlResponse.length());
            os.write(htmlResponse.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
        }
    }
}
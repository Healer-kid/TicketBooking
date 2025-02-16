import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.stream.Collectors;

public class TrainBookingServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/book", new BookTicketHandler());
        server.createContext("/pnr", new CheckPNRHandler());

        server.setExecutor(null);
        server.start();
        System.out.println(" Server running at http://localhost:8080/");
    }

  static class BookTicketHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String body = new String(exchange.getRequestBody().readAllBytes());
            Map<String, String> params = parseQuery(body);

            int trainNumber = Integer.parseInt(params.get("trainNumber"));
            String passengerName = params.get("name");
            int passengerAge = Integer.parseInt(params.get("age"));

            String response = BookingSystem.bookTicket(trainNumber, passengerName, passengerAge);

            int statusCode = response.contains("\"error\"") ? 400 : 200;
            exchange.sendResponseHeaders(statusCode, response.length());

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}

static class CheckPNRHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            String query = exchange.getRequestURI().getQuery();
            int pnr = Integer.parseInt(query.split("=")[1]);

            String response = BookingSystem.checkPNR(pnr);

            if (response.contains("PNR Not Found")) {
                exchange.sendResponseHeaders(404, response.length());  // Set 404 for not found
            } else if (response.contains("Error fetching PNR")) {
                exchange.sendResponseHeaders(500, response.length());  // Set 500 for DB errors
            } else {
                exchange.sendResponseHeaders(200, response.length());  // Success
            }

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}


    private static Map<String, String> parseQuery(String query) {
        return query.lines().flatMap(line -> java.util.Arrays.stream(line.split("&")))
                .map(param -> param.split("=")).collect(Collectors.toMap(pair -> pair[0], pair -> pair[1]));
    }
}

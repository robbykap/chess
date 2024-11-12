import server.Server;


public class ServerMain {

    public static void main(String[] args) {
        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            var server = new Server();
            server.run(port);
            port = server.port();
            System.out.printf("Server started on port %d with %s%n", port);
            return;
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        System.out.println("""
                Chess Server:
                java ServerMain <port> [sql]
                """);
    }
}
import server.ChessServer;


public class ServerMain {

    public static void main(String[] args) {
        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            String dataAccess = "Memory";
            if (args.length >= 2 && args[1].equals("sql")) {
                dataAccess = "SQL";
            }

            var server = new ChessServer(dataAccess);
            server.run(port);
            port = server.port();
            System.out.printf("Server started on port %d with %s%n", port, dataAccess);
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
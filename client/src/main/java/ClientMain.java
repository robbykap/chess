import client.Repl;

public class ClientMain {

    public static void main(String[] args) {
        var serverURL = "http://localhost:8080";
        if (args.length == 1) {
            serverURL = args[0];
        }

        new Repl(serverURL).run();
    }
}
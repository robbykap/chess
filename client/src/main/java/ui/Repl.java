package ui;

import client.ChessClient;
import client.NotificationHandler;
import webSocketMessages.Notification;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl(String serverURL) {
        client = new ChessClient(serverURL, this);
    }

    public void run() {
        System.out.println(YELLOW + "♕ " +
                           WHITE + BOLD + "Welcome to 240 Chess. Type Help to get started. " +
                           RESET_BOLD_FAINT + YELLOW + "♕\n");
        System.out.println(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(BLUE + result + "\n\n");
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg + "\n");
            }
        }
        System.out.println();
    }

    @Override
    public void notify(Notification notification) {
        System.out.println(RED + notification.message());
        printPrompt();
    }

    private void printPrompt() {
        System.out.print(WHITE + BOLD + "[" + client.getState() + "] " + RESET_BOLD_FAINT + ">>> " + GREEN + FAINT);
    }
}

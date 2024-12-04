package ui;

import chess.ChessGame;
import client.ChessClient;
import client.NotificationHandler;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.Error;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl(String serverURL) {
        this.client = new ChessClient(serverURL, this);
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

    private void printPrompt() {
        System.out.print(WHITE + BOLD + "[" + client.getState() + "] " + RESET_BOLD_FAINT + ">>> " + GREEN + FAINT);
    }

    @Override
    public void notify(Notification notification) {
        if (notification.getMessage() != null) {
            System.out.print("\033[2K\033[2K");
            System.out.print("\n" + MAGENTA + BOLD + "[Notification] " + RESET_BOLD_FAINT + ">>> " + GREEN + FAINT);
            System.out.print(MAGENTA + BOLD + notification.getMessage());
            System.out.println("\n");
            printPrompt();
        }
    }

    @Override
    public void error(Error error) {
        System.out.print("\033[2K\033[2K");
        System.out.print("\n" + RED + BOLD + "[Error] " + RESET_BOLD_FAINT + ">>> " + GREEN + FAINT);
        System.out.print(RED + BOLD + error.getMessage());
        System.out.println("\n");
        printPrompt();
    }

    @Override
    public void loadGame(LoadGame loadGame) {
        client.setGame(loadGame.getGame());

        DrawBoard.initializeBoard(loadGame.getGame());
        ChessGame.TeamColor teamColor = client.teamColor();

        if (teamColor == ChessGame.TeamColor.BLACK) {
            System.out.print("\n" + DrawBoard.getBlackPerspective(loadGame.getGame()));
        } else {
            System.out.print("\n" + DrawBoard.getWhitePerspective(loadGame.getGame()));
        }

        System.out.println("\n");
        printPrompt();
    }
}

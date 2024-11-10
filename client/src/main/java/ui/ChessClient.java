package ui;

import static ui.EscapeSequences.*;

import client.DrawBoard;
import client.ServerFacade;
import client.ResponseException;
import client.State;

import server.request.user.*;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChessClient {
    private String playerName = null;
    private final ServerFacade server;
    private final String serverURL;
    private State state = State.SIGNEDOUT;
    private final Map<Integer, Map<String, Object>> gameDetails = new HashMap<>();

    public ChessClient(String serverURL) {
        this.server = new ServerFacade(serverURL);
        this.serverURL = serverURL;
    }

    public String eval(String input) throws ResponseException {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "list" -> listGames();
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            state = State.SIGNEDIN;
            playerName = params[0];
            String password = params[1];
            LoginRequest request = new LoginRequest(playerName, password);
            server.login(request);
            return String.format(WHITE + "Logged in as " + BOLD + "%s", playerName + RESET_BOLD_FAINT);
        }
        throw new ResponseException(400, "Expected: login <USERNAME> <PASSWORD>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            state = State.SIGNEDIN;
            playerName = params[0];
            String password = params[1];
            String email = params[2];
            RegisterRequest request = new RegisterRequest(playerName, password, email);
            server.register(request);
            return String.format(WHITE + "Registered as " + BOLD + "%s", playerName + RESET_BOLD_FAINT);
        }
        throw new ResponseException(400, "Expected: register <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String logout() throws ResponseException {
        if (state == State.SIGNEDIN) {
            state = State.SIGNEDOUT;
            server.logout();
            return String.format(WHITE + "Logged out " + BOLD + "%s", playerName + RESET_BOLD_FAINT);
        }
        throw new ResponseException(400, "You are not logged in");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        Collection<Map<String, Object>> games = server.listGames();
        StringBuilder result = new StringBuilder();
        int counter = 1;
        gameDetails.clear();
        for (Map<String, Object> game : games) {
            gameDetails.put(counter, game);
            result.append(String.format("ID: %d, Game Name: %s, White Username: %s, Black Username: %s\n",
                              counter++,
                              game.get("gameName"),
                              game.get("whiteUsername"),
                              game.get("blackUsername")));
        }
        if (!result.isEmpty() && result.charAt(result.length() - 1) == '\n') {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            String gameName = params[0];
            server.createGame(gameName);
            return String.format(WHITE + "Created game " + BOLD + "%s", gameName + RESET_BOLD_FAINT);
        }
        throw new ResponseException(400, "Expected: create <NAME>");
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 2) {
            int gameID = Integer.parseInt(params[0]);
            String color = params[1].toUpperCase();

            Map<String, Object> game = gameDetails.get(gameID);
            gameID = ((Double) game.get("gameID")).intValue();
            String gameName = (String) game.get("gameName");

            server.joinGame(gameID, color);
            String result = "";
            result += String.format(WHITE + "Joined game " + BOLD + "%s\n\n", gameName + RESET_BOLD_FAINT);
            result += DrawBoard.getBlackPerspective();
            result += "\n\n";
            result += DrawBoard.getWhitePerspective();
            return result;
        }
        throw new ResponseException(400, "Expected: join <ID> [WHITE|BLACK]");
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
             int gameID = Integer.parseInt(params[0]);

            Map<String, Object> game = gameDetails.get(gameID);
            gameID = ((Double) game.get("gameID")).intValue();
            String gameName = (String) game.get("gameName");

            server.observeGame(gameID);
            String result = "";
            result += String.format(WHITE + "Observed game " + BOLD + "%s\n\n", gameName + RESET_BOLD_FAINT);
            result += DrawBoard.getBlackPerspective();
            result += "\n\n";
            result += DrawBoard.getWhitePerspective();
            return result;
        }
        throw new ResponseException(400, "Expected: observe <ID>");
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return BLUE + BOLD + "register " + RESET_BOLD_FAINT + "<USERNAME> <PASSWORD> <EMAIL>" +
                   MAGENTA + " - to create an account\n" +
                   BLUE + BOLD + "login " + RESET_BOLD_FAINT + "<USERNAME> <PASSWORD>" +
                   MAGENTA + " - to play chess\n" +
                   BLUE + BOLD + "quit" + RESET_BOLD_FAINT +
                   MAGENTA + " - playing chess\n" +
                   BLUE + BOLD + "help" + RESET_BOLD_FAINT +
                   MAGENTA + " - with possible commands\n";
        } else {
            return BLUE + BOLD + "create " + RESET_BOLD_FAINT + "<NAME>" +
                   MAGENTA + " - a game\n" +
                   BLUE + BOLD + "list" + RESET_BOLD_FAINT +
                   MAGENTA + " - games\n" +
                   BLUE + BOLD + "join " + RESET_BOLD_FAINT + "<ID> [WHITE|BLACK]" +
                   MAGENTA + " - a game\n" +
                   BLUE + BOLD + "observe " + RESET_BOLD_FAINT + "<ID>" +
                   MAGENTA + " - a game\n" +
                   BLUE + BOLD + "logout" + RESET_BOLD_FAINT +
                   MAGENTA + " - when you are done\n" +
                   BLUE + BOLD + "quit" + RESET_BOLD_FAINT +
                   MAGENTA + " - playing chess\n" +
                   BLUE + BOLD + "help" + RESET_BOLD_FAINT +
                   MAGENTA + " - with possible commands";
        }
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }

    public String getState() {
        return state.toString();
    }

}

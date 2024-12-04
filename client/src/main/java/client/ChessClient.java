package client;

import static ui.EscapeSequences.*;

import java.util.*;

import chess.*;
import server.request.user.LoginRequest;
import server.request.user.RegisterRequest;
import ui.DrawBoard;

public class ChessClient {
    private String playerName = null;
    private final ServerFacade server;
    private WebSocketFacade ws;
    private State state = State.SIGNEDOUT;
    private final NotificationHandler notificationHandler;
    private final String serverURL;
    private Integer gameID;
    private ChessGame chessGame;
    public ChessGame.TeamColor teamColor;
    private final Map<Integer, Map<String, Object>> gameDetails = new HashMap<>();
    private String authToken = null;

    public ChessClient(String serverURL, NotificationHandler notificationHandler) {
        this.server = new ServerFacade(serverURL);
        this.notificationHandler = notificationHandler;
        this.serverURL = serverURL;
        this.gameID = null;
        this.chessGame = null;
        this.teamColor = null;
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
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
                case "clear" -> clear();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            playerName = params[0];
            String password = params[1];
            LoginRequest request = new LoginRequest(playerName, password);
            authToken = server.login(request);
            state = State.SIGNEDIN;
            return String.format(WHITE + "Logged in as " + BOLD + "%s", playerName + RESET_BOLD_FAINT);
        }
        throw new ResponseException(400, "Expected: login <USERNAME> <PASSWORD>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            playerName = params[0];
            String password = params[1];
            String email = params[2];
            RegisterRequest request = new RegisterRequest(playerName, password, email);
            authToken = server.register(request);
            state = State.SIGNEDIN;
            return String.format(WHITE + "Registered as " + BOLD + "%s", playerName + RESET_BOLD_FAINT);
        }
        throw new ResponseException(400, "Expected: register <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String logout() throws ResponseException {
        if (state == State.SIGNEDIN) {
            state = State.SIGNEDOUT;
            server.logout();
            authToken = null;
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
            int game = Integer.parseInt(params[0]);
            String color = params[1].toUpperCase();

            Map<String, Object> gameInfo = gameDetails.get(game);
            gameID = ((Double) gameInfo.get("gameID")).intValue();
            String gameName = (String) gameInfo.get("gameName");

            chessGame = server.joinGame(gameID, color);

             if (ws == null) {
                ws = new WebSocketFacade(serverURL, notificationHandler);
            }
            ws.connectGame(authToken, gameID);

            state = State.PLAYING;
            teamColor = color.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            String result = "";
            result += String.format(WHITE + "Joined game " + BOLD + "%s", gameName + RESET_BOLD_FAINT);
            return result;
        }
        throw new ResponseException(400, "Expected: join <ID> [WHITE|BLACK]");
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
             int game = Integer.parseInt(params[0]);

            Map<String, Object> gameInfo = gameDetails.get(game);
            gameID = ((Double) gameInfo.get("gameID")).intValue();
            String gameName = (String) gameInfo.get("gameName");

            chessGame = server.observeGame(gameID);

            if (ws == null) {
                ws = new WebSocketFacade(serverURL, notificationHandler);
            }
            ws.connectGame(authToken, gameID);

            state = State.OBSERVING;

            String result = "";
            result += String.format(WHITE + "Observing game " + BOLD + "%s", gameName + RESET_BOLD_FAINT);
            return result;
        }
        throw new ResponseException(400, "Expected: observe <ID>");
    }

    public String redraw() throws ResponseException {
        assertInGame();
        if (teamColor == ChessGame.TeamColor.BLACK) {
            return DrawBoard.getBlackPerspective(chessGame);
        } else {
            return DrawBoard.getWhitePerspective(chessGame);
        }
    }

    public String leave() throws ResponseException {
        assertInGame();
        state = State.SIGNEDIN;

        ws.leaveGame(authToken, gameID);
        ws = null;

        teamColor = null;
        chessGame = null;

        return String.format(WHITE + "Left game " + BOLD + "%s", playerName + RESET_BOLD_FAINT);
    }

    public String move(String... params) throws ResponseException, InvalidMoveException {
        assertInGame();
        assertWS();
        if (params.length == 2 && params[0].matches("[a-h][1-8]") && params[1].matches("[a-h][1-8]")) {
            String[] start = params[0].split("");
            String[] end = params[1].split("");

            int startCol = start[0].charAt(0) - 96;
            int endCol = end[0].charAt(0) - 96;

            ChessPosition startPiece = new ChessPosition(Integer.parseInt(start[1]), startCol);
            ChessPosition endPiece = new ChessPosition(Integer.parseInt(end[1]), endCol);

            try {
                if (chessGame.isOver()) {
                    return "Game is over";
                }
                chessGame.makeMove(new ChessMove(startPiece, endPiece, null));
            } catch (InvalidMoveException e) {
                return e.getMessage();
            }

            ws.move(authToken, gameID, new ChessMove(startPiece, endPiece, null));
            return "Made move from " + params[0] + " to " + params[1];
        }
        throw new ResponseException(400, "Expected: move <START POS> <ENDING POS>");
    }

    public String resign() throws ResponseException {
        assertInGame();

        System.out.println("Are you sure you want to resign? (yes/no)");
        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            ws.resign(authToken, gameID);
            return "Forfeited the game.";
        } else {
            return "Resignation cancelled.";
        }
    }

    public String highlight(String... params) throws ResponseException {
        assertInGame();
        assertWS();
        if (params.length == 1 && params[0].matches("[a-h][1-8]")) {
            String[] pos = params[0].split("");
            int col = pos[0].charAt(0) - 96;
            ChessPosition piece = new ChessPosition(Integer.parseInt(pos[1]), col);
            String result = DrawBoard.highlightMoves(chessGame, piece, teamColor);
            DrawBoard.initializeBoard(chessGame);
            return result;
        }
        throw new ResponseException(400, "Expected: highlight <POS>");
    }

    public String clear() throws ResponseException {
        server.clear();
        state = State.SIGNEDOUT;
        gameDetails.clear();
        chessGame = null;
        teamColor = null;
        playerName = null;
        return "Cleared all games";
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
        } else if (state == State.PLAYING || state == State.OBSERVING) {
            return BLUE + BOLD + "redraw " + RESET_BOLD_FAINT +
                    MAGENTA + " - redraws the chess board\n" +
                    BLUE + BOLD + "leave " + RESET_BOLD_FAINT +
                    MAGENTA + " - leave the game\n" +
                    BLUE + BOLD + "move" + RESET_BOLD_FAINT + "<START POS> <ENDING POS>" +
                    MAGENTA + " - make move (i.e. h2, a5)\n" +
                    BLUE + BOLD + "resign" + RESET_BOLD_FAINT +
                    MAGENTA + " - forfeit the game\n" +
                    BLUE + BOLD + "highlight" + RESET_BOLD_FAINT + "<POS>" +
                    MAGENTA + " - highlight the legal moves for a piece\n" +
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

    private void assertInGame() throws ResponseException {
        if (state != State.PLAYING && state != State.OBSERVING) {
            throw new ResponseException(400, "You must be in a game");
        }
    }

    private void assertWS() throws ResponseException {
        if (ws == null) {
            throw new ResponseException(400, "You must be connected to a game");
        }
    }

    public String getState() {
        return state.toString();
    }

    public ChessGame.TeamColor teamColor() {
        return teamColor;
    }

    public void setGame(ChessGame game) {
        this.chessGame = game;
    }
}

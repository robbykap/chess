package websocket.commands;

import chess.ChessMove;

public class MoveCommand extends UserGameCommand {
    ChessMove move;

    public MoveCommand(String authToken, int gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }

}

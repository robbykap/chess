package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class BishopMoves {

    public static Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {

        int[][] directions = {
            {1, 1},    // up-right
            {1, -1},   // up-left
            {-1, 1},   // down-right
            {-1, -1}   // down-left
        };

        return FindMoves.getMoves(board, myPosition, directions);
    }
}

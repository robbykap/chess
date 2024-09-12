package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class RookMoves {

    public static Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {

        int[][] directions = {
                {1, 0},  // forward
                {-1, 0}, // backward
                {0, 1},  // right
                {0, -1}  // left
        };

        return FindMoves.getMoves(board, myPosition, directions);
    }
}

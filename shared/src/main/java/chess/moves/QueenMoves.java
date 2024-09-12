package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class QueenMoves {

    public static Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {

        int[][] directions = {
                {1, 0},  // forward
                {-1, 0}, // backward
                {0, 1},  // right
                {0, -1},  // left
                {1, 1},  // forward right
                {1, -1},  // forward left
                {-1, 1},  // backward right
                {-1, -1}  // backward left
        };

        return FindMoves.getMoves(board, myPosition, directions);
    }
}

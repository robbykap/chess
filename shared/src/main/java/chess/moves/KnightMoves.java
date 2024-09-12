package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class KnightMoves {

    public static Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {

        int[][] directions = {
                {1, 2},
                {2, 1},
                {2, -1},
                {1, -2},
                {-1, -2},
                {-2, -1},
                {-2, 1},
                {-1, 2}
        };
        return FindMoves.getMoves(board, myPosition, directions);
    }
}

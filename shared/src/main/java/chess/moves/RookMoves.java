package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoves {

    public static Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int direction = board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;

        // Forward
        for (int i = row + direction; i <= 8 && i > 0; i += direction) {
            ChessPosition pos = new ChessPosition(i, col);
            if (board.getPiece(pos) == null) {
                moves.add(new ChessMove(myPosition, pos, null));
            } else {
                if (board.getPiece(pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, pos, null));
                }
                break;
            }
        }
        // Backward
         for (int i = row - direction; i <= 8 && i > 0; i -= direction) {
            ChessPosition pos = new ChessPosition(i, col);
            if (board.getPiece(pos) == null) {
                moves.add(new ChessMove(myPosition, pos, null));
            } else {
                if (board.getPiece(pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, pos, null));
                }
                break;
            }
        }
        // Left
        for (int i = col - direction; i <= 8 && i > 0; i -= direction) {
            ChessPosition pos = new ChessPosition(row, i);
            if (board.getPiece(pos) == null) {
                moves.add(new ChessMove(myPosition, pos, null));
            } else {
                if (board.getPiece(pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, pos, null));
                }
                break;
            }
        }
        // right
        for (int i = col + direction; i <= 8 && i > 0; i += direction) {
            ChessPosition pos = new ChessPosition(row, i);
            if (board.getPiece(pos) == null) {
                moves.add(new ChessMove(myPosition, pos, null));
            } else {
                if (board.getPiece(pos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, pos, null));
                }
                break;
            }
        }
        return moves;
    }
}

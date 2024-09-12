package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class FindMoves {
    public static Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        ChessPiece.PieceType curPiece = board.getPiece(myPosition).getPieceType();
        ChessGame.TeamColor currColor = board.getPiece(myPosition).getTeamColor();

        for (int[] direction : directions) {
            int x = direction[0];
            int y = direction[1];

            int nextRow = row + x;
            int nextCol = col + y;

            // Check if the next position is within the board
            while (nextRow > 0 && nextRow <= 8 && nextCol > 0 && nextCol <= 8) {
                ChessPosition pos = new ChessPosition(nextRow, nextCol);
                ChessPiece pieceAtPos = board.getPiece(pos);

                if (pieceAtPos == null) {
                    moves.add(new ChessMove(myPosition, pos, null));
                } else {
                    if (pieceAtPos.getTeamColor() != currColor) {
                        moves.add(new ChessMove(myPosition, pos, null));
                    }
                    break;
                }

                // Prevents knight & king from moving more than once
                if (curPiece == ChessPiece.PieceType.KNIGHT || curPiece == ChessPiece.PieceType.KING) {
                    break;
                }

                nextRow += x;
                nextCol += y;
            }
        }
        return moves;
    }
}

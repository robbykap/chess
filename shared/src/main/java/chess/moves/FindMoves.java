package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;

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

        public static Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition,
                                                  ChessGame.TeamColor currColor, Dictionary<String, Integer> info) {
        Collection<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Forward
        int forward = row + info.get("forward");
        ChessPosition pos = new ChessPosition(forward, col);
        ChessPiece pieceAtPos = board.getPiece(pos);
        if (pieceAtPos == null) {
            // Moves forward two, if it's the first move
            initial(board, myPosition, info, moves);
            promote(myPosition, info, pos, moves);
        }

        // Attack right
        int right = col + info.get("attack right");
        attack(board, myPosition, currColor, info, forward, right, moves);

        // Attack left
        int left = col + info.get("attack left");
        attack(board, myPosition, currColor, info, forward, left, moves);

        return moves;
    }

    private static void attack(ChessBoard board, ChessPosition myPosition,
                               ChessGame.TeamColor currColor, Dictionary<String, Integer> info,
                               int forward, int attackSide, Collection<ChessMove> moves) {
        ChessPosition pos;
        ChessPiece pieceAtPos;
        if (attackSide > 0 && attackSide <= 8) {
            pos = new ChessPosition(forward, attackSide);
            pieceAtPos = board.getPiece(pos);
            if (pieceAtPos != null && pieceAtPos.getTeamColor() != currColor) {
                promote(myPosition, info, pos, moves);
            }
        }
    }

    private static void initial(ChessBoard board, ChessPosition myPosition,
                                Dictionary<String, Integer> info,
                                Collection<ChessMove> moves) {
        if (myPosition.getRow() == info.get("start")) {
            ChessPosition pos = new ChessPosition(myPosition.getRow() + info.get("initial"), myPosition.getColumn());
            ChessPiece pieceAtPos = board.getPiece(pos);
            if (pieceAtPos == null) {
                moves.add(new ChessMove(myPosition, pos, null));
            }
        }
    }


    private static void promote(ChessPosition myPosition,
                                Dictionary<String, Integer> info,
                                ChessPosition pos, Collection<ChessMove> moves) {
        if (pos.getRow() == info.get("promotion")) {
            for (var type : new ChessPiece.PieceType[]{ChessPiece.PieceType.KNIGHT,
                                                       ChessPiece.PieceType.BISHOP,
                                                       ChessPiece.PieceType.ROOK,
                                                       ChessPiece.PieceType.QUEEN}) {
                moves.add(new ChessMove(myPosition, pos, type));
            }
        } else {
            moves.add(new ChessMove(myPosition, pos, null));
        }
    }
}

package chess;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final Map<ChessPosition, ChessPiece> boardState = new HashMap<>();
    private ChessPiece[][] chessBoard = new ChessPiece[8][8];

    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessBoard[position.getRow() - 1][position.getColumn() - 1] = piece;
        boardState.put(position, piece);
        boardState.values().removeIf(Objects::isNull);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return chessBoard[position.getRow() - 1][position.getColumn() - 1];
    }

    public ChessPiece findPiece(ChessPiece.PieceType type, ChessGame.TeamColor color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = chessBoard[i][j];
                if (piece != null && piece.getPieceType() == type && piece.getTeamColor() == color) {
                    return piece;
                }
            }
        }
        return null;
    }

    public List<ChessPiece> getTeamPieces(ChessGame.TeamColor color) {
        return boardState.values().stream()
                .filter(piece -> piece.getTeamColor() == color)
                .collect(Collectors.toList());
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        boardState.clear();
        chessBoard = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            chessBoard[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            boardState.put(new ChessPosition(2, i + 1), chessBoard[1][i]);
            chessBoard[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            boardState.put(new ChessPosition(7, i + 1), chessBoard[6][i]);
        }
        chessBoard[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        boardState.put(new ChessPosition(1, 1), chessBoard[0][0]);
        chessBoard[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        boardState.put(new ChessPosition(1, 8), chessBoard[0][7]);
        chessBoard[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        boardState.put(new ChessPosition(8, 1), chessBoard[7][0]);
        chessBoard[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        boardState.put(new ChessPosition(8, 8), chessBoard[7][7]);

        chessBoard[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        boardState.put(new ChessPosition(1, 2), chessBoard[0][1]);
        chessBoard[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        boardState.put(new ChessPosition(1, 7), chessBoard[0][6]);
        chessBoard[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        boardState.put(new ChessPosition(8, 2), chessBoard[7][1]);
        chessBoard[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        boardState.put(new ChessPosition(8, 7), chessBoard[7][6]);

        chessBoard[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        boardState.put(new ChessPosition(1, 3), chessBoard[0][2]);
        chessBoard[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        boardState.put(new ChessPosition(1, 6), chessBoard[0][5]);
        chessBoard[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        boardState.put(new ChessPosition(8, 3), chessBoard[7][2]);
        chessBoard[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        boardState.put(new ChessPosition(8, 6), chessBoard[7][5]);

        chessBoard[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        boardState.put(new ChessPosition(1, 4), chessBoard[0][3]);
        chessBoard[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        boardState.put(new ChessPosition(8, 4), chessBoard[7][3]);

        chessBoard[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        boardState.put(new ChessPosition(1, 5), chessBoard[0][4]);
        chessBoard[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        boardState.put(new ChessPosition(8, 5), chessBoard[7][4]);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "boardState=" + boardState +
                ", chessBoard=" + Arrays.toString(chessBoard) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        };
        if (o == null || getClass() != o.getClass()) {
            return false;
        };
        ChessBoard that = (ChessBoard) o;
        return Objects.equals(boardState, that.boardState) && Objects.deepEquals(chessBoard, that.chessBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardState, Arrays.deepHashCode(chessBoard));
    }
}

package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() { return teamTurn; }


    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) { teamTurn = team; }


    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK;
    }


    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        ChessGame.TeamColor teamTurn = piece.getTeamColor();
        Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : allMoves) {
            ChessPiece capturedPiece = simulateMove(piece, move);
            if (!isInCheck(teamTurn)) {
                validMoves.add(move);
            }
            undoMove(piece, move, capturedPiece);
        }
        return validMoves;
    }

    private ChessPiece simulateMove(ChessPiece piece, ChessMove move) {
        ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), piece);
        return capturedPiece;
    }

    private void undoMove(ChessPiece piece, ChessMove move, ChessPiece capturedPiece) {
        board.addPiece(move.getStartPosition(), piece);
        board.addPiece(move.getEndPosition(), capturedPiece);

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not this team's turn");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves == null || !validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }

        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }

        simulateMove(piece, move);

        teamTurn = teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPiece king = board.findPiece(ChessPiece.PieceType.KING, teamColor);
        if (king == null) { return false; }

        ChessPosition kingPosition = king.getPosition(board);

        ChessGame.TeamColor enemyColors = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        List<ChessPiece> enemyPieces = board.getTeamPieces(enemyColors);

        for (ChessPiece piece : enemyPieces) {
            Collection<ChessMove> moves = piece.pieceMoves(board, piece.getPosition(board));
            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (board.findPiece(ChessPiece.PieceType.KING, teamColor) == null) { return false;}

        if (!isInCheck(teamColor)) {
            return false;
        }

        List<ChessPiece> teamPieces = board.getTeamPieces(teamColor);

        for (ChessPiece piece : teamPieces) {
            Collection<ChessMove> moves = piece.pieceMoves(board, piece.getPosition(board));
            for (ChessMove move : moves) {
                ChessPiece capturedPiece = simulateMove(piece, move);
                if (!isInCheck(teamColor)) {
                    undoMove(piece, move, capturedPiece);
                    return false;
                }
                undoMove(piece, move, capturedPiece);
            }
        }
        return true;
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (board.findPiece(ChessPiece.PieceType.KING, teamColor) == null) { return false;}

        if (isInCheck(teamColor)) {
            return false;
        }

        List<ChessPiece> teamPieces = board.getTeamPieces(teamColor);

        for (ChessPiece piece : teamPieces) {
            Collection<ChessMove> moves = piece.pieceMoves(board, piece.getPosition(board));
            for (ChessMove move : moves) {
                ChessPiece capturedPiece = simulateMove(piece, move);
                if (!isInCheck(teamColor)) {
                    undoMove(piece, move, capturedPiece);
                    return false;
                }
                undoMove(piece, move, capturedPiece);
            }
        }
        return true;
    }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) { this.board = board; }


    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() { return board; }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}

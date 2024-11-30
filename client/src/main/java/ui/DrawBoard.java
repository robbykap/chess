package ui;

import chess.*;
import client.ResponseException;
import dataaccess.BadRequestException;

import java.util.Collection;

import static chess.ChessPiece.PieceType.*;
import static ui.EscapeSequences.*;

public class DrawBoard {
    private static String[][] board;
    private static boolean highlight = false;

    public static String[][] initializeBoard(ChessGame game) {
        ChessBoard chessBoard = game.getBoard();

        String[][] board = new String[8][8];
        boolean isWhite = true;
        for (int i = 7; i >= 0; i--) {
            for (int j = 7; j >= 0; j--) {
                if (isWhite) {
                    board[i][j] = SET_BG_COLOR_WHITE + "   " + RESET_BG_COLOR;
                } else {
                    board[i][j] = SET_BG_COLOR_BLACK + "   " + RESET_BG_COLOR;
                }

                if (chessBoard.getPiece(new ChessPosition(i + 1,  8 - j)) != null) {
                    ChessPiece piece = chessBoard.getPiece(new ChessPosition(i + 1, 8 - j));
                    String pieceColor = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? RED : BLUE;
                    String pieceType =
                            piece.getPieceType() == PAWN ? (pieceColor + (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN)) :
                            piece.getPieceType() == ROOK ? (pieceColor + (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK)) :
                            piece.getPieceType() == KNIGHT ? (pieceColor + (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT)) :
                            piece.getPieceType() == BISHOP ? (pieceColor + (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP)) :
                            piece.getPieceType() == QUEEN ? (pieceColor + (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN)) :
                            piece.getPieceType() == KING ? (pieceColor + (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING)) : "";
                    board[i][j] = (isWhite ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK) + BOLD + pieceType + RESET_BOLD_FAINT + RESET_BG_COLOR;

                }
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
        DrawBoard.board = board;
        return board;
    }

    public static String getBlackPerspective(ChessGame game) {
        if (!highlight) {
            board = initializeBoard(game);
        }

       StringBuilder boardString = new StringBuilder();
         boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK)
                   .append("    h  g  f  e  d  c  b  a    ").append(RESET).append(RESET_BG_COLOR).append("\n");

        for (int i = 0; i < 8; i++) {
               boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK + " ").append(1 + i)
                    .append(" ").append(RESET).append(RESET_BG_COLOR);
            for (int j = 0; j < 8; j++) {
                boardString.append(board[i][j]);
            }
                  boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK + " ").append(1 + i)
                    .append(" ").append(RESET).append(RESET_BG_COLOR).append("\n");
        }

        boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK)
                   .append("    h  g  f  e  d  c  b  a    ").append(RESET).append(RESET_BG_COLOR);

        highlight = false;
        return boardString.toString();
    }

    public static String getWhitePerspective(ChessGame game) {
        if (!highlight) {
            board = initializeBoard(game);
        }

        StringBuilder boardString = new StringBuilder();
        boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK)
                   .append("    a  b  c  d  e  f  g  h    ").append(RESET).append(RESET_BG_COLOR).append("\n");

        for (int i = 7; i >= 0; i--) {
        boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK + " ").append(i + 1)
                    .append(" ").append(RESET).append(RESET_BG_COLOR);
            for (int j = 7; j >= 0; j--) {
                boardString.append(board[i][j]);
            }
            boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK + " ").append(i + 1)
                    .append(" ").append(RESET).append(RESET_BG_COLOR).append("\n");
        }

           boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK)
                   .append("    a  b  c  d  e  f  g  h    ").append(RESET).append(RESET_BG_COLOR);

        highlight = false;
        return boardString.toString();
    }

    public static String highlightMoves(ChessGame game, ChessPosition pos) throws ResponseException {
        highlight = true;

        ChessPiece piece = game.getBoard().getPiece(pos);

        if (piece == null) {
            throw new ResponseException(400, "No piece at position " + (char) (pos.getColumn() + 96) + pos.getRow());
        }

        if (piece.getTeamColor() != game.getTeamTurn()) {
            throw new ResponseException(400, "Requested piece is not on your team");
        }

        Collection<ChessMove> moves = game.validMoves(pos);
        for (ChessMove move : moves) {
            ChessPosition endPos = move.getEndPosition();
            board[endPos.getRow() - 1][7 - (endPos.getColumn() - 1)] = SET_BG_COLOR_GREEN + "   " + RESET_BG_COLOR;
        }

        return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? getWhitePerspective(game) : getBlackPerspective(game);
    }
}

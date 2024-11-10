package client;

import static ui.EscapeSequences.*;

public class DrawBoard {
    public final static String whitePerspective = drawBoard();
    public final static String blackPerspective = drawBoardFlipped();

    public static String drawBoard() {
        String[][] board = initializeBoard();
        StringBuilder boardString = new StringBuilder();

        boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK).append("    a  b  c  d  e  f  g  h    ").append(RESET).append(RESET_BG_COLOR).append("\n");

        for (int i = 0; i < board.length; i++) {
            boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK + " ").append(8 - i).append(" ").append(RESET).append(RESET_BG_COLOR);
            for (String cell : board[i]) {
                boardString.append(cell);
            }
            boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK).append(" ").append(8 - i).append(RESET).append(" ").append(RESET_BG_COLOR).append("\n");
        }

        boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK).append("    a  b  c  d  e  f  g  h    ").append(RESET).append(RESET_BG_COLOR).append("\n");

        return boardString.toString();
    }

    public static String drawBoardFlipped() {
        String[][] board = initializeBoard();
        StringBuilder boardString = new StringBuilder();

        boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK).append("    h  g  f  e  d  c  b  a    ").append(RESET).append(RESET_BG_COLOR).append("\n");

        for (int i = board.length - 1; i >= 0; i--) {
            boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK + " ").append(8 - i).append(" ").append(RESET).append(RESET_BG_COLOR);
            for (int j = board[i].length - 1; j >= 0; j--) {
                boardString.append(board[i][j]);
            }
            boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK).append(" ").append(8 - i).append(RESET).append(" ").append(RESET_BG_COLOR).append("\n");
        }

        boardString.append(SET_BG_COLOR_LIGHT_GREY).append(BOLD + BLACK).append("    h  g  f  e  d  c  b  a    ").append(RESET).append(RESET_BG_COLOR).append("\n");

        return boardString.toString();
    }

    public static String[][] initializeBoard() {
        String[][] board = new String[8][8];
        boolean isWhite = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isWhite) {
                    board[i][j] = SET_BG_COLOR_WHITE + "   " + RESET_BG_COLOR;
                } else {
                    board[i][j] = SET_BG_COLOR_BLACK + "   " + RESET_BG_COLOR;
                }
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
        board[0][0] = SET_BG_COLOR_WHITE + BOLD + BLUE + BLACK_ROOK + RESET_BOLD_FAINT + RESET_BG_COLOR;
        board[0][1] = SET_BG_COLOR_BLACK + BOLD + BLUE + BLACK_KNIGHT + RESET_BOLD_FAINT + RESET_BG_COLOR;
        board[0][2] = SET_BG_COLOR_WHITE + BOLD + BLUE + BLACK_BISHOP + RESET_BOLD_FAINT + RESET_BG_COLOR;
        board[0][3] = SET_BG_COLOR_BLACK + BOLD + BLUE + BLACK_QUEEN + RESET_BOLD_FAINT + RESET_BG_COLOR;
        board[0][4] = SET_BG_COLOR_WHITE + BOLD + BLUE + BLACK_KING + RESET_BOLD_FAINT + RESET_BG_COLOR;
        board[0][5] = SET_BG_COLOR_BLACK + BOLD + BLUE + BLACK_BISHOP + RESET_BOLD_FAINT + RESET_BG_COLOR;
        board[0][6] = SET_BG_COLOR_WHITE + BOLD + BLUE + BLACK_KNIGHT + RESET_BOLD_FAINT + RESET_BG_COLOR;
        board[0][7] = SET_BG_COLOR_BLACK + BOLD + BLUE + BLACK_ROOK + RESET_BOLD_FAINT + RESET_BG_COLOR;
        for (int i = 0; i < 8; i++) {
            board[1][i] = (i % 2 == 0 ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE) + BOLD + BLUE + BLACK_PAWN + RESET_BOLD_FAINT + RESET_BG_COLOR;
        }
        board[7][0] = SET_BG_COLOR_BLACK + BOLD + RED + WHITE_ROOK + RESET_BOLD_FAINT + RESET_BG_COLOR;
        board[7][1] = SET_BG_COLOR_WHITE + BOLD + RED + WHITE_KNIGHT + RESET_BOLD_FAINT + RESET_BG_COLOR;
        board[7][2] = SET_BG_COLOR_BLACK + BOLD + RED + WHITE_BISHOP + RESET_BOLD_FAINT + RESET_BG_COLOR;
        board[7][3] = SET_BG_COLOR_WHITE + BOLD + RED + WHITE_QUEEN + RESET_BOLD_FAINT + RESET_BG_COLOR;
        board[7][4] = SET_BG_COLOR_BLACK + BOLD + RED + WHITE_KING + RESET_BOLD_FAINT + RESET_BG_COLOR;
        board[7][5] = SET_BG_COLOR_WHITE + BOLD + RED + WHITE_BISHOP + RESET_BOLD_FAINT + RESET_BG_COLOR;
        board[7][6] = SET_BG_COLOR_BLACK + BOLD + RED + WHITE_KNIGHT + RESET_BOLD_FAINT + RESET_BG_COLOR;
        board[7][7] = SET_BG_COLOR_WHITE + BOLD + RED + WHITE_ROOK + RESET_BOLD_FAINT + RESET_BG_COLOR;
        for (int i = 0; i < 8; i++) {
            board[6][i] = (i % 2 == 0 ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK) + BOLD + RED + WHITE_PAWN + RESET_BOLD_FAINT + RESET_BG_COLOR;
        }
        return board;
    }

    public static String getWhitePerspective() {
        return whitePerspective;
    }

    public static String getBlackPerspective() {
        return blackPerspective;
    }
}

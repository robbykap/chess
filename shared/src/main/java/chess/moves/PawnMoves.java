package chess.moves;

import chess.*;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

public class PawnMoves {
    public static Dictionary<String, Integer> getInfo(ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            return new Hashtable<>(
            Map.of( "start", 2,
                    "promotion", 8,
                    "initial", 2,
                    "forward", 1,
                    "attack right", 1,
                    "attack left", -1)
            );
        } else {
            return new Hashtable<>(
            Map.of( "start", 7,
                    "promotion", 1,
                    "initial", -2,
                    "forward", -1,
                    "attack right", -1,
                    "attack left", 1)
            );
        }
    }

    public static Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        ChessGame.TeamColor currColor = board.getPiece(myPosition).getTeamColor();
        Dictionary<String, Integer> info = getInfo(currColor);
        return FindMoves.pawnMoves(board, myPosition, currColor, info);
    }
}

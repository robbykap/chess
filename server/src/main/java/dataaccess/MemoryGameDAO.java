package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void createGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())) {
            throw new DataAccessException("Game already exists, gameID: " + game.gameID());
        }
        games.put(game.gameID(), game);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try {
            return games.get(gameID);
        } catch (Exception e) {
            throw new DataAccessException("Game not found, gameID: " + gameID);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        try {
            games.put(game.gameID(), game);
        } catch (Exception e) {
            throw new DataAccessException("Error updating game, gameID: " + game.gameID());
        }
    }

    @Override
    public void clear() {
        games.clear();
    }
}

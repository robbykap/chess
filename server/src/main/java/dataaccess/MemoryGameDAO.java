package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MemoryGameDAO implements GameDAO {
    final private ConcurrentHashMap<Integer, GameData> games = new ConcurrentHashMap<>();

    @Override
    public void createGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())) {
            throw new DataAccessException("Game already exists, gameID: " + game.gameID());
        }
        games.put(game.gameID(), game);
    }


    @Override
    public Collection<Map<String, Object>> getGames() {
        return games.values().stream().map(game -> {
            Map<String, Object> gameMap = new HashMap<>();
            gameMap.put("gameID", game.gameID());
            gameMap.put("whiteUsername", game.whiteUsername());
            gameMap.put("blackUsername", game.blackUsername());
            gameMap.put("gameName", game.gameName());
            return gameMap;
        }).collect(Collectors.toList());
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData gameData = games.get(gameID);

        if (gameData == null) {
            throw new DataAccessException("Game not found, gameID: " + gameID);
        }

        return gameData;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        try {
            games.remove(game.gameID());
            games.put(game.gameID(), game);
        } catch (Exception e) {
            throw new DataAccessException("Error updating game, gameID: " + game.gameID());
        }
    }

    @Override
    public void clear() {
        games.clear();
    }

    public int size() {
        return games.size();
    }
}

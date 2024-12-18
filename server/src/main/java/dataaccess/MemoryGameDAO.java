package dataaccess;

import model.GameData;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.stream.Collectors;

public class MemoryGameDAO implements GameDAO {
    final private Map<Integer, GameData> games = new LinkedHashMap<>();

    @Override
    public void createGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())) {
            throw new DataAccessException("Game already exists");
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
            throw new DataAccessException("Game not found");
        }

        return gameData;
    }

    @Override
    public void updateGame(GameData game) {
        games.remove(game.gameID());
        games.put(game.gameID(), game);
    }

    @Override
    public void clear() {
        games.clear();
    }

    public int size() {
        return games.size();
    }
}

package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface GameDAO {

    Collection<Map<String, Object>> getGames() throws DataAccessException;

    void createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    void clear();
}

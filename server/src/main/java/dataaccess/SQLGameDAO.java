package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SQLGameDAO implements GameDAO {

    @Override
    public Collection<Map<String, Object>> getGames() {
        return List.of();
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}

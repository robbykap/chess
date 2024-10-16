package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {

    void createGame(GameData game) throws DataAccessException;

    Collection<GameData> listGames();

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    void clear();
}

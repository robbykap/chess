package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {

    Collection<GameData> getGames();

    void createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    void clearGames();
}

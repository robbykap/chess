package dataaccess;

import model.AuthData;

public interface AuthDAO {

    void createAuth(AuthData authData);

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken);

    void clear();
}

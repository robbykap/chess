package dataaccess;

import model.AuthData;

public interface AuthDAO {

    void createAuth(AuthData authData);

    AuthData verifyAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken);

    void clearAuths();
}

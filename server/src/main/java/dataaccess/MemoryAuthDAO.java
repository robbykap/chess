package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, String> auths = new HashMap<>();

    @Override
    public void createAuth(AuthData auth) {
        auths.put(auth.authToken(), auth.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try {
            return new AuthData(auths.get(authToken), authToken);
        } catch (NullPointerException e) {
            throw new DataAccessException("No such auth token");
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    @Override
    public void clear() {
        auths.clear();
    }
}

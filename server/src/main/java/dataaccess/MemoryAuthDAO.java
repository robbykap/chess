package dataaccess;

import model.AuthData;

import java.util.concurrent.ConcurrentHashMap;

public class MemoryAuthDAO implements AuthDAO {
    private int size = 0;
    final private ConcurrentHashMap<String, String> auths = new ConcurrentHashMap<>();

    @Override
    public void createAuth(AuthData auth) {
        auths.put(auth.authToken(), auth.username());
        size++;
    }

    @Override
    public AuthData verifyAuth(String authToken) throws DataAccessException {
        try {
            return new AuthData(auths.get(authToken), authToken);
        } catch (NullPointerException e) {
            throw new DataAccessException("No such auth token");
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
        size--;
    }

    @Override
    public void clearAuths() {
        auths.clear();
        size = 0;
    }

    public int size() {
        return size;
    }
}

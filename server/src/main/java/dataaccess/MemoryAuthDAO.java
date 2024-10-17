package dataaccess;

import model.AuthData;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryAuthDAO implements AuthDAO {
    final private ConcurrentHashMap<String, String> auths = new ConcurrentHashMap<>();

    @Override
    public void createAuth(AuthData auth) {
        auths.put(auth.authToken(), auth.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String username = auths.get(authToken);

        if (username == null) {
            throw new DataAccessException("Invalid authToken");
        }

        return new AuthData(username, authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    @Override
    public void clear() {
        auths.clear();
    }

    public int size() {
        return auths.size();
    }
}

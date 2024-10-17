package dataaccess;

import model.UserData;

import java.util.concurrent.ConcurrentHashMap;

public class MemoryUserDAO implements UserDAO {
    final private ConcurrentHashMap<String, UserData> users = new ConcurrentHashMap<>();


    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData userData = users.get(username);

        if (userData == null) {
            throw new DataAccessException("User not found: " + username);
        }

        return userData;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try {
            getUser(user.username());

        } catch (DataAccessException e) {
            users.put(user.username(), user);
            return;
        }

        throw new DataAccessException("User already exists: " + user.username());
    }

    @Override
    public void clear() {
        users.clear();
    }

    public int size() {
        return users.size();
    }
}

package dataaccess;

import model.UserData;

import java.util.concurrent.ConcurrentHashMap;

public class MemoryUserDAO implements UserDAO {
    private int size = 0;
    final private ConcurrentHashMap<String, UserData> users = new ConcurrentHashMap<>();


    @Override
    public UserData getUser(String username) throws DataAccessException {
        try {
            return users.get(username);
        } catch (Exception e) {
            throw new DataAccessException("User not found: " + username);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try {
            getUser(user.username());
        } catch (DataAccessException e) {
            users.put(user.username(), user);
            size++;
            return;
        }
        throw new DataAccessException("User already exists: " + user.username());
    }

    @Override
    public void clearUsers() {
        users.clear();
        size = 0;
    }

    public int size() {
        return size;
    }
}

package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();


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
            return;
        }
        throw new DataAccessException("User already exists: " + user.username());
    }

    @Override
    public void clear() {
        users.clear();
    }
}

package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.concurrent.ConcurrentHashMap;

public class MemoryUserDAO implements UserDAO {
    final private ConcurrentHashMap<String, UserData> users = new ConcurrentHashMap<>();


    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData userData = users.get(username);

        if (userData == null) {
            throw new DataAccessException("User not found");
        }

        return userData;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        // Check if user already exists, if so throw exception
        try {
            getUser(user.username());

        } catch (DataAccessException e) {
            // User does not exist, add user
            UserData hashedPasswordUser = new UserData(user.username(), BCrypt.hashpw(user.password(), BCrypt.gensalt()), user.email());
            users.put(user.username(), hashedPasswordUser);
            return;
        }

        throw new DataAccessException("User already exists");
    }

    @Override
    public void clear() {
        users.clear();
    }

    public int size() {
        return users.size();
    }
}

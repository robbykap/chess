package service;

import dataaccess.*;

import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData userData) throws BadRequestException {
        try {
            userDAO.createUser(userData);
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(userData.username(), authToken);

        authDAO.createAuth(authData);

        return authData;
    };

    public AuthData login(UserData userData) throws UnauthorizedException {
        String username = userData.username();
        String password = userData.password();

        try {
            UserData user = userDAO.getUser(username);

            if (user.password().equals(password)) {

                String authToken = UUID.randomUUID().toString();
                AuthData authData = new AuthData(username, authToken);

                authDAO.createAuth(authData);

                return authData;

            } else {
                throw new UnauthorizedException("Invalid password");
            }

        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    };

    public void logout(String authToken) throws UnauthorizedException {
        try {
            authDAO.verifyAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }

        authDAO.deleteAuth(authToken);
    };

    public void clear() {
        userDAO.clearUsers();
        authDAO.clearAuths();
    }
}

package service;

import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;


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

    public AuthData login(String username, String password) throws UnauthorizedException {
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
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }

        authDAO.deleteAuth(authToken);
    };

}

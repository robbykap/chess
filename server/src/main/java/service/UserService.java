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

    public AuthData register(UserData userData) throws AlreadyTakenException, BadRequestException {
        // Check if required fields are present
        if (userData.username() == null || userData.password() == null || userData.email() == null) {
            throw new BadRequestException("Missing required fields");
        }

        // If the user already exists, throw an AlreadyTakenException
        try {
            userDAO.createUser(userData);

        } catch (DataAccessException e) {
            throw new AlreadyTakenException("User already exists");
        }

        // Create the AuthData to insert into the AuthDAO
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(userData.username(), authToken);
        authDAO.createAuth(authData);

        return authData;
    };

    public AuthData login(String username, String password) throws UnauthorizedException, BadRequestException {
        // Get the user from the UserDAO, throw a BadRequestException if the user is not found
        try {
            UserData user = userDAO.getUser(username);

            // Check if the password is correct, throw an UnauthorizedException if it is not
            if (user.password().equals(password)) {

                // Create an AuthData and insert it into the AuthDAO
                String authToken = UUID.randomUUID().toString();
                AuthData authData = new AuthData(username, authToken);
                authDAO.createAuth(authData);

                return authData;

            } else {
                throw new UnauthorizedException("Invalid password");
            }

        } catch (DataAccessException e) {
            throw new BadRequestException("User not found");
        }
    };

    public void logout(String authToken) throws UnauthorizedException {
        // Get the AuthData from the AuthDAO, throw an UnauthorizedException if it is not found
        try {
            authDAO.getAuth(authToken);

        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }

        authDAO.deleteAuth(authToken);
    };

}

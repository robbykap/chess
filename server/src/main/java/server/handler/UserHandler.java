package server.handler;

import com.google.gson.Gson;

import model.AuthData;
import model.UserData;

import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;

import service.UserService;

import spark.Request;
import spark.Response;

import server.request.user.*;
import server.response.user.*;
import server.response.exception.*;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object register(Request req, Response resp) {
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());

        try {
            AuthData authData = userService.register(userData);
            return RegisterResult.response(resp, authData);

        } catch (BadRequestException e) {
            return BadRequest.response(resp);

        } catch (AlreadyTakenException e) {
            return AlreadyTaken.response(resp);
        }

    }

    public Object login(Request req, Response resp) {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);

        try {
            AuthData authData = userService.login(loginRequest.username(), loginRequest.password());
           return LoginResult.response(resp, authData);

        } catch (UnauthorizedException | BadRequestException e) {
            return Unauthorized.response(resp);
        }

    }

    public Object logout(Request req, Response resp) {
        LogoutRequest logoutRequest = new LogoutRequest(req.headers("Authorization"));

        try {
            userService.logout(logoutRequest.authToken());
            return LogoutResult.response(resp);

        } catch (UnauthorizedException e) {
            return Unauthorized.response(resp);
        }
    }

}

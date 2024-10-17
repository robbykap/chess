package server;

import com.google.gson.Gson;

import model.AuthData;
import model.UserData;

import dataaccess.*;

import service.UserService;

import spark.Request;
import spark.Response;

import server.Request.*;
import server.Response.*;

public class UserHandler {
    private UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object register(Request req, Response resp) {
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);

        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            return BadRequest.response(resp);
        }

        UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());

        try {
            AuthData authData = userService.register(userData);
            return RegisterResult.response(resp, authData);

        } catch (AlreadyTakenException e) {
            return AlreadyTaken.response(resp);
        }

    };

    public Object login(Request req, Response resp) {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);

        try {
            AuthData authData = userService.login(loginRequest.username(), loginRequest.password());
           return LoginResult.response(resp, authData);

        } catch (UnauthorizedException e) {
            return Unauthorized.response(resp);
        }

    };

    public Object logout(Request req, Response resp) {
        LogoutRequest logoutRequest = new LogoutRequest(req.headers("Authorization"));

        try {
            userService.logout(logoutRequest.authToken());
            return LogoutResult.response(resp);

        } catch (UnauthorizedException e) {
            return Unauthorized.response(resp);
        }
    };


}

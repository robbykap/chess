package server;

import com.google.gson.Gson;

import model.AuthData;
import model.UserData;

import dataaccess.*;

import service.UserService;

import spark.Request;
import spark.Response;

import server.Request.*;

public class UserHandler {
    private UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object register(Request req, Response resp) {
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);

        if (registerRequest.username() == null ||
            registerRequest.password() == null ||
            registerRequest.email() == null) {
            resp.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        }

        UserData userData = new UserData(registerRequest.username(),
                                         registerRequest.password(),
                                         registerRequest.email());

        try {
            AuthData authData = userService.register(userData);
            resp.status(200);
            return new Gson().toJson(authData);

        } catch (BadRequestException e) {
            resp.status(403);
            return "{ \"message\": \"Error: already taken\" }";
        }

    };

    public Object login(Request req, Response resp) {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);

        try {
            AuthData authData = userService.login(loginRequest.username(), loginRequest.password());
            resp.status(200);
            return new Gson().toJson(authData);

        } catch (UnauthorizedException e) {
            resp.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }

    };

    public Object logout(Request req, Response resp) {
        LogoutRequest logoutRequest = new LogoutRequest(req.headers("Authorization"));

        try {
            userService.logout(logoutRequest.authToken());
            resp.status(200);
            return "{}";

        } catch (UnauthorizedException e) {
            resp.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    };


}

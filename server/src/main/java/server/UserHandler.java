package server;

import com.google.gson.Gson;
import dataaccess.*;

import model.AuthData;
import model.UserData;

import spark.Request;
import spark.Response;

import service.UserService;

public class UserHandler {
    private UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object register(Request req, Response resp) throws BadRequestException {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);

        if (userData.username() == null || userData.password() == null || userData.email() == null) {
            resp.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        }

        try {
            AuthData authData = userService.register(userData);
            resp.status(200);
            return new Gson().toJson(authData);

        } catch (BadRequestException e) {
            resp.status(403);
            return "{ \"message\": \"Error: already taken\" }";
        }

    };

    public Object login(Request req, Response resp) throws UnauthorizedException {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);

        try {
            AuthData authData = userService.login(userData.username(), userData.password());
            resp.status(200);
            return new Gson().toJson(authData);
        } catch (UnauthorizedException e) {
            resp.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }

    };

    public Object logout(Request req, Response resp) throws UnauthorizedException {
        String authToken = req.headers("Authorization");
        try {
            userService.logout(authToken);
            resp.status(200);
            return "{}";
        } catch (UnauthorizedException e) {
            resp.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    };


}

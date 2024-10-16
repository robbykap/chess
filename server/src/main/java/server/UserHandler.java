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

    public Object register(Request req, Response resp) {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);

        if (userData.username() == null || userData.password() == null || userData.email() == null) {
            throw new BadRequestException("Missing required fields");
        }

        try {
            AuthData authData = userService.register(userData);
            resp.status(200);
            return new Gson().toJson(authData);

        } catch (BadRequestException e) {
            resp.status(403);
            return "{ \"message\": \"" + e.getMessage() + "\" }";
        }

    };

    public Object login(Request req, Response resp) {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData = userService.login(userData);

        if (authData == null) {
            resp.status(401);
            return "{ \"message\": \"Invalid username or password\" }";
        }

        resp.status(200);
        return new Gson().toJson(authData);

    };

    public Object logout(Request req, Response resp) {
        String authToken = req.headers("Authorization");
        userService.logout(authToken);
        resp.status(200);
        return "{}";

    };


}

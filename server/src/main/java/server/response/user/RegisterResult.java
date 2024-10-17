package server.response.user;

import com.google.gson.Gson;
import model.AuthData;
import spark.Response;

public class RegisterResult {
    public static Object response(Response resp, AuthData authData) {
        resp.status(200);
        return new Gson().toJson(authData);
    }
}

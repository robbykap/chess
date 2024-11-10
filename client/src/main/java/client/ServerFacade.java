package client;

import com.google.gson.Gson;
import model.AuthData;
import model.ListGameData;
import server.request.game.CreateGameRequest;
import server.request.user.*;
import server.response.game.ListGamesResult;

import java.io.*;
import java.net.*;
import java.util.Collection;
import java.util.Map;


public class ServerFacade {
    private final String serverURL;
    private static String authToken;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
        this.authToken = null;
    }

    public void register(RegisterRequest request) throws ResponseException {
        var resp = this.makeRequest("POST", "/user", request, AuthData.class);
        authToken = resp.authToken();
    }

    public void login(LoginRequest request) throws ResponseException {
        var resp = this.makeRequest("POST", "/session", request, AuthData.class);
        authToken = resp.authToken();
    }

    public void logout() throws ResponseException {
        this.makeRequest("DELETE", "/session", null, null);
    }

    public Collection<Map<String, Object>> listGames() throws ResponseException {
        var resp = this.makeRequest("GET", "/game", null, ListGameData.class);
        return resp.games();
    }

    public void createGame(String gameName) throws ResponseException {
        CreateGameRequest request = new CreateGameRequest(authToken, gameName);
        this.makeRequest("POST", "/game", request, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (authToken != null) {
                http.addRequestProperty("authorization", authToken);
        } if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws ResponseException, IOException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream resBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(resBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private static boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

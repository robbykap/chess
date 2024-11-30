package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.ListGameData;
import server.request.game.CreateGameRequest;
import server.request.game.JoinGameRequest;
import server.request.user.LoginRequest;
import server.request.user.RegisterRequest;

import java.io.*;
import java.net.*;
import java.util.Collection;
import java.util.Map;

public class HttpCommunicator {
    private final String serverURL;
    private static String authToken;

    public HttpCommunicator(String serverURL) {
        this.serverURL = serverURL;
        authToken = null;
    }

    public String register(RegisterRequest request) throws ResponseException {
        try {
            var resp = this.makeRequest("POST", "/user", request, AuthData.class);
            authToken = resp.authToken();
        } catch (ResponseException e) {
            if (e.statusCode() == 403) {
                throw new ResponseException(403, "Username or email already taken");
            } else {
                throw new ResponseException(e.statusCode(), e.getMessage());
            }
        }
        return authToken;
    }

   public String login(LoginRequest request) throws ResponseException {
       try {
           var resp = this.makeRequest("POST", "/session", request, AuthData.class);
           authToken = resp.authToken();
       } catch (ResponseException e) {
           if (e.statusCode() == 401) {
               throw new ResponseException(401, "Username or password incorrect");
           } else {
               throw new ResponseException(e.statusCode(), e.getMessage());
           }
       }

       return authToken;
   }

   public Boolean logout() throws ResponseException {
       try {
           this.makeRequest("DELETE", "/session", null, null);
           authToken = null;
       } catch (ResponseException e) {
           if (e.statusCode() == 401) {
               throw new ResponseException(401, "You are not logged in");
           } else {
               throw new ResponseException(e.statusCode(), e.getMessage());
           }
       }
       return true;
   }

   public Collection<Map<String, Object>> listGames() throws ResponseException {
       try {
           var resp = this.makeRequest("GET", "/game", null, ListGameData.class);
           return resp.games();
       } catch (ResponseException e) {
           if (e.statusCode() == 401) {
               throw new ResponseException(401, "You are not logged in");
           } else {
               throw new ResponseException(e.statusCode(), e.getMessage());
           }
       }
   }

   public Boolean createGame(String gameName) throws ResponseException {
       try {
           CreateGameRequest request = new CreateGameRequest(authToken, gameName);
           this.makeRequest("POST", "/game", request, null);
       } catch (ResponseException e) {
           if (e.statusCode() == 401) {
               throw new ResponseException(401, "You are not logged in");
           } else {
               throw new ResponseException(e.statusCode(), e.getMessage());
           }
       }
       return true;
   }

   public ChessGame joinGame(int gameID, String color) throws ResponseException {
       try {
           JoinGameRequest request = new JoinGameRequest(authToken, color, gameID);
           return this.makeRequest("PUT", "/game", request, ChessGame.class);
       } catch (ResponseException e) {
           if (e.statusCode() == 401) {
               throw new ResponseException(401, "You are not logged in");
           } else if (e.statusCode() == 403) {
               throw new ResponseException(403, "Color already taken, or game is full");
           } else if (e.statusCode() == 400) {
               throw new ResponseException(400, "Game not found, check id");
           } else {
               throw new ResponseException(e.statusCode(), e.getMessage());
           }
       }
   }

  public ChessGame leaveGame(int gameID, String color) throws ResponseException {
       JoinGameRequest request = new JoinGameRequest(authToken, color, gameID);
       return this.makeRequest("DELETE", "/game", request, null);
   }

   public ChessGame observeGame(int gameID) throws ResponseException {
       try {
           JoinGameRequest request = new JoinGameRequest(authToken, "OBSERVE", gameID);
           return this.makeRequest("PUT", "/game", request, ChessGame.class);
       } catch (ResponseException e) {
           if (e.statusCode() == 401) {
               throw new ResponseException(401, "You are not logged in");
           } else if (e.statusCode() == 400) {
               throw new ResponseException(400, "Game not found, check id");
           } else {
               throw new ResponseException(e.statusCode(), e.getMessage());
           }
       }
   }

   public void clear() throws ResponseException {
         try {
                this.makeRequest("DELETE", "/db", null, null);
            } catch (ResponseException e) {
                if (e.statusCode() == 401) {
                    throw new ResponseException(401, "You are not logged in");
                } else {
                    throw new ResponseException(e.statusCode(), e.getMessage());
                }
            }
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
        } catch (ResponseException e) {
            throw new ResponseException(e.statusCode(), e.getMessage());
        } catch (IOException | URISyntaxException ex) {
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
            if (status == 403) {
                throw new ResponseException(403, "Already taken");
            }
            else if (status == 401) {
                throw new ResponseException(401, "Unauthorized");
            }
            else if (status == 400) {
                throw new ResponseException(400, "Bad request");
            }
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

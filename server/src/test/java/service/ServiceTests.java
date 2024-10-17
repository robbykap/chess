//// ServiceTests.java
//package service;
//
//import dataaccess.*;
//import model.*;
//import org.junit.jupiter.api.*;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//
//public class ServiceTests {
//
//    private UserService userService;
//    private GameService gameService;
//    private ClearService clearService;
//
//    private MemoryUserDAO userDAO;
//    private MemoryAuthDAO authDAO;
//    private MemoryGameDAO gameDAO;
//
//    @BeforeEach
//    public void setUp() {
//        userDAO = new MemoryUserDAO();
//        authDAO = new MemoryAuthDAO();
//        gameDAO = new MemoryGameDAO();
//
//        userService = new UserService(userDAO, authDAO);
//        gameService = new GameService(gameDAO, authDAO);
//        clearService = new ClearService(userDAO, authDAO, gameDAO);
//    }
//
//    @Test
//    public void testRegisterPositive() throws BadRequestException {
//        assertEquals(0, userDAO.size(), "register should start with no users");
//        assertEquals(0, authDAO.size(), "register should start with no auths");
//
//        userService.register(new UserData("mike", "password", "bob@example.com"));
//
//        assertEquals(1, userDAO.size(), "register should add a user");
//        assertEquals(1, authDAO.size(), "register should add an auth");
//
//        userService.register(new UserData("joe", "password", "joe@example.com"));
//        userService.register(new UserData("sue", "password", "sue@example.com"));
//        userService.register(new UserData("jane", "password", "jane@example.com"));
//
//        assertEquals(4, userDAO.size(), "register should add multiple users");
//        assertEquals(4, authDAO.size(), "register should add multiple auths");
//    }
//
//    @Test
//    public void testRegisterNegative() throws BadRequestException {
//        assertEquals(0, userDAO.size(), "register should start with no users");
//        assertEquals(0, authDAO.size(), "register should start with no auths");
//
//        userService.register(new UserData("mike", "password", "bob@example.com"));
//
//        assertEquals(1, userDAO.size(), "register should add a user");
//        assertEquals(1, authDAO.size(), "register should add an auth");
//
//        try {
//            userService.register(new UserData("mike", "password", "bob@example.com"));
//        } catch (BadRequestException e) {
//            assertEquals("User already exists: mike", e.getMessage(), "register should not allow duplicate users");
//        }
//    }
//
//    @Test
//    public void testLogoutPositive() throws BadRequestException, UnauthorizedException {
//        assertEquals(0, userDAO.size(), "register should start with no users");
//        assertEquals(0, authDAO.size(), "register should start with no auths");
//
//        AuthData authData = userService.register(new UserData("mike", "password", "bob@example.com"));
//
//        assertEquals(1, userDAO.size(), "register should add a user");
//        assertEquals(1, authDAO.size(), "register should add an auth");
//
//        userService.logout(authData.authToken());
//
//        assertEquals(1, userDAO.size(), "logout should not remove the user");
//        assertEquals(0, authDAO.size(), "logout should remove the auth");
//    }
//
//    @Test
//    public void testLogoutNegative() throws BadRequestException {
//        assertEquals(0, userDAO.size(), "register should start with no users");
//        assertEquals(0, authDAO.size(), "register should start with no auths");
//
//        AuthData authData = userService.register(new UserData("mike", "password", "bob@example.com"));
//
//        assertEquals(1, userDAO.size(), "register should add a user");
//        assertEquals(1, authDAO.size(), "register should add an auth");
//
//        try {
//            userService.logout("badToken");
//        } catch (UnauthorizedException e) {
//            assertEquals("Invalid auth token", e.getMessage(), "logout should not allow invalid tokens");
//        }
//    }
//
//    @Test
//    public void testLoginPositive() throws BadRequestException, UnauthorizedException {
//        assertEquals(0, userDAO.size(), "register should start with no users");
//        assertEquals(0, authDAO.size(), "register should start with no auths");
//
//        AuthData authData = userService.register(new UserData("mike", "password", "bob@example.com"));
//        userService.logout(authData.authToken());
//
//        assertEquals(1, userDAO.size(), "register should add a user");
//        assertEquals(0, authDAO.size(), "register should add an auth");
//
//        AuthData newAuthData = userService.login("mike", "password");
//
//        assertNotEquals(authData.authToken(), newAuthData.authToken(), "login should create a new auth token");
//
//        assertEquals(1, userDAO.size(), "login should not add a user");
//        assertEquals(1, authDAO.size(), "login should add an auth");
//    }
//
//    @Test
//    public void testLoginWrongPassword() throws BadRequestException, UnauthorizedException {
//        assertEquals(0, userDAO.size(), "register should start with no users");
//        assertEquals(0, authDAO.size(), "register should start with no auths");
//
//        AuthData authData = userService.register(new UserData("mike", "password", "bob@example.com"));
//        userService.logout(authData.authToken());
//
//        assertEquals(1, userDAO.size(), "register should add a user");
//        assertEquals(0, authDAO.size(), "register should add an auth");
//
//        try {
//            AuthData newAuthData = userService.login("mike", "password1");
//        } catch (UnauthorizedException e) {
//            assertEquals("Invalid password", e.getMessage(), "login should not allow invalid passwords");
//        }
//
//        assertEquals(1, userDAO.size(), "login should not add a user");
//        assertEquals(0, authDAO.size(), "login should not add an auth");
//    }
//
//    @Test
//    public void testClear() throws BadRequestException, UnauthorizedException, DataAccessException {
//        UserData userData = new UserData("mike", "password", "bob@example.com");
//
//        AuthData authData = userService.register(userData);
//
//        gameService.createGame(authData.authToken(), "game1");
//
//        assertEquals(1, userDAO.size(), "clear should start with users");
//        assertEquals(1, authDAO.size(), "clear should start with auths");
//        assertEquals(1, gameDAO.size(), "clear should start with games");
//
//        clearService.clear(null, null);
//        assertEquals(0, userDAO.size(), "clearUsers should clear all users");
//        assertEquals(0, authDAO.size(), "clearAuths should clear all auths");
//        assertEquals(0, gameDAO.size(), "clearGames should clear all games");
//    }
//}

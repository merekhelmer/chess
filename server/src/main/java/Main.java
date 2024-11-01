import dataaccess.*;
import server.Server;
import service.GameService;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        try {
            int port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            UserDAO userDAO;
            AuthDAO authDAO;
            GameDAO gameDAO;

            if (args.length >= 2 && args[1].equalsIgnoreCase("sql")) {
                DatabaseManager.createDatabase();
                DatabaseManager.createTables();
                userDAO = new SQLUserDAO();
                authDAO = new SQLAuthDAO();
                gameDAO = new SQLGameDAO();
            } else {
                userDAO = new MemoryUserDAO();
                authDAO = new MemoryAuthDAO();
                gameDAO = new MemoryGameDAO();
            }

            var userService = new UserService(userDAO, authDAO);
            var gameService = new GameService(gameDAO, authDAO);
            var server = new Server(userService, gameService).run(port);

            System.out.printf("Server started on port %d", server, args.length >= 2 && args[1].equalsIgnoreCase("sql") ? "MySQL" : "Memory");
        } catch (Exception ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}




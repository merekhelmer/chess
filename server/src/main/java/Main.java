import dataaccess.DatabaseManager;
import server.Server;

public class Main {

    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Server");

        try {
            // ensure the database and tables are created if they do not already exist
            DatabaseManager.createDatabase();
            DatabaseManager.createTable();

            Server server = new Server();
            int port = server.run(8080);

            System.out.println("Server is running on port: " + port);
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}



import server.Server;

public class Main {

    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Server");

        Server server = new Server();
        int port = server.run(8080);

        System.out.println("Server is running on port: " + port);
    }
}


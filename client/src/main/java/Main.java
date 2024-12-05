import client.ChessClient;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        System.out.println("♔ Welcome to 240 Chess Client");
        System.out.println("Connecting to server: " + serverUrl);

        Scanner scanner = new Scanner(System.in);
        ChessClient chessClient = new ChessClient(serverUrl, scanner);

        // start the client
        chessClient.start();
        System.out.println("♕ Chess Client terminated. Goodbye!");
    }
}

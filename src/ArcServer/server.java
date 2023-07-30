// server.java

package ArcServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class server {
    // List to store all connected clients
    private static List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(config.SERVER_PORT);
            System.out.println("Server is running...");

            // Load user commands
            userCommands userCommandsHandler = new userCommands(clientWriters);
            System.out.println("Commands have been loaded.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create a PrintWriter for the current client and add it to the list
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(writer);

                // Handle client in a new thread
                Thread clientThread = new Thread(() -> handleClient(clientSocket, writer, userCommandsHandler));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket, PrintWriter writer, userCommands userCommandsHandler) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String pseudo = reader.readLine();
            String thumbprint = reader.readLine();
            String serverKey = reader.readLine();

            System.out.println("User connected: Pseudo = " + pseudo + ", Thumbprint = " + thumbprint);
            System.out.println("Client-supplied Server Key: " + serverKey);

            // Check if the server key matches the one in config.java
            if (!serverKey.equals(config.SERVER_KEY)) {
                writer.println("Invalid Server Key. Connection not authorized.");
                clientSocket.close();
                return;
            }

            // Send welcome message to the client
            writer.println("Connection authorized.");
            writer.println(config.DEFAULT_WELCOME_MESSAGE); // Send the welcome message from the config

            // Start a thread to listen for messages from the client
            Thread messageListenerThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println("Received message from " + pseudo + ": " + message);

                        // Check if the message is a command
                        if (message.startsWith("/")) {
                            String commandResponse = userCommandsHandler.executeCommand(message, writer);
                            if ("disconnect".equals(commandResponse)) {
                                // Disconnect the client gracefully
                                clientSocket.close();
                                break;
                            } else {
                                writer.println("[Server] " + commandResponse);
                            }
                        } else {
                            // Broadcast the received message to all connected clients, including the sender
                            for (PrintWriter clientWriter : clientWriters) {
                                clientWriter.println(pseudo + ": " + message);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            messageListenerThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
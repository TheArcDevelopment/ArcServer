// userCommands.java

package ArcServer;

import java.util.List;
import java.io.PrintWriter;
import java.util.ArrayList;

public class userCommands {
    // List to store all connected clients
    private List<PrintWriter> clientWriters = new ArrayList<>();

    public userCommands(List<PrintWriter> clientWriters) {
        this.clientWriters = clientWriters;
    }

    public String executeCommand(String command, String pseudo, PrintWriter writer) {
        if (command.startsWith("/ping")) {
            return "Pong!";
        } else if (command.startsWith("/leave")) {
            // Disconnect the client gracefully
            clientWriters.remove(writer);
            broadcast("[Server] " + pseudo + " has disconnected!");
            writer.println("[Server] Your connection to the server has been terminated.");
            writer.println("[Server] Please close your client - it's now useless");
            return "disconnect";
        } else {
            return "Unknown command: " + command;
        }
    }

    // Helper method to broadcast a message to all connected clients
    private void broadcast(String message) {
        for (PrintWriter clientWriter : clientWriters) {
            clientWriter.println(message);
        }
    }

    // Helper method to get the pseudo of the client associated with the given PrintWriter
    private String getClientPseudo(PrintWriter writer) {
        for (PrintWriter clientWriter : clientWriters) {
            if (clientWriter.equals(writer)) {
                // Assuming that the pseudo is the first line sent by the client during connection
                return clientWriter.toString().split("\n")[0];
            }
        }
        return null;
    }
}

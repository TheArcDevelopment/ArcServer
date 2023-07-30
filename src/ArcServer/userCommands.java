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

    public String executeCommand(String command, PrintWriter writer) {
        if (command.startsWith("/ping")) {
            return "Pong!";
        } else if (command.startsWith("/disconnect")) {
            // Disconnect the client gracefully
            clientWriters.remove(writer);
            writer.println("[Server] Your connection to the server has been terminated.");
            writer.println("[Server] Please close your client - it's now useless");
            return "disconnect";
        } else {
            return "Unknown command: " + command;
        }
    }
}

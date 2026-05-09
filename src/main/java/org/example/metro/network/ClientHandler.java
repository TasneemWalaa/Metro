package org.example.metro.network;


import org.example.metro.model.Ticket;

import java.io.*;
import java.net.Socket;

import static java.lang.Double.*;

/**
 * ClientHandler runs in its own thread and handles all communication
 * with one connected client.
 *
 * Protocol commands (client → server):
 *   REGISTER|username|password|fullName
 *   LOGIN|username|password
 *   BOOK|from|to|price|username
 *   GET_TICKETS|username
 *   DISCONNECT
 *
 * Server responses are plain strings sent back line-by-line.
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final MetroServer server;

    public ClientHandler(Socket socket, MetroServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (
                DataInputStream  in  = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))
        ) {
            String request;
            // Keep reading commands until the client disconnects
            while ((request = in.readUTF()) != null) {
                String response = handleRequest(request);
                out.writeUTF(response);
                out.flush();

                if ("DISCONNECT".equals(request.trim())) break;
            }
        } catch (EOFException e) {
            System.out.println("[Server] Client disconnected.");
        } catch (IOException e) {
            System.err.println("[Server] Handler error: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    /** Parses a command string and delegates to the appropriate server method. */
    private String handleRequest(String request) {
        String[] parts = request.split("\\|");
        String command = parts[0].toUpperCase();

        switch (command) {

            case "REGISTER": {
                if (parts.length < 4) return "ERROR|Missing fields";
                boolean success = server.registerUser(parts[1], parts[2], parts[3]);
                return success ? "REGISTER_OK" : "ERROR|Username already taken";
            }

            case "LOGIN": {
                if (parts.length < 3) return "ERROR|Missing credentials";
                String fullName = server.loginUser(parts[1], parts[2]);
                if (fullName != null) {
                    return "LOGIN_OK|" + fullName;
                }
                return "ERROR|Invalid username or password";
            }

            case "BOOK": {
                // BOOK|from|to|price|username
                if (parts.length < 5) return "ERROR|Incomplete booking data";
                // Build a serialised Ticket string on the server side
                org.example.metro.model.Ticket t = new Ticket(parts[1], parts[2], parseDouble(parts[3]), parts[4]);
                server.addTicket(t.serialise());
                return "BOOK_OK|" + t.serialise();
            }

            case "GET_TICKETS": {
                if (parts.length < 2) return "ERROR|Missing username";
                String ticketData = server.getTicketsForUser(parts[1]);
                if (ticketData.isEmpty()) return "TICKETS|NONE";
                return "TICKETS|" + ticketData;
            }

            case "DISCONNECT":
                return "BYE";

            default:
                return "ERROR|Unknown command";
        }
    }
}
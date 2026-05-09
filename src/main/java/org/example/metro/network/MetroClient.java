package org.example.metro.network;


import org.example.metro.model.Ticket;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * MetroClient wraps a TCP socket connection to MetroServer.
 * All network I/O is done synchronously here; callers must invoke
 * these methods from a background thread to avoid blocking the JavaFX UI.
 */
public class MetroClient {

    private static final String HOST = "localhost";

    private Socket socket;
    private DataInputStream  in;
    private DataOutputStream out;

    // ---------- Connection lifecycle ----------

    /**
     * Opens a connection to the server.
     *
     * @throws IOException if the server is unreachable.
     */
    public void connect() throws IOException {
        socket = new Socket(HOST, MetroServer.PORT);
        in  = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    /** Sends a DISCONNECT command and closes the socket. */
    public void disconnect() {
        try {
            if (out != null) {
                out.writeUTF("DISCONNECT");
                out.flush();
            }
        } catch (IOException ignored) {
        } finally {
            try { if (socket != null) socket.close(); } catch (IOException ignored) {}
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    // ---------- Protocol methods ----------

    /**
     * Sends a REGISTER request.
     *
     * @return null on success, or an error message string on failure.
     */
    public String register(String username, String password, String fullName) throws IOException {
        send("REGISTER|" + username + "|" + password + "|" + fullName);
        String resp = receive();
        if ("REGISTER_OK".equals(resp)) return null;
        return resp.replace("ERROR|", "");
    }

    /**
     * Sends a LOGIN request.
     *
     * @return The passenger's full name on success, or null on failure.
     * @throws IOException         on network error.
     * @throws SecurityException   with a human-readable message on invalid credentials.
     */
    public String login(String username, String password) throws IOException {
        send("LOGIN|" + username + "|" + password);
        String resp = receive();
        if (resp.startsWith("LOGIN_OK|")) {
            return resp.substring("LOGIN_OK|".length());
        }
        throw new SecurityException(resp.replace("ERROR|", ""));
    }

    /**
     * Sends a BOOK request and returns the booked Ticket on success.
     *
     * @throws IOException on network error.
     * @throws IllegalStateException if the server rejects the booking.
     */
    public org.example.metro.model.Ticket bookTicket(String from, String to, double price, String username) throws IOException {
        send("BOOK|" + from + "|" + to + "|" + price + "|" + username);
        String resp = receive();

        if (resp.startsWith("BOOK_OK|")) {
            return Ticket.deserialise(resp.substring("BOOK_OK|".length()));
        }

        throw new IllegalStateException(resp.replace("ERROR|", ""));
    }

    /**
     * Fetches all tickets for a given user from the server.
     *
     * @return A (possibly empty) list of Ticket objects.
     */
    public List<Ticket> getTickets(String username) throws IOException {
        send("GET_TICKETS|" + username);
        String resp = receive();
        List<Ticket> result = new ArrayList<>();
        if (resp.startsWith("TICKETS|")) {
            String payload = resp.substring("TICKETS|".length());
            if (!"NONE".equals(payload)) {
                for (String line : payload.split("\n")) {
                    if (!line.isEmpty()) {
                        result.add(Ticket.deserialise(line.trim()));
                    }
                }
            }
        }
        return result;
    }

    // ---------- Low-level helpers ----------

    private void send(String message) throws IOException {
        out.writeUTF(message);
        out.flush();
    }

    private String receive() throws IOException {
        return in.readUTF();
    }
}

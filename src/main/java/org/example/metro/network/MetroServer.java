package org.example.metro.network;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MetroServer listens on a TCP port and spawns a ClientHandler thread
 * for every new connection — demonstrating multithreading.
 *
 * All shared state (users, tickets) lives here and is accessed by handlers
 * through synchronised methods.
 */
public class MetroServer implements Runnable {

    public static final int PORT = 9090;

    // username -> "password|fullName"
    private final Map<String, String> users = new ConcurrentHashMap<>();

    // list of serialised ticket strings
    private final List<String> tickets = Collections.synchronizedList(new ArrayList<>());

    // ---------- Runnable ----------

    @Override
    public void run() {
        System.out.println("[Server] Starting on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Server] Client connected: " + clientSocket.getInetAddress());
                // Each client is handled in its own thread
                Thread handlerThread = new Thread(new ClientHandler(clientSocket, this));
                handlerThread.setDaemon(true);
                handlerThread.start();
            }
        } catch (IOException e) {
            System.err.println("[Server] Server stopped: " + e.getMessage());
        }
    }

    // ---------- User operations (thread-safe) ----------

    /** Registers a new user. Returns true on success, false if username is taken. */
    public synchronized boolean registerUser(String username, String password, String fullName) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, password + "|" + fullName);
        return true;
    }

    /**
     * Validates login credentials.
     * Returns "fullName" on success, or null on failure.
     */
    public synchronized String loginUser(String username, String password) {
        String record = users.get(username);
        if (record == null) return null;
        String[] parts = record.split("\\|", 2);
        if (parts[0].equals(password)) {
            return parts[1]; // fullName
        }
        return null;
    }

    // ---------- Ticket operations (thread-safe) ----------

    /** Adds a serialised ticket string to the server store. */
    public synchronized void addTicket(String serialisedTicket) {
        tickets.add(serialisedTicket);
    }

    /**
     * Returns all tickets belonging to a specific user as a newline-delimited string.
     */
    public synchronized String getTicketsForUser(String username) {
        StringBuilder sb = new StringBuilder();
        for (String t : tickets) {
            // ticket format: TICKET|id|from|to|price|date|owner
            if (t.endsWith("|" + username)) {
                sb.append(t).append("\n");
            }
        }
        return sb.toString().trim();
    }

    // ---------- Entry point ----------

    /** Starts the server in its own background thread. */
    public static void startInBackground() {
        Thread serverThread = new Thread(new MetroServer());
        serverThread.setDaemon(true);
        serverThread.setName("MetroServer-Thread");
        serverThread.start();
    }

    public static void main(String[] args) {
        new MetroServer().run(); // blocking — for standalone server launch
    }
}

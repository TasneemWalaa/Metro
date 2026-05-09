package org.example.metro.util;
import org.example.metro.model.Passenger;
import org.example.metro.model.Ticket;

import java.util.ArrayList;
import java.util.List;

/**
 * SessionManager keeps the currently logged-in passenger in memory
 * and holds tickets cached from the server for this session.
 *
 * This is a simple singleton — only one user is logged in at a time.
 */
public class SessionManager {

    private static SessionManager instance;

    private Passenger currentPassenger;
    private final List<Ticket> sessionTickets = new ArrayList<>();

    private SessionManager() {}

    /** Returns the single application-wide session. */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // ---------- Passenger ----------

    public void login(Passenger passenger) {
        this.currentPassenger = passenger;
        sessionTickets.clear();
    }

    public void logout() {
        currentPassenger = null;
        sessionTickets.clear();
    }

    public Passenger getCurrentPassenger() {
        return currentPassenger;
    }

    public boolean isLoggedIn() {
        return currentPassenger != null;
    }

    // ---------- Ticket cache ----------

    public void setSessionTickets(List<Ticket> tickets) {
        sessionTickets.clear();
        sessionTickets.addAll(tickets);
        if (currentPassenger != null) {
            // Sync ticket count on the passenger object
            long ownedCount = tickets.stream()
                    .filter(t -> t.getOwnerUsername().equals(currentPassenger.getUsername()))
                    .count();
            for (int i = currentPassenger.getTicketCount(); i < ownedCount; i++) {
                currentPassenger.incrementTicketCount();
            }
        }
    }

    public List<Ticket> getSessionTickets() {
        return new ArrayList<>(sessionTickets);
    }

    public void addTicket(Ticket ticket) {
        sessionTickets.add(ticket);
        if (currentPassenger != null) {
            currentPassenger.incrementTicketCount();
        }
    }
}



































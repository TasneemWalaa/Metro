package org.example.metro.model;

public class Passenger extends User {

    private String fullName;
    private int ticketCount;

    public Passenger(String username, String password, String fullName) {
        super(username, password);
        this.fullName = fullName;
        this.ticketCount = 0;
    }

    // ---------- Getters & Setters ----------

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getTicketCount() {
        return ticketCount;
    }

    public void incrementTicketCount() {
        this.ticketCount++;
    }

    /**
     * Overrides User.getDisplayInfo() — polymorphism in action.
     */
    @Override
    public String getDisplayInfo() {
        return "Passenger: " + fullName + " (@" + getUsername() + ") | Tickets: " + ticketCount;
    }
}

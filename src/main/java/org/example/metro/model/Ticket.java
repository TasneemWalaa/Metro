package org.example.metro.model;
    import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

    /**
     * Represents a single metro ticket booking.
     * Demonstrates encapsulation with private fields and public accessors.
     */
    public class Ticket {

        private static int idCounter = 1000;

        private final int ticketId;
        private final String fromStation;
        private final String toStation;
        private final double price;
        private final String bookingDate;
        private final String ownerUsername;

        public Ticket(String fromStation, String toStation, double price, String ownerUsername) {
            this.ticketId     = ++idCounter;
            this.fromStation  = fromStation;
            this.toStation    = toStation;
            this.price        = price;
            this.ownerUsername = ownerUsername;
            this.bookingDate  = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }

        // ---------- Getters ----------

        public int getTicketId() {
            return ticketId;
        }

        public String getFromStation() {
            return fromStation;
        }

        public String getToStation() {
            return toStation;
        }

        public double getPrice() {
            return price;
        }

        public String getBookingDate() {
            return bookingDate;
        }

        public String getOwnerUsername() {
            return ownerUsername;
        }

        /**
         * Serialises this ticket into a pipe-delimited string for TCP transport.
         * Format: TICKET|id|from|to|price|date|owner
         */
        public String serialise() {
            return "TICKET|" + ticketId + "|" + fromStation + "|" + toStation
                    + "|" + price + "|" + bookingDate + "|" + ownerUsername;
        }

        /**
         * Reconstructs a Ticket from the serialised string produced by {@link #serialise()}.
         */
        public static Ticket deserialise(String raw) {
            // raw format: TICKET|id|from|to|price|date|owner
            String[] parts = raw.split("\\|");
            Ticket t = new Ticket(parts[2], parts[3], Double.parseDouble(parts[4]), parts[6]);
            // Restore the original id counter so deserialized tickets don't clash
            idCounter = Math.max(idCounter, Integer.parseInt(parts[1]));
            return t;
        }

        @Override
        public String toString() {
            return "#" + ticketId + " | " + fromStation + " → " + toStation
                    + " | EGP " + price + " | " + bookingDate;
        }
    }

}

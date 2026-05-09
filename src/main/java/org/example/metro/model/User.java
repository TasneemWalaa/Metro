package org.example.metro.model;

public class User {
    public User(String username, String password) {
    }

    /**
     * Base class representing a system user.
     * Demonstrates encapsulation with private fields and public getters/setters.
     */
    public class User {

        private String username;
        private String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        // ---------- Getters & Setters ----------

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        /**
         * Returns a basic string description of this user.
         * Overridden in subclasses (polymorphism).
         */
        public String getDisplayInfo() {
            return "User: " + username;
        }

        @Override
        public String toString() {
            return getDisplayInfo();
        }
    }

}

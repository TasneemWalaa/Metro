package org.example.metro.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.metro.util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for dashboard.fxml — the main navigation hub.
 */
public class DashboardController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Display personalised welcome using polymorphic getDisplayInfo()
        if (SessionManager.getInstance().isLoggedIn()) {
            String name = SessionManager.getInstance().getCurrentPassenger().getFullName();
            welcomeLabel.setText("Welcome back, " + name + "!");
        }
    }

    @FXML private void handleBookTicket()     { navigateTo("book_ticket.fxml",   "Metro — Book Ticket"); }
    @FXML private void handleMyTickets()      { navigateTo("my_tickets.fxml",    "Metro — My Tickets"); }
    @FXML private void handleMetroStations()  { navigateTo("stations.fxml",      "Metro — Stations"); }
    @FXML private void handleProfile()        { navigateTo("profile.fxml",       "Metro — Profile"); }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        navigateTo("login.fxml", "Metro — Login");
    }

    // ---------- Navigation helper ----------

    private void navigateTo(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/metro/view/" + fxml));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package org.example.metro.controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.metro.model.Passenger;
import org.example.metro.util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for profile.fxml — displays the logged-in user's information.
 * Uses the polymorphic getDisplayInfo() method from Passenger.
 */
public class ProfileController implements Initializable {

    @FXML private Label fullNameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label ticketCountLabel;
    @FXML private Label displayInfoLabel;
    @FXML private Button logoutButton;
    @FXML private Button backButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Passenger p = SessionManager.getInstance().getCurrentPassenger();
        if (p != null) {
            fullNameLabel.setText(p.getFullName());
            usernameLabel.setText("@" + p.getUsername());
            ticketCountLabel.setText(String.valueOf(
                    SessionManager.getInstance().getSessionTickets().size()));
            // Demonstrates polymorphism — calls overridden getDisplayInfo()
            displayInfoLabel.setText(p.getDisplayInfo());
        }
    }
    }
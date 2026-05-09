package org.example.metro.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.metro.model.Passenger;
import org.example.metro.network.MetroClient;
import org.example.metro.network.MetroServer;
import org.example.metro.util.SessionManager;

import java.io.IOException;

/**
 * Controller for login.fxml — handles user authentication.
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private Button loginButton;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please fill in all fields.", true);
            return;
        }

        loginButton.setDisable(true);
        showStatus("Connecting…", false);

        // Run network call on a background thread so the UI stays responsive
        Thread loginThread = new Thread(() -> {
            MetroClient client = new MetroClient();
            try {
                client.connect();
                String fullName = client.login(username, password);

                // Build session on success
                Passenger passenger = new Passenger(username, password, fullName);
                SessionManager.getInstance().login(passenger);

                Platform.runLater(() -> navigateTo("dashboard.fxml", "Metro — Dashboard"));

            } catch (SecurityException e) {
                Platform.runLater(() -> {
                    showStatus(e.getMessage(), true);
                    loginButton.setDisable(false);
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showStatus("Server is offline. Please try again.", true);
                    loginButton.setDisable(false);
                });
            } finally {
                client.disconnect();
            }
        });
        loginThread.setDaemon(true);
        loginThread.start();
    }

    @FXML
    private void handleGoToRegister() {
        navigateTo("register.fxml", "Metro — Register");
    }

    // ---------- Helpers ----------

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #2ecc71;");
    }

    private void navigateTo(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/metro/view/" + fxml));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) {
            showStatus("Navigation error: " + e.getMessage(), true);
        }
    }
}
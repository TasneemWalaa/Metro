package org.example.metro.controller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.metro.network.MetroClient;
import org.example.metro.model.Passenger;
import org.example.metro.util.SessionManager;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;
    @FXML private Button registerButton;

    @FXML
    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm  = confirmPasswordField.getText().trim();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showStatus("All fields are required.", true);
            return;
        }
        if (!password.equals(confirm)) {
            showStatus("Passwords do not match.", true);
            return;
        }
        if (password.length() < 4) {
            showStatus("Password must be at least 4 characters.", true);
            return;
        }

        registerButton.setDisable(true);
        showStatus("Registering…", false);

        Thread registerThread = new Thread(() -> {
            MetroClient client = new MetroClient();
            try {
                client.connect();
                String error = client.register(username, password, fullName);

                if (error == null) {
                    // ✓ بعد Register ناجح — اعمل session وروح Dashboard
                    Passenger passenger = new Passenger(username, password, fullName);
                    SessionManager.getInstance().login(passenger);

                    Platform.runLater(() -> {
                        navigateTo("dashboard.fxml", "Metro — Dashboard");
                    });
                } else {
                    Platform.runLater(() -> {
                        showStatus(error, true);
                        registerButton.setDisable(false);
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showStatus("Server is offline. Please try again.", true);
                    registerButton.setDisable(false);
                });
            } finally {
                client.disconnect();
            }
        });
        registerThread.setDaemon(true);
        registerThread.start();
    }

    @FXML
    private void handleBackToLogin() {
        navigateTo("login.fxml", "Metro — Login");
    }

    private void clearFields() {
        fullNameField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #2ecc71;");
    }

    private void navigateTo(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/metro/view/" + fxml)); // ✓
            Parent root = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) {
            showStatus("Navigation error: " + e.getMessage(), true);
        }
    }
}
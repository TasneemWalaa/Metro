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

public class ProfileController implements Initializable {

    @FXML private Label fullNameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label ticketCountLabel;
    @FXML private Label displayInfoLabel;
    @FXML private Button logoutButton;
    @FXML private Button backButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // بنجيب المستخدم الحالي من الـ SessionManager اللي إنتِ بعتيه
        Passenger p = SessionManager.getInstance().getCurrentPassenger();
        if (p != null) {
            fullNameLabel.setText(p.getFullName());
            usernameLabel.setText("@" + p.getUsername());

            // بنعرض عدد التذاكر اللي متخزنة في الـ Session
            ticketCountLabel.setText(String.valueOf(
                    SessionManager.getInstance().getSessionTickets().size()));

            // تطبيق الـ Polymorphism
            displayInfoLabel.setText(p.getDisplayInfo());
        }
    }

    // الميثودز دي هي اللي هتخلي الـ Buttons تشتغل واللون الرمادي يختفي
    @FXML
    private void handleBack() {
        navigateTo("dashboard.fxml", "Metro — Dashboard");
    }

    @FXML
    private void handleLogout() {
        // بننادي ميثود الـ logout من الـ SessionManager بتاعك
        SessionManager.getInstance().logout();
        navigateTo("login.fxml", "Metro — Login");
    }

    private void navigateTo(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/metro/view/" + fxml));
            Parent root = loader.load();
            // هنا بنستخدم الـ backButton عشان نوصل للـ Stage
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
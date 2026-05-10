package org.example.metro.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for stations.fxml — shows Cairo Metro lines and their stations.
 */
public class StationsController implements Initializable {

    @FXML
    private ListView<String> line1List;
    @FXML
    private ListView<String> line2List;
    @FXML
    private ListView<String> line3List;
    @FXML
    private Button backButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateLine1();
        populateLine2();
        populateLine3();
    }

    private void populateLine1() {
        line1List.getItems().addAll(
                "Helwan", "Ain Helwan", "Helwan University", "Wadi Hof",
                "Hadayek Helwan", "El-Maasara", "Tora El-Balad", "Kozzika",
                "Tora El-Asmant", "Mar Girgis", "El-Malek El-Saleh", "Al-Zahraa",
                "Dar El-Salam", "Hadayek El-Maadi", "Sakanat El-Maadi", "El-Maadi",
                "Thakanat El-Maadi", "Zahraa El-Maadi", "Nasser (Tahrir)",
                "Sadat (Tahrir)", "Opera", "Attaba", "Mohammed Naguib",
                "Al-Shohadaa", "Ghamra", "El-Demerdash", "Manshiet El-Sadr",
                "Kobri El-Qubba", "Hammamat El-Qubba", "Saray El-Qubba",
                "Hadayek El-Zeitoun", "Helmeyet El-Zeitoun", "El-Matareyya",
                "Ain Shams", "Ezbet El-Nakhl (1)", "El-Marg", "New El-Marg",
                "Shubra El-Kheima"
        );
    }

    private void populateLine2() {
        line2List.getItems().addAll(
                "El-Mounib", "Sakiat Mekky", "Omm El-Masryeen", "Giza",
                "Faisal", "Cairo University", "Massara", "Rod El-Farag",
                "Messarra", "St. Teresa", "Khalafawy", "Mezallat",
                "Kolleyet El-Ziraa", "Shubra El-Kheima", "Sadat (Tahrir)",
                "Opera", "Ataba", "Naguib", "Al-Shohadaa", "Imbaba",
                "Sudan", "Bulaq El-Dakrour"
        );
    }

    private void populateLine3() {
        line3List.getItems().addAll(
                "Adly Mansour", "El Haykstep", "Omar Ibn El-Khattab",
                "Qiaada", "Alf Maskan", "Nozha", "Hesham Barakat",
                "El-Ahram", "Haroun", "Fairous", "Abbassia",
                "Abdou Basha", "El-Geish", "Bab El Shaaria",
                "Attaba", "Nasser (Tahrir)", "Cairo Stadium",
                "Ard El-Lewa", "Al-Ahly Club", "Al-Tawfikia",
                "Wadi El-Nil", "Gamaat El-Dowal", "Bein El-Sarayat",
                "El-Behoos", "Cairo University"
        );
    }

    @FXML
    private void handleBack() {
        navigateTo("dashboard.fxml", "Metro — Dashboard");
    }

    private void navigateTo(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/metro/view/" + fxml));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) {
            System.err.println("Navigation error: " + e.getMessage());
        }
    }
}
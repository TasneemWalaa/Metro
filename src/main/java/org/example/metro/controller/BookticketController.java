package org.example.metro.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.metro.model.Ticket;
import org.example.metro.network.MetroClient;
import org.example.metro.util.SessionManager;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for book_ticket.fxml — lets passengers select stations and book.
 */
public class BookTicketController implements Initializable {

    // Cairo Metro stations (Line 1, 2 & 3 samples)
    private static final List<String> STATIONS = List.of(
            "Helwan", "Ain Helwan", "Helwan University", "Wadi Hof", "Hadayek Helwan",
            "El-Maasara", "Tora El-Balad", "Kozzika", "Tora El-Asmant", "Mar Girgis",
            "El-Malek El-Saleh", "Al-Zahraa", "Dar El-Salam", "Hadayek El-Maadi",
            "Sakanat El-Maadi", "El-Maadi", "Thakanat El-Maadi", "Zahraa El-Maadi",
            "Nasser (Tahrir)", "Sadat (Tahrir)", "Opera", "Attaba", "Mohammed Naguib",
            "Al-Shohadaa", "Ghamra", "El-Demerdash", "Manshiet El-Sadr", "Kobri El-Qubba",
            "Hammamat El-Qubba", "Saray El-Qubba", "Hadayek El-Zeitoun", "Helmeyet El-Zeitoun",
            "El-Matareyya", "Ain Shams", "Ezbet El-Nakhl (1)", "El-Marg", "New El-Marg",
            "Shubra El-Kheima", "Kolleyet El-Ziraa", "Mezallat", "Khalafawy", "St. Teresa",
            "Rod El-Farag", "Massara", "Cairo University", "Faisal", "Giza",
            "Omm El-Masryeen", "Sakiat Mekky", "El-Mounib",
            "Adly Mansour", "El Haykstep", "Omar Ibn El-Khattab", "Qiaada", "Alf Maskan",
            "Nozha", "Hesham Barakat", "El-Ahram", "Haroun", "Fairous",
            "Abbassia", "Abdou Basha", "El-Geish", "Bab El Shaaria", "Cairo Stadium",
            "Imbaba", "El-Bohy", "Kebaa", "Ring Road", "Kit Kat",
            "Sudan", "Bulaq El-Dakrour", "Ard El-Lewa", "Al-Ahly Club", "Al-Tawfikia",
            "Wadi El-Nil", "Gamaat El-Dowal", "Bein El-Sarayat", "El-Behoos"
    );

    // Price per station gap (EGP)
    private static final double PRICE_PER_STOP = 2.5;
    private static final double BASE_FARE      = 5.0;

    @FXML private ComboBox<String> fromCombo;
    @FXML private ComboBox<String> toCombo;
    @FXML private Label priceLabel;
    @FXML private Button bookButton;
    @FXML private Button backButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fromCombo.getItems().addAll(STATIONS);
        toCombo.getItems().addAll(STATIONS);

        // Auto-calculate price whenever either selection changes
        fromCombo.setOnAction(e -> updatePrice());
        toCombo.setOnAction(e -> updatePrice());
    }

    /** Calculates a distance-based price between the two chosen stations. */
    private void updatePrice() {
        String from = fromCombo.getValue();
        String to   = toCombo.getValue();
        if (from == null || to == null) return;

        if (from.equals(to)) {
            priceLabel.setText("Select different stations");
            priceLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        int indexFrom = STATIONS.indexOf(from);
        int indexTo   = STATIONS.indexOf(to);
        int stops     = Math.abs(indexFrom - indexTo);
        double price  = BASE_FARE + stops * PRICE_PER_STOP;

        priceLabel.setText(String.format("EGP %.2f", price));
        priceLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
    }

    @FXML
    private void handleBook() {
        String from = fromCombo.getValue();
        String to   = toCombo.getValue();

        if (from == null || to == null) {
            showAlert(Alert.AlertType.WARNING, "Incomplete", "Please select both stations.");
            return;
        }
        if (from.equals(to)) {
            showAlert(Alert.AlertType.ERROR, "Invalid", "Departure and destination cannot be the same.");
            return;
        }

        int stops    = Math.abs(STATIONS.indexOf(from) - STATIONS.indexOf(to));
        double price = BASE_FARE + stops * PRICE_PER_STOP;
        String username = SessionManager.getInstance().getCurrentPassenger().getUsername();

        bookButton.setDisable(true);

        Thread bookThread = new Thread(() -> {
            MetroClient client = new MetroClient();
            try {
                client.connect();
                Ticket ticket = client.bookTicket(from, to, price, username);
                SessionManager.getInstance().addTicket(ticket);

                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Booking Confirmed",
                            "Ticket booked!\n" + ticket);
                    fromCombo.setValue(null);
                    toCombo.setValue(null);
                    priceLabel.setText("—");
                    bookButton.setDisable(false);
                });
            } catch (IOException | IllegalStateException e) {
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "Booking Failed", e.getMessage());
                    bookButton.setDisable(false);
                });
            } finally {
                client.disconnect();
            }
        });
        bookThread.setDaemon(true);
        bookThread.start();
    }

    @FXML
    private void handleBack() {
        navigateTo("dashboard.fxml", "Metro — Dashboard");
    }

    // ---------- Helpers ----------

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void navigateTo(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/metro/view/" + fxml));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package org.example.metro.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.metro.model.Ticket;
import org.example.metro.network.MetroClient;
import org.example.metro.util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for my_tickets.fxml — displays the user's booked tickets in a TableView.
 */
public class MyTicketsController implements Initializable {

    @FXML private TableView<Ticket> ticketTable;
    @FXML private TableColumn<Ticket, Integer> idCol;
    @FXML private TableColumn<Ticket, String>  fromCol;
    @FXML private TableColumn<Ticket, String>  toCol;
    @FXML private TableColumn<Ticket, Double>  priceCol;
    @FXML private TableColumn<Ticket, String>  dateCol;
    @FXML private Label statusLabel;
    @FXML private Button backButton;

    private final ObservableList<Ticket> ticketData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // اتأكدي إن الأسماء دي (ticketId, fromStation, إلخ) هي نفس الأسماء الموجودة في كلاس Ticket عندك
        idCol.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        fromCol.setCellValueFactory(new PropertyValueFactory<>("fromStation"));
        toCol.setCellValueFactory(new PropertyValueFactory<>("toStation"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));

        ticketTable.setItems(ticketData);
        loadTickets();
    }

    /** Fetches tickets from the server on a background thread. */
    private void loadTickets() {
        statusLabel.setText("Loading tickets…");
        String username = SessionManager.getInstance().getCurrentPassenger().getUsername();

        Thread fetchThread = new Thread(() -> {
            MetroClient client = new MetroClient();
            try {
                client.connect();
                List<Ticket> tickets = client.getTickets(username);
                SessionManager.getInstance().setSessionTickets(tickets);

                Platform.runLater(() -> {
                    ticketData.setAll(tickets);
                    if (tickets.isEmpty()) {
                        statusLabel.setText("You have no booked tickets yet.");
                    } else {
                        statusLabel.setText("Showing " + tickets.size() + " ticket(s).");
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() ->
                        statusLabel.setText("Could not load tickets: " + e.getMessage()));
            } finally {
                client.disconnect();
            }
        });
        fetchThread.setDaemon(true);
        fetchThread.start();
    }

    @FXML
    private void handleRefresh() {
        ticketData.clear();
        loadTickets();
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
package org.example.metro;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.metro.network.MetroServer;

/**
 * Application entry point
 * Starts the embedded MetroServer as a daemon thread, then launches the JavaFX GUI.
 */
public class

MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Start the server in the background so client can connect immediately
        MetroServer.startInBackground();

        // Give the server a moment to bind to the port
        Thread.sleep(300);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/metro/view/login.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Metro Ticket Booking System");
        primaryStage.setScene(new Scene(root, 520, 480));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.DBConnection;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SplashScreenFormController {
    private final SimpleDoubleProperty progress = new SimpleDoubleProperty(0.0);
    private final SimpleStringProperty statusText = new SimpleStringProperty("Initializing...");
    public Label lblStatus;
    public ProgressBar pgb;

    public void initialize() {
        lblStatus.textProperty().bind(statusText);
        pgb.progressProperty().bind(progress);

        new Thread(() -> {
            try {
                Connection connection = establishDBConnection();
                updateProgress("Stating the app", 1.0);
                Thread.sleep(100);
                DBConnection.getInstance().init(connection);

                AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/LoginForm.fxml"));
                Platform.runLater(() -> {
                    ((Stage) (pgb.getScene().getWindow())).close();
                    Stage stage = new Stage();
                    Scene loginScene = new Scene(root);
                    stage.setScene(loginScene);
                    stage.setTitle("Hello JDBC: Login");
                    stage.setResizable(false);
                    stage.sizeToScene();
                    stage.centerOnScreen();
                    stage.show();
                });
            } catch (Throwable e) {
                e.printStackTrace();
                try {
                    updateProgress("Failed to initialize", 0.8);
                    Thread.sleep(100);

                    updateProgress("Shutting down the app", 1.0);
                    Thread.sleep(100);
                    System.exit(0);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    private Connection establishDBConnection() throws Throwable {
        try {
            updateProgress("Establishing DB Connection", 0.2);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.
                    getConnection("jdbc:mysql://localhost:3306/dep8_hello", "root", "mysql");

            updateProgress("Found an existing DB", 0.5);
            Thread.sleep(100);

            updateProgress("Setting up the connection", 0.8);
            Thread.sleep(100);

            return connection;
        } catch (SQLException e) {
            if (e.getSQLState().equals("42000")) {      // Unknown database
                createDB();
                return DriverManager.
                        getConnection("jdbc:mysql://localhost:3306/dep8_hello", "root", "mysql");
            } else {
                updateProgress("Network failure", 0.8);
                Thread.sleep(100);
                throw new RuntimeException("Network failure");
            }
        }
    }

    private void createDB() throws Throwable {

        updateProgress("Loading DB Script", 0.6);
        try (InputStream is = this.getClass().getResourceAsStream("/assets/dbscript.sql");
             Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306?allowMultiQueries=true", "root", "mysql")) {

            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String dbScript = new String(buffer);
            Thread.sleep(100);

            updateProgress("Executing DB Script", 0.8);
            Statement stm = connection.createStatement();
            stm.execute(dbScript);
            Thread.sleep(100);
        }
    }

    private void updateProgress(String status, double value) {
        Platform.runLater(() -> {
            statusText.set(status);
            progress.set(value);
        });
    }

}

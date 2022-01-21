package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SplashScreenFormController {
    public Label lblStatus;
    public ProgressBar pgb;

    private final SimpleDoubleProperty progress = new SimpleDoubleProperty(0.0);
    private final SimpleStringProperty statusText = new SimpleStringProperty("Initializing...");

    public void initialize() {
        lblStatus.textProperty().bind(statusText);
        pgb.progressProperty().bind(progress);

        new Thread(() -> {
            establishDBConnection();
        }).start();
    }

    private void establishDBConnection() {
        try {
            updateProgress("Establishing DB Connection", 0.2);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.
                    getConnection("jdbc:mysql://localhost:3306/dep8_hello?allowMultiQueries=true", "root", "mysql");

            updateProgress("Found an existing DB", 0.5);
            sleep(100);

            updateProgress("Setting up the connection", 0.8);
            sleep(100);
        } catch (SQLException e) {
            if (e.getSQLState().equals("42000")) {
                createDB();
            } else {
                updateProgress("Network failure", 0.8);
                sleep(100);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            //updateProgress("Done", 1.0);
        }
    }

    private void createDB() {

        updateProgress("Loading DB Script", 0.6);
        try (InputStream is = this.getClass().getResourceAsStream("/assets/dbscript.sql");
             Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306?allowMultiQueries=true", "root", "mysql")) {

            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String dbScript = new String(buffer);
            sleep(100);

            updateProgress("Executing DB Script", 0.8);
            Statement stm = connection.createStatement();
            stm.execute(dbScript);
            sleep(100);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            /* Todo: Handle exception */
            e.printStackTrace();
        }
    }

    private void updateProgress(String status, double value) {
        Platform.runLater(() -> {
            statusText.set(status);
            progress.set(value);
        });
    }

}

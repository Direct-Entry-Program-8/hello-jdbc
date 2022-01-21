package controller;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SplashScreenFormController {
    public Label lblStatus;
    public ProgressBar pgb;

    private SimpleDoubleProperty progress = new SimpleDoubleProperty(0.0);
    private SimpleStringProperty statusText = new SimpleStringProperty("Initializing...");

    public void initialize(){
        lblStatus.textProperty().bind(statusText);
        pgb.progressProperty().bind(progress);

        new Thread(()->{
            establishDBConnection();
        }).start();
    }

    private void establishDBConnection(){
        try {
            updateProgress("Establishing DB Connection", 0.2);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.
                    getConnection("jdbc:mysql://localhost:3306/dep8_hello11?allowMultiQueries=true", "root", "mysql");

            updateProgress("Found an existing DB", 0.5);
            Thread.sleep(100);

            updateProgress("Setting up the connection", 0.8);
            Thread.sleep(100);
        } catch (SQLException  e) {
            if (e.getSQLState().equals("42000")){
                createDB();
            }else{
                updateProgress("Network failure", 0.8);
            }
        } catch (InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }finally{
            updateProgress("Done", 1.0);
        }
    }

    private void createDB(){

    }

    private void updateProgress(String status, double value){
        Platform.runLater(()->{
            statusText.set(status);
            progress.set(value);
        });
    }
    
}

package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SplashScreenFormController {
    public Label lblStatus;
    public ProgressBar pgb;

    private SimpleDoubleProperty progress = new SimpleDoubleProperty(0.0);
    private SimpleStringProperty statusText = new SimpleStringProperty("Loading...");

    public void initialize(){
        lblStatus.textProperty().bind(statusText);
        pgb.progressProperty().bind(progress);
    }

    private void updateProgress(String status, double value){
        Platform.runLater(()->{
            statusText.set(status);
            progress.set(value);
        });
    }


}

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.DBConnection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class AppInitializer extends Application {

    public static void main(String[] args) throws SQLException {
        launch(args);
        DBConnection.getInstance().getConnection().close();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/SplashScreenForm.fxml"));
        Scene loginScene = new Scene(root);
        primaryStage.setScene(loginScene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        loginScene.setFill(Color.TRANSPARENT);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }
}

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class main extends Application {
    static Stage primaryStage;
    AnchorPane rootLayout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Dane");

        initRootLayout();

    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("rootLayout.fxml"));
            rootLayout = loader.load();
            //FXMLLoader loader = new FXMLLoader(ReportMenu.this.getClass().getResource("/fxml/" + controller.getClass().getCanonicalName().substring(18).replaceAll("Controller", "") + ".fxml"));

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Dane");
            primaryStage.show();
            primaryStage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String args[]){launch(args);}
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}

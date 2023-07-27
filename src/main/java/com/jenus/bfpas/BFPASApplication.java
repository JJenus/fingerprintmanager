package com.jenus.bfpas;

import com.jenus.bfpas.controllers.MainController;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import fr.brouillard.oss.cssfx.CSSFX;
import javafx.stage.StageStyle;


import java.io.IOException;

public class BFPASApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        CSSFX.start();

        FXMLLoader fxmlLoader = new FXMLLoader(
                MFXResourceLoader.loadURL("main-view.fxml")
        );

        fxmlLoader.setControllerFactory(c -> new MainController(stage));

        Scene scene = new Scene(fxmlLoader.load());
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);

        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);

        stage.setTitle("Fingerprint Analysis");
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
package com.jenus.bfpas.controllers;

import com.jenus.bfpas.MFXResourceLoader;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoader;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoaderBean;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private MFXFontIcon alwaysOnTopIcon;

    @FXML
    private MFXFontIcon closeIcon;

    @FXML
    private StackPane contentPane;

    @FXML
    private StackPane logoContainer;

    @FXML
    private MFXFontIcon minimizeIcon;

    @FXML
    private VBox navBar;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private MFXScrollPane scrollPane;

    @FXML
    private HBox windowHeader;

    private final Stage stage;
    private double xOffset;
    private double yOffset;
    private final ToggleGroup toggleGroup;

    public MainController(Stage stage) {
        this.stage = stage;
        toggleGroup = new ToggleGroup();
        ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.exit());
        closeIcon.setDescription("fas-xmark");
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) rootPane.getScene().getWindow()).setIconified(true));
        minimizeIcon.setDescription("fas-minus");

        alwaysOnTopIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            boolean newVal = !stage.isAlwaysOnTop();
            alwaysOnTopIcon.pseudoClassStateChanged(PseudoClass.getPseudoClass("always-on-top"), newVal);
            stage.setAlwaysOnTop(newVal);
        });

        windowHeader.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });
        windowHeader.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });

//        Add (initialize) the toggle menu options
        initializeLoader();

        ScrollUtils.addSmoothScrolling(scrollPane);

//        Set application logo

        Image image = new Image(MFXResourceLoader.load("images/icons/bfpas-logo.png"), 64, 64, true, true);
        ImageView logo = new ImageView(image);

        Circle circle = new Circle();
        double radius = 20.0;
        circle.setRadius(radius);
        circle.setCenterX(radius);
        circle.setCenterY(radius);

        logo.setClip(circle);
        logo.setFitWidth(radius * 2);
        logo.setFitHeight(radius * 2);

        logoContainer.getChildren().add(logo);
    }

    private ToggleButton createToggle(String icon, String text) {
        return createToggle(icon, text, 0);
    }

    private ToggleButton createToggle(String icon, String text, double rotate) {
        MFXIconWrapper wrapper = new MFXIconWrapper(icon, 24, 32);
        MFXRectangleToggleNode toggleNode = new MFXRectangleToggleNode(text, wrapper);
        toggleNode.setAlignment(Pos.CENTER_LEFT);
        toggleNode.setMaxWidth(Double.MAX_VALUE);
        toggleNode.setToggleGroup(toggleGroup);
        if (rotate != 0) wrapper.getIcon().setRotate(rotate);
        return toggleNode;
    }

    private void initializeLoader() {
        MFXLoader loader = new MFXLoader();

        loader.addView(MFXLoaderBean.of("Enrollment", MFXResourceLoader.loadURL("views/enrollment-view.fxml"))
                .setBeanToNodeMapper(() -> createToggle("fas-fingerprint", "Enrollment"))
                .setDefaultRoot(true).get());

        loader.addView(MFXLoaderBean.of("Identify", MFXResourceLoader.loadURL("views/identify-view.fxml"))
                .setBeanToNodeMapper(() -> createToggle("fas-circle-check", "Identify"))
                .get());

        loader.addView(MFXLoaderBean.of("Analysis", MFXResourceLoader.loadURL("views/analysis-view.fxml"))
                .setBeanToNodeMapper(() -> createToggle("fas-chart-simple", "Analysis"))
                .get());
//        fa-fingerprint fas-circle-dot

        loader.setOnLoadedAction(beans -> {
            List<ToggleButton> nodes = beans.stream()
                    .map(viewBean -> {
                        ToggleButton toggle = (ToggleButton) viewBean.getBeanToNodeMapper().get();
                        toggle.setOnAction(event -> contentPane.getChildren().setAll(viewBean.getRoot()));
                        if (viewBean.isDefaultView()) {
                            contentPane.getChildren().setAll(viewBean.getRoot());
                            toggle.setSelected(true);
                        }
                        return toggle;
                    })
                    .toList();
            navBar.getChildren().setAll(nodes);
        });
        loader.start();
    }
}
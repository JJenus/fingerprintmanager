package com.jenus.bmfs.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class EnrollmentController implements Initializable {

    @FXML
    private MFXButton btnCapture;

    @FXML
    private MFXIconWrapper iconHandIndex;

    @FXML
    private MFXIconWrapper iconHandLittle;

    @FXML
    private MFXIconWrapper iconHandMiddle;

    @FXML
    private MFXIconWrapper iconHandRing;

    @FXML
    private MFXIconWrapper iconHandThumb;

    @FXML
    private ImageView imgHand;

    @FXML
    private MFXTextField inputAge;

    @FXML
    private MFXTextField inputFirstname;

    @FXML
    private MFXTextField inputLastname;

    @FXML
    private MFXTextField inputUsername;

    @FXML
    private Text txtFingerprintInfo;

    @FXML
    void checkExistingUser(KeyEvent event) {

    }

    @FXML
    void resetForm(ActionEvent event) {

    }

    @FXML
    void saveUserData(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String iconDes = "fas-fingerprint";
        MFXFontIcon thumbIcon = new MFXFontIcon(iconDes);
        thumbIcon.getStyleClass().add("bs-primary-text");

        iconHandThumb.setIcon(thumbIcon);

        iconHandIndex.setIcon(new MFXFontIcon(iconDes));
        iconHandIndex.getIcon().getStyleClass().add("bs-danger"); //Change active color like this

        iconHandMiddle.setIcon(new MFXFontIcon(iconDes));
        iconHandRing.setIcon(new MFXFontIcon(iconDes));
        iconHandLittle.setIcon(new MFXFontIcon(iconDes));

    }
}
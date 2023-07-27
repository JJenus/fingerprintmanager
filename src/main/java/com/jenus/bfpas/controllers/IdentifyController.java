package com.jenus.bfpas.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class IdentifyController implements Initializable {

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
    private Text txtFingerprintInfo;

    @FXML
    void captureFingerprint(ActionEvent event) {

    }

//    Class variables


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        String iconDes = "fas-fingerprint";
        iconHandThumb.setIcon(new MFXFontIcon(iconDes));

        iconHandIndex.setIcon(new MFXFontIcon(iconDes));
//        iconHandIndex.getIcon().getStyleClass().add("bs-success"); //Change active color like this

        iconHandMiddle.setIcon(new MFXFontIcon(iconDes));
        iconHandMiddle.getIcon().getStyleClass().add("bs-success");

        iconHandRing.setIcon(new MFXFontIcon(iconDes));
        iconHandLittle.setIcon(new MFXFontIcon(iconDes));
    }
}

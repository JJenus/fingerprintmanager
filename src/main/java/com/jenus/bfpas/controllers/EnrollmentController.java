package com.jenus.bfpas.controllers;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.jenus.bfpas.models.Finger;
import com.jenus.bfpas.models.Person;
import com.jenus.bfpas.utils.FingerprintUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EnrollmentController implements Initializable {

    @FXML
    private MFXButton btnCapture;

    @FXML
    private MFXButton btnCaptureUserImage;
    @FXML
    private MFXButton btnUploadUserImage;

    @FXML
    private MFXComboBox<String> comboSelectFinger;

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
    private Text txtUsernameWarning;

    @FXML
    private ImageView imgUserImage;

    private Webcam webcam;
    private ScheduledExecutorService executor;
    private Person person;

    @FXML
    void captureUserImageEvent(ActionEvent event) {
        if (btnCaptureUserImage.getText()
                .equalsIgnoreCase("Start camera") || !webcam.isOpen()){
            btnCaptureUserImage.setText("Capture");
            executor.scheduleAtFixedRate(this::captureLoop, 0, 50, TimeUnit.MILLISECONDS);
        } else {
            this.stopCamera();
        }
    }

    private void captureLoop() {
        try {

            if (webcam == null) {
                webcam = Webcam.getDefault();
                webcam.setViewSize(WebcamResolution.VGA.getSize());
                webcam.open();
            }

            Image image = SwingFXUtils.toFXImage(webcam.getImage(), null);

            Platform.runLater(() -> imgUserImage.setImage(image));

        } catch (Exception any){
//            System.out.println("Camera error: "+any.getMessage());
        }
    }

    public void stopCamera() {
        // Clean up and close the camera when the application is closed
        try {
            if (webcam != null && webcam.isOpen()) {
                webcam.close();
                webcam = null;
            }

            // Stop the capturing thread
            if (executor != null) {
                executor.shutdown();
            }
        } catch (Exception any){
            System.out.println("Unable to close camera: "+any.getMessage());
        }
    }

//    Initializer
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        Setup user image
        double radius = imgUserImage.getFitHeight()/2;
        Circle circle = new Circle();

        circle.setRadius(radius);
        circle.setCenterX(radius);
        circle.setCenterY(radius);

        imgUserImage.setClip(circle);

        btnUploadUserImage.setText(null);

//        Setup finger
        String iconDes = "fas-fingerprint";

        iconHandThumb.setIcon(new MFXFontIcon(iconDes));

        iconHandIndex.setIcon(new MFXFontIcon(iconDes));
//        iconHandIndex.getIcon().getStyleClass().add("bs-danger"); //Change active color like this

        iconHandMiddle.setIcon(new MFXFontIcon(iconDes));
        iconHandRing.setIcon(new MFXFontIcon(iconDes));
        iconHandLittle.setIcon(new MFXFontIcon(iconDes));

        String[] fingers = {"Thumb", "Index", "Middle", "Ring", "Little"};

        comboSelectFinger.getItems().addAll(fingers);

        executor = Executors.newSingleThreadScheduledExecutor();
    }

    @FXML
    void resetForm(ActionEvent event) {

    }

    @FXML
    void saveUserData(ActionEvent event) {
        if (!this.isValidForm())
            return;
        // Do the rest stuff

        person = new Person();

        String capitalizedFName = inputFirstname.getText().substring(0, 1).toUpperCase()
                +  inputFirstname.getText().substring(1).toLowerCase();
        String capitalizedLName = inputLastname.getText().substring(0, 1).toUpperCase()
                +  inputFirstname.getText().substring(1).toLowerCase();

        person.setFirstname(capitalizedFName);
        person.setLastname(capitalizedLName);
        person.setUsername(inputUsername.getText());
        person.setAge(Integer.parseInt(inputAge.getText()));

    }

    boolean isValidForm(){
        boolean isValid = true;
        String invalidClass = "is-invalid";

        if (inputUsername.getText().isBlank() || !inputUsername.isValid()){
            isValid = false;
            inputUsername.getStyleClass().add(invalidClass);
        } else if (inputUsername.getText().length() < 4){
            txtUsernameWarning.setText("Username is too short");
            txtUsernameWarning.setVisible(true);
            isValid = false;
            inputUsername.getStyleClass().add(invalidClass);
        }

        if (inputFirstname.getText().isBlank() || !inputFirstname.isValid()){
            isValid = false;
            inputFirstname.getStyleClass().add(invalidClass);
        }
        if (inputLastname.getText().isBlank() || !inputLastname.isValid()){
            isValid = false;
            inputLastname.getStyleClass().add(invalidClass);
        }
        if (inputAge.getText().isBlank() || !inputAge.isValid()){
            isValid = false;
            inputAge.getStyleClass().add(invalidClass);
        } else {
            try {
                int age = Integer.parseInt(inputAge.getText());
            } catch (Exception err){
                inputAge.getStyleClass().add(invalidClass);
            }
        }

        return isValid;
    }

    @FXML
    void removeInvalidClassEvent(KeyEvent event) {
        String invalidClass = "is-invalid";
        MFXTextField input = (MFXTextField) event.getSource();

        input.getStyleClass().remove(invalidClass);

        if (input.getFloatingText().length() > 3
                && input.getFloatingText().equalsIgnoreCase("username"))
        {
            if (userExist(input.getText())){
                txtUsernameWarning.setText("Username exists");
                txtUsernameWarning.setVisible(true);
            }
            else {
                txtUsernameWarning.setVisible(false);
            }
        }
        System.out.println(input.getText());
    }

    boolean userExist(String username){
        return username.equalsIgnoreCase("jjenus");
    }

    @FXML
    void saveUserFingerprints(ActionEvent event) {

    }

    @FXML
    void selectFinger(ActionEvent event) {
        String selected = comboSelectFinger.getSelectedItem();
        String selectedColor = "bs-info";

        iconHandThumb.getIcon().getStyleClass().remove(selectedColor);
        iconHandIndex.getIcon().getStyleClass().remove(selectedColor);
        iconHandMiddle.getIcon().getStyleClass().remove(selectedColor);
        iconHandRing.getIcon().getStyleClass().remove(selectedColor);
        iconHandLittle.getIcon().getStyleClass().remove(selectedColor);

        if (selected.equalsIgnoreCase("Thumb")){
            iconHandThumb.getIcon().getStyleClass().add(selectedColor);
        }
        else if (selected.equalsIgnoreCase("Index")){
            iconHandIndex.getIcon().getStyleClass().add(selectedColor);
        }
        else if (selected.equalsIgnoreCase("Middle")){
            iconHandMiddle.getIcon().getStyleClass().add(selectedColor);
        }
        else if (selected.equalsIgnoreCase("Ring")){
            iconHandRing.getIcon().getStyleClass().add(selectedColor);
        }
        else {
            iconHandLittle.getIcon().getStyleClass().add(selectedColor);
        }

        System.out.println(selected);
    }

    private FingerprintUtil fingerprintUtil;
    @FXML
    void captureFingerprintEvent(ActionEvent event) {
        if (comboSelectFinger.getSelectedIndex() < 0){
            txtFingerprintInfo.setText("Select a finger");
            txtFingerprintInfo.getStyleClass().add("bs-danger");
            return;
        }

        txtFingerprintInfo.getStyleClass().remove("bs-danger");
        fingerprintUtil =  new FingerprintUtil();

        txtFingerprintInfo.textProperty().bindBidirectional(fingerprintUtil.MESSAGE);

        fingerprintUtil.MESSAGE.addListener(((observableValue, oldValue, newVal) -> {
            if (newVal.equalsIgnoreCase("Enroll successful")){
                setFinger(fingerprintUtil.getFingerTemplate(), fingerprintUtil.getImage());
                btnCapture.setDisable(false);
            }
            txtFingerprintInfo.getStyleClass().remove("bs-success");
            txtFingerprintInfo.getStyleClass().remove("bs-danger");
            txtFingerprintInfo.getStyleClass().remove("bs-warning");

            if (fingerprintUtil.getStatusCode() == 0) {
                txtFingerprintInfo.getStyleClass().add("bs-success");
            }
            else if (fingerprintUtil.getStatusCode() == 1) {
                txtFingerprintInfo.getStyleClass().add("bs-danger");
                btnCapture.setDisable(false);
            }
            else if (fingerprintUtil.getStatusCode() == 2)
                txtFingerprintInfo.getStyleClass().add("bs-warning");
        }));

        fingerprintUtil.startEnrollment();
        btnCapture.setDisable(true);
    }

    void setFinger(byte[] temp, Image image){
        String finger = comboSelectFinger.getSelectedItem();

        for(Finger pFinger: person.getRightHand().getFingers()){
            if (pFinger.getName().equalsIgnoreCase(finger)){
                pFinger.setImageBytes(temp);
                pFinger.setImage(image);
                break;
            }
        }
    }
}
module com.jenus.bmfs {
//    requires javafx;
//    requires javafx;

    requires MaterialFX;
    requires fr.brouillard.oss.cssfx;
    requires webcam.capture;
    requires zkfinger;

    opens com.jenus.bfpas to javafx.fxml;
    exports com.jenus.bfpas;
    exports com.jenus.bfpas.controllers;
    opens com.jenus.bfpas.controllers to javafx.fxml;
}


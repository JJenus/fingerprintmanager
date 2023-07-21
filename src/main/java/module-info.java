module com.jenus.bmfs {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires MaterialFX;
    requires fr.brouillard.oss.cssfx;
//    requires eu.hansolo.tilesfx;

    opens com.jenus.bmfs to javafx.fxml;
    exports com.jenus.bmfs;
    exports com.jenus.bmfs.controllers;
    opens com.jenus.bmfs.controllers to javafx.fxml;
}
module com.chalwk {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    opens com.chalwk to javafx.fxml;
    opens com.chalwk.model to com.fasterxml.jackson.databind;
    exports com.chalwk;
    exports com.chalwk.model;
}
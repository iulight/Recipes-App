module com.retete {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.json;

    opens com.retete to javafx.fxml;
    opens com.retete.controller to javafx.fxml;
    opens com.retete.model to javafx.base;

    exports com.retete;
    exports com.retete.model;
    exports com.retete.controller;
    exports com.retete.service;
}

module com.retete {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.json;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.retete to javafx.fxml;
    opens com.retete.controller to javafx.fxml;
    opens com.retete.model to javafx.base;

    exports com.retete;
    exports com.retete.model;
    exports com.retete.controller;
    exports com.retete.service;
}

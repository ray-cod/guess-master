module com.guessing.gamemaster {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.guessing.gamemaster to javafx.fxml;
    opens com.guessing.gamemaster.controllers to javafx.fxml;
    opens com.guessing.gamemaster.utils to javafx.base;
    exports com.guessing.gamemaster;
}
module com.guessing.gamemaster {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.guessing.gamemaster to javafx.fxml;
    exports com.guessing.gamemaster;
}
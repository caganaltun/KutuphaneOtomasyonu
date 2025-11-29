module cagan.kutuphane {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

 requires mysql.connector.j;

    opens cagan.controller to javafx.fxml;

    opens cagan.model to javafx.base;

    exports cagan;
}
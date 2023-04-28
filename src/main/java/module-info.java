module com.example.capstone {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.knowm.xchart;
    requires java.desktop;

    opens com.example.capstone to javafx.fxml;
    opens com.example.capstone.Controllers.ClosedSystem to javafx.fxml;
    exports com.example.capstone;
    exports com.example.capstone.Controllers;
    exports com.example.capstone.Controllers.ClosedSystem;
    exports com.example.capstone.Controllers.OpenSystem;
    exports com.example.capstone.Models;
    exports com.example.capstone.Views;
    exports com.example.capstone.Models.Logic;
}
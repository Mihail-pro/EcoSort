module md.ecosort.ecosort {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.xml.dom;


    opens md.ecosort.ecosort to javafx.fxml;
    exports md.ecosort.ecosort;
}
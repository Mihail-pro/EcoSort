module md.ecosort.ecosort {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.xml.dom;
    requires jdk.compiler;
    requires java.sql;
    // Driverul MySQL nu are module-info: numele automat derivă din numele jar-ului.
    // Trebuie cerut explicit ca să fie încărcat în graful de module (DriverManager îl găsește prin ServiceLoader).
    requires mysql.connector.j;
    requires org.bouncycastle.provider;


    opens md.ecosort.ecosort to javafx.fxml;
    exports md.ecosort.ecosort;
}
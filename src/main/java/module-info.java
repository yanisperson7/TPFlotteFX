module org.example.tpflottefx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires bcrypt;
    requires tools.jackson.databind;


    opens org.example.tpflottefx to javafx.fxml;
    exports org.example.tpflottefx;
}
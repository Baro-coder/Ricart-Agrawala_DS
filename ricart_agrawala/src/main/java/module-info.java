module pl.edu.wat.sr.ricart_agrawala {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;

    opens pl.edu.wat.sr.ricart_agrawala to javafx.fxml;
    exports pl.edu.wat.sr.ricart_agrawala;
}
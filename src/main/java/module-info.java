module ru.shchelkin.geometricmethod {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens ru.shchelkin.geometricmethod to javafx.fxml;
    exports ru.shchelkin.geometricmethod;
}
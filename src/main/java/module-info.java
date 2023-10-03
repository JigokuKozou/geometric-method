module ru.shchelkin.geometricmethod {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens ru.shchelkin.geometricmethod to javafx.fxml;
    exports ru.shchelkin.geometricmethod;
    exports ru.shchelkin.geometricmethod.util;
    opens ru.shchelkin.geometricmethod.util to javafx.fxml;
    exports ru.shchelkin.geometricmethod.model;
    opens ru.shchelkin.geometricmethod.model to javafx.fxml;
}
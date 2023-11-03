package pl.edu.wat.sr.ricart_agrawala;

import javafx.beans.property.StringProperty;

import java.util.ResourceBundle;

public class StringPropertyBind {
    private final StringProperty property;
    private final String resourceKey;

    public StringPropertyBind(StringProperty property, String resourceKey, ResourceBundle resourceBundle) {
        this.property = property;
        this.resourceKey = resourceKey;
        update(resourceBundle);
    }

    public void update(ResourceBundle resourceBundle) {
        property.set(resourceBundle.getString(resourceKey));
    }
}

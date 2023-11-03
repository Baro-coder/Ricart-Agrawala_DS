package pl.edu.wat.sr.ricart_agrawala;

import java.util.ResourceBundle;

public class StringsResourceController {
    private static StringsResourceController instance;
    private ResourceBundle resourceBundle = null;

    public static StringsResourceController getInstance() {
        if (instance == null) {
            instance = new StringsResourceController();
        }
        return instance;
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public String getText(String key) {
        if (resourceBundle == null) {
            throw new NullPointerException("`resourceBundle` is null!");
        }
        return resourceBundle.getString(key);
    }
}

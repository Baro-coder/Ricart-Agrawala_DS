package pl.edu.wat.sr.ricart_agrawala;

import pl.edu.wat.sr.ricart_agrawala.core.log.LogController;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class StringsResourceController {
    public enum Lang {
        PL,
        EN
    }

    public static final String RESOURCE_STRINGS_PATH = "pl.edu.wat.sr.ricart_agrawala.strings";
    private static StringsResourceController instance;
    private final LogController logController;
    private ResourceBundle resourceBundle = null;

    private StringsResourceController() {
        logController = LogController.getInstance();
    }

    public static StringsResourceController getInstance() {
        if (instance == null) {
            instance = new StringsResourceController();
        }
        return instance;
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }
    public void setResourceBundleLang(Lang lang) {
        this.resourceBundle = ResourceBundle.getBundle(RESOURCE_STRINGS_PATH, Locale.forLanguageTag(lang.name().toLowerCase()));
        logController.logInfo(this.getClass().getName(), String.format("%s : %s", getText("log_info_lang_changed"), lang.name()));
    }
    public ArrayList<String> getLanguagesTagsStrings() {
        ArrayList<String> arr = new ArrayList<>();
        for (Lang lang : Lang.values()) {
            arr.add(lang.name().toLowerCase());
        }
        return arr;
    }
    public Lang getLanguageTagByCountryCode(String countryCode) {
        for (Lang lang : Lang.values()) {
            if (countryCode.equalsIgnoreCase(lang.name())) {
                return lang;
            }
        }
        return null;
    }
    public String getText(String key) {
        if (resourceBundle == null) {
            throw new NullPointerException("`resourceBundle` is null!");
        }
        return resourceBundle.getString(key);
    }
}

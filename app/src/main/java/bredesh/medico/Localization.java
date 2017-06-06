package bredesh.medico;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

import bredesh.medico.Camera.LocalDBManager;

/**
 * Created by ophir on 6/6/2017.
 */


public class Localization {
    public static final String PREF_SET = "MEDICO_PREF";
    public static final String LANG_PREF = "LANG";
    private static Locale _deviceDefaultLocale = null;
    static ContextWrapper currentContextWrapper = null;

    public static String init(ContextWrapper activity, LocalDBManager dbManager)
    {
        currentContextWrapper = activity;
        if (_deviceDefaultLocale == null) {
            Locale deviceDefaultLocale = activity.getResources().getConfiguration().locale;
            _deviceDefaultLocale = deviceDefaultLocale;
        }
        return setLanguageFromPrefs(activity, dbManager);

    }


    private static String setLanguageFromPrefs(ContextWrapper activity, LocalDBManager dbManager) {
        String language = dbManager.getLang();
        if (language != null) {
            setLanguage(language, activity);
            return language;
        }
        return "default";
    }

    public static void setLanguage(String language, ContextWrapper activity)
    {
        Locale locale = language != null && language.compareTo("") != 0 ? new Locale(language) : _deviceDefaultLocale;
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config,
                activity.getBaseContext().getResources().getDisplayMetrics());
        activity.getResources().getConfiguration().setLayoutDirection(locale);
    }

    public static void saveLanguageInPrefs(String language, LocalDBManager dbManager)
    {
        dbManager.setLang(language);
    }
}

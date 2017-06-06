package bredesh.medico;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by ophir on 6/6/2017.
 */


public class Localization {
    public static final String PREF_SET = "MEDICO_PREF";
    public static final String LANG_PREF = "LANG";
    private static Locale _deviceDefaultLocale = null;

    public static String init(Activity activity)
    {
        if (_deviceDefaultLocale == null) {
            Locale deviceDefaultLocale = activity.getResources().getConfiguration().locale;
            _deviceDefaultLocale = deviceDefaultLocale;
        }
        return setLanguageFromPrefs(activity);
    }

    private static String setLanguageFromPrefs(Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences(PREF_SET, 0);
        if (settings != null) {
            String language = settings.getString(LANG_PREF, null);
            if (language != null) {
                setLanguage(language, activity);
                return language;
            }
        }
        return "default";
    }

    public static void setLanguage(String language, Activity activity)
    {
        Locale locale = language != null && language.compareTo("") != 0 ? new Locale(language) : _deviceDefaultLocale;
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config,
                activity.getBaseContext().getResources().getDisplayMetrics());
        activity.getResources().getConfiguration().setLayoutDirection(locale);
        saveLanguageInPrefs(language, activity);
    }

    public static void saveLanguageInPrefs(String language, Activity activity)
    {
        SharedPreferences settings = activity.getSharedPreferences(PREF_SET, 0);
        SharedPreferences.Editor editor = settings.edit();
        if (language != null)
            editor.putString(LANG_PREF, language);
        else
            editor.remove(LANG_PREF);

        // Commit the edits!
        editor.commit();
    }
}

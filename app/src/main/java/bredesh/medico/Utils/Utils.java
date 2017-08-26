package bredesh.medico.Utils;

import android.content.res.Resources;
import android.content.res.TypedArray;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by ophir on 6/9/2017.
 */

public class Utils {
    private static DecimalFormat df = new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    static void Utils()
    {
        df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
    }

    public static String right(String s, int charsCount)
    {
        return s.substring(s.length() - charsCount);
    }

    public static String floatToString(float f)
    {
        return df.format(f);
    }

    public static String noToString(int input, int charsCount)
    {
        String zeros = String.format(Locale.ENGLISH, "%0" + charsCount + "d", 0);
        return right(zeros + Integer.toString(input), charsCount);
    }

    public static String stringOrFromResource(Resources rscs, String idOrString)
    {
        int resId = 0;
        try {
            resId = Integer.parseInt(idOrString);
            return rscs.getString(resId);
        }
        catch (Exception ex)
        {
            return idOrString;
        }
    }

    public static String findResourceIdInResourcesArray(Resources rscs, int arrayResId, String value)
    {
        TypedArray typedArray = rscs.obtainTypedArray(arrayResId);
        for (int i=0; i< typedArray.length(); i++) {
            if (value.equals(typedArray.getString(i)))
                return Integer.toString(typedArray.getResourceId(i, 0));
        }
        return value;
    }

    public static int findIndexInResourcesArray(Resources rscs, int arrayResId, String value)
    {
        CharSequence[] stringsFromResources = rscs.getStringArray(arrayResId);

        value = stringOrFromResource(rscs, value);
        for (int i=0; i< stringsFromResources.length; i++) {
            if (value.equals(stringsFromResources[i]))
                return i;
        };
        return 0;
    }

}

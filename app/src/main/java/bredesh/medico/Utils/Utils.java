package bredesh.medico.Utils;

/**
 * Created by ophir on 6/9/2017.
 */

public class Utils {
    public static String right(String s, int charsCount)
    {
        return s.substring(s.length() - charsCount);
    }

    public static String noToString(int input, int charsCount)
    {
        String zeros = String.format("%0" + charsCount + "d", 0);
        return right(zeros + Integer.toString(input), charsCount);
    }
}

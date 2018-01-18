package bredesh.medico.DAL;

/**
 * Created by ophir on 1/19/2018.
 */

public class ConvertionTable {
    public int newCode;
    public String[] possibleValues;

    public ConvertionTable(int newCode, String [] possibleValues) {
        this.newCode = newCode;
        this.possibleValues = possibleValues;
    }

    static int getValue (ConvertionTable [] map, String oldValue, int defaultVal) {
        for (int i=0; i< map.length; i++) {
            for (int j=0; j< map[i].possibleValues.length; j++) {
                if (oldValue.equals(map[i].possibleValues[j]))
                    return map[i].newCode;
            }
        }
        return defaultVal;
    }


}

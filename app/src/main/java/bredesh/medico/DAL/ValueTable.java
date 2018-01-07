package bredesh.medico.DAL;


/**
 * Created by ophir on 1/6/2018.
 */

public class ValueTable {
    private ValueConstant[] values;
    public int defaultValue;

    public ValueTable(ValueConstant[] values, int defaultValue) {
        this.values = values;
        this.defaultValue = defaultValue;
    }

    public int getStringCodeFromDBCode(int dbCode) {
        for (ValueConstant val : this.values) {
            if (val.dbCode == dbCode)
                return val.stringCode;
        }
        return -1;
    }

    public int getDBCodeFromStringCode(int stringCode) {
        for (ValueConstant val : this.values) {
            if (val.stringCode == stringCode)
                return val.dbCode;
        }
        return -1;
    }

}

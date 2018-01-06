package bredesh.medico.DAL;


/**
 * Created by ophir on 1/6/2018.
 */

public final class ValueConstant {
    public int dbCode;
    public String stringCode;

    public ValueConstant(int dbCode, int stringCode) {
        this.dbCode = dbCode;
        this.stringCode = Integer.toString(stringCode);
    }
}

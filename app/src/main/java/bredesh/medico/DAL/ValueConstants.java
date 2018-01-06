package bredesh.medico.DAL;

import bredesh.medico.R;

/**
 * Created by ophir on 1/6/2018.
 */

public class ValueConstants {
    public static ValueTable DrugDosage = new ValueTable ( new ValueConstant []{
        new ValueConstant(1, R.string.medicine_dosage_type_tab),
        new ValueConstant(2, R.string.medicine_dosage_type_capsule),
        new ValueConstant(3, R.string.medicine_dosage_type_ml_cc),
        new ValueConstant(4, R.string.medicine_dosage_type_units),
        new ValueConstant(5, R.string.medicine_dosage_drops),
        new ValueConstant(6, R.string.medicine_dosage_type_patch),
        new ValueConstant(7, R.string.medicine_dosage_type_supp),
        new ValueConstant(8, R.string.medicine_dosage_other),
    }, 1);

    public static ValueTable DrugDosageNotes = new ValueTable(new ValueConstant[]  {
            new ValueConstant(1, R.string.medicine_usage_notes_none),
            new ValueConstant(2, R.string.medicine_usage_notes_before_meal),
            new ValueConstant(3, R.string.medicine_usage_notes_after_meal),
            new ValueConstant(4, R.string.medicine_usage_notes_2_hours_before_meal),
            new ValueConstant(5, R.string.medicine_usage_notes_2_hours_after_meal),
    }, 1);

    public static ValueTable ExerciseRepetitionType = new ValueTable(  new ValueConstant[] {
            new ValueConstant(1, R.string.repetition_type_repetitions),
            new ValueConstant(2, R.string.repetition_type_minutes),
            new ValueConstant(3, R.string.repetition_type_seconds),
    }, 1);

}

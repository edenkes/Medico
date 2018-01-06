package bredesh.medico.Fragments.DataMediGo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.DAL.ValueConstants;
import bredesh.medico.R;
import bredesh.medico.Utils.Utils;

/**
 * Created by edenk on 12/10/2017.
 */

public class ExerciseDa extends DataGeneral implements IRemoveLastAlert{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_exercises_data);

        setFindView();

        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource (this, R.array.repetition_types, R.layout.spinner_item );
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spRepetitionType.setAdapter(adapterType);

        etRepeats.setText(Integer.toString(1));

        setOnCreate();
    }

    @Override
    protected void setExistingData(Intent intent) {
        oldDataName = intent.getStringExtra("dataName");
        oldTimes = intent.getStringExtra("dataTime");
        oldDays = intent.getIntArrayExtra("dataDays");
        oldUriStringVideo = intent.getStringExtra("dataUriVideo");
        oldRepeats = intent.getIntExtra("dataRepeats",1);
        String repetitionTypeStr = intent.getStringExtra("dataRepetitionType");
        int repetitionType = ValueConstants.ExerciseRepetitionType.defaultValue;

        etDataName.setText(oldDataName);

        String times = oldTimes;
        String[] timeAL = null;
        arrayList = new ArrayList<>();
        if (times != null && !times.contentEquals("")) {
            timeAL = times.split(resources.getString(R.string.times_splitter));
            Collections.addAll(arrayList, timeAL);
        }

//        int repeats = oldRepeats;
        etRepeats.setText(Integer.toString(oldRepeats));
//        etRepeats.setText(Integer.toString(repeats));

        if (repetitionTypeStr != null)
            repetitionType = Integer.parseInt(repetitionTypeStr);
        oldRepetitionType = repetitionType;
        int index = Utils.findIndexInResourcesArray(resources, R.array.repetition_types, ValueConstants.ExerciseRepetitionType.getStringCodeFromDBCode(repetitionType));
        spRepetitionType.setSelection(index);

//        int[] days = oldDays;
        for (int i=0; i< 7; i++)
//            selectedDays[i] = days[i] != 0;
            selectedDays[i] = oldDays[i] != 0;
        updateSelectedDays();
        this.setAddAlertsButtons(timeAL == null || timeAL.length == 0);
    }

    @Override
    protected String getDeletedMessage() {
        return  getResources().getString(R.string.exercise_deleted);
    }

    @Override
    protected void setNewData() {
        arrayList = new ArrayList<>();
        for(int i=0; i<selectedDays.length; i++) {
            selectedDays[i] = true;
            oldDays[i] = 1;
        }
    }

    @Override
    protected CharSequence getDeletedMessageConfirm() {
        return resources.getString(R.string.delete_exercise_confirm);
    }

    @Override
    protected TimeAdapterRecyclerMedGo getNewTimeAdapter(Button[] buttons) {
        return new TimeAdapterRecyclerMedGo(ExerciseDa.this,arrayList, buttons, this);
    }

    @Override
    protected void confirm(boolean askBeforeSave) {
        if(etDataName.getText().toString().length() == 0 && !askBeforeSave)
            Toast.makeText(getApplicationContext(), resources.getString(R.string.name_too_short), Toast.LENGTH_SHORT).show();
        else {
            if (etRepeats.getText().toString().length() == 0 && !askBeforeSave)
                Toast.makeText(getApplicationContext(), R.string.exercise_repeats_mandatory, Toast.LENGTH_LONG).show();
            else {
                if (etDataName.getText().toString().length() <= Max_Size) {
                    int[] days_to_alert = new int[selectedDays.length];
                    for (int i = 0; i < days_to_alert.length; i++) {
                        if (selectedDays[i])
                            days_to_alert[i] = 1;
                        else
                            days_to_alert[i] = 0;
                    }
                    int repeats = Integer.parseInt(etRepeats.getText().toString());
                    String times = "";
                    Collections.sort(arrayList);
                    for (int i = 0; i < arrayList.size(); i++)
                        times = times + (i > 0 ? getResources().getString(R.string.times_splitter) : "") + arrayList.get(i);

                    String exerciseName = etDataName.getText().toString();
                    int repetitionTypeToWrite = ValueConstants.ExerciseRepetitionType.getDBCodeFromStringCode(
                            Utils.findResourceIdInResourcesArray(resources, R.array.repetition_types, spRepetitionType.getSelectedItem().toString())
                    );

                    if (askBeforeSave)
                    {
                        boolean dataNotChanged =
                                oldDataName.equals(exerciseName) &&
                                        oldRepeats == repeats &&
                                        oldTimes.equals(times) &&
                                        oldRepetitionType == repetitionTypeToWrite &&
                                        Arrays.equals(oldDays, days_to_alert) &&
                                        (oldUriStringVideo == null ? uriStringVideo == null : oldUriStringVideo.equals(uriStringVideo)) &&
                                        (oldAlertSoundUriString == null ? alertSoundUriString == null : oldAlertSoundUriString.equals(alertSoundUriString));
                        if (dataNotChanged)
                            finish();
                        else
                            askUserBeforeSave();
                        return;
                    }

                    Bundle params = new Bundle();
                    if (dataId != NewData) {
                        db.updateRow(dataId, exerciseName, times, repeats, repetitionTypeToWrite, uriStringVideo,uriStringImage, days_to_alert, alertSoundUriString);
                        mFirebaseAnalytics.logEvent("Excercise_updated", params);
                    }
                    else {
                        db.addAlert(exerciseName, MedicoDB.KIND.Exercise, times, repeats, repetitionTypeToWrite, uriStringVideo, uriStringImage, days_to_alert, alertSoundUriString);
                        mFirebaseAnalytics.logEvent("Excercise_added", params);
                    }
                    finish();
                } else
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.name_too_long), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

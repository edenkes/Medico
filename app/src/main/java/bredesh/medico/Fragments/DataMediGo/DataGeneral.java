package bredesh.medico.Fragments.DataMediGo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.R;
import bredesh.medico.Utils.Utils;

/**
 * Created by edenk on 12/10/2017.
 */
interface IRemoveLastAlert {   void OnRemoveLastAlert();}

public abstract class DataGeneral extends AppCompatActivity {
    protected static final int REQUEST_VIDEO_CAPTURE = 1;
    protected static final int CHOOSE_ALERT_SOUND = 5;

    protected EditText etDataName, etNotes;
    protected ArrayList<String> arrayList;
    protected TextView lblSelectedDays;
    protected Button btDelete, btConfirm;

    protected AlertDialog dialogDays;
    // array to keep the selected days
    protected final boolean[] selectedDays = new boolean[7];
    protected final boolean[] newSelectedDays = new boolean[7];
    protected final Button[] alertPlanButtons = new Button[5];
    protected View mainView;
    protected MedicoDB db;
    protected final int Max_Size = 16, NewData = -6;
    protected Resources resources;
    protected int dataId, oldRepeats = 1;
    protected ImageButton btPlayStill, btPlayVideo, btChooseSound;
    protected EditText etAmount, etRepeats;
    protected Spinner spType, spSpecial, spRepetitionType;

    protected String uriString, alertSoundUriString;
    protected TimeAdapterRecyclerMedGo timeAdapter = null;
    protected Button btAddAlert;
    protected TextView lbAddMultiAlert;
    protected FirebaseAnalytics mFirebaseAnalytics;
    protected Boolean isVideo = false;

    protected String oldDataName = "";
    protected String oldNotes = "";
    protected String oldTimes = "";
    protected String oldAlertSoundUriString = null;
    protected String oldDosageType = "";
    protected String oldSpecialNotes = "";
    protected String oldAmount = "1";
    protected String oldRepetitionType = "";
    protected int[] oldDays = new int[7];
    protected String oldVideoUriString = null;

    private final String[][] AlertPlans = {
            {"07 : 00"},
            {"07 : 00", "19 : 00"},
            {"07 : 00", "12 : 00", "19 : 00"},
            {"07 : 00", "12 : 00", "17 : 00", "21 : 00"},
            {"07 : 00", "11 : 00", "15 : 00", "17 : 00", "21 : 00"}
    };

    protected abstract void setExistingData(Intent intent);

    protected DialogInterface.OnClickListener onDelete = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            MedicoDB db = new MedicoDB(getApplicationContext());
            if (dataId != NewData)
                db.deleteRow(dataId);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.exercise_deleted) , Toast.LENGTH_LONG).show();
            finish();
        }
    };

    protected DialogInterface.OnClickListener onReshootConfirmStill = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            ShootImage();
        }
    };

    protected DialogInterface.OnClickListener onReshootConfirmVideo = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            ShootVideo();
        }
    };

    protected DialogInterface.OnClickListener onConfirm = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            confirm(false);
        }
    };

    protected DialogInterface.OnClickListener onCancel = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            finish();
        }
    };


    public void OnRemoveLastAlert()
    {
        this.setAddAlertsButtons(true);
    }

    protected void setAddAlertsButtons(boolean alertPlan)
    {
        int visibleAlertPlan = alertPlan? View.VISIBLE : View.GONE;
        int invisibleAlertPlan = alertPlan? View.GONE : View.VISIBLE;
        btAddAlert.setVisibility(invisibleAlertPlan);
        lbAddMultiAlert.setVisibility(visibleAlertPlan);
        for (int i=0; i< 5; i++)
            alertPlanButtons[i].setVisibility(visibleAlertPlan);
    }

    protected View.OnClickListener setAlertPlan = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (int i=0; i< 5; i++)
            {
                if (alertPlanButtons[i].getId() == view.getId())
                {
                    arrayList.addAll(Arrays.asList(AlertPlans[i]).subList(0, i + 1));
                    timeAdapter.notifyItemInserted(arrayList.size() - 1);

                }
            }
            setAddAlertsButtons(false);
        }
    };

    protected View.OnClickListener clickHandler = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            System.arraycopy(selectedDays, 0, newSelectedDays, 0, 7);
            dialogDays.show();
            showButtons(false);

        }
    };


    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        isVideo = false;
        uriString = Uri.fromFile(image).toString();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    protected void ShootImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Build.VERSION.SDK_INT <= Build.VERSION_CODES.M ?
                        Uri.fromFile(photoFile) :
                        FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                isVideo = false;
                uriString = photoURI.toString();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    protected void ShootVideo()
    {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(DataGeneral.this.getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    protected void ChooseSound()
    {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
        Uri existingAlertSoundUri = alertSoundUriString == null ? null : Uri.parse(alertSoundUriString);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, existingAlertSoundUri);
        this.startActivityForResult(intent, CHOOSE_ALERT_SOUND);
    }

    protected AlertDialog askBeforeSave = null;



    protected void askUserBeforeSave()
    {
        askBeforeSave.show();
    }

    protected void confirm()
    {
        confirm(false);
    }

    protected abstract void confirm(boolean askBeforeSave);


    protected void updateSelectedDays()
    {
        String result = "";
        Resources rscs = getResources();
        final CharSequence[] items = {
                rscs.getString(R.string.Sunday_short),
                rscs.getString(R.string.Monday_Short),
                rscs.getString(R.string.Tuesday_Short),
                rscs.getString(R.string.Wednesday_Short),
                rscs.getString(R.string.Thursday_Short),
                rscs.getString(R.string.Friday_Short),
                rscs.getString(R.string.Saturday_Short)};
        Boolean anyDayAbsent = false;
        for (int i=0; i< selectedDays.length; i++)
        {
            if (selectedDays[i])
                result += (!result.equals("") ? " " : "") + items[i];
            else
                anyDayAbsent = true;
        }
        if (!anyDayAbsent)
            result = rscs.getString(R.string.all_days);
        lblSelectedDays.setText(result);
    }

    private void showButtons(boolean show)
    {
        int state = show ? View.VISIBLE : View.INVISIBLE;
        btDelete.setVisibility(state);
        btConfirm.setVisibility(state);
    }

    protected void setDialog() {
        final Resources rscs = getResources();
        final CharSequence[] items = {
                rscs.getString(R.string.Sunday),
                rscs.getString(R.string.Monday),
                rscs.getString(R.string.Tuesday),
                rscs.getString(R.string.Wednesday),
                rscs.getString(R.string.Thursday),
                rscs.getString(R.string.Friday),
                rscs.getString(R.string.Saturday)};

        dialogDays = new AlertDialog.Builder(this)
                .setTitle(rscs.getString(R.string.alert_dialog_select_days))
                .setMultiChoiceItems(items, newSelectedDays, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (!isChecked) {
                            Boolean anyOtherDaySelected = false;
                            for (int i=0; i< 7; i++)
                                if (i != indexSelected && newSelectedDays[i]) {
                                    anyOtherDaySelected = true;
                                    break;
                                }
                            if (!anyOtherDaySelected) {
                                newSelectedDays[indexSelected] = true;
                                ((AlertDialog) dialog).getListView().setItemChecked(indexSelected, true);
                                Toast.makeText(getApplicationContext(), rscs.getString(R.string.error_must_select_one_day),Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                }).setPositiveButton(rscs.getString(R.string.alert_dialog_set), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        System.arraycopy(newSelectedDays, 0, selectedDays, 0, 7);
                        updateSelectedDays();
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here
                    }
                }).setNegativeButton(rscs.getString(R.string.alert_dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        updateSelectedDays();
                        //  Your code when user clicked on Cancel
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        showButtons(true);
                    }
                }).create();

    }

    /*
        return string format of the current time.
        DO NOT CHANGE THIS FORMAT [database and other checks relying on that!!]
     */
    private String Right(String s)
    {
        return s.substring(s.length() - 2);
    }

    protected String makeTimeString()
    {
        Calendar cal = Calendar.getInstance();
        int minute =  cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        String str_hour = Right ("0" + hour);
        String str_minute = Right("0" + minute);
        return  str_hour + " : " + str_minute;
    }

    protected void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void setExistingExercise(Intent intent)
    {

        oldDataName = intent.getStringExtra("exercise_name");
        etDataName.setText(oldDataName);

        oldTimes = intent.getStringExtra("time");
        String times = oldTimes;
        String[] timeAL = null;
        arrayList = new ArrayList<>();
        if (times != null && !times.contentEquals("")) {
            timeAL = times.split(resources.getString(R.string.times_splitter));
            Collections.addAll(arrayList, timeAL);
        }

        oldRepeats = intent.getIntExtra("repeats",1);
        int repeats = oldRepeats;
        etRepeats.setText(Integer.toString(repeats));

        String repetitionType = intent.getStringExtra("repetition_type");
        if (repetitionType == null)
            repetitionType = Integer.toString(R.string.repetition_type_repetitions);
        oldRepetitionType = repetitionType;
        int index;
        index = Utils.findIndexInResourcesArray(resources, R.array.repetition_types, repetitionType);
        spRepetitionType.setSelection(index);


        oldDays = intent.getIntArrayExtra("days");
        int[] days = oldDays;
        for (int i=0; i< 7; i++)
            selectedDays[i] = days[i] != 0;
        updateSelectedDays();
        this.setAddAlertsButtons(timeAL == null || timeAL.length == 0);
    }

    @Override
    public void onBackPressed() {
        confirm(true);
    }
}

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

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.R;

/**
 * Created by edenk on 12/10/2017.
 * The Data Class contain Features that common for all fragments in the app,
 * And responsible for the look of each Activity in add/edit alerts.
 *
 *      For extend this abstract class DataGeneral, needed to implements:
 *
 *     protected abstract void setExistingData(Intent intent);
 *
 *     protected abstract String getDeletedMessage();
 *
 *     protected abstract void confirm(boolean askBeforeSave);
 *
 */
interface IRemoveLastAlert {   void OnRemoveLastAlert();}

public abstract class DataGeneral extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    protected static final int CHOOSE_ALERT_SOUND = 5;

    protected MedicoDB db;
    protected View mainView;
    protected final boolean[] selectedDays = new boolean[7], newSelectedDays = new boolean[7];
    protected final Button[] alertPlanButtons = new Button[5];
    protected final int Max_Size = 16, NewData = -6;

    protected EditText etDataName, etNotes, etAmount, etRepeats;
    protected TextView lblSelectedDays, lbAddMultiAlert;
    protected Button btDelete, btConfirm, btAddAlert;
    protected ImageButton btPlayImage, btPlayVideo, btChooseSound;
    protected Spinner spType, spSpecial, spRepetitionType;

    protected Resources resources;
    protected TimeAdapterRecyclerMedGo timeAdapter = null;
    protected FirebaseAnalytics mFirebaseAnalytics;
    protected AlertDialog dialogDays;
    protected ArrayList<String> arrayList;

    protected String uriStringVideo, uriStringImage, alertSoundUriString;
    protected String oldDataName = "", oldNotes = "", oldTimes = "", oldAlertSoundUriString = null, oldDosageType = "",
            oldSpecialNotes = "", oldAmount = "1", oldRepetitionType = "", oldUriStringVideo = null, oldUriStringImage = null;
    protected int[] oldDays = new int[7];
    protected int dataId, oldRepeats = 1;

    private final String[][] AlertPlans = {
            {"07 : 00"},
            {"07 : 00", "19 : 00"},
            {"07 : 00", "12 : 00", "19 : 00"},
            {"07 : 00", "12 : 00", "17 : 00", "21 : 00"},
            {"07 : 00", "11 : 00", "15 : 00", "17 : 00", "21 : 00"}
    };

    protected DialogInterface.OnClickListener onDelete = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            MedicoDB db = new MedicoDB(getApplicationContext());
            if (dataId != NewData)
                db.deleteRow(dataId);
            Toast.makeText(getApplicationContext(), getDeletedMessage() , Toast.LENGTH_LONG).show();
            finish();
        }
    };

    protected abstract void setExistingData(Intent intent);

    protected abstract String getDeletedMessage();

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
        uriStringImage = Uri.fromFile(image).toString();
        return image;
    }


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
                uriStringImage = photoURI.toString();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    protected void ShootVideo()
    {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(DataGeneral.this.getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_TAKE_PHOTO);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Check which request we're responding to
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Make sure the request was successful
            if (intent != null && intent.getData() != null) {
                    String path = intent.getData().getPath();
                    if(path != null) {
                        if (path.contains("/video/")) {
                            uriStringVideo = intent.getData().toString();
                            btPlayVideo.setVisibility(View.VISIBLE);
                            Toast.makeText(DataGeneral.this.getApplicationContext(), resources.getString(R.string.AttachSuccessVideo), Toast.LENGTH_LONG).show();
                        }
                        else if (path.contains("/images/")) {
                            btPlayImage.setVisibility(View.VISIBLE);
                            Glide.with(this).load(uriStringImage).into(btPlayImage);
                            btPlayImage.invalidate();
                            Toast.makeText(DataGeneral.this.getApplicationContext(), resources.getString(R.string.AttachSuccessImage), Toast.LENGTH_LONG).show();
                        }
                        else Toast.makeText(DataGeneral.this.getApplicationContext(), resources.getString(R.string.AttachFailed), Toast.LENGTH_LONG).show();
                    }
                    else Toast.makeText(DataGeneral.this.getApplicationContext(), resources.getString(R.string.AttachFailed), Toast.LENGTH_LONG).show();
            }else{
                btPlayImage.setVisibility(View.VISIBLE);
                Glide.with(this).load(uriStringImage).into(btPlayImage);
                btPlayImage.invalidate();
                Toast.makeText(DataGeneral.this.getApplicationContext(), resources.getString(R.string.AttachSuccessImage), Toast.LENGTH_LONG).show();
            }
        }
        // Check which request we're responding to
        else if (requestCode == CHOOSE_ALERT_SOUND) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK && intent != null) {
                Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (uri != null) {
                    this.alertSoundUriString = uri.toString();
                    Toast.makeText(DataGeneral.this.getApplicationContext(), resources.getString(R.string.AttachSuccessSound), Toast.LENGTH_LONG).show();
                }
            }
        } else Toast.makeText(DataGeneral.this.getApplicationContext(), resources.getString(R.string.AttachFailed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        confirm(true);
    }
}

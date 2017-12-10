package bredesh.medico.Fragments.DataMediGo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.Collections;
import java.util.Date;

import bredesh.medico.Camera.RemindersData;
import bredesh.medico.Camera.TimeAdapterRecycler;
import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.R;

/**
 * Created by edenk on 12/10/2017.
 */
public class RemindersDa extends DataGeneral implements IRemoveLastAlert{
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int CHOOSE_ALERT_SOUND = 5;

    private EditText etRemindersName, etNotes;
    private ArrayList<String> arrayList;
    private TextView lblSelectedDays;
    private Button btDelete, btConfirm;

    private AlertDialog dialogDays;
    // array to keep the selected days
    private final boolean[] selectedDays = new boolean[7];
    private final boolean[] newSelectedDays = new boolean[7];
    private MedicoDB db;
    private final int Max_Size = 16;
    private Resources resources;
    private int remindersId;
    private final int NewReminders = -6;
    private ImageButton btPlayStill;
    private ImageButton btPlayVideo;
    private ImageButton btChooseSound;

    private String UriString;
    private String alertSoundUriString;
    private final Button[] alertPlanButtons = new Button[5];
    private TimeAdapterRecyclerMedGo timeAdapter = null;
    private Button btAddAlert;
    private TextView lbAddMultiAlert;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Boolean isVideo = false;


    private final String[][] AlertPlans =
            {
                    {"07 : 00"},
                    {"07 : 00", "19 : 00"},
                    {"07 : 00", "12 : 00", "19 : 00"},
                    {"07 : 00", "12 : 00", "17 : 00", "21 : 00"},
                    {"07 : 00", "11 : 00", "15 : 00", "17 : 00", "21 : 00"}
            };


    private DialogInterface.OnClickListener onDelete = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            MedicoDB db = new MedicoDB(getApplicationContext());
            if (remindersId != NewReminders)
                db.deleteRow(remindersId);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.exercise_deleted) , Toast.LENGTH_LONG).show();
            finish();
        }
    };

    private DialogInterface.OnClickListener onReshootConfirmStill = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            ShootImage();
        }
    };

    private DialogInterface.OnClickListener onReshootConfirmVideo = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            ShootVideo();
        }
    };

    private DialogInterface.OnClickListener onConfirm = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            confirm(false);
        }
    };

    private DialogInterface.OnClickListener onCancel = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            finish();
        }
    };


    public void OnRemoveLastAlert()
    {
        this.setAddAlertsButtons(true);
    }

    private void setAddAlertsButtons(boolean alertPlan)
    {
        int visibleAlertPlan = alertPlan? View.VISIBLE : View.GONE;
        int invisibleAlertPlan = alertPlan? View.GONE : View.VISIBLE;
        btAddAlert.setVisibility(invisibleAlertPlan);
        lbAddMultiAlert.setVisibility(visibleAlertPlan);
        for (int i=0; i< 5; i++)
            alertPlanButtons[i].setVisibility(visibleAlertPlan);
    }

    private View.OnClickListener setAlertPlan = new View.OnClickListener() {
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

    private String oldRemindersName = "";
    private String oldNotes = "";
    private String oldTimes = "";
    private int[] oldDays = null;
    private String oldViedoUriString = "";
    private String oldAlertSoundUriString = null;

    private void setExistingReminders(Intent intent)
    {
        oldRemindersName = intent.getStringExtra("reminders_name");
        etRemindersName.setText(oldRemindersName);

        oldNotes = intent.getStringExtra("reminders_notes");
        etNotes.setText(oldNotes);

        String times = intent.getStringExtra("time");
        oldTimes = times;
        String[] timeAL = null;
        arrayList = new ArrayList<>();
        if (times != null && !times.contentEquals("")) {
            timeAL = times.split(resources.getString(R.string.times_splitter));
            Collections.addAll(arrayList, timeAL);
        }
        int[] days = intent.getIntArrayExtra("days");
        oldDays = days;
        for (int i=0; i< 7; i++)
            selectedDays[i] = days[i] != 0;
        updateSelectedDays();
        this.setAddAlertsButtons(timeAL == null || timeAL.length == 0);
    }

    private View.OnClickListener clickHandler = new View.OnClickListener() {

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
        UriString = Uri.fromFile(image).toString();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void ShootImage() {
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
                UriString = photoURI.toString();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private void ShootVideo()
    {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(RemindersDa.this.getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void ChooseSound()
    {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
        Uri existingAlertSoundUri = alertSoundUriString == null ? null : Uri.parse(alertSoundUriString);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, existingAlertSoundUri);
        this.startActivityForResult(intent, CHOOSE_ALERT_SOUND);
    }

    private AlertDialog askBeforeSave = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_reminders_data);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("");
        actionBar.setLogo(R.mipmap.ic_medigo_logo_clock);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        RecyclerView timeViews = findViewById(R.id.time_views);
        timeViews.setLayoutManager(new LinearLayoutManager(this));

        resources = getResources();
        btConfirm = findViewById(R.id.btConfirm);
        btDelete = findViewById(R.id.btDelete);
        etRemindersName = findViewById(R.id.etRemindersName);
        lblSelectedDays = findViewById(R.id.lblSelectedDays);
        lblSelectedDays.setMovementMethod(new ScrollingMovementMethod());

        btPlayStill = findViewById(R.id.btPlayStill);
        btPlayVideo = findViewById(R.id.btPlayVideo);
        if(/*!isVideo || */oldViedoUriString==null) btPlayVideo.setVisibility(View.INVISIBLE);
        btPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                Uri videoUri = Uri.parse(UriString);
                if (videoUri != null) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (RuntimeException e) {
                        Toast.makeText(context.getApplicationContext(),
                                resources.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                    }
                } else Toast.makeText(context.getApplicationContext(),
                        resources.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
            }
        });
        btChooseSound = findViewById(R.id.btChooseSound);

        alertPlanButtons[0] = findViewById(R.id.bt1time);
        alertPlanButtons[1] = findViewById(R.id.bt2times);
        alertPlanButtons[2] = findViewById(R.id.bt3times);
        alertPlanButtons[3] = findViewById(R.id.bt4times);
        alertPlanButtons[4] = findViewById(R.id.bt5times);
        btAddAlert = findViewById(R.id.btAddAlert);
        lbAddMultiAlert = findViewById(R.id.lbAddMultiAlert);

        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource (this, R.array.drugs_dosage, R.layout.spinner_item );
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        etNotes = findViewById(R.id.et_notes);
        etNotes.setMovementMethod(new ScrollingMovementMethod());
        etNotes.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                etNotes.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });



        final AlertDialog reShootConfirmStill = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onReshootConfirmStill)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .setMessage(resources.getString(R.string.reshootPic)).create();

        final AlertDialog reShootConfirmVideo = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onReshootConfirmVideo)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .setMessage(resources.getString(R.string.reshootPic)).create();


        askBeforeSave = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onConfirm)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), onCancel)
                .setMessage(resources.getString(R.string.save_changes)).create();

        ImageButton still = findViewById(R.id.btShootStill);
        still.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UriString != null)
                    reShootConfirmStill.show();
                else
                    ShootImage();
            }
        });

        ImageButton video = findViewById(R.id.btShootVideo);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UriString != null)
                    reShootConfirmVideo.show();
                else
                    ShootVideo();
            }
        });


        db = new MedicoDB(getApplicationContext());
        Intent intent = getIntent();
        UriString = intent.getStringExtra("RecordedUri");
        oldViedoUriString = UriString;
        oldAlertSoundUriString = alertSoundUriString = intent.getStringExtra("AlertSoundUri");

        this.remindersId = intent.getIntExtra("remindersId", NewReminders);
        if (this.remindersId != NewReminders)
        {
            setExistingReminders(intent);
        }
        else {
            arrayList = new ArrayList<>();
            // arrayList.add(makeTimeString());
            for(int i=0; i<selectedDays.length; i++)
                selectedDays[i] = true;
//            oldDosageType = Integer.toString (R.string.medicine_dosage_type_tab);
//            oldSpecialNotes = Integer.toString(R.string.medicine_usage_notes_none);
            oldDays = new int[7];
            for (int i=0 ;i < 7; i++)
                oldDays[i] = 1;
        }

        Button [] buttons = new Button[] {btConfirm, btDelete};


        timeAdapter = new TimeAdapterRecyclerMedGo(RemindersDa.this,arrayList, buttons, this);
        timeViews.setAdapter(timeAdapter);
        setDialog();

        lblSelectedDays.setOnClickListener(clickHandler);

        for (int i=0; i< 5; i++)
            alertPlanButtons[i].setOnClickListener(setAlertPlan);

        if (UriString == null)
            btPlayStill.setVisibility(View.INVISIBLE);
        else {
            Glide.with(this).load(UriString).into(btPlayStill);
            btPlayStill.invalidate();
        }
        btPlayStill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                Uri imageUri = Uri.parse(UriString);
                if (imageUri != null) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(imageUri,"image/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (RuntimeException e) {
                        Toast.makeText(context.getApplicationContext(),
                                resources.getString(R.string.media_not_found_image), Toast.LENGTH_SHORT).show();
                    }
                } else Toast.makeText(context.getApplicationContext(),
                        resources.getString(R.string.media_not_found_image), Toast.LENGTH_SHORT).show();
            }
        });

        btChooseSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseSound();
            }
        });

        btConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        final AlertDialog deleteDialog = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onDelete)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .setMessage(resources.getString(R.string.delete_reminders_confirm)).create();


        btDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteDialog.show();
            }
        });



        // updating the list + adding another alert
        btAddAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arrayList.add(makeTimeString());
                timeAdapter.notifyItemInserted(arrayList.size() - 1);
            }
        });
    }

    private void askUserBeforeSave()
    {
        askBeforeSave.show();
    }

    private void confirm()
    {
        confirm(false);
    }

    private void confirm(boolean askBeforeSave) {
        {
            if(etRemindersName.getText().toString().length() == 0 && !askBeforeSave) {
                Toast.makeText(getApplicationContext(), resources.getString(R.string.name_too_short_reminders), Toast.LENGTH_SHORT).show();
            }
            else {
                if (etRemindersName.getText().toString().length() <= Max_Size) {
                    int[] days_to_alert = new int[selectedDays.length];
                    for (int i = 0; i < days_to_alert.length; i++) {
                        if (selectedDays[i])
                            days_to_alert[i] = 1;
                        else
                            days_to_alert[i] = 0;
                    }
                    String times = "";
                    Collections.sort(arrayList);
                    for (int i = 0; i < arrayList.size(); i++)
                        times = times + (i > 0 ? getResources().getString(R.string.times_splitter) : "") + arrayList.get(i);

//                    String typeToWrite = Utils.findResourceIdInResourcesArray(resources, R.array.drugs_dosage, /*spType.getSelectedItem().toString()*/"");
//                    String specialNotesToWrite =Utils.findResourceIdInResourcesArray(resources, R.array.drugs_dosage_notes,""/* spSpecial.getSelectedItem().toString()*/);
                    String remindersName = etRemindersName.getText().toString();
                    String newNotes = etNotes.getText().toString();
//                    String amountText = "0";
//                    int amount = Integer.parseInt(amountText);

                    if (askBeforeSave)
                    {
                        boolean dataNotChanged = (
                                oldRemindersName.equals(remindersName) &&
                                        oldTimes.equals(times) &&
//                                        oldSpecialNotes.equals(specialNotesToWrite) &&
                                        oldNotes.equals(newNotes) &&
                                        (oldViedoUriString == null ? UriString == null : oldViedoUriString.equals(UriString)) &&
                                        (oldAlertSoundUriString == null ? alertSoundUriString == null : oldAlertSoundUriString.equals(alertSoundUriString)) &&
                                        Arrays.equals(oldDays, days_to_alert)
                        );
                        if (dataNotChanged)
                            finish();
                        else
                            askUserBeforeSave();
                        return;

                    }

                    Bundle bundle = new Bundle();
                    if (remindersId != NewReminders) {
                        db.updateRow(remindersId, remindersName, times, 0, "" ,  UriString, days_to_alert, alertSoundUriString);
                        db.updateReminders(remindersId, newNotes);
                        mFirebaseAnalytics.logEvent("Reminders_updated", bundle);
                    }
                    else
                    {
                        if (askBeforeSave) {
                            askUserBeforeSave();
                            return;
                        }
                        else
                        {
                            db.addAlert(etRemindersName.getText().toString(), MedicoDB.KIND.Reminders, times, 0, "", UriString, days_to_alert, alertSoundUriString);
                            db.addReminders(etNotes.getText().toString());
                            mFirebaseAnalytics.logEvent("Reminders_added", bundle);
                        }
                    }
                    finish();
                } else
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.name_too_long), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateSelectedDays()
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

    private void setDialog() {
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CHOOSE_ALERT_SOUND) {
            if (resultCode == RESULT_OK && intent != null) {
                Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                if (uri != null) {
                    this.alertSoundUriString = uri.toString();
                }
            }
        }
        else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK && intent != null) {
            if (intent.getData() != null) {
                UriString = intent.getData().toString();
                btPlayVideo.setVisibility(View.VISIBLE);
                Toast.makeText(RemindersDa.this.getApplicationContext(), resources.getString(R.string.AttachSuccess), Toast.LENGTH_LONG).show();
            }
        }
        else if(resultCode == RESULT_OK)
        {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                btPlayStill.setVisibility(View.VISIBLE);
                Glide.with(this).load(UriString).into(btPlayStill);
                btPlayStill.invalidate();
            }
        }
        else Toast.makeText(RemindersDa.this.getApplicationContext(), resources.getString(R.string.AttachFailed), Toast.LENGTH_LONG).show();
    }


    /*
        return string format of the current time.
        DO NOT CHANGE THIS FORMAT [database and other checks relying on that!!]
     */

    private String Right(String s)
    {
        return s.substring(s.length() - 2);
    }

    private String makeTimeString()
    {
        Calendar cal = Calendar.getInstance();
        int minute =  cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        String str_hour = Right ("0" + hour);
        String str_minute = Right("0" + minute);
        return  str_hour + " : " + str_minute;
    }

    @Override
    public void onBackPressed() {
        confirm(true);
    }
}

package bredesh.medico.Fragments.DataMediGo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
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
    protected Toolbar app_bar;
    protected RecyclerView timeViews;
    protected final boolean[] selectedDays = new boolean[7], newSelectedDays = new boolean[7];
    protected final Button[] alertPlanButtons = new Button[5];
    protected final int Max_Size = 16, NewData = -6;

    protected EditText etDataName, etNotes, etAmount, etRepeats;
    protected TextView tvSelectedDays, tvAddMultiAlert;
    protected Button btDelete, btConfirm, btAddAlert;
    protected ImageButton btPlayImage, btPlayVideo, btChooseSound, btShootVideo, btShootImage;
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

    protected void setFindView() {
        app_bar             = findViewById(R.id.app_bar);
        mainView            = findViewById(R.id.llData);
        timeViews           = findViewById(R.id.timeViews);

        etDataName          = findViewById(R.id.etDataName);
        tvSelectedDays      = findViewById(R.id.tvSelectedDays);
        tvAddMultiAlert     = findViewById(R.id.tvAddMultiAlert);

        btShootVideo        = findViewById(R.id.btShootVideo);
        btShootImage        = findViewById(R.id.btShootImage);
        btPlayVideo         = findViewById(R.id.btPlayVideo);
        btPlayImage         = findViewById(R.id.btPlayImage);
        btChooseSound       = findViewById(R.id.btChooseSound);
        btAddAlert          = findViewById(R.id.btAddAlert);
        btConfirm           = findViewById(R.id.btConfirm);
        btDelete            = findViewById(R.id.btDelete);

        etRepeats           = findViewById(R.id.etRepeats);
        etAmount            = findViewById(R.id.amount_number);
        etNotes             = findViewById(R.id.et_notes);
        spSpecial           = findViewById(R.id.spinner_special);
        spRepetitionType    = findViewById(R.id.spRepetitionType);
        spType              = findViewById(R.id.spinner_type);

        alertPlanButtons[0] = findViewById(R.id.bt1time);
        alertPlanButtons[1] = findViewById(R.id.bt2times);
        alertPlanButtons[2] = findViewById(R.id.bt3times);
        alertPlanButtons[3] = findViewById(R.id.bt4times);
        alertPlanButtons[4] = findViewById(R.id.bt5times);
    }

    protected void setOnCreate() {
        setSupportActionBar(app_bar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setLogo(R.mipmap.ic_medigo_logo_clock);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        db = new MedicoDB(getApplicationContext());
        Intent intent = getIntent();
        resources = getResources();
        this.dataId = intent.getIntExtra("dataId", NewData);
        oldUriStringVideo       = uriStringVideo        = intent.getStringExtra("dataUriVideo");
        oldUriStringImage       = uriStringImage        = intent.getStringExtra("dataUriImage");
        oldAlertSoundUriString  = alertSoundUriString   = intent.getStringExtra("dataAlertSoundUri");

        mainView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(mainView);
                return false;
            }
        });
        timeViews.setLayoutManager(new LinearLayoutManager(this));
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        //this.setAutoCloseKeyboard(etRepeats);
        tvSelectedDays.setMovementMethod(new ScrollingMovementMethod());

        askBeforeSave = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onConfirm)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), onCancel)
                .setMessage(resources.getString(R.string.save_changes)).create();

        final AlertDialog reShootConfirm = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onReshootConfirmVideo)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .setMessage(resources.getString(R.string.reshootVideo)).create();

        if (this.dataId != NewData) {setExistingData(intent);}
        else setNewData();

        Button [] buttons = new Button[] {btConfirm, btDelete};

        timeAdapter = getNewTimeAdapter(buttons);
//        timeAdapter = new TimeAdapterRecyclerMedGo(ExerciseDa.this,arrayList, buttons, this);
        timeViews.setAdapter(timeAdapter);
        setDialog();

        tvSelectedDays.setOnClickListener(clickHandler);

        for (int i=0; i< 5; i++)
            alertPlanButtons[i].setOnClickListener(setAlertPlan);

        if(btShootVideo != null) {
            btShootVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (uriStringVideo != null)
                        reShootConfirm.show();
                    else
                        ShootVideo();
                }
            });
        }

        if (uriStringVideo == null && btPlayVideo != null)
            btPlayVideo.setVisibility(View.INVISIBLE);
        if (btPlayVideo != null) {
            btPlayVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = getApplicationContext();
                    Uri videoUri = Uri.parse(uriStringVideo);
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
        }

        if (btShootImage != null) {
            btShootImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (uriStringImage != null)
                        reShootConfirm.show();
                    else
                        ShootImage();
                }
            });
        }

        if (uriStringImage == null && btPlayImage != null)
            btPlayImage.setVisibility(View.INVISIBLE);
        else if(btPlayImage != null){
            Glide.with(this).load(uriStringImage).into(btPlayImage);
            btPlayImage.invalidate();
        }

        if(btPlayImage != null) {
            btPlayImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = getApplicationContext();
                    Uri imageUri = Uri.parse(uriStringImage);
                    if (imageUri != null) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(imageUri, "image//");
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
        }
        if (btChooseSound != null)
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
                .setMessage(getDeletedMessageConfirm()).create();
//                .setMessage(resources.getString(R.string.delete_exercise_confirm)).create();

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
                hideKeyboard(v);
                arrayList.add(makeTimeString());
                timeAdapter.notifyItemInserted(arrayList.size() - 1);
            }
        });

    }

    protected abstract void setNewData();

    protected abstract CharSequence getDeletedMessageConfirm();

    protected abstract TimeAdapterRecyclerMedGo getNewTimeAdapter(Button[] buttons);

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
        tvAddMultiAlert.setVisibility(visibleAlertPlan);
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
        tvSelectedDays.setText(result);
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

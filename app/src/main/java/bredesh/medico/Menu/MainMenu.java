package bredesh.medico.Menu;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.Localization;
import bredesh.medico.MainActivity;
import bredesh.medico.NotificationService;
import bredesh.medico.R;

/**
 * Created by Omri on 01/06/2017.
 */

public class MainMenu extends AppCompatActivity
{
    public enum MODE {Exercise,Medicine}
    public static MODE CURRENT;
    public static String language = "default";
    public static boolean languageChanged = false;
    private Menu optionsMenu = null;
    private Resources resources;
    MedicoDB dbManager = null;

    public static boolean active = false;

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        if (language == null)
            language = "";
        switch (language)
        {
            default:
                menu.findItem(R.id.action_lang_default).setChecked(true);
                break;
            case "iw":
                menu.findItem(R.id.action_lang_hebrew).setChecked(true);
                break;
            case "ar":
                menu.findItem(R.id.action_lang_arabic).setChecked(true);
                break;
            case "en":
                menu.findItem(R.id.action_lang_english).setChecked(true);
                break;
            case "ru":
                menu.findItem(R.id.action_lang_russian).setChecked(true);
                break;

        }

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String languageToLoad = null;
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_lang_english:
                languageToLoad = Locale.ENGLISH.toString(); // your language
                break;
            case R.id.action_lang_hebrew:
                 languageToLoad = "iw"; // your language
                break;
            case R.id.action_lang_arabic:
                languageToLoad = "ar"; // your language
                break;
            case R.id.action_lang_russian:
                languageToLoad = "ru"; // your language
                break;
            case R.id.action_lang_default:
                languageToLoad = "";
            default:
                break;

        }

        language = languageToLoad;
        this.optionsMenu.findItem(itemId).setChecked(true);
        this.invalidateOptionsMenu();

        if (languageToLoad != null)
        {
            Localization.setLanguage(languageToLoad, this);
            Localization.saveLanguageInPrefs(languageToLoad, this.dbManager);
            this.setContentView(R.layout.activity_menu);
            PrepareMenu();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = new MedicoDB(getApplicationContext());
        resources = getResources();
        final Intent SERVICE_INTENT = new Intent(getBaseContext(), NotificationService.class);
        startService(SERVICE_INTENT);
        startView();
    }

    void startView()
    {
        language = Localization.init(this, dbManager);
        setContentView(R.layout.activity_menu);
        PrepareMenu();

    }

    void PrepareMenu()
    {
        final int NUMBER_OF_COLUMNS = 2;

        View root = findViewById(R.id.main_menu_root);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setLogo(R.mipmap.ic_medigo_logo_clock);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }


        RecyclerView menu = (RecyclerView) findViewById(R.id.menu_items);
        menu.setLayoutManager(new GridLayoutManager(this, NUMBER_OF_COLUMNS));

        Resources resources = getResources();
        List<Item_Menu> menu_items = new ArrayList<>();

        // initiate list
        menu_items.add(new Item_Menu(R.drawable.ic_rowing_black_48dp,
                resources.getString(R.string.menu_item_1),
                new Intent(MainMenu.this, MainActivity.class),
                MODE.Exercise, R.drawable.card_menu1));

        menu_items.add(new Item_Menu(R.drawable.ic_pill,
                resources.getString(R.string.menu_item_2),
                new Intent(MainMenu.this, MainActivity.class),
                MODE.Medicine, R.drawable.card_menu2));

        menu_items.add(new Item_Menu(R.drawable.ic_transfer_within_a_station_black_48dp,
                resources.getString(R.string.menu_item_3),
                null,
                null, R.drawable.card_menu3));

        menu_items.add(new Item_Menu(R.drawable.ic_toys_black_48dp,
                resources.getString(R.string.menu_item_4),
                null,
                null, R.drawable.card_menu4));

        MenuAdapterRecycler adapterRecycler = new MenuAdapterRecycler(getApplicationContext(),menu_items, this);
        menu.setAdapter(adapterRecycler);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (languageChanged) {
            startView();
            languageChanged = false;
        }

    }

    private DialogInterface.OnClickListener onExit = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        final AlertDialog exitDialog = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onExit)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .setMessage(resources.getString(R.string.exit_query)).create();

        exitDialog.show();
    }
}

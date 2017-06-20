package bredesh.medico;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.bottom_bar.BottomBar;
import com.example.bottom_bar.OnTabReselectListener;
import com.example.bottom_bar.OnTabSelectListener;

import java.util.Locale;

import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.Fragments.FragmentHome;
import bredesh.medico.Fragments.FragmentMedicine;
import bredesh.medico.Fragments.PersonalProfileFragment;
import bredesh.medico.Menu.MainMenu;


public class MainActivity extends AppCompatActivity {


    private String language = "default";
    private Menu optionsMenu = null;
    MedicoDB dbManager = null;
    int prevTabId = 0;
    BottomBar bottomBar = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        if (this.language == null)
            this.language = "";
        switch (this.language)
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

        if (languageToLoad != null && languageToLoad.compareTo(language) != 0)
        {
            this.prevTabId = bottomBar.getCurrentTabId();
            this.language = languageToLoad;
            this.optionsMenu.findItem(itemId).setChecked(true);
            this.invalidateOptionsMenu();
            Localization.setLanguage(languageToLoad, this);
            if (this.dbManager == null)
                this.dbManager = new MedicoDB(getApplicationContext());
            Localization.saveLanguageInPrefs(languageToLoad, this.dbManager);

            this.setContentView(R.layout.activity_main);
            prepareView();
            MainMenu.languageChanged = true;
        }

        return true;
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        session = new Session(this);
        if(!session.loggedin()){
            logout();
        }
*/
        this.language = MainMenu.language;
        prepareView();
    }

    private void prepareView()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setLogo(R.mipmap.ic_medigo_logo_clock);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                changeFragment(tabId);
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if(tabId == R.id.tab_home)  onBackPressed();
            }
        });

        if (this.prevTabId != 0) {
            bottomBar.selectTabWithId(this.prevTabId);
            this.prevTabId = 0;
        }
        else
        {
            switch (MainMenu.CURRENT) {
                case Exercise:
                    bottomBar.selectTabWithId(R.id.tab_exercises);
                    break;
                case Medicine:
                    bottomBar.selectTabWithId(R.id.tab_drugs);
                    break;
            }
        }
    }

    public void changeFragment(@IdRes int menuItemId) {
        Fragment fragment = null;
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.enter, R.animator.exit);

        switch (menuItemId) {
            case R.id.tab_home:
                onBackPressed();
                break;
            case R.id.tab_exercises:
                fragment = new FragmentHome();
                break;
            case R.id.tab_drugs:
                fragment = new FragmentMedicine();
                break;
            case R.id.tab_personal:
                fragment = new PersonalProfileFragment();
                break;
        }

        if (fragment != null) {
            ft.replace(R.id.fragment_place, fragment);
            ft.commit();
        }
    }

   /* private void logout(){
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(MainActivity.this,MainLoginActivity.class));
    }*/
}
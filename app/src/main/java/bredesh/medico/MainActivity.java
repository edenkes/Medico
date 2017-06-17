package bredesh.medico;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.bottom_bar.BottomBar;
import com.example.bottom_bar.BottomBarTab;
import com.example.bottom_bar.OnTabSelectListener;

import bredesh.medico.Fragments.FragmentHome;
import bredesh.medico.Fragments.FragmentMedicine;
import bredesh.medico.Fragments.PersonalFragment;
import bredesh.medico.Menu.MainMenu;

import static bredesh.medico.Menu.MainMenu.MODE.Medicine;
import bredesh.medico.Fragments.PersonalProfileFragment;


public class MainActivity extends AppCompatActivity {

    private BottomBarTab home;

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

    //    private Session session;

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


        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setLogo(R.mipmap.ic_medigo_logo_clock);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        home = bottomBar.getTabWithId(R.id.tab_home);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {

                changeFragment(tabId);
            }

        });


    }

    @SuppressLint("ShowToast")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            /*
            case R.id.action_setting:
                Toast.makeText(MainActivity.this, "setting", Toast.LENGTH_SHORT);
                return true;
                */
            /*case R.id.action_logout:
                Toast.makeText(MainActivity.this, "logout", Toast.LENGTH_SHORT);
                logout();
                return true;
            */default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeFragment(@IdRes int menuItemId) {
        Fragment fragment = null;
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.enter, R.animator.exit);

        switch (menuItemId) {
           /* case R.id.tab_recents:
                fragment = new FragmentRecents();
                break;
            case R.id.tab_favorites:
                fragment = new FragmentFavorites();
                break;*/
            case R.id.tab_home:
                switch (MainMenu.CURRENT)
                {
                    case Exercise:
                        fragment = new FragmentHome();
                        break;
                    case Medicine:
                        fragment = new FragmentMedicine();
                        break;

                }
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
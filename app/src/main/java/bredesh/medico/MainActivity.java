package bredesh.medico;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bottom_bar.BottomBar;
import com.example.bottom_bar.BottomBarTab;
import com.example.bottom_bar.OnTabReselectListener;
import com.example.bottom_bar.OnTabSelectListener;

import bredesh.medico.Fragments.FragmentCamera;
import bredesh.medico.Fragments.FragmentFavorites;
import bredesh.medico.Fragments.FragmentHome;
import bredesh.medico.Fragments.FragmentPersonal;
import bredesh.medico.Fragments.FragmentRecents;
import bredesh.medico.Login.MainLoginActivity;
import bredesh.medico.Login.Session;


public class MainActivity extends AppCompatActivity {
    private BottomBarTab recents;
    private BottomBarTab favorites;
    private BottomBarTab home;
    private BottomBarTab personal;
    private BottomBarTab camera;
    private int counterHome;
    private Session session;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new Session(this);
        if(!session.loggedin()){
            logout();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setLogo(R.mipmap.ic_medico);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        counterHome = 0;

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        recents = bottomBar.getTabWithId(R.id.tab_recents);
        favorites = bottomBar.getTabWithId(R.id.tab_favorites);
        home = bottomBar.getTabWithId(R.id.tab_home);
        personal = bottomBar.getTabWithId(R.id.tab_personal);
        camera = bottomBar.getTabWithId(R.id.tab_camera);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
//                messageView.setText(TabMessage.get(tabId, false));
                setBadgeCount(R.id.tab_home, ++counterHome);
                setBadgeCount(tabId, 0);
                changeFragment(tabId);
            }

        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                Toast.makeText(getApplicationContext(), TabMessage.get(tabId, true), Toast.LENGTH_LONG).show();
                setBadgeCount(R.id.tab_home, ++counterHome);
                setBadgeCount(tabId, 0);
            }
        });

        setBadgeCount(R.id.tab_home, 6);
        setBadgeCount(R.id.tab_recents, 1);
        setBadgeCount(R.id.tab_personal, 3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_setting:
                Toast.makeText(MainActivity.this, "setting", Toast.LENGTH_SHORT);
                return true;
            case R.id.action_logout:
                Toast.makeText(MainActivity.this, "logout", Toast.LENGTH_SHORT);
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout(){
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(MainActivity.this,MainLoginActivity.class));
    }

    private void setBadgeCount(@IdRes int menuItemId, int counter) {
        switch (menuItemId) {
            case R.id.tab_recents:
                recents.setBadgeCount(counter);
                break;
            case R.id.tab_favorites:
                favorites.setBadgeCount(counter);
                break;
            case R.id.tab_home:
                home.setBadgeCount(counter);
                counterHome = counter;
                break;
            case R.id.tab_personal:
                personal.setBadgeCount(counter);
                break;
            case R.id.tab_camera:
                camera.setBadgeCount(counter);
                break;
        }
    }

    public void changeFragment(@IdRes int menuItemId){
        Fragment fragment;
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (menuItemId) {
            case R.id.tab_recents:
                fragment = new FragmentRecents();
                ft.replace(R.id.fragment_place, fragment);
                ft.commit();
                break;
            case R.id.tab_favorites:
                fragment = new FragmentFavorites();
                ft.replace(R.id.fragment_place, fragment);
                ft.commit();
                break;
            case R.id.tab_home:
                fragment = new FragmentHome();
                ft.replace(R.id.fragment_place, fragment);
                ft.commit();
                break;
            case R.id.tab_personal:
                fragment = new FragmentPersonal();
                ft.replace(R.id.fragment_place, fragment);
                ft.commit();
                break;
            case R.id.tab_camera:
                fragment = new FragmentCamera();
                ft.replace(R.id.fragment_place, fragment);
                ft.commit();
                break;
        }
    }
}


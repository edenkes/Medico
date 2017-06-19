package bredesh.medico;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.bottom_bar.BottomBar;
import com.example.bottom_bar.OnTabReselectListener;
import com.example.bottom_bar.OnTabSelectListener;

import bredesh.medico.Fragments.FragmentHome;
import bredesh.medico.Fragments.FragmentMedicine;
import bredesh.medico.Fragments.PersonalProfileFragment;
import bredesh.medico.Menu.MainMenu;


public class MainActivity extends AppCompatActivity {
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
    }

    public void changeFragment(@IdRes int menuItemId) {
        Fragment fragment = null;
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.enter, R.animator.exit);

        switch (menuItemId) {
            case R.id.tab_home:
                switch (MainMenu.CURRENT) {
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
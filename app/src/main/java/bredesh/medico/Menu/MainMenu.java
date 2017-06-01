package bredesh.medico.Menu;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import bredesh.medico.MainActivity;
import bredesh.medico.R;

/**
 * Created by Omri on 01/06/2017.
 */

public class MainMenu extends AppCompatActivity
{
    private final int NUMBER_OF_COLUMNS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setLogo(R.mipmap.ic_medico_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        RecyclerView menu = (RecyclerView) findViewById(R.id.menu_items);
        menu.setLayoutManager(new GridLayoutManager(this, NUMBER_OF_COLUMNS));

        Resources resources = getResources();
        List<Item_Menu> menu_items = new ArrayList<>();

        // initiate list
        menu_items.add(new Item_Menu(R.drawable.ic_pill,
                resources.getString(R.string.menu_item_1),
                new Intent(MainMenu.this, MainActivity.class)));
        menu_items.add(new Item_Menu(R.drawable.ic_rowing_black_48dp,
                resources.getString(R.string.menu_item_2),
                new Intent(MainMenu.this, MainActivity.class)));

        MenuAdapterRecycler adapterRecycler = new MenuAdapterRecycler(getApplicationContext(),menu_items, this);
        menu.setAdapter(adapterRecycler);
    }
}

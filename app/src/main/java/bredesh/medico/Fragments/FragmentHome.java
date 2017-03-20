package bredesh.medico.Fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import bredesh.medico.Fragments.PictureItem.ItemListAdapter;
import bredesh.medico.Fragments.PictureItem.PictureItem;
import bredesh.medico.Fragments.PictureItem.SQLiteHelper;
import bredesh.medico.R;


public class FragmentHome extends Fragment {
    //PictureItem
    private ItemListAdapter adapter = null;
    public static SQLiteHelper sqLiteHelper;

    public FragmentHome(){
        //
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment_home, container, false);

        //PictureItem
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sqLiteHelper = new SQLiteHelper(getContext(), "FoodDB.sqlite", null, 1);
        }

        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS FOOD(Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, price VARCHAR, image BLOB)");

        ArrayList<PictureItem> list = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            adapter = new ItemListAdapter(getContext(), R.layout.activity_picture_item, list);
        }

        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setAdapter(adapter);
//        btnAddNew = (Button) findViewById(R.id.btnAddNew);

        // get all data from sqlite
        Cursor cursor = sqLiteHelper.getData("SELECT * FROM FOOD");
        list.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String exerciseName = cursor.getString(1);
            String numAlerts = cursor.getString(2);
            byte[] image = cursor.getBlob(3);
            //            todo
            String crateBy = cursor.getString(2);
            String txtDate = cursor.getString(2);

            list.add(new PictureItem(exerciseName, numAlerts, crateBy, txtDate, image, id));
        }
        adapter.notifyDataSetChanged();

        // Inflate the layout for this fragment
        return view;
    }


}

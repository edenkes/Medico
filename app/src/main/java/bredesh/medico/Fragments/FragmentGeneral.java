package bredesh.medico.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.Fragments.RecyclerAdapterMediGo.RecyclerAdapterGeneral;
import bredesh.medico.R;

/**
 * Created by edenk on 12/13/2017.
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * getFragmentTitle()
 * protected abstract void addItemToList(int index, int id, String time, String name, String uriVideo, String uriImage,
    int[] days, boolean detailedTimes, String allTimes, String alertSoundUri);
 * protected abstract RecyclerAdapterGeneral<T> getNewRecyclerAdapter();
 * protected abstract Intent getNewIntent();
 *
 * create an instance of this fragment.
 **/
public abstract class FragmentGeneral<T> extends Fragment{
    protected Context context;
    protected List<T> arrayList;
    protected RecyclerView lvHome;
    protected RecyclerAdapterGeneral<T> adapter;
    protected MedicoDB.KIND kindGeneral;
    protected MedicoDB db;

    protected void setUpOnCreateView(View view){
        TextView titleFragment = view.findViewById(R.id.tvTitle);
        titleFragment.setText(getFragmentTitle());

        lvHome = view.findViewById(R.id.recycler_view);
        lvHome.setLayoutManager(new LinearLayoutManager(getActivity()));
        context = getActivity().getApplicationContext();

        setArrayList();

        FloatingActionButton btAddAlert = view.findViewById(R.id.addAlert);
        btAddAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getNewIntent());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        bredesh.medico.Fragments.ExerciseFragment fragment = new bredesh.medico.Fragments.ExerciseFragment();
        ft.replace(R.id.fragment_place, fragment);
        ft.commit();
    }

    private void setArrayList() {
        db = new MedicoDB(getActivity().getApplicationContext());
        Cursor c = db.getAllAlertsByKind(kindGeneral);

        arrayList = new ArrayList<>();
        int index;

        for (c.moveToFirst(), index = 0; !c.isAfterLast(); c.moveToNext(), index++) {
            int id =                c.getInt(c.getColumnIndex(MedicoDB.KEY_ID));
            String time =           c.getString(c.getColumnIndex(MedicoDB.KEY_TIME));
            String name =           c.getString(c.getColumnIndex(MedicoDB.KEY_NAME));
            String uriVideo =       c.getString(c.getColumnIndex(MedicoDB.KEY_URIVIDEO));
            String uriImage =       c.getString(c.getColumnIndex(MedicoDB.KEY_URIIMAGE));
            String alertSoundUri =  c.getString(c.getColumnIndex(MedicoDB.KEY_ALERT_SOUND_URI));

            String[] times = time.split(Pattern.quote(getResources().getString(R.string.times_splitter)));
            boolean detailedTimes = true;
            String allTimes = time;
            if (times.length > 4 || (times.length > 3 && uriVideo != null)  || (times.length > 2 && uriImage != null)) {
                time = String.format(getString(R.string.several_times), times.length);
                detailedTimes = false;
            }

            int[] days = new int[7];
            days[0] = c.getInt(c.getColumnIndex(MedicoDB.SUNDAY));
            days[1] = c.getInt(c.getColumnIndex(MedicoDB.MONDAY));
            days[2] = c.getInt(c.getColumnIndex(MedicoDB.TUESDAY));
            days[3] = c.getInt(c.getColumnIndex(MedicoDB.WEDNESDAY));
            days[4] = c.getInt(c.getColumnIndex(MedicoDB.THURSDAY));
            days[5] = c.getInt(c.getColumnIndex(MedicoDB.FRIDAY));
            days[6] = c.getInt(c.getColumnIndex(MedicoDB.SATURDAY));

            addItemToList(index,id, time, name, uriVideo, uriImage, days, detailedTimes, allTimes, alertSoundUri);
        }
        c.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        setArrayList();
        adapter = getNewRecyclerAdapter();
        lvHome.setAdapter(adapter);
    }

    protected abstract CharSequence getFragmentTitle();

    protected abstract void addItemToList(int index, int id, String time, String name, String uriVideo, String uriImage,
                                          int[] days, boolean detailedTimes, String allTimes, String alertSoundUri);

    protected abstract RecyclerAdapterGeneral<T> getNewRecyclerAdapter();

    protected abstract Intent getNewIntent();


}

package bredesh.medico.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import bredesh.medico.Camera.VideoData;
import bredesh.medico.Fragments.PictureItem.ExerciseRecyclerAdapter;
import bredesh.medico.Fragments.PictureItem.VideoItem;
import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.R;

public class FragmentHome extends Fragment {
    private Context context;
    List<VideoItem> arrayList;
    RecyclerView lvHome;
    ExerciseRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.fragment_fragment_home, container, false);
        lvHome = (RecyclerView) view.findViewById(R.id.recycler_view);
        lvHome.setLayoutManager(new LinearLayoutManager(getActivity()));
        context = getActivity().getApplicationContext();

        setArrayList();

       //final Adapter adapter = new Adapter(context, R.layout.exercises_item, arrayList);
        adapter = new ExerciseRecyclerAdapter(context,arrayList,getActivity());
        lvHome.setAdapter(adapter);

        Resources resources = getResources();

        final AlertDialog dialog = new AlertDialog.Builder(this.getActivity())
                .setMessage(resources.getString(R.string.remove_all_alerts))
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                MedicoDB db = new MedicoDB(getActivity().getApplicationContext());
                    db.DeleteAllAlerts();
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.all_alerts_deleted) , Toast.LENGTH_LONG).show();

                    //Refresh the home page
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    FragmentHome fragment = new FragmentHome();
                    ft.replace(R.id.fragment_place, fragment);
                    ft.commit();
                    }
            })
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .create();

        FloatingActionButton btDeleteForever = (FloatingActionButton) view.findViewById(R.id.deleteAll);
        btDeleteForever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        FloatingActionButton btAddAlert = (FloatingActionButton) view.findViewById(R.id.addAlert);
        btAddAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), VideoData.class));
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentHome fragment = new FragmentHome();
        ft.replace(R.id.fragment_place, fragment);
        ft.commit();
    }

    private void setArrayList() {
        MedicoDB db = new MedicoDB(getActivity().getApplicationContext());
        Cursor c = db.getAllAlertsByKind(MedicoDB.KIND.Exercise);
        arrayList = new ArrayList<>();
        int index;

        for ( c.moveToFirst(),  index=0; !c.isAfterLast(); c.moveToNext(), index++){
            int id = c.getInt(c.getColumnIndex(db.KEY_ID));
            String time = c.getString(c.getColumnIndex(db.KEY_TIME));
            String [] times = time.split(Pattern.quote(getResources().getString(R.string.times_splitter)));
            boolean detailedTimes = true;
            String allTimes = time;
            if (times.length > 3) {
                time = String.format(getString(R.string.several_times), times.length);
                detailedTimes = false;
            }

            String name = c.getString(c.getColumnIndex(db.KEY_NAME));
            String uri = c.getString(c.getColumnIndex(db.URIVIDEO));
            int[] days = new int[7];
            days[0] = c.getInt(c.getColumnIndex(db.SUNDAY));
            days[1] = c.getInt(c.getColumnIndex(db.MONDAY));
            days[2] = c.getInt(c.getColumnIndex(db.TUESDAY));
            days[3] = c.getInt(c.getColumnIndex(db.WEDNESDAY));
            days[4] = c.getInt(c.getColumnIndex(db.THURSDAY));
            days[5] = c.getInt(c.getColumnIndex(db.FRIDAY));
            days[6] = c.getInt(c.getColumnIndex(db.SATURDAY));
            int noOfRepetitions = c.getInt(c.getColumnIndex(db.KEY_REPEATS));

            arrayList.add(index, new VideoItem(id, time, name, uri, days, noOfRepetitions, detailedTimes, allTimes, MedicoDB.KIND.Exercise));
        }
        c.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        setArrayList();
        adapter = new ExerciseRecyclerAdapter(context,arrayList,getActivity());
        lvHome.setAdapter(adapter);
    }


}

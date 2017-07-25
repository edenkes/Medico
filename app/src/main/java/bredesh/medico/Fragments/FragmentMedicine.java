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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import bredesh.medico.Camera.MedicineData;
import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.Fragments.PictureItem.MedicineItem;
import bredesh.medico.Fragments.PictureItem.MedicineRecyclerAdapter;
import bredesh.medico.R;

/**
 * Created by Omri on 12/06/2017.
 */

public class FragmentMedicine extends Fragment {
        private Context context;
        List<MedicineItem> arrayList;
        RecyclerView lvHome;
        MedicineRecyclerAdapter adapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)  {
            View view = inflater.inflate(R.layout.fragment_fragment_home, container, false);
            lvHome = (RecyclerView) view.findViewById(R.id.recycler_view);
            lvHome.setLayoutManager(new LinearLayoutManager(getActivity()));
            context = getActivity().getApplicationContext();

            TextView title = (TextView) view.findViewById(R.id.tvExercises);
            title.setText(getResources().getText(R.string.menu_item_2));

            setArrayList();

            adapter = new MedicineRecyclerAdapter(context,arrayList,getActivity());
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
                            bredesh.medico.Fragments.FragmentHome fragment = new bredesh.medico.Fragments.FragmentHome();
                            ft.replace(R.id.fragment_place, fragment);
                            ft.commit();
                        }
                    })
                    .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                    .create();


            FloatingActionButton btAddAlert = (FloatingActionButton) view.findViewById(R.id.addAlert);
            btAddAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), MedicineData.class));
                }
            });

            return view;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            bredesh.medico.Fragments.FragmentHome fragment = new bredesh.medico.Fragments.FragmentHome();
            ft.replace(R.id.fragment_place, fragment);
            ft.commit();
        }

        private void setArrayList() {
            MedicoDB db = new MedicoDB(getActivity().getApplicationContext());
            Cursor c = db.getAllAlertsByKind(MedicoDB.KIND.Medicine);

            arrayList = new ArrayList<>();
            int index;

                for (c.moveToFirst(), index = 0; !c.isAfterLast(); c.moveToNext(), index++) {
                int id = c.getInt(c.getColumnIndex(MedicoDB.KEY_ID));
                String time = c.getString(c.getColumnIndex(MedicoDB.KEY_TIME));
                String[] times = time.split(Pattern.quote(getResources().getString(R.string.times_splitter)));
                boolean detailedTimes = true;
                String allTimes = time;
                if (times.length > 3) {
                    time = String.format(getString(R.string.several_times), times.length);
                    detailedTimes = false;
                }

                String name = c.getString(c.getColumnIndex(MedicoDB.KEY_NAME));
                String uri = c.getString(c.getColumnIndex(MedicoDB.URIVIDEO));
                int[] days = new int[7];
                days[0] = c.getInt(c.getColumnIndex(MedicoDB.SUNDAY));
                days[1] = c.getInt(c.getColumnIndex(MedicoDB.MONDAY));
                days[2] = c.getInt(c.getColumnIndex(MedicoDB.TUESDAY));
                days[3] = c.getInt(c.getColumnIndex(MedicoDB.WEDNESDAY));
                days[4] = c.getInt(c.getColumnIndex(MedicoDB.THURSDAY));
                days[5] = c.getInt(c.getColumnIndex(MedicoDB.FRIDAY));
                days[6] = c.getInt(c.getColumnIndex(MedicoDB.SATURDAY));

                Cursor cMedicine = db.getMedicineByID(id);
                int amount =  cMedicine.getInt(cMedicine.getColumnIndex(MedicoDB.KEY_AMOUNT));
                String type =    cMedicine.getString(cMedicine.getColumnIndex(MedicoDB.KEY_TYPE));
                String special = cMedicine.getString(cMedicine.getColumnIndex(MedicoDB.KEY_SPECIAL));
                String notes =   cMedicine.getString(cMedicine.getColumnIndex(MedicoDB.KEY_NOTES));

                arrayList.add(index, new MedicineItem(id, time, name, uri, days, detailedTimes, allTimes,
                        MedicoDB.KIND.Medicine, type, special, notes, ""+ amount));
            }
            c.close();
        }

        @Override
        public void onResume() {
            super.onResume();
            setArrayList();
            adapter = new MedicineRecyclerAdapter(context,arrayList,getActivity());
            lvHome.setAdapter(adapter);
        }
}

package bredesh.medico.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.Fragments.DataMediGo.ExerciseDa;
import bredesh.medico.Fragments.ItemMediGo.ExerciseIt;
import bredesh.medico.Fragments.RecyclerAdapterMediGo.ExerciseRA;
import bredesh.medico.Fragments.RecyclerAdapterMediGo.RecyclerAdapterGeneral;
import bredesh.medico.R;


public class ExerciseFragment extends FragmentGeneral<ExerciseIt>{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.fragment_fragment_home, container, false);
        kindGeneral = MedicoDB.KIND.Exercise;

        setUpOnCreateView(view);
        adapter = new ExerciseRA(context,arrayList,getActivity());
        lvHome.setAdapter(adapter);

        return view;
    }

    @Override
    protected CharSequence getFragmentTitle() {
        return getResources().getText(R.string.exercises);
    }

    @Override
    protected void addItemToList(int index, int id, String time, String name, String uriVideo, String uriImage,
                                 int[] days, boolean detailedTimes, String allTimes, String alertSoundUri, Cursor cursor) {
        int noOfRepetitions = cursor.getInt(cursor.getColumnIndex(MedicoDB.KEY_REPEATS));
        int repetitionType = cursor.getInt(cursor.getColumnIndex(MedicoDB.KEY_REPETITION_TYPE));

        arrayList.add(index, new ExerciseIt(id, time, name, uriVideo, days, noOfRepetitions,
                repetitionType, detailedTimes, allTimes, kindGeneral, alertSoundUri));
    }

    @Override
    protected RecyclerAdapterGeneral<ExerciseIt> getNewRecyclerAdapter() {
        return new ExerciseRA(context,arrayList,getActivity());
    }

    @Override
    protected Intent getNewIntent() {
        return new Intent(getActivity(), ExerciseDa.class);
    }
}



        /*Fragment {
    private Context context;
    List<ExerciseIt> arrayList;
    RecyclerView lvHome;
    ExerciseRA adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.fragment_fragment_home, container, false);
        lvHome = view.findViewById(R.id.recycler_view);
        lvHome.setLayoutManager(new LinearLayoutManager(getActivity()));
        context = getActivity().getApplicationContext();

        setArrayList();

        //final Adapter adapter = new Adapter(context, R.layout.exercises_item, arrayList);
        adapter = new ExerciseRA(context,arrayList,getActivity());
        lvHome.setAdapter(adapter);

        Resources resources = getResources();

        FloatingActionButton btAddAlert = view.findViewById(R.id.addAlert);
        btAddAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ExerciseDa.class));
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ExerciseFragment fragment = new ExerciseFragment();
        ft.replace(R.id.fragment_place, fragment);
        ft.commit();
    }

    private void setArrayList() {
        MedicoDB db = new MedicoDB(getActivity().getApplicationContext());
        Cursor c = db.getAllAlertsByKind(MedicoDB.KIND.Exercise);
        arrayList = new ArrayList<>();
        int index;

        for ( c.moveToFirst(),  index=0; !c.isAfterLast(); c.moveToNext(), index++){
            int id = c.getInt(c.getColumnIndex(MedicoDB.KEY_ID));
            String time = c.getString(c.getColumnIndex(MedicoDB.KEY_TIME));
            String [] times = time.split(Pattern.quote(getResources().getString(R.string.times_splitter)));
            boolean detailedTimes = true;
            String allTimes = time;
            if (times.length > 3) {
                time = String.format(getString(R.string.several_times), times.length);
                detailedTimes = false;
            }

            String name = c.getString(c.getColumnIndex(MedicoDB.KEY_NAME));
            String uri = c.getString(c.getColumnIndex(MedicoDB.URIVIDEO));
            String alertSoundUri = c.getString(c.getColumnIndex(MedicoDB.KEY_ALERT_SOUND_URI));
            int[] days = new int[7];
            days[0] = c.getInt(c.getColumnIndex(MedicoDB.SUNDAY));
            days[1] = c.getInt(c.getColumnIndex(MedicoDB.MONDAY));
            days[2] = c.getInt(c.getColumnIndex(MedicoDB.TUESDAY));
            days[3] = c.getInt(c.getColumnIndex(MedicoDB.WEDNESDAY));
            days[4] = c.getInt(c.getColumnIndex(MedicoDB.THURSDAY));
            days[5] = c.getInt(c.getColumnIndex(MedicoDB.FRIDAY));
            days[6] = c.getInt(c.getColumnIndex(MedicoDB.SATURDAY));
            int noOfRepetitions = c.getInt(c.getColumnIndex(MedicoDB.KEY_REPEATS));
            String repetitionTpye = c.getString(c.getColumnIndex(MedicoDB.KEY_REPETITION_TYPE));

            arrayList.add(index, new ExerciseIt(id, time, name, uri, days, noOfRepetitions, repetitionTpye, detailedTimes, allTimes, MedicoDB.KIND.Exercise, alertSoundUri));
        }
        c.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        setArrayList();
        adapter = new ExerciseRA(context,arrayList,getActivity());
        lvHome.setAdapter(adapter);
    }


}
*/
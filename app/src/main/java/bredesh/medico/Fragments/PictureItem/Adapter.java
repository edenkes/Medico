package bredesh.medico.Fragments.PictureItem;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bredesh.medico.Camera.LocalDBManager;
import bredesh.medico.R;

/**
 * Created by edenk on 22-Apr-17.
 */

public class Adapter extends ArrayAdapter<VideoItem> {

    private Context context;
    private int id;
    private ArrayList<VideoItem> arrayList;


    public Adapter(Context context, @LayoutRes int resource, ArrayList<VideoItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.id = resource;
        this.arrayList = objects;
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public VideoItem getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrayList.indexOf(getItem(position));   //ToFix
    }
/*
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(id, null);
        }

//        setupTestView(convertView);

        final VideoItem item = arrayList.get(position);

*//*

        item.switch1 = (Switch) convertView.findViewById(R.id.switch1);
//        switch1 = (Switch) convertView.findViewById(R.id.switch1);
        item.btPlay = (Button) convertView.findViewById(R.id.btPlay);

        item.btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context.getApplicationContext(), "onClickCacked! :)", Toast.LENGTH_SHORT).show();
//                tvExercisesName.setText("eden keshet nkl");

            }
        });
        item.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                item.setIsChecked(isChecked);
                Toast.makeText(context.getApplicationContext(), "onClickCacked! :)", Toast.LENGTH_SHORT).show();
                Log.i("tag", "onCheckedChanged: fg");
                item.setName("eden keshet");

            }
        });
*//*

        convertView.setTag(item);

        *//*
        tvExercisesName.setText(item.getName());
        tvExerciseTime.setText(item.getTime());
        switch1.setChecked(item.isChecked());

        int[] days = item.getDays();
        if(days[0] == 1) tvSUN.setVisibility(View.VISIBLE);
        if(days[1] == 1) tvMON.setVisibility(View.VISIBLE);
        if(days[2] == 1) tvTUE.setVisibility(View.VISIBLE);
        if(days[3] == 1) tvWED.setVisibility(View.VISIBLE);
        if(days[4] == 1) tvTHU.setVisibility(View.VISIBLE);
        if(days[5] == 1) tvFRI.setVisibility(View.VISIBLE);
        if(days[6] == 1) tvSAT.setVisibility(View.VISIBLE);
*//*
        return super.getView(position, convertView, parent);
    }

    */

    /*

    private void setupTestView(View gridView) {
        btPlay = (Button) gridView.findViewById(R.id.btPlay);
        tvExercisesName = (TextView) gridView.findViewById(R.id.tvExercisesName);
        tvExerciseTime = (TextView) gridView.findViewById(R.id.tvExerciseTime);
        tvSUN = (TextView) gridView.findViewById(R.id.tvSUN);
        tvMON = (TextView) gridView.findViewById(R.id.tvMON);
        tvTUE = (TextView) gridView.findViewById(R.id.tvTUE);
        tvWED = (TextView) gridView.findViewById(R.id.tvWED);
        tvTHU = (TextView) gridView.findViewById(R.id.tvTHU);
        tvFRI = (TextView) gridView.findViewById(R.id.tvFRI);
        tvSAT = (TextView) gridView.findViewById(R.id.tvSAT);

    }

*/


    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mainViewHolder = null;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
//            LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
            convertView = inflater.inflate(R.layout.exercises_item, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.btPlay = (Button) convertView.findViewById(R.id.btPlay);
            viewHolder.tvExercisesName = (TextView) convertView.findViewById(R.id.tvExercisesName);

            VideoItem row_pos = arrayList.get(position);

            viewHolder.btPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });

            viewHolder.tvExercisesName.setText("eden keshet :)");
            convertView.setTag(viewHolder);
        } else {
            mainViewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }


    public class ViewHolder{
        TextView tvExercisesName, tvExerciseTime;
        TextView tvSUN, tvMON, tvTUE, tvWED, tvTHU, tvFRI, tvSAT;
        Switch switch1;
        Button btPlay;
    }

}

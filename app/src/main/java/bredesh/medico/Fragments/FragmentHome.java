package bredesh.medico.Fragments;

import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import bredesh.medico.Camera.LocalDBManager;
import bredesh.medico.Fragments.PictureItem.Adapter;
import bredesh.medico.Fragments.PictureItem.AlertAdapter;
import bredesh.medico.Fragments.PictureItem.VideoItem;
import bredesh.medico.MainActivity;
import bredesh.medico.PushNotfications.NotificationService;
import bredesh.medico.R;

import static android.content.Context.NOTIFICATION_SERVICE;


public class FragmentHome extends Fragment implements AdapterView.OnItemClickListener {

    ListView lvHome;
//    Button btPlay;
    private Uri videoUri;
    private int resource;
    ArrayList<VideoItem> arrayList;


    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int notification_id;
    private RemoteViews remoteViews;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {

        View view = inflater.inflate(R.layout.fragment_fragment_home, container, false);
        lvHome = (ListView) view.findViewById(R.id.lvHome);


        //Notifications
        context = getActivity().getApplicationContext();
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(context);

        remoteViews = new RemoteViews(context.getPackageName(),R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.notif_icon,R.mipmap.ic_medico);
        remoteViews.setTextViewText(R.id.notif_title,"TEXT");
        remoteViews.setProgressBar(R.id.progressBar,100,40,true);



        view.findViewById(R.id.button_show_notif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notification_id = (int) System.currentTimeMillis();

                Intent button_intent = new Intent("button_click");
                button_intent.putExtra("id",notification_id);
                PendingIntent button_pending_event = PendingIntent.getBroadcast(context,notification_id,
                        button_intent,0);

                remoteViews.setOnClickPendingIntent(R.id.button,button_pending_event);

                Intent notification_intent = new Intent(context,MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context,0,notification_intent,0);

                builder.setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setCustomBigContentView(remoteViews)
                        .setContentIntent(pendingIntent);


                notificationManager.notify(notification_id,builder.build());


            }
        });


        LocalDBManager db = new LocalDBManager(getActivity().getApplicationContext());
        Cursor c = db.getAllAlerts();
        arrayList = new ArrayList<>();
        int index;
        for ( c.moveToFirst(),  index=0; !c.isAfterLast(); c.moveToNext(), index++){
            String time = c.getString(c.getColumnIndex(db.KEY_TIME));
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

            arrayList.add(index, new VideoItem(time, name, uri, days));
        }
        c.close();
        resource = R.layout.exercises_item;
        final Adapter adapter = new Adapter(getActivity().getApplicationContext(), resource, arrayList);
        lvHome.setAdapter(adapter);


        lvHome.setOnItemClickListener(this);

//        Button btPlay = (Button) view.findViewById(R.id.btPlay);

        final Intent SERVICE_INTENT = new Intent(getActivity().getBaseContext(), NotificationService.class);
        getActivity().startService(SERVICE_INTENT);

        lvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity().getApplicationContext(), "onListItemClick: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                videoUri = ((VideoItem)(adapter.getItem(position))).getUri();

                if(videoUri != null ) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                    startActivity(intent);
                }else
                    Toast.makeText(getActivity().getApplicationContext(), "Couldn't find the video/photo", Toast.LENGTH_SHORT).show();

                Button btPlay = (Button) parent.findViewById(R.id.btPlay);

                btPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity().getApplicationContext(), "onClick: Play", Toast.LENGTH_SHORT).show();
                        if(videoUri != null ) {
//                            Toast.makeText(getActivity().getApplicationContext(), "Intent video", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                            startActivity(intent);
                        }else
                            Toast.makeText(getActivity().getApplicationContext(), "Couldn't find the video/photo", Toast.LENGTH_SHORT).show();

                    }
                });



            }

        });

/*

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "onListItemClick: play", Toast.LENGTH_SHORT).show();

            }
        });*/



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        lvHome.setAdapter(new AlertAdapter(getActivity().getApplicationContext()));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        String member_name = arrayList.get(position).getName();
        Toast.makeText(getActivity().getApplicationContext(), "" + member_name,
                Toast.LENGTH_SHORT).show();
    }

}

package bredesh.medico.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import bredesh.medico.Camera.ChangeVideoData;
import bredesh.medico.Camera.LocalDBManager;
import bredesh.medico.Fragments.PictureItem.Adapter;
import bredesh.medico.Fragments.PictureItem.VideoItem;
import bredesh.medico.R;

public class FragmentHome extends Fragment {
    private Context context;
    ArrayList<VideoItem> arrayList;
    ListView lvHome;

//    private Uri videoUri;
//    private int resource;
/*
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int notification_id;
    private RemoteViews remoteViews;
*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.fragment_fragment_home, container, false);
        lvHome = (ListView) view.findViewById(R.id.lvHome);
        context = getActivity().getApplicationContext();

        setNotifications(view);

        setArrayList();

        final Adapter adapter = new Adapter(context, R.layout.exercises_item, arrayList);
        lvHome.setAdapter(adapter);

        lvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity().getApplicationContext(), "Enter to setting", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), ChangeVideoData.class);
                startActivity(intent);
            }
        });

        ImageButton btDeleteForever = (ImageButton) view.findViewById(R.id.btDeleteForever);
        btDeleteForever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDBManager db = new LocalDBManager(getActivity().getApplicationContext());
                db.DeleteAllAlerts();
                Toast.makeText(getActivity().getApplicationContext(), "Alerts been Removed", Toast.LENGTH_LONG).show();

                //Refresh the home page
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                FragmentHome fragment = new FragmentHome();
                ft.replace(R.id.fragment_place, fragment);
                ft.commit();
            }
        });

        return view;
    }

    private void setArrayList() {
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
    }

    private void setNotifications(View view) {
/*
        //Notifications
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
*/

    }

    @Override
    public void onResume() {
        super.onResume();
        lvHome.setAdapter(new Adapter(context, R.layout.exercises_item, arrayList));
    }
}

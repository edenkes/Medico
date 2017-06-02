package bredesh.medico.Menu;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bredesh.medico.R;

/**
 * Created by Omri on 01/06/2017.
 */

public class MenuAdapterRecycler extends RecyclerView.Adapter<MenuAdapterRecycler.CustomViewHolder> {

    private List<Item_Menu> menuItems;
    private Context context;
    private Activity activity;

    public MenuAdapterRecycler(Context context, List<Item_Menu> menuItems, Activity activity)
    {
        this.context = context;
        this.menuItems = menuItems;
        this.activity = activity;
    }

    @Override
    public MenuAdapterRecycler.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_menu_item, viewGroup, false);
        return new MenuAdapterRecycler.CustomViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MenuAdapterRecycler.CustomViewHolder holder, int position) {
        final Item_Menu item = menuItems.get(position);

        holder.itemImage.setImageResource(item.image_src);
        holder.itemText.setText(item.text);

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.nextScreen != null)
                    activity.startActivity(item.nextScreen);
                else Toast.makeText(context.getApplicationContext(),
                        context.getResources().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != menuItems ? menuItems.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView itemText;
        private ImageButton itemImage;
        private LinearLayout v;

        private CustomViewHolder(View convertView) {
            super(convertView);
            this.v = (LinearLayout) convertView.findViewById(R.id.whole_view);
            this.itemText = (TextView) convertView.findViewById(R.id.itemText);
            this.itemImage = (ImageButton) convertView.findViewById(R.id.itemImage);
        }
    }


}



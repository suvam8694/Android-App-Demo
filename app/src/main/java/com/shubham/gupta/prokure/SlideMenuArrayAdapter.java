package com.shubham.gupta.prokure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by shubham on 9/27/15.
 */

public class SlideMenuArrayAdapter extends ArrayAdapter<String>
{
    private String[] values;
    private int[] images;   // Contains resource id of images
    private Context context;

    public SlideMenuArrayAdapter(Context context, int resourceId, String[] values, int[] images)
    {
        super(context, resourceId, values);

        this.context = context;
        this.values = values;
        this.images = images;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.slide_menu_list_item, parent, false);

        TextView textView = (TextView) itemView.findViewById(R.id.slide_menu_item_textview);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.slide_menu_item_icon);
        textView.setText(values[position]);
        imageView.setImageResource(images[position]);

        return itemView;
    }
}

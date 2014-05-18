package com.hnaohiro.bangohan.app;

import java.util.List;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cengalabs.flatui.FlatUI;

/**
 * Created by hnaohiro on 2014/05/11.
 */
public class UsersAdaptor extends ArrayAdapter<UserData> {

    private LayoutInflater layoutInflater;
    private Resources resources;

    public UsersAdaptor(Context context, int textViewResourceId, List<UserData> objects) {
        super(context, textViewResourceId, objects);

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        resources = layoutInflater.getContext().getResources();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserData user = getItem(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        Bitmap statusImage;
        String userTime;
        int textColor;

        if (user.isDefined()) {
            if (user.isNeed()) {
                userTime = user.getTime();
                statusImage = BitmapFactory.decodeResource(resources, R.drawable.fork);
                textColor = resources.getColor(R.color.sky_primary);
            } else {
                userTime = "";
                statusImage = BitmapFactory.decodeResource(resources, R.drawable.remove);
                textColor = resources.getColor(R.color.candy_primary);
            }
        } else {
            userTime = "";
            statusImage = BitmapFactory.decodeResource(resources, R.drawable.question);
            textColor = resources.getColor(R.color.dark_light);
        }

        ImageView userStatusImageView = (ImageView) convertView.findViewById(R.id.user_status);
        userStatusImageView.setImageBitmap(statusImage);

        TextView userTimeTextView = (TextView) convertView.findViewById(R.id.user_time);
        userTimeTextView.setTextColor(textColor);
        userTimeTextView.setText(userTime);

        TextView userNameTextView = (TextView) convertView.findViewById(R.id.user_name);
        userNameTextView.setTextColor(textColor);
        userNameTextView.setText(user.getName());

        return convertView;
    }
}

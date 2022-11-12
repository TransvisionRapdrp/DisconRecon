package com.example.disconrecon_library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.disconrecon_library.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class InfoWindowCustom implements GoogleMap.InfoWindowAdapter {
    Context context;
    LayoutInflater inflater;
    public InfoWindowCustom(Context context) {
        this.context = context;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.custom_info_window, null);

        TextView title = (TextView) v.findViewById(R.id.info_window_title);
        TextView subtitle = (TextView) v.findViewById(R.id.info_window_subtitle);
        title.setText(marker.getTitle());
        subtitle.setText(marker.getSnippet());
        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}

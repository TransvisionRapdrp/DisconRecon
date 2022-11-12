package com.example.disconrecon_library.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disconrecon_library.R;
import com.example.disconrecon_library.ViewAllLocation;
import com.example.disconrecon_library.model.DisconData;

import java.util.List;
import java.util.Random;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ApproveHolder> {
    private List<DisconData> arrayList;
    private Context context;
    private static int currentPosition = 0;

    public LocationAdapter(Context context, List<DisconData> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public LocationAdapter.ApproveHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.location_adapter, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new LocationAdapter.ApproveHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApproveHolder approveHolder, int i) {
        DisconData disconData = arrayList.get(i);
        Random r = new Random();
        int red = r.nextInt(255 + 1);
        int green = r.nextInt(255 + 1);
        int blue = r.nextInt(255 + 1);
        GradientDrawable draw = new GradientDrawable();
        draw.setShape(GradientDrawable.OVAL);
        draw.setColor(Color.rgb(red, green, blue));
        approveHolder.tv_id.setBackground(draw);
        approveHolder.tv_id.setText(String.valueOf(i + 1));
        approveHolder.tv_cons_name.setText(disconData.getCONSUMER_NAME());
        approveHolder.tv_cons_no.setText(disconData.getACCT_ID());
        approveHolder.tv_address.setText(disconData.getADD1());

        if (currentPosition == i) {
            approveHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
        } else {
            approveHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ApproveHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_cons_name, tv_cons_no, tv_address, tv_id;
        CardView cardView;

        public ApproveHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_cons_name = itemView.findViewById(R.id.txt_cons_name);
            tv_cons_no = itemView.findViewById(R.id.txt_cons_no);
            tv_address = itemView.findViewById(R.id.txt_address);
            tv_id = itemView.findViewById(R.id.txt_user_first_letter);
            cardView=itemView.findViewById(R.id.cardview);
        }

        //--------------------------------------------------------------------------------------------------------------------
        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            currentPosition = pos;
            notifyDataSetChanged();
            if (!TextUtils.isEmpty(arrayList.get(pos).getLAT()) || !TextUtils.isEmpty(arrayList.get(pos).getLON())) {
                double lat = Double.parseDouble(arrayList.get(pos).getLAT());
                double lon = Double.parseDouble(arrayList.get(pos).getLON());
                ((ViewAllLocation) context).drawPolyline(lat, lon);
            } else Toast.makeText(context, "LocationActivity Not Available", Toast.LENGTH_SHORT).show();
        }
    }
}

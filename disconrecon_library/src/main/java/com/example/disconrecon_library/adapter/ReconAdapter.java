package com.example.disconrecon_library.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disconrecon_library.LocationActivity;
import com.example.disconrecon_library.R;
import com.example.disconrecon_library.model.DisconData;

import java.util.ArrayList;
import java.util.List;

import static com.example.disconrecon_library.values.constant.CONNECTION_UPDATE_DIALOG;

public class ReconAdapter extends RecyclerView.Adapter<ReconAdapter.Discon_Holder> implements Filterable {
    private List<DisconData> arraylist;
    private List<DisconData> filteredList;
    private Context context;
    private static OnItemClickListener mOnItemClickLister;

    public interface OnItemClickListener {
        void show_connection_update_dialog(int id, DisconData details);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickLister = listener;
    }

    public ReconAdapter(Context context, List<DisconData> arraylist) {
        this.context = context;
        this.arraylist = arraylist;
        this.filteredList = arraylist;
    }

    @NonNull
    @Override
    public ReconAdapter.Discon_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discon_adapter_layout, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new Discon_Holder(view);
    }

    @Override
    public void onBindViewHolder(ReconAdapter.Discon_Holder holder, int position) {
        DisconData reconData = arraylist.get(position);
        holder.tv_cons_name.setText(reconData.getCONSUMER_NAME());
        holder.tv_cons_no.setText(reconData.getACCT_ID());
        holder.tv_arrears.setText(String.format("%s %s", context.getResources().getString(R.string.rupee), reconData.getARREARS()));
        holder.tv_prev_read.setText(reconData.getPREVREAD());
        holder.tv_final_read.setText(reconData.getMTR_READ());
        if (!TextUtils.isEmpty(reconData.getMTR_READ())) {
            holder.tv_disconnect.setText(context.getResources().getText(R.string.reconnected));
            holder.tv_disconnect.setTextColor(context.getResources().getColor(R.color.red));
            holder.curr_read_show_hide.setVisibility(View.VISIBLE);
        } else {
            holder.tv_disconnect.setText(context.getResources().getText(R.string.reconnect));
            holder.tv_disconnect.setTextColor(context.getResources().getColor(R.color.black));
            holder.curr_read_show_hide.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String search = constraint.toString();
                if (search.isEmpty())
                    arraylist = filteredList;
                else {
                    ArrayList<DisconData> filterlist = new ArrayList<>();
                    for (int i = 0; i < filteredList.size(); i++) {
                        DisconData details = filteredList.get(i);
                        //todo searching based on discon_acc_id
                        if (details.getACCT_ID().contains(search) || details.getCONSUMER_NAME().contains(search)) {
                            filterlist.add(details);
                        }
                    }
                    arraylist = filterlist;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = arraylist;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arraylist = (ArrayList<DisconData>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class Discon_Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_cons_no, tv_arrears, tv_prev_read, tv_disconnect, tv_cons_name, tv_final_read, tv_location;
        LinearLayout curr_read_show_hide;

        public Discon_Holder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            curr_read_show_hide = itemView.findViewById(R.id.lin_final_reading);
            tv_cons_no = itemView.findViewById(R.id.txt_account_id);
            tv_arrears = itemView.findViewById(R.id.txt_arrears);
            tv_prev_read = itemView.findViewById(R.id.txt_prevread);
            tv_disconnect = itemView.findViewById(R.id.txt_disconnect);
            tv_cons_name = itemView.findViewById(R.id.txt_cons_name);
            tv_location = itemView.findViewById(R.id.txt_location);
            tv_final_read = itemView.findViewById(R.id.txt_finalread);
            tv_location.setOnClickListener(this);
            tv_disconnect.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            DisconData details = arraylist.get(position);
            int id = v.getId();
            if (id == R.id.txt_location) {
                Intent intent = new Intent(context, LocationActivity.class);
                intent.putExtra("LOCATION", details);
                context.startActivity(intent);
            }
            if (id == R.id.txt_disconnect) {
                if (!TextUtils.isEmpty(details.getMTR_READ()))
                    Toast.makeText(context, "Account ID already Reconnected", Toast.LENGTH_SHORT).show();
                else
                    mOnItemClickLister.show_connection_update_dialog(CONNECTION_UPDATE_DIALOG, details);
            }
        }
    }
}

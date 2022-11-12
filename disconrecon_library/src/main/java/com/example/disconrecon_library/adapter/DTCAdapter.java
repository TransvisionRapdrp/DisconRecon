package com.example.disconrecon_library.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disconrecon_library.R;
import com.example.disconrecon_library.model.DTC;

import java.util.ArrayList;
import java.util.List;

import static com.example.disconrecon_library.values.constant.DTC_DETAILS_UPDATE_DIALOG;


public class DTCAdapter extends RecyclerView.Adapter<DTCAdapter.FeederHolder> implements Filterable {
    private List<DTC> arrayList;
    private List<DTC> filteredList;
    private Context context;
    private static OnItemClickListener mOnItemClickLister;

    public interface OnItemClickListener {
        void show_dtc_details_update_dialog(int id, DTC dtc);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickLister = listener;
    }

    public DTCAdapter(List<DTC> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.filteredList = arrayList;
    }

    @NonNull
    @Override
    public DTCAdapter.FeederHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dtc_adapter_layout, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new FeederHolder(view);
    }

    @Override
    public void onBindViewHolder(DTCAdapter.FeederHolder holder, int position) {
        DTC dtc = arrayList.get(position);
        holder.dtc_name.setText(dtc.getDTCNAME());
        holder.dtc_code.setText(dtc.getTCCODE());
        holder.dtc_ir.setText(dtc.getTCIR());
        holder.dtc_fr.setText(dtc.getTCFR());
        holder.dtc_mf.setText(dtc.getTCMF());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String search = constraint.toString();
                if (search.isEmpty())
                    arrayList = filteredList;
                else {
                    ArrayList<DTC> filterlist = new ArrayList<>();
                    for (int i = 0; i < filteredList.size(); i++) {
                        DTC dtc = filteredList.get(i);
                        //todo searching based on fdrcode
                        if (dtc.getDTCCODE().contains(search)||dtc.getDTCNAME().contains((search))) {
                            filterlist.add(dtc);
                        }
                    }
                    arrayList = filterlist;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = arrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arrayList = (ArrayList<DTC>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class FeederHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView dtc_name, dtc_code, dtc_ir, dtc_fr, dtc_mf;

        public FeederHolder(View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
            dtc_name = itemView.findViewById(R.id.txt_dtc_name);
            dtc_code = itemView.findViewById(R.id.txt_dtc_code);
            dtc_ir = itemView.findViewById(R.id.txt_dtc_ir);
            dtc_fr = itemView.findViewById(R.id.txt_dtc_fr);
            dtc_mf = itemView.findViewById(R.id.txt_dtc_mf);
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            DTC dtc = arrayList.get(position);
            mOnItemClickLister.show_dtc_details_update_dialog(DTC_DETAILS_UPDATE_DIALOG, dtc);
            return true;
        }
    }
}

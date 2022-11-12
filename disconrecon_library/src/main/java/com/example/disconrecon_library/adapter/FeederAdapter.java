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
import com.example.disconrecon_library.model.Feeder;

import java.util.ArrayList;
import java.util.List;

import static com.example.disconrecon_library.values.constant.FEEDER_DETAILS_UPDATE_DIALOG;


public class FeederAdapter extends RecyclerView.Adapter<FeederAdapter.FeederHolder> implements Filterable {
    private List<Feeder> arrayList;
    private List<Feeder> filteredList;
    private Context context;
    private static OnItemClickListener mOnItemClickLister;

    public interface OnItemClickListener {
        void show_fdr_details_update_dialog(int id, Feeder feeder);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickLister = listener;
    }

    public FeederAdapter(List<Feeder> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.filteredList = arrayList;
    }

    @NonNull
    @Override
    public FeederAdapter.FeederHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feeder_adapter_layout, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new FeederHolder(view);
    }

    @Override
    public void onBindViewHolder(FeederAdapter.FeederHolder holder, int position) {
        Feeder feeder = arrayList.get(position);
        holder.fdr_name.setText(feeder.getFDRNAME());
        holder.fdr_code.setText(feeder.getFDRCODE());
        holder.fdr_ir.setText(feeder.getFDRIR());
        holder.fdr_fr.setText(feeder.getFDRFR());
        holder.fdr_mf.setText(feeder.getFDRMF());
        holder.fdr_srtpv.setText(feeder.getSRTPV_INPUT());
        holder.fdr_export.setText(feeder.getBoundary_Mtr_Export());

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
                    ArrayList<Feeder> filterlist = new ArrayList<>();
                    for (int i = 0; i < filteredList.size(); i++) {
                        Feeder feeder = filteredList.get(i);
                        //todo searching based on fdrcode
                        if (feeder.getFDRCODE().contains(search)||feeder.getFDRNAME().contains((search))) {
                            filterlist.add(feeder);
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
                arrayList = (ArrayList<Feeder>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class FeederHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView fdr_name, fdr_code, fdr_ir, fdr_fr, fdr_mf, fdr_srtpv, fdr_export;

        public FeederHolder(View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
            fdr_name = itemView.findViewById(R.id.txt_feeder_name);
            fdr_code = itemView.findViewById(R.id.txt_feeder_code);
            fdr_ir = itemView.findViewById(R.id.txt_fdrir);
            fdr_fr = itemView.findViewById(R.id.txt_fdrfr);
            fdr_mf = itemView.findViewById(R.id.txt_fdr_mf);
            fdr_srtpv = itemView.findViewById(R.id.txt_fdr_srtpv);
            fdr_export = itemView.findViewById(R.id.txt_fdr_export);
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            Feeder feeder = arrayList.get(position);
            mOnItemClickLister.show_fdr_details_update_dialog(FEEDER_DETAILS_UPDATE_DIALOG, feeder);
            return true;
        }
    }
}

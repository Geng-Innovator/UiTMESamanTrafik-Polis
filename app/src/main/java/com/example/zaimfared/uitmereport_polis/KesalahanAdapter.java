package com.example.zaimfared.uitmereport_polis;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Zaim Fared on 19/1/2018.
 */

public class KesalahanAdapter extends RecyclerView.Adapter<KesalahanAdapter.MyViewHolder> {

    private List<String> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtKesalahan;
        private CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtKesalahan = itemView.findViewById(R.id.txtRowKesalahan);
            checkBox = itemView.findViewById(R.id.chkBoxRowKesalahan);
        }
    }

    public KesalahanAdapter(List<String> list) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_kesalahan, parent, false);
        return new KesalahanAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.txtKesalahan.setText(list.get(position));
        holder.checkBox.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

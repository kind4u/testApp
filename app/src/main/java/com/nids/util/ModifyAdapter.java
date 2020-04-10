package com.nids.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nids.kind4u.testapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ModifyAdapter extends RecyclerView.Adapter<ModifyAdapter.ModifyViewHolder> {

    public static class ModifyViewHolder extends RecyclerView.ViewHolder    {
        public TextView infoView;
        public TextView descView;

        public ModifyViewHolder(View v) {
            super(v);
            infoView = v.findViewById(R.id.info_text);
            descView = v.findViewById(R.id.desc_text);
        }
    }

    private ArrayList<InfoItem> infoItemArrayList;
    public ModifyAdapter(ArrayList<InfoItem> infoItemArrayList)    {
        this.infoItemArrayList = infoItemArrayList;
    }

    @NonNull
    @Override
    public ModifyAdapter.ModifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.modify_info_row, parent, false);

        ModifyViewHolder vh = new ModifyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ModifyAdapter.ModifyViewHolder holder, int position) {
        ModifyViewHolder modifyViewHolder = holder;

        modifyViewHolder.infoView.setText(infoItemArrayList.get(position).infoName);
        if(position != 1) {
            modifyViewHolder.descView.setText(infoItemArrayList.get(position).desc);
        }   else    {
            int count = infoItemArrayList.get(position).desc.length();
            modifyViewHolder.descView.setText(new String(new char[count]).replace("\0","*"));
        }
    }

    @Override
    public int getItemCount() {
        return infoItemArrayList.size();
    }
}

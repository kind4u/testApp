package com.nids.util;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nids.kind4u.testapp.R;
import com.nids.views.ModifyPwActivity;
import java.util.ArrayList;


public class ModifyAdapter extends RecyclerView.Adapter<ModifyAdapter.ModifyViewHolder> {

    static class ModifyViewHolder extends RecyclerView.ViewHolder {
        TextView infoView;
        TextView descView;

        void setUserId(String userId) {
            ModifyViewHolder.userId = userId;
        }

        private static String userId;

        ModifyViewHolder(View v) {
            super(v);
            infoView = v.findViewById(R.id.info_text);
            descView = v.findViewById(R.id.desc_text);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos == 1) {
                        Intent intent = new Intent(v.getContext(), ModifyPwActivity.class);
                        intent.putExtra("id", userId);
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }
    }

    private ArrayList<InfoItem> infoItemArrayList;

    public ModifyAdapter(ArrayList<InfoItem> infoItemArrayList) {
        this.infoItemArrayList = infoItemArrayList;
    }

    @NonNull
    @Override
    public ModifyAdapter.ModifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.modify_info_row, parent, false);

        return new ModifyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ModifyAdapter.ModifyViewHolder holder, int position) {

        holder.infoView.setText(infoItemArrayList.get(position).infoName);
        switch (position) {
            case 0:
                holder.setUserId(infoItemArrayList.get(position).desc);
                holder.descView.setText(infoItemArrayList.get(position).desc);
                holder.descView.setTextColor(Color.parseColor("#CCCCCC"));
                break;
            case 1:
                int count = infoItemArrayList.get(position).desc.length();
                holder.descView.setText(new String(new char[count]).replace("\0", "*"));
                holder.descView.setTextColor(Color.parseColor("#000000"));
                break;
            default:
                holder.descView.setText(infoItemArrayList.get(position).desc);
                holder.descView.setTextColor(Color.parseColor("#CCCCCC"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return infoItemArrayList.size();
    }
}

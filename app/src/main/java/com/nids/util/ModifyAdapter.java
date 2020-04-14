package com.nids.util;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.interfaces.NetworkCallBackInterface;
import com.nids.util.network.CommunicationUtil;
import com.nids.views.ModifyPwActivity;

import java.util.ArrayList;
import java.util.List;


public class ModifyAdapter extends RecyclerView.Adapter<ModifyAdapter.ModifyViewHolder> {

    public static class ModifyViewHolder extends RecyclerView.ViewHolder {
        public TextView infoView;
        public TextView descView;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        private static String userId;

        public ModifyViewHolder(View v) {
            super(v);
            infoView = v.findViewById(R.id.info_text);
            descView = v.findViewById(R.id.desc_text);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    switch (pos) {
                        case 1:
                            Intent intent = new Intent(v.getContext(), ModifyPwActivity.class);
                            intent.putExtra("id", userId);
                            v.getContext().startActivity(intent);
                            break;
                        default:
                            break;
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

        ModifyViewHolder vh = new ModifyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ModifyAdapter.ModifyViewHolder holder, int position) {
        ModifyViewHolder modifyViewHolder = holder;

        modifyViewHolder.infoView.setText(infoItemArrayList.get(position).infoName);
        switch (position) {
            case 0:
                modifyViewHolder.setUserId(infoItemArrayList.get(position).desc);
                modifyViewHolder.descView.setText(infoItemArrayList.get(position).desc);
                modifyViewHolder.descView.setTextColor(Color.parseColor("#CCCCCC"));
                break;
            case 1:
                int count = infoItemArrayList.get(position).desc.length();
                modifyViewHolder.descView.setText(new String(new char[count]).replace("\0", "*"));
                modifyViewHolder.descView.setTextColor(Color.parseColor("#000000"));
                break;
            default:
                modifyViewHolder.descView.setText(infoItemArrayList.get(position).desc);
                modifyViewHolder.descView.setTextColor(Color.parseColor("#CCCCCC"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return infoItemArrayList.size();
    }
}

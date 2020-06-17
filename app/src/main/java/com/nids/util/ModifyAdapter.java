package com.nids.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nids.kind4u.testapp.R;
import com.nids.views.ModifyPwActivity;

import java.util.ArrayList;


public class ModifyAdapter extends RecyclerView.Adapter<ModifyAdapter.ModifyViewHolder> {

    private ArrayList<InfoItem> infoItemArrayList;
    private Context mContext;

    public class ModifyViewHolder extends RecyclerView.ViewHolder {
        TextView infoView;
        TextView descView;

        private boolean platform = false;
        private String userId;

        public void setUserId(String userId)    { this.userId = userId; }
        public void setPlatform(boolean platform) { this.platform = platform; }

        ModifyViewHolder(View v) {
            super(v);
            infoView = v.findViewById(R.id.info_text);
            descView = v.findViewById(R.id.desc_text);

            v.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    switch(getAdapterPosition())    {
                        case 1:
                            if(!platform)    {
                                break;
                            }   else    {
                                Intent modPwIntent = new Intent(v.getContext(), ModifyPwActivity.class);
                                modPwIntent.putExtra("id",userId);
                                v.getContext().startActivity(modPwIntent);
                                break;
                            }
                        case 3:
                            View viewGender = LayoutInflater.from(mContext).inflate(R.layout.custom_dialog_gender, null, false);
                            builder.setView(viewGender);
                            final Button buttonSubmitG = viewGender.findViewById(R.id.okButton_g);
                            final Button buttonCancelG = viewGender.findViewById(R.id.cancelButton_g);
                            final TextView dialogTextViewG = viewGender.findViewById(R.id.title_g);
                            final RadioGroup radioGroup = viewGender.findViewById(R.id.gender_group);
                            final RadioButton maleButton = viewGender.findViewById(R.id.maleButton);
                            final RadioButton femaleButton = viewGender.findViewById(R.id.femaleButton);

                            int gender = infoItemArrayList.get(getAdapterPosition()).desc.equals("남성")?0:infoItemArrayList.get(getAdapterPosition()).desc.equals("여성")?1:9;
                            switch (gender) {
                                case 0:
                                    radioGroup.check(maleButton.getId());
                                    break;
                                case 1:
                                    radioGroup.check(femaleButton.getId());
                                    break;
                            }
                            dialogTextViewG.setText(infoItemArrayList.get(getAdapterPosition()).infoName + "정보 수정");

                            final AlertDialog dialogG = builder.create();
                            buttonSubmitG.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String strDesc = radioGroup.getCheckedRadioButtonId()==maleButton.getId()?"남성":radioGroup.getCheckedRadioButtonId()==femaleButton.getId()?"여성":"미설정";
                                    String strInfo = infoItemArrayList.get(getAdapterPosition()).infoName;
                                    InfoItem infoItem = new InfoItem(strInfo,strDesc);
                                    infoItemArrayList.set(getAdapterPosition(),infoItem);
                                    notifyItemChanged(getAdapterPosition());
                                    // TODO : 아이템 정보에 따른 DB 수정 c_util method
                                    dialogG.dismiss();
                                }
                            });

                            buttonCancelG.setOnClickListener(new View.OnClickListener()  {
                                @Override
                                public void onClick(View v) {
                                    dialogG.dismiss();
                                }
                            });

                            dialogG.show();
                            break;
                        default:
                            View view = LayoutInflater.from(mContext).inflate(R.layout.custom_dialog,null,false);
                            builder.setView(view);
                            final Button buttonSubmit = view.findViewById(R.id.okButton);
                            final Button buttonCancel = view.findViewById(R.id.cancelButton);
                            final EditText dialogEditText = view.findViewById(R.id.mesgase);
                            final TextView dialogTextView = view.findViewById(R.id.title);

                            dialogEditText.setText(infoItemArrayList.get(getAdapterPosition()).desc);
                            dialogTextView.setText(infoItemArrayList.get(getAdapterPosition()).infoName + " 정보 수정");

                            final AlertDialog dialog = builder.create();
                            buttonSubmit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String strDesc = dialogEditText.getText().toString();
                                    String strInfo = infoItemArrayList.get(getAdapterPosition()).infoName;
                                    InfoItem infoItem = new InfoItem(strInfo,strDesc);
                                    infoItemArrayList.set(getAdapterPosition(),infoItem);
                                    notifyItemChanged(getAdapterPosition());
                                    // TODO : 아이템 정보에 따른 DB 수정 c_util method
                                    dialog.dismiss();
                                }
                            });
                            buttonCancel.setOnClickListener(new View.OnClickListener()  {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                            break;
                    }
                }
            });
        }
    }


    public ModifyAdapter(Context mContext, ArrayList<InfoItem> infoItemArrayList) {
        this.mContext = mContext;
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
            case 0:     // id
                holder.setUserId(infoItemArrayList.get(position).desc);
                holder.descView.setText(infoItemArrayList.get(position).desc);
                holder.descView.setTextColor(Color.parseColor("#CCCCCC"));
                break;
            case 1:     // password
                int count = infoItemArrayList.get(position).desc.length();
                if (count != 0) {
                    holder.setPlatform(true);
                    holder.descView.setText(new String(new char[count]).replace("\0", "*"));
                    holder.descView.setTextColor(Color.parseColor("#000000"));
                } else {
                    holder.descView.setText(infoItemArrayList.get(position).desc);
                    holder.descView.setTextColor(Color.parseColor("#CCCCCC"));
                }
                break;
            default:    // another case
                holder.descView.setText(infoItemArrayList.get(position).desc);
                holder.descView.setTextColor(Color.parseColor("#000000"));

                break;
        }
    }

    @Override
    public int getItemCount() {
        return infoItemArrayList.size();
    }
}

package com.nids.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nids.kind4u.testapp.R;
import com.nids.views.MainActivity;
import com.nids.views.ModifyActivity;
import com.nids.views.ModifyPwActivity;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class ModifyAdapter extends RecyclerView.Adapter<ModifyAdapter.ModifyViewHolder> {

    private static String userId;

    private String getUserId() { return userId; }
    private void setUserId(String userId)    { ModifyAdapter.userId = userId; }

    public ArrayList<InfoItem> infoItemArrayList;
    public Context mContext;

    public class ModifyViewHolder extends RecyclerView.ViewHolder {
        TextView infoView;
        TextView descView;

        private boolean platform = false;

        public void setPlatform(boolean platform) { this.platform = platform; }


        ModifyViewHolder(View v) {
            super(v);
            infoView = v.findViewById(R.id.info_text);
            descView = v.findViewById(R.id.desc_text);

            v.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    switch(getAdapterPosition())    {
                        case 1:
                            if(!platform)    {
                                break;
                            }   else    {
                                Intent modPwIntent = new Intent(v.getContext(), ModifyPwActivity.class);
                                modPwIntent.putExtra("id",getUserId());
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
                            dialogTextViewG.setText(infoItemArrayList.get(getAdapterPosition()).infoName + " 정보 수정");

                            final AlertDialog dialogG = builder.create();
                            buttonSubmitG.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String strDesc = radioGroup.getCheckedRadioButtonId()==maleButton.getId()?"남성":radioGroup.getCheckedRadioButtonId()==femaleButton.getId()?"여성":"미설정";
                                    String strInfo = infoItemArrayList.get(getAdapterPosition()).infoName;
                                    InfoItem infoItem = new InfoItem(strInfo,strDesc);
                                    infoItemArrayList.set(getAdapterPosition(),infoItem);
                                    notifyItemChanged(getAdapterPosition());
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
                        case 6:
                            View viewAge = LayoutInflater.from(mContext).inflate(R.layout.custom_dialog_age, null, false);
                            builder.setView(viewAge);
                            final Button buttonSubmitA = viewAge.findViewById(R.id.okButton_a);
                            final Button buttonCancelA = viewAge.findViewById(R.id.cancelButton_a);
                            final TextView dialogTextViewA = viewAge.findViewById(R.id.title_a);
                            final Spinner spinner = viewAge.findViewById(R.id.spinner);
                            if(infoItemArrayList.get(getAdapterPosition()).desc != null) {
                                int index = Integer.parseInt(infoItemArrayList.get(getAdapterPosition()).desc.substring(0, 1));
                                spinner.setSelection(index);
                            }
                            else    { spinner.setSelection(0);}

                            dialogTextViewA.setText(infoItemArrayList.get(getAdapterPosition()).infoName + " 정보 수정");

                            final AlertDialog dialogA = builder.create();
                            buttonSubmitA.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String strDesc = spinner.getSelectedItem().toString();
                                    String strInfo = infoItemArrayList.get(getAdapterPosition()).infoName;
                                    InfoItem infoItem = new InfoItem(strInfo,strDesc);
                                    infoItemArrayList.set(getAdapterPosition(),infoItem);
                                    notifyItemChanged(getAdapterPosition());
                                    dialogA.dismiss();
                                }
                            });

                            buttonCancelA.setOnClickListener(new View.OnClickListener()  {
                                @Override
                                public void onClick(View v) {
                                    dialogA.dismiss();
                                }
                            });

                            dialogA.show();
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
                                    switch (getAdapterPosition())   {
                                       case 4:
                                        if(!Pattern.matches("^01(?:[0-1]|[6-9])-(\\d{3,4})-(\\d{4})$",dialogEditText.getText().toString()))    {
                                            // 휴대폰 형식 오류 예외처리
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                                            builder1.setMessage("올바른 전화번호 형식이 아닙니다.");
                                            builder1.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            AlertDialog alertDialog = builder1.create();
                                            alertDialog.show();
                                        }  else    {
                                            String strDesc = dialogEditText.getText().toString();
                                            String strInfo = infoItemArrayList.get(getAdapterPosition()).infoName;
                                            InfoItem infoItem = new InfoItem(strInfo,strDesc);
                                            infoItemArrayList.set(getAdapterPosition(),infoItem);
                                            notifyItemChanged(getAdapterPosition());
                                            dialog.dismiss();
                                        }
                                            break;
                                        case 5:
                                            if(!Pattern.matches("^(0[1-9]|1[012])/(0[1-9]|[12][0-9]|3[01])$",dialogEditText.getText().toString())){
                                                //생일 형식 오류
                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                                                builder1.setMessage("올바른 생일 형식(MM/dd) 이 아닙니다.");
                                                builder1.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                AlertDialog alertDialog = builder1.create();
                                                alertDialog.show();
                                            }   else    {
                                                String strDesc = dialogEditText.getText().toString();
                                                String strInfo = infoItemArrayList.get(getAdapterPosition()).infoName;
                                                InfoItem infoItem = new InfoItem(strInfo,strDesc);
                                                infoItemArrayList.set(getAdapterPosition(),infoItem);
                                                notifyItemChanged(getAdapterPosition());
                                                dialog.dismiss();
                                            }
                                            break;
                                        case 7:
                                            if(!Pattern.matches("^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.+[a-zA-Z0-9]{2,6}+$",dialogEditText.getText().toString()))    {
                                                // 이메일 형식 오류
                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                                                builder1.setMessage("올바른 이메일 형식이 아닙니다.");
                                                builder1.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                AlertDialog alertDialog = builder1.create();
                                                alertDialog.show();
                                            }   else    {
                                                String strDesc = dialogEditText.getText().toString();
                                                String strInfo = infoItemArrayList.get(getAdapterPosition()).infoName;
                                                InfoItem infoItem = new InfoItem(strInfo,strDesc);
                                                infoItemArrayList.set(getAdapterPosition(),infoItem);
                                                notifyItemChanged(getAdapterPosition());
                                                dialog.dismiss();
                                            }
                                            break;
                                        default:
                                            String strDesc = dialogEditText.getText().toString();
                                            String strInfo = infoItemArrayList.get(getAdapterPosition()).infoName;
                                            InfoItem infoItem = new InfoItem(strInfo,strDesc);
                                            infoItemArrayList.set(getAdapterPosition(),infoItem);
                                            notifyItemChanged(getAdapterPosition());
                                            dialog.dismiss();
                                            break;
                                    }

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
                setUserId(infoItemArrayList.get(position).desc);
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

package com.nids.views;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nids.data.VOOutdoor;
import com.nids.kind4u.testapp.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OutsideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OutsideFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    MainActivity activity;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String station;
    TextView dataText;
    TextView stationText;



    public OutsideFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OutsideFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OutsideFragment newInstance(String param1, String param2) {
        OutsideFragment fragment = new OutsideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if(bundle != null){
            station = bundle.getString("station");
            //runDataThread();
        }
        View v = inflater.inflate(R.layout.fragment_outside, container,false);
        bindComponent(v);

        // Inflate the layout for this fragment
        return v;
    }

    private  void bindComponent(View v){
        stationText = v.findViewById(R.id.stationText);
        dataText = v.findViewById(R.id.dateText);
    }

    private  void setData(VOOutdoor data){
        stationText.setText("실외 미세먼지 현황 ("+ station +"측정소)");
        dataText.setText("측정시간 : " + data.getMeasureDate());
    }
}

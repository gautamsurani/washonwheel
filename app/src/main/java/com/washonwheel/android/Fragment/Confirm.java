package com.washonwheel.android.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.washonwheel.android.R;

public class Confirm extends Fragment implements View.OnClickListener {
    @SuppressLint("StaticFieldLeak")
    private static Button btnMyAcc;

    public Confirm() {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.confirm, container, false);
        TextView tvTitle = getActivity().findViewById(R.id.toolbar_title);

        tvTitle.setText("Service Confirmed");
        init(v);

        btnMyAcc.setOnClickListener(this);
        return v;
    }

    private void init(View v) {
        btnMyAcc = v.findViewById(R.id.btnMyAcc);
    }

    @Override
    public void onClick(View v) {
        if (v == btnMyAcc) {
            Fragment fragment = new MyAccount();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.trans_left_in, R.anim.trans_left_out);
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
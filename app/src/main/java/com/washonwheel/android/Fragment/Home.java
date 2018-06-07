package com.washonwheel.android.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.washonwheel.android.Activity.MainActivity;
import com.washonwheel.android.R;

public class Home extends Fragment implements View.OnClickListener {
    private static RelativeLayout btnFirst, btnLast, btnSecond, btnThird;
    TextView tvtitle;

    public Home() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_dashboard, container, false);
        tvtitle = getActivity().findViewById(R.id.toolbar_title);

        init(v);

        btnFirst.setOnClickListener(this);
        btnSecond.setOnClickListener(this);
        btnThird.setOnClickListener(this);
        btnLast.setOnClickListener(this);

        return v;
    }

    private void init(View v) {
        btnFirst = v.findViewById(R.id.btnFirst);
        btnSecond = v.findViewById(R.id.btnSecond);
        btnThird = v.findViewById(R.id.btnThird);
        btnLast = v.findViewById(R.id.btnLast);
    }

    @Override
    public void onClick(View v) {
        if (v == btnFirst) {
            Log.e("THE CLICK", "FIRST");
        } else if (v == btnSecond) {
            MainActivity.isHome = false;
            Fragment fragment = new Book_Services();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (v == btnThird) {
            MainActivity.isHome = false;
            Fragment fragment = new MyAccount();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (v == btnLast) {
            MainActivity.isHome = false;
            Fragment fragment = new Service_History();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Log.e("THE CLICK", "ELSE");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        tvtitle.setText(R.string.app_name);
    }
}

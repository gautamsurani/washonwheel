package com.washonwheel.android.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.washonwheel.android.Activity.ChangePasswordActivity;
import com.washonwheel.android.Activity.LoginActivity;
import com.washonwheel.android.Activity.MyPackageActivity;
import com.washonwheel.android.Activity.MyVehiclesActivity;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppPersistance;
import com.washonwheel.android.Util.AppPreference;
import com.washonwheel.android.Util.Utils;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccount extends Fragment implements View.OnClickListener {
    @SuppressLint("StaticFieldLeak")
    private static Button btnLogout;
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout ll_wallet, ll_service, ll_profile, ll_vehicle, ll_change_password, ll_earn_refer, ll_my_package;
    @SuppressLint("StaticFieldLeak")
    private static TextView tvTitle;
    TextView tvUserName, tvMobileNo;
    String UserName, MobileNo;
    Activity context;
    CircleImageView circleView;

    public MyAccount() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_account, container, false);

        tvTitle = getActivity().findViewById(R.id.toolbar_title);
        tvTitle.setText("My Account");

        context = getActivity();

        init(v);

        UserName = AppPreference.getPreference(context, AppPersistance.keys.USER_NAME);
        MobileNo = AppPreference.getPreference(context, AppPersistance.keys.USER_NUMBER);

        btnLogout.setOnClickListener(this);
        ll_wallet.setOnClickListener(this);
        ll_service.setOnClickListener(this);
        ll_profile.setOnClickListener(this);
        ll_vehicle.setOnClickListener(this);
        ll_change_password.setOnClickListener(this);
        ll_earn_refer.setOnClickListener(this);
        ll_my_package.setOnClickListener(this);

        tvUserName.setText(UserName);
        tvMobileNo.setText(MobileNo);

        Glide.with(context)
                .load(Utils.getUserImage(context))
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.default_icon)
                .into(circleView);

        return v;

    }

    private void init(View v) {
        btnLogout = v.findViewById(R.id.btnLogout);
        ll_wallet = v.findViewById(R.id.ll_wallet);
        ll_service = v.findViewById(R.id.ll_service);
        ll_profile = v.findViewById(R.id.ll_profile);
        ll_my_package = v.findViewById(R.id.ll_my_package);
        ll_vehicle = v.findViewById(R.id.ll_vehicle);
        ll_change_password = v.findViewById(R.id.ll_change_password);
        ll_earn_refer = v.findViewById(R.id.ll_earn_refer);
        tvUserName = v.findViewById(R.id.tvUserName);
        tvMobileNo = v.findViewById(R.id.tvMobileNo);
        circleView = v.findViewById(R.id.circleView);
    }

    @Override
    public void onClick(View v) {
        if (v == btnLogout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to logout ?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    AppPreference.removePreference(context, AppPersistance.keys.USER_ID);
                    AppPreference.removePreference(context, AppPersistance.keys.USER_NAME);
                    AppPreference.removePreference(context, AppPersistance.keys.USER_EMAIL);
                    AppPreference.removePreference(context, AppPersistance.keys.USER_NUMBER);
                    Intent i = new Intent(context, LoginActivity.class);
                    startActivity(i);
                    context.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    context.finish();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (v == ll_wallet) {
            Fragment fragment = new Wallet();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.trans_left_in, R.anim.trans_left_out);
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (v == ll_service) {
            Fragment fragment = new Service_History();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.trans_left_in, R.anim.trans_left_out);
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (v == ll_profile) {
            Fragment fragment = new MyProfile();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.trans_left_in, R.anim.trans_left_out);
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (v == ll_vehicle) {
            startActivity(new Intent(context, MyVehiclesActivity.class));
            context.overridePendingTransition(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
        } else if (v == ll_change_password) {
            startActivity(new Intent(context, ChangePasswordActivity.class));
            context.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (v == ll_earn_refer) {
            Fragment fragment = new Refer_Friend();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.trans_left_in, R.anim.trans_left_out);
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (v == ll_my_package) {
            startActivity(new Intent(context, MyPackageActivity.class));
            context.overridePendingTransition(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
        }
    }
}

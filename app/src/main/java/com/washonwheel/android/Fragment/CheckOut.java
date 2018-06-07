package com.washonwheel.android.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.washonwheel.android.R;

public class CheckOut extends Fragment implements View.OnClickListener {
    private static Button btnPaynow;
    private static LinearLayout ll_wallet, ll_coupon, view_coupon;
    private static CheckBox chk_wallet;
    private static ImageView imgOpen;

    //Header
    private static TextView tvtitle;

    public CheckOut() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.checkout, container, false);
        tvtitle = getActivity().findViewById(R.id.toolbar_title);
        tvtitle.setText("Check Out");

        init(v);

        btnPaynow.setOnClickListener(this);
        ll_wallet.setOnClickListener(this);
        ll_coupon.setOnClickListener(this);

        return v;
    }

    private void init(View v) {
        btnPaynow = v.findViewById(R.id.btnPaynow);
        ll_wallet = v.findViewById(R.id.ll_wallet);
        chk_wallet = v.findViewById(R.id.chk_wallet);
        ll_coupon = v.findViewById(R.id.ll_coupon);
        view_coupon = v.findViewById(R.id.view_coupon);
        imgOpen = v.findViewById(R.id.imgOpen);
    }

    @Override
    public void onClick(View v) {
        if (v == btnPaynow) {
            Fragment fragment = new Confirm();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.trans_left_in, R.anim.trans_left_out);
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (ll_wallet == v) {
            if (ll_wallet.getTag().equals("f")) {
                ll_wallet.setTag("t");
                chk_wallet.setChecked(true);
            } else {
                ll_wallet.setTag("f");
                chk_wallet.setChecked(false);
            }
        } else if (v == ll_coupon) {
            if (view_coupon.getVisibility() == View.VISIBLE) {
                view_coupon.animate()
                        .translationY(0)
                        .alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                view_coupon.setVisibility(View.GONE);
                                imgOpen.setImageResource(R.drawable.ic_more);
                            }
                        });
            } else {
                // Prepare the View for the animation
                view_coupon.setVisibility(View.VISIBLE);
                view_coupon.setAlpha(0.0f);

                // Start the animation
                view_coupon.animate()
                        .translationY(20)
                        .alpha(1.0f)
                        .setListener(null);

                imgOpen.setImageResource(R.drawable.ic_collapse);
            }
        }
    }
}

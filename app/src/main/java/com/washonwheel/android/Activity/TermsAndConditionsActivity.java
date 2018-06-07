package com.washonwheel.android.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.washonwheel.android.R;

public class TermsAndConditionsActivity extends AppCompatActivity {

    TextView tvTitle;

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        init();
        initComp();
    }

    private void initComp() {

        webView = findViewById(R.id.webView);

        webView.loadUrl("http://www.washonwheel.com/terms-amp-conditions.html");

    }

    private void init() {
        tvTitle = findViewById(R.id.toolbar_title);
        tvTitle.setText("Term & Conditions");
        Toolbar toolbar = findViewById(R.id.toolbar);
        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            // Toast.makeText(MainCategoriesActivity.this, "BackWorking", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);

    }
}

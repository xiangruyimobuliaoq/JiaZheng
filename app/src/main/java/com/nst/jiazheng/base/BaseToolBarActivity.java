package com.nst.jiazheng.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import com.nst.jiazheng.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseToolBarActivity extends BaseActivity {

    public Toolbar toolbar;
    public TextView actTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = findViewById(R.id.toolbar);
        actTitle = findViewById(R.id.tv_toolbar_title);
        initToolbar();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                hideInput();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initToolbar() {
        super.initToolbar(toolbar);
        setActTitle(getTitle());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(getHomeButtonEnable());
        actionBar.setDisplayHomeAsUpEnabled(getHomeButtonEnable());
    }

    public void setActTitle(CharSequence title) {
        if (TextUtils.isEmpty(title)) return;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            if (actTitle != null)
                actTitle.setText(title);
        }
    }

    protected boolean getHomeButtonEnable() {
        return true;
    }
}

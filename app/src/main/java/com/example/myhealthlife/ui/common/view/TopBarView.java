package com.example.myhealthlife.ui.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myhealthlife.R;

public class TopBarView extends LinearLayout {

    private ImageView back, right;
    private TextView title;

    public TopBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.top_bar_view, this);
        back = findViewById(R.id.backTop);
        right = findViewById(R.id.connect_device);
        title = findViewById(R.id.title);
    }

    public void setTitle(String text) {
        title.setText(text);
    }

    public void setOnBackClick(OnClickListener l) {
        back.setOnClickListener(l);
    }

    public void setOnRightClick(OnClickListener l) {
        right.setOnClickListener(l);
    }

    public void showBack(boolean show) {
        back.setVisibility(show ? VISIBLE : GONE);
    }

    public void showRight(boolean show) {
        right.setVisibility(show ? VISIBLE : GONE);
    }
}


package com.example.myhealthlife.model;

import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myhealthlife.R;

import java.util.ArrayList;
import java.util.List;

public class TabManager {
    private List<LinearLayout> tabs = new ArrayList<>();
    private int activeColor;
    private int inactiveColor;

    public TabManager(int activeColor, int inactiveColor) {
        this.activeColor = activeColor;
        this.inactiveColor = inactiveColor;
    }

    public void addTab(LinearLayout tab, String text) {
        tabs.add(tab);
        setupTab(tab, text, false);

        tab.setOnClickListener(v -> setActiveTab(tab));
    }

    public void setActiveTab(LinearLayout activeTab) {
        for (LinearLayout tab : tabs) {
            boolean isActive = tab == activeTab;
            setupTab(tab, getTabText(tab), isActive);
        }
    }

    private void setupTab(LinearLayout tab, String text, boolean isActive) {
        TextView textView = tab.findViewById(R.id.tabText);
        View lineView = tab.findViewById(R.id.tabLine);

        textView.setText(text);
        textView.setTextColor(isActive ? activeColor : inactiveColor);
        textView.setTypeface(null, isActive ? Typeface.BOLD : Typeface.NORMAL);
        lineView.setVisibility(isActive ? View.VISIBLE : View.GONE);
    }

    private String getTabText(LinearLayout tab) {
        TextView textView = tab.findViewById(R.id.tabText);
        return textView.getText().toString();
    }
}
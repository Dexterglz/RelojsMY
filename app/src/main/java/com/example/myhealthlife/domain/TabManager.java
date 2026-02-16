package com.example.myhealthlife.domain;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.myhealthlife.R;

import java.util.ArrayList;
import java.util.List;

public class TabManager {
    private List<LinearLayout> tabs = new ArrayList<>();
    private int activeColor, inactiveColor, activeBackground, inactiveBackground;

    public TabManager(int activeColor, int inactiveColor,int activeBackground, int inactiveBackground) {
        this.activeColor = activeColor;
        this.inactiveColor = inactiveColor;
        this.activeBackground = activeBackground;
        this.inactiveBackground = inactiveBackground;
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
        CardView cardTabContainer = tab.findViewById(R.id.cardTabContainer);

        cardTabContainer.setCardBackgroundColor(isActive ? activeBackground : inactiveBackground);
        textView.setText(text);
        textView.setTextColor(isActive ? activeColor : inactiveColor);
        /*textView.setTypeface(null, isActive ? Typeface.BOLD : Typeface.NORMAL);
        lineView.setVisibility(isActive ? View.VISIBLE : View.GONE);*/
    }

    private String getTabText(LinearLayout tab) {
        TextView textView = tab.findViewById(R.id.tabText);
        return textView.getText().toString();
    }
}
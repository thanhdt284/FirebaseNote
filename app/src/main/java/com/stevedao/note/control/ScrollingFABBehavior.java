package com.stevedao.note.control;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

/**
 * Created by thanh.dao on 4/22/2016.
 *
 */
public class ScrollingFABBehavior extends FloatingActionButton.Behavior {
    private int toolbarHeight;
    private int statusbarHeight;

    public ScrollingFABBehavior(Context context) {
        super();
        this.toolbarHeight = Common.getToolbarHeight(context);
        this.statusbarHeight = Common.getStatusBarHeight(context);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        return super.layoutDependsOn(parent, fab, dependency) || (dependency instanceof AppBarLayout);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;
            int distanceToScroll = fab.getHeight() + fabBottomMargin;
            float ratio = (dependency.getY() - statusbarHeight) /(float)toolbarHeight;
            fab.setTranslationY(-distanceToScroll * ratio);
        }
        return super.onDependentViewChanged(parent, fab, dependency);
    }
}
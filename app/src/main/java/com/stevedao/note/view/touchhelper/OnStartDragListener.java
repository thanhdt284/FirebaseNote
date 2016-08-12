package com.stevedao.note.view.touchhelper;

import android.support.v7.widget.RecyclerView;

/**
 * Created by thanh.dao on 14/04/2016.
 *
 * original is OnStartDragListener is used only for dragging item
 */
public interface OnStartDragListener {
    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);
}

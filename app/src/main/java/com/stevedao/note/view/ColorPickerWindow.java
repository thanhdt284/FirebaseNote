package com.stevedao.note.view;

import android.content.Context;
import android.firebase.note.R;
import android.graphics.PorterDuff;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import com.stevedao.note.model.ColorPickerInterface;

/**
 * Created by thanh.dao on 12/04/2016.
 *
 */
public class ColorPickerWindow{
    private View mainView;
    private ColorPickerAdapter colorAdapter;
    private PopupWindow mPopupWindow;

    public ColorPickerWindow(Context context, int[] color, ColorPickerInterface iTask) {

        LayoutInflater inflater = LayoutInflater.from(context);
        mainView =  inflater.inflate(R.layout.color_picker_popup_window_layout, null);
        mPopupWindow = new PopupWindow(mainView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setAnimationStyle(R.style.popup_window_animation);

        RecyclerView colorPickerView = (RecyclerView) mainView.findViewById(R.id.color_picker_view);
        colorAdapter = new ColorPickerAdapter(context, color, iTask);
        colorPickerView.setLayoutManager(new CustomGridLayoutManger(context, 3));
        colorPickerView.setAdapter(colorAdapter);
    }

    public void show() {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(mainView, Gravity.TOP | Gravity.START, 30, 280);
//            mPopupWindow.showAsDropDown(mAnchorView,
//                    (int) mContext.getResources().getDimension(R.dimen.color_picker_window_offset_x),
//                    (int) mContext.getResources().getDimension(R.dimen.color_picker_window_offset_y));
        }
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }
    public void setCurrentColor(int index) {
        if (colorAdapter != null) {
            colorAdapter.setCheckedPosition(index);
        }
    }

    class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ViewHolder> {
        private Context mContext;
        private int[] color;
        private int checkedPosition = -1;
        private ColorPickerInterface mITask;

        public ColorPickerAdapter(Context context, int[] color, ColorPickerInterface iTask) {
            mContext = context;
            this.color = color;
            this.mITask = iTask;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View itemView = inflater.inflate(R.layout.color_picker_item, null);

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.colorShape.setColorFilter(color[position], PorterDuff.Mode.SRC_ATOP);

            if (checkedPosition == position) {
                holder.colorShapeSelector.setVisibility(View.VISIBLE);
            } else {
                holder.colorShapeSelector.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return color.length;
        }

        public void setCheckedPosition(int checkedPosition) {
            this.checkedPosition = checkedPosition;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageButton colorShape;
            private ImageButton colorShapeSelector;

            public ViewHolder(View itemView) {
                super(itemView);

                colorShape = (ImageButton) itemView.findViewById(R.id.color_item);
                colorShapeSelector = (ImageButton) itemView.findViewById(R.id.selector_item);
                colorShape.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkedPosition = getAdapterPosition();
//                    notifyDataSetChanged();
                        mITask.changeColor(checkedPosition);
                    }
                });
            }
        }
    }

    class CustomGridLayoutManger extends GridLayoutManager {

        public CustomGridLayoutManger(Context context, int spanCount) {
            super(context, spanCount);
        }

        @Override
        public boolean canScrollVertically() {
            return false;
        }
    }
}






package com.stevedao.note.view;

import android.app.Activity;
import android.content.Context;
import android.firebase.note.R;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import com.amulyakhare.textdrawable.TextDrawable;
import com.stevedao.note.control.Common;
import com.stevedao.note.model.ColorPickerInterface;
import com.stevedao.note.model.Item;
import com.stevedao.note.model.Note;
import com.stevedao.note.view.touchhelper.ItemTouchHelperAdapter;
import com.stevedao.note.view.touchhelper.ItemTouchHelperViewHolder;
import com.stevedao.note.view.touchhelper.OnStartDragListener;

/**
 * Created by thanh.dao on 4/10/2016.
 *
 */
public class NoteDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
    private Context mContext;
    private Note mNote;
    private ArrayList<Item> mItemData;
    private int[] mColorList;
    private NoteDetailAdapterInterface mNoteAdapterInterface;
    private OnStartDragListener mOnStartDragListener;

    private int focusPosition = -1;

    private final int TITLE_LAYOUT_TYPE = 0;
    private final int CONTENT_LAYOUT_TYPE = 1;
    private final int ADD_LAYOUT_TYPE = 2;
    private ColorPickerWindow mColorDialog;
    private int curPos;

    public NoteDetailAdapter(Context context, Note note, ArrayList<Item> itemData, int[] colorList,
                             NoteDetailAdapterInterface adapterInterface, OnStartDragListener onStartDragListener) {
        mContext = context;
        mNote = note;
        mItemData = itemData;
        mColorList = colorList;
        mNoteAdapterInterface = adapterInterface;
        mOnStartDragListener = onStartDragListener;
    }

    public void setData(Note note, ArrayList<Item> itemData) {
        mNote = note;
        mItemData = itemData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (viewType) {
        case TITLE_LAYOUT_TYPE:
            View titleLayout = inflater.inflate(R.layout.note_activity_title_layout, parent, false);
            return new NoteTitleViewHolder(titleLayout);
        case ADD_LAYOUT_TYPE:
            View addLayout = inflater.inflate(R.layout.note_activity_add_item_layout, parent, false);
            return new NoteAddItemViewHolder(addLayout);
        default: //CONTENT_LAYOUT_TYPE
            View contentLayout = inflater.inflate(R.layout.note_activity_content_item_layout, parent, false);
            return new NoteContentViewHolder(contentLayout);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TITLE_LAYOUT_TYPE;
        } else if (position == mItemData.size() + 1) {
            return ADD_LAYOUT_TYPE;
        } else {
            return CONTENT_LAYOUT_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
        case TITLE_LAYOUT_TYPE:
            NoteTitleViewHolder titleViewHolder = (NoteTitleViewHolder) holder;
            titleViewHolder.mTitleText.setText(mNote.getTitle());
            TextDrawable drawable = TextDrawable.builder().buildRound("", mColorList[mNote.getColor()]);
            titleViewHolder.mColorIndicator.setImageDrawable(drawable);
            //                titleViewHolder.mColorIndicator.setColorFilter(mColorList[mNote.getColor()], PorterDuff.Mode.SRC_ATOP);
            break;
        case CONTENT_LAYOUT_TYPE:
            final NoteContentViewHolder contentViewHolder = (NoteContentViewHolder) holder;
            int contentPosition = position - 1;
            contentViewHolder.itemCheckbox.setChecked(mItemData.get(contentPosition).isChecked());
            contentViewHolder.itemEditText.setText(mItemData.get(contentPosition).getContent());

            if (position == focusPosition) {
                //                    focusPosition = -1;
                contentViewHolder.itemEditText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        contentViewHolder.itemEditText.requestFocus();
                        //                        if (curPos) {
                        //                            contentViewHolder.itemEditText.setSelection(contentViewHolder.itemEditText.length());
                        //                        } else {
                        contentViewHolder.itemEditText.setSelection(curPos);
                        //                        }

                        Common.showIME(mContext, contentViewHolder.itemEditText);
                    }
                }, 50);
            }
            contentViewHolder.itemHandler.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mOnStartDragListener.onStartDrag(contentViewHolder);
                    }
                    return false;
                }
            });
            contentViewHolder.itemRemove.setClickable(true);
            break;
        case ADD_LAYOUT_TYPE:
            //                NoteAddItemViewHolder addViewHolder = (NoteAddItemViewHolder) holder;
            break;
        default:
            break;
        }
    }

    @Override
    public int getItemCount() {
        return mItemData.size() + 2;
    }

    public void focusOnItem(int position, int pos) {
        focusPosition = position;
        curPos = pos;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        int from = fromPosition - 1;
        int to = toPosition - 1;
        if (from >= 0 && from < mItemData.size() && to >= 0 && to < mItemData.size()) {
            Collections.swap(mItemData, from, to);
            notifyItemMoved(fromPosition, toPosition);
        }

        return true;
    }

    @Override
    public void onItemDismiss(int position) {
    }

    public class NoteTitleViewHolder extends RecyclerView.ViewHolder {
        private ImageView mColorIndicator;
        private EditText mTitleText;

        public NoteTitleViewHolder(View itemView) {
            super(itemView);

            mColorIndicator = (ImageView) itemView.findViewById(R.id.color_indicator);
            mTitleText = (EditText) itemView.findViewById(R.id.edt_title);

            mColorIndicator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mNote.setTitle(mTitleText.getText().toString());

                    if (mColorDialog == null) {
                        mColorDialog = new ColorPickerWindow(mContext, mColorList, new ColorPickerInterface() {
                            @Override
                            public void changeColor(int index) {
                                mNote.setColor(index);

                                TextDrawable drawable = TextDrawable.builder().beginConfig().withBorder(2).endConfig()
                                        .buildRound("", mColorList[index]);
                                mColorIndicator.setImageDrawable(drawable);
                                //                                mColorIndicator.setColorFilter(mColorList[index], PorterDuff.Mode.SRC_ATOP);
                                mNoteAdapterInterface.onChangeColor(index);
                                notifyItemChanged(0);

                                if (mColorDialog != null) {
                                    mColorDialog.dismiss();
                                }
                            }
                        });
                        mColorDialog.setCurrentColor(mNote.getColor());
                    }
                    View focusingView = ((Activity) mContext).getCurrentFocus();
                    Common.hideIME(mContext, focusingView);
                    mColorDialog.show();
                }
            });

            mTitleText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        mNote.setTitle(mTitleText.getText().toString());
                        //                        Common.hideIME(mContext, view);
                    }
                }
            });

            mTitleText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mNoteAdapterInterface.scrollToPosition(1);
                }
            });
        }
    }

    public class NoteContentViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public ImageView itemHandler;
        public CheckBox itemCheckbox;
        public EditText itemEditText;
        public FrameLayout itemRemove;
        private View itemView;

        public NoteContentViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            itemView = itemLayoutView;

            itemHandler = (ImageView) itemView.findViewById(R.id.item_handler);
            itemCheckbox = (CheckBox) itemView.findViewById(R.id.item_checkbox);
            itemEditText = (EditText) itemView.findViewById(R.id.item_content);
            itemRemove = (FrameLayout) itemView.findViewById(R.id.item_remove_container);


            itemCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        itemEditText.setPaintFlags(itemEditText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        itemEditText.setPaintFlags(itemEditText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                    mItemData.get(getAdapterPosition() - 1).setChecked(isChecked);
                }
            });

            itemEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        int pos = getAdapterPosition();
                        if (pos > 0 && pos < mItemData.size() + 1) {
                            mItemData.get(getAdapterPosition() - 1).setContent(itemEditText.getText().toString());
                        }
                    } else {
                        focusPosition = getAdapterPosition();
                    }
                }
            });

            itemEditText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        EditText edt = (EditText) view;
                        String curContent = edt.getText().toString();
                        int cursorIndex = edt.getSelectionStart();
                        int pos = getAdapterPosition();

                        switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                        case KeyEvent.KEYCODE_NUMPAD_ENTER:
                            if (cursorIndex == curContent.length()) {
                                if (pos == mItemData.size()) {
                                    mItemData.add(new Item(Item.defaultValue, "", false, mItemData.size()));
                                    focusOnItem(mItemData.size(), 0);
                                    notifyItemInserted(mItemData.size());
                                    mNoteAdapterInterface.scrollToPosition(mItemData.size() + 1);
                                } else {
                                    mItemData.add(pos, new Item(Item.defaultValue, "", false, mItemData.size()));
                                    focusOnItem(pos + 1, 0);
                                    notifyItemInserted(pos + 1);
                                    mNoteAdapterInterface.scrollToPosition(pos + 1);
                                }
                            } else {
                                String newItemContent =
                                        curContent.substring(edt.getSelectionEnd(), curContent.length());
                                edt.setText(curContent.substring(0, cursorIndex));
                                Item newItem = new Item(mNote.getId(), newItemContent, false, pos);
                                mItemData.add(pos, newItem);
                                focusOnItem(pos + 1, 0);
                                notifyItemInserted(pos + 1);
                            }
                            return true;
                        case KeyEvent.KEYCODE_DEL:
                            if (cursorIndex == 0 && pos > 1) {
                                Item item = mItemData.get(pos - 2);
                                int previousLength = item.getContent().length();
                                item.setContent(item.getContent() + curContent);

                                mItemData.remove(pos - 1);
                                focusOnItem(pos - 1, previousLength);
                                notifyItemRemoved(pos);
                                notifyItemChanged(pos - 1);

                                return true;
                            }
                            break;
                        default:
                            break;

                        }
                    }
                    return false;
                }
            });

            itemEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (getAdapterPosition() == mItemData.size()) {
                        mNoteAdapterInterface.scrollToPosition(mItemData.size() + 1);
                    }
                }
            });

            itemRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemRemove.setClickable(false);
                    if (focusPosition == getAdapterPosition()) {
                        focusPosition = -1;
                        Common.hideIME(mContext, itemEditText);
                    }
                    int contentPosition = getAdapterPosition() - 1;
                    mItemData.remove(contentPosition);
                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }

        @Override
        public void onItemSelected() {
            //            itemView.setBackground(mContext.getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
        }

        @Override
        public void onItemClear() {
            //            itemView.setBackground(null);
        }
    }

    public class NoteAddItemViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout newItemContainer;

        public NoteAddItemViewHolder(View itemView) {
            super(itemView);

            newItemContainer = (RelativeLayout) itemView.findViewById(R.id.add_sub_task_container);

            newItemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemData.add(new Item(Item.defaultValue, "", false, mItemData.size()));
                    focusOnItem(mItemData.size(), 0);
                    notifyItemInserted(mItemData.size());
                    mNoteAdapterInterface.scrollToPosition(mItemData.size() + 1);
                }
            });
        }
    }
}

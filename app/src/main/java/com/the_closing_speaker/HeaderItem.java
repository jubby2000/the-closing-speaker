package com.the_closing_speaker;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;

import com.the_closing_speaker.databinding.ItemHeaderBinding;
import com.xwray.groupie.databinding.BindableItem;

/**
 * Created by jacob on 8/7/17.
 */

public class HeaderItem extends BindableItem<ItemHeaderBinding> {

    private String titleStringResId;
    private String subtitleResId;
    @DrawableRes
    private int iconResId;
    private View.OnClickListener onIconClickListener;

//    public HeaderItem(@StringRes int titleStringResId) {
//        this(titleStringResId, 0);
//    }

    public HeaderItem(String titleStringResId, String subtitleResId) {
        this(titleStringResId, subtitleResId, 0, null);
    }

    public HeaderItem(String titleStringResId, String subtitleResId, @DrawableRes int iconResId, View.OnClickListener onIconClickListener) {
        this.titleStringResId = titleStringResId;
        this.subtitleResId = subtitleResId;
        this.iconResId = iconResId;
        this.onIconClickListener = onIconClickListener;
    }

    @Override public int getLayout() {
        return com.the_closing_speaker.R.layout.item_header;
    }

    @Override public void bind(@NonNull ItemHeaderBinding viewBinding, int position) {
        viewBinding.title.setText(titleStringResId);
        if (subtitleResId != null) {
            viewBinding.subtitle.setText(subtitleResId);
        }
        viewBinding.subtitle.setVisibility(subtitleResId != null ? View.VISIBLE : View.GONE);

        if (iconResId > 0) {
            viewBinding.icon.setImageResource(iconResId);
            viewBinding.icon.setOnClickListener(onIconClickListener);
        }
        viewBinding.icon.setVisibility(iconResId > 0 ? View.VISIBLE : View.GONE);
    }
}
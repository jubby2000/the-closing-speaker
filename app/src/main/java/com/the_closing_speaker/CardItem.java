package com.the_closing_speaker;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.the_closing_speaker.databinding.ItemCardBinding;
import com.xwray.groupie.databinding.BindableItem;

/**
 * Created by jacob on 8/8/17.
 */

public class CardItem extends BindableItem<ItemCardBinding> {

    private static final String INSET_TYPE_KEY = "inset_type";
    private static final String INSET = "inset";

    @ColorInt
    private int colorRes;
    private CharSequence text;

    public CardItem(@ColorInt int colorRes) {
        this(colorRes, "");
    }

    public CardItem(@ColorInt int colorRes, CharSequence text) {
        this.colorRes = colorRes;
        this.text = text;
        getExtras().put(INSET_TYPE_KEY, INSET);
    }

    @Override
    public int getLayout() {
        return R.layout.item_card;
    }

    @Override
    public void bind(@NonNull ItemCardBinding viewBinding, int position) {
        //viewBinding.getRoot().setBackgroundColor(colorRes);
        viewBinding.text.setText(text);
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public CharSequence getText() {
        return text;
    }
}
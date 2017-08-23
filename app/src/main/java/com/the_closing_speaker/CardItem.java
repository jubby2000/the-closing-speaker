package com.the_closing_speaker;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.the_closing_speaker.databinding.ItemCardBinding;
import com.xwray.groupie.databinding.BindableItem;

import static com.the_closing_speaker.MainActivity.INSET;
import static com.the_closing_speaker.MainActivity.INSET_TYPE_KEY;

/**
 * Created by jacob on 8/8/17.
 */

public class CardItem extends BindableItem<ItemCardBinding> {

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
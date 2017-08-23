package com.the_closing_speaker;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Animatable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.the_closing_speaker.databinding.ItemHeartCardBinding;
import com.xwray.groupie.databinding.BindableItem;

import java.util.List;

/**
 * Created by jacob on 8/11/17.
 */

public class HeartCardItem extends BindableItem<ItemHeartCardBinding> {

    public static final String FAVORITE = "FAVORITE";
    private String authorStringResId;
    private String quoteStringResId;
    private String referenceStringResId;
    private String quoteKeyStringResId;

    @ColorInt
    private int colorRes;
    private OnFavoriteListener onFavoriteListener;
    private boolean checked = false;
    private boolean inProgress = false;

    public HeartCardItem(@ColorInt int colorRes, long id, OnFavoriteListener onFavoriteListener, String authorStringResId, String quoteStringResId, String referenceStringResId, String quoteKeyStringResId) {
        super(id);
        this.authorStringResId = authorStringResId;
        this.quoteStringResId = quoteStringResId;
        this.referenceStringResId = referenceStringResId;
        this.quoteKeyStringResId = quoteKeyStringResId;
        this.colorRes = colorRes;
        this.onFavoriteListener = onFavoriteListener;
        getExtras().put(MainActivity.INSET_TYPE_KEY, MainActivity.INSET);
    }

    @Override
    public int getLayout() {
        return R.layout.item_heart_card;
    }

    public String getQuoteKey() {
        return quoteKeyStringResId;
    }

    @Override
    public void bind(@NonNull final ItemHeartCardBinding binding, int position) {
        //binding.getRoot().setBackgroundColor(colorRes);
        bindHeart(binding);
        if (authorStringResId != null) {
            binding.text.setText(authorStringResId);
        }
        if (quoteStringResId != null) {
            binding.quoteView.setText(quoteStringResId);
        }
        if (referenceStringResId != null) {
            binding.referenceView.setText(referenceStringResId);
        }

        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inProgress = true;
                animateProgress(binding);

                onFavoriteListener.onFavorite(HeartCardItem.this, !checked);
            }
        });
    }

    private void bindHeart(ItemHeartCardBinding binding) {
        if (inProgress) {
            animateProgress(binding);
        } else {
            binding.favorite.setImageResource(R.drawable.favorite_state_list);
        }
        binding.favorite.setChecked(checked);
    }

    private void animateProgress(ItemHeartCardBinding binding) {
        binding.favorite.setImageResource(R.drawable.avd_favorite_progress);

//        int cx = (binding.favorite.getLeft() + binding.favorite.getRight()) / 2;
//        int cy = (binding.favorite.getTop() + binding.favorite.getBottom()) / 2;
//        int finalRadius = Math.max(binding.favorite.getWidth(), binding.favorite.getHeight());
//        Animator anim = ViewAnimationUtils.createCircularReveal(binding.favorite, cx, cy, 0, finalRadius);
        ((Animatable) binding.favorite.getDrawable()).start();
//        anim.start();
    }

    public void setFavorite(boolean favorite) {
        inProgress = false;
        checked = favorite;
    }

    @Override
    public void bind(@NonNull ItemHeartCardBinding binding, int position, List<Object> payloads) {
        if (payloads.contains(FAVORITE)) {
            bindHeart(binding);
        } else {
            bind(binding, position);
        }
    }

    @Override
    public boolean isClickable() {
        return false;
    }

    public interface OnFavoriteListener {
        void onFavorite(HeartCardItem item, boolean favorite);
    }
}

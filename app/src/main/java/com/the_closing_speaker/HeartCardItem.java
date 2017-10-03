package com.the_closing_speaker;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Animatable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.robinhood.ticker.TickerUtils;
import com.the_closing_speaker.databinding.ItemHeartCardBinding;
import com.xwray.groupie.databinding.BindableItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jacob on 8/11/17.
 */

public class HeartCardItem extends BindableItem<ItemHeartCardBinding> {

    public static final String FAVORITE = "FAVORITE";
    private String authorStringResId;
    private String quoteStringResId;
    private String referenceStringResId;
    private String quoteKeyStringResId;
    private boolean isFavorite;
    private String favoriteCount;

    @ColorInt
    private int colorRes;
    private OnFavoriteListener onFavoriteListener;
    private boolean checked = false;
    private boolean inProgress = false;

    public HeartCardItem(@ColorInt int colorRes, long id, OnFavoriteListener onFavoriteListener,
                         String authorStringResId, String quoteStringResId,
                         String referenceStringResId, String quoteKeyStringResId,
                         boolean isFavorite, String favoriteCount) {
        super(id);
        this.authorStringResId = authorStringResId;
        this.quoteStringResId = quoteStringResId;
        this.referenceStringResId = referenceStringResId;
        this.quoteKeyStringResId = quoteKeyStringResId;
        this.colorRes = colorRes;
        this.onFavoriteListener = onFavoriteListener;
        this.isFavorite = isFavorite;
        this.favoriteCount = favoriteCount;
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
    public void bind(@NonNull final ItemHeartCardBinding binding, final int position) {
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
        if (favoriteCount != null) {
//            binding.favoriteCounter.setCharacterList(TickerUtils.getDefaultNumberList());
            binding.favoriteCounter.setText(favoriteCount);
//            if (favoriteCount.equals("null") || favoriteCount.equals("0")) {
//                binding.favoriteCounter.setVisibility(View.VISIBLE);
//            } else {
//                binding.favoriteCounter.setVisibility(View.INVISIBLE);
//            }
        }

        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                inProgress = true;
//                animateProgress(binding);
                if(isFavorite) {
                    subtractFromCount(binding);
                    binding.favorite.setChecked(!isFavorite);
                } else {
                    addToCount(binding);
                    binding.favorite.setChecked(isFavorite);
                }

                onFavoriteListener.onFavorite(HeartCardItem.this, isFavorite);
            }
        });
    }

    private void bindHeart(final ItemHeartCardBinding binding) {
//        if (inProgress) {
//            animateProgress(binding);
//        } else {
            binding.favorite.setImageResource(R.drawable.favorite_state_list);
//        }
        binding.favoriteCounter.setCharacterList(TickerUtils.getDefaultNumberList());
//        binding.favoriteCounter.setText(favoriteCount);

        binding.favorite.setChecked(isFavorite);

    }

    private void addToCount(ItemHeartCardBinding binding) {
        favoriteCount = String.valueOf(Integer.parseInt(favoriteCount) + 1);
        binding.favoriteCounter.setText(favoriteCount);
        isFavorite = true;
    }

    private void subtractFromCount(ItemHeartCardBinding binding) {
        favoriteCount = String.valueOf(Integer.parseInt(favoriteCount) - 1);
        binding.favoriteCounter.setText(favoriteCount);
        isFavorite = false;
    }

    private void animateProgress(ItemHeartCardBinding binding) {
        binding.favorite.setImageResource(R.drawable.avd_favorite_progress);
        ((Animatable) binding.favorite.getDrawable()).start();
    }

    public void setIsFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void setFavorite(boolean favorite) {
        inProgress = false;
        checked = favorite;
        isFavorite = favorite;
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

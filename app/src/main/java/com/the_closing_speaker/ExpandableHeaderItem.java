package com.the_closing_speaker;

import android.graphics.drawable.Animatable;
import android.support.annotation.NonNull;
import android.view.View;

import com.the_closing_speaker.databinding.ItemHeaderBinding;
import com.xwray.groupie.ExpandableGroup;
import com.xwray.groupie.ExpandableItem;

/**
 * Created by jacob on 8/7/17.
 */

public class ExpandableHeaderItem extends HeaderItem implements ExpandableItem {

    private ExpandableGroup expandableGroup;

    public ExpandableHeaderItem(String titleStringResId, String subtitleResId) {
        super(titleStringResId, subtitleResId);
    }

    @Override public void bind(@NonNull final ItemHeaderBinding viewBinding, int position) {
        super.bind(viewBinding, position);

        // Initial icon state -- not animated.
        viewBinding.icon.setVisibility(View.VISIBLE);
        viewBinding.icon.setImageResource(expandableGroup.isExpanded() ? R.drawable.collapse : R.drawable.expand);
        viewBinding.headerItem.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                expandableGroup.onToggleExpanded();
                if (expandableGroup.isExpanded()) {
                    viewBinding.subtitle.setVisibility(View.GONE);
                } else {
                    viewBinding.subtitle.setVisibility(View.VISIBLE);
                }

                bindIcon(viewBinding);
            }
        });
    }

    private void bindIcon(ItemHeaderBinding viewBinding) {
        viewBinding.icon.setVisibility(View.VISIBLE);
        viewBinding.icon.setImageResource(expandableGroup.isExpanded() ? R.drawable.collapse_animated : R.drawable.expand_animated);
        Animatable drawable = (Animatable) viewBinding.icon.getDrawable();
        drawable.start();
    }

    @Override public void setExpandableGroup(@NonNull ExpandableGroup onToggleListener) {
        this.expandableGroup = onToggleListener;
    }
}
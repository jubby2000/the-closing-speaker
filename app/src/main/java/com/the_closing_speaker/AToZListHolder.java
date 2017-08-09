package com.the_closing_speaker;

import android.os.Build;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

/**
 * Created by Jacob on 3/1/2016.
 */
public class AToZListHolder extends ParentViewHolder{
    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */

    private final String LOG_TAG = AToZListHolder.class.getSimpleName();
    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private TextView mAToZTextView;
    private TextView mAToZSubTextView;
    private final ImageView mArrowExpandImageView;
    private LinearLayout mAToZListItem;

    public AToZListHolder(View itemView) {
        super(itemView);
        mAToZTextView = (TextView) itemView.findViewById(R.id.a_to_z_list_text_view);
        mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.arrow_expand_imageview);
        mAToZListItem = (LinearLayout) itemView.findViewById(R.id.a_to_z_list_item);
        mAToZSubTextView = (TextView) itemView.findViewById(R.id.a_to_z_list_sub_text);

        mArrowExpandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExpanded()) {
                    collapseView();
                } else {
                    expandView();
                }
            }
        });
    }

    public void bind (AToZList aToZList) {
        mAToZTextView.setText(aToZList.getName());

        StringBuilder builder = new StringBuilder();

//        for(Object obj : aToZList.getChildItemList()) {
//
//            Topic topic = (Topic) obj;
//
//            String lastNameOnly = null;
//
//            if (topic.getName().contains(",")) {
//                lastNameOnly = topic.getName().substring(0, topic.getName().indexOf(","));
//            }
//
//            if(builder.length() != 0) {
//                builder.append(", ");
//            }
//            if (lastNameOnly == null) {
//                builder.append(topic.getName());
//            } else {
//                builder.append(lastNameOnly);
//            }
//
//        }
        String lastNameOnly = null;
        Topic topic = (Topic) aToZList.getChildItemList().get(0);
        String sizeConcat = " and (" + String.valueOf(aToZList.getChildItemList().size()-1) + ") more";
        if (topic.getName().contains(",")) {
            lastNameOnly = topic.getName().substring(0, topic.getName().indexOf(","));
        }

        if (lastNameOnly == null) {
            builder.append(topic.getName());
        } else {
            builder.append(lastNameOnly);
        }

        if (aToZList.getChildItemList().size()-1 > 0) {
            builder.append(sizeConcat);
        }


        mAToZSubTextView.setText(builder.toString());
//        String newString = "";
//        for (int i = 0; i < aToZList.getChildItemList().size(); i++) {
//            newString.concat(aToZList.getChildItemList().get(i).toString() + ", ");
//            Log.v(LOG_TAG, );
//        }
//        mAToZSubTextView.setText(newString);
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (expanded) {
                mArrowExpandImageView.setRotation(ROTATED_POSITION);
                mAToZSubTextView.setVisibility(View.GONE);
            } else {
                mArrowExpandImageView.setRotation(INITIAL_POSITION);
                mAToZSubTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            RotateAnimation rotateAnimation;
            if (expanded) { // rotate clockwise
                rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            } else { // rotate counterclockwise
                rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            }

            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            mArrowExpandImageView.startAnimation(rotateAnimation);
        }
    }
}

package com.the_closing_speaker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

/**
 * Created by Jacob on 3/1/2016.
 */
public class AToZAdapter extends ExpandableRecyclerAdapter<AToZListHolder, TopicViewHolder> {

    private LayoutInflater mInflator;

    public AToZAdapter(Context context, @NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
    }

    // onCreate ...
    @Override
    public AToZListHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View aToZListView = mInflator.inflate(R.layout.a_to_z_view, parentViewGroup, false);
        return new AToZListHolder(aToZListView);
    }

    @Override
    public TopicViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View topicListView = mInflator.inflate(R.layout.topic_view, childViewGroup, false);
        return new TopicViewHolder(topicListView);
    }

    // onBind ...
    @Override
    public void onBindParentViewHolder(AToZListHolder aToZListHolder, int position, ParentListItem parentListItem) {
        AToZList aToZList = (AToZList) parentListItem;
        aToZListHolder.bind(aToZList);
    }

    @Override
    public void onBindChildViewHolder(TopicViewHolder topicViewHolder, int position, Object childListItem) {
        Topic topic = (Topic) childListItem;
        topicViewHolder.bind(topic);
    }
}

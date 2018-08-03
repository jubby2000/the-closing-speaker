package com.the_closing_speaker;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

/**
 * Created by Jacob on 3/1/2016.
 */
public class AToZList implements ParentListItem{

    private String mName;
    private List<Topic> mTopics;

    public AToZList(String name, List<Topic> topics) {
        mTopics = topics;
        mName = name;
    }

    public String getName() {
        return mName;
    }

    @Override
    public List getChildItemList() {
        return mTopics;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}

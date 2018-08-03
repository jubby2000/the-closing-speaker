package com.the_closing_speaker;

/**
 * Created by Jacob on 3/1/2016.
 */
public class Topic implements Comparable<Topic> {
    private String mName;

    public Topic(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Topic)) {
            return false;
        }
        Topic other = (Topic) obj;
        return this.mName.equals(other.mName);
    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }

    @Override
    public int compareTo(Topic o) {
        return this.mName.compareTo(o.mName);
    }
    // now override hashCode()
}

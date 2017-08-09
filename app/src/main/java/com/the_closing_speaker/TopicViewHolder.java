package com.the_closing_speaker;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

/**
 * Created by Jacob on 3/1/2016.
 */
public class TopicViewHolder extends ChildViewHolder {

    final String LOG_TAG = TopicViewHolder.class.getSimpleName();

    private TextView mTopicViewHolder;
    private Window mWindow = null;

    public TopicViewHolder(View itemView) {
        super(itemView);
        mTopicViewHolder = (TextView) itemView.findViewById(R.id.topic_list_text_view);

    }

    public void bind(final Topic topic) {

        final MainActivity main = (MainActivity) itemView.getContext();

        mTopicViewHolder.setText(topic.getName());
        mTopicViewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(main, DetailActivity.class);

                String reformatName = null;
                String firstName;
                String lastName;
                int count = 0;
                int lastCommaPosition = 0;

                for (int i = 0; i < topic.getName().length(); i++) {
                    char c = topic.getName().charAt(i);
                    if (String.valueOf(c).equals(",")) {
                        count++;
                        if (count == 2) {
                            lastCommaPosition = i;
                            Log.v(LOG_TAG, String.valueOf(lastCommaPosition + " " + i));
                        }
                    }
                }

                if (topic.getName().contains(",") && count <= 1) {
                    firstName = topic.getName().substring(topic.getName().indexOf(",") + 2);
                    lastName = topic.getName().substring(0, topic.getName().indexOf(","));
                    reformatName = firstName + " " + lastName;
                }

                if (topic.getName().contains(",") && count == 2) {
                    firstName = topic.getName().substring(lastCommaPosition + 2);
                    lastName = topic.getName().substring(0, lastCommaPosition);
                    reformatName = firstName + " " + lastName;
                    Log.v(LOG_TAG, reformatName);
                }

                if (count != 1 && count != 2) {
                    intent.putExtra("Topic", topic.getName());
                    Log.v(LOG_TAG, topic.getName());
                } else {
                    intent.putExtra("Topic", reformatName);
                }

                main.click(v, intent);

            }
        });
    }
}

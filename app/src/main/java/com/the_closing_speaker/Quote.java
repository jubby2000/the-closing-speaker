package com.the_closing_speaker;

/**
 * Created by jacob on 3/17/16.
 */
public class Quote {
    Long id;
    String firstName;
    String lastName;
    String groupName;
    String topic;
    String quote;
    String reference;
    String date;
    String pageNumber;
    Boolean favorite;

    Quote(Long id, String firstName, String lastName, String groupName, String topic, String quote,
          String reference, String date, String pageNumber, Boolean favorite) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.groupName = groupName;
        this.topic = topic;
        this.quote = quote;
        this.reference = reference;
        this.date = date;
        this.pageNumber = pageNumber;
        this.favorite = favorite;
    }
}

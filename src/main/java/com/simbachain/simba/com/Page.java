package com.simbachain.simba.com;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class Page {

    @JsonProperty
    private int count;
    @JsonProperty
    private String next;
    @JsonProperty
    private String previous;
    @JsonProperty
    private List<FullTransaction> results = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<FullTransaction> getResults() {
        return results;
    }

    public void setResults(List<FullTransaction> results) {
        this.results = results;
    }
}

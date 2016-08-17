package com.cubegames.slava.cubegame.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MyCollectionResponse<T> extends BasicResponse{
    private Collection<T> collection;

    public Collection<T> getCollection() {
        return collection;
    }
    public void setCollection(Collection<T> collection) {
        this.collection = collection;
    }
}

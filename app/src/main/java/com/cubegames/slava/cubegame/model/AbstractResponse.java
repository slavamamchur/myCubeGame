package com.cubegames.slava.cubegame.model;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractResponse {

    @JsonProperty("id")
    protected String id;
    @JsonProperty("error")
    protected String error;

    public AbstractResponse() {}
    public AbstractResponse(String id, String error) {
        this.id = id;
        this.error = error;
    }

    public String getId() {
        return id;
    }
    public String getError() {
        return error;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setError(String error) {
        this.error = error;
    }

    public String toString() {
		return "id:[" + this.getId() + "] error:[" + this.getError() + "]";
	}

    protected abstract void save2Parcel(Parcel dest);
    protected abstract void loadFromParcel(Parcel in);
}

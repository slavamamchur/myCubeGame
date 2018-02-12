package com.sadgames.dicegame.rest_api.model.entities;

import android.os.Parcel;

public abstract class BasicDbEntity extends BasicEntity {
    protected String id;
    protected String tenantId;
    protected boolean deleted;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    protected void save2Parcel(Parcel dest) {
        dest.writeString(id);
        dest.writeString(tenantId);
        dest.writeBooleanArray(new boolean[]{deleted});
    }

    @Override
    protected void loadFromParcel(Parcel in) {
        setId(in.readString());
        setTenantId(in.readString());
        boolean[] deleted= new boolean[1];
        in.readBooleanArray(deleted);
        setDeleted(deleted[0]);
    }
}

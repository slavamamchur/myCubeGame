package com.cubegames.slava.cubegame.model;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BasicDbEntity extends BasicEntity {
    protected String tenantId;
    protected boolean deleted;


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
        super.save2Parcel(dest);

        dest.writeString(tenantId);
        dest.writeBooleanArray(new boolean[]{deleted});
    }

    @Override
    protected void loadFromParcel(Parcel in) {
        super.loadFromParcel(in);

        setTenantId(in.readString());
        boolean[] deleted= new boolean[1];
        in.readBooleanArray(deleted);
        setDeleted(deleted[0]);
    }
}

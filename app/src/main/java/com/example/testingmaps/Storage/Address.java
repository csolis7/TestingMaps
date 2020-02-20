package com.example.testingmaps.Storage;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Address extends RealmObject {

    @PrimaryKey
    private int  idAddress;
    private String address;

    public int getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(int idAddress) {
        this.idAddress = idAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}

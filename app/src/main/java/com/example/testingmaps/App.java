package com.example.testingmaps;

import android.app.Application;

import com.google.gson.Gson;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {
    private static Gson gson = null;
    private static App Instance;
    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        Instance=this;
        gson = new Gson();
        Realm.init(getInstance());
       // byte[] key = getResources().getString(R.string.encryption_key_realm).getBytes();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("maps.realm")
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(1)
               // .encryptionKey(key)
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public static App getInstance(){
        return Instance;
    }

    public static Gson gson(){
        return gson;
    }

}

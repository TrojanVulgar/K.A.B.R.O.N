package com.rifcode.holalapremium.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class ServiceDestroyApp extends Service {

    private String myuserID;
    private FirebaseAuth mAuth;

    @androidx.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        mAuth =  FirebaseAuth.getInstance();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {

            myuserID = mAuth.getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("users").child(myuserID).child("state_app").setValue("destroy");
            FirebaseDatabase.getInstance().getReference().child("search").child(myuserID).removeValue();

        }
        stopSelf();

    }
}

package com.app.eventify.Utils;

import com.google.firebase.database.FirebaseDatabase;

public class DatabaseUtil
{
    private static FirebaseDatabase mFirebaseDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mFirebaseDatabase == null)
        {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseDatabase.setPersistenceEnabled(true);

        }
        return mFirebaseDatabase;
    }
}

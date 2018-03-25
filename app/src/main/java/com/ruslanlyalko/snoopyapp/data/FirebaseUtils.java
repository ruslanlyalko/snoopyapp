package com.ruslanlyalko.snoopyapp.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.User;

import java.util.Date;

public class FirebaseUtils {

    private static boolean mIsAdmin;
    private static User mUser;

    public static boolean isAdmin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return mIsAdmin
                || (user != null
                && user.getEmail() != null
                && (user.getEmail().equalsIgnoreCase("tanya.porynets2@gmail.com")
                || user.getEmail().equalsIgnoreCase("ruslan.lyalko@gmail.com")));
    }

    public static void setIsAdmin(boolean mIsAdmin) {
        FirebaseUtils.mIsAdmin = mIsAdmin;
    }

    public static void clearPushToken() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseDatabase.getInstance()
                    .getReference(DefaultConfigurations.DB_USERS)
                    .child(user.getUid())
                    .child("isOnline")
                    .removeValue();
            FirebaseDatabase.getInstance()
                    .getReference(DefaultConfigurations.DB_USERS)
                    .child(user.getUid())
                    .child("lastOnline")
                    .setValue(new Date());
            FirebaseDatabase.getInstance()
                    .getReference(DefaultConfigurations.DB_USERS)
                    .child(user.getUid())
                    .child("token")
                    .removeValue();
        }
    }

    public static void clearNotificationsForAllUsers(final String notKey) {
        FirebaseDatabase.getInstance()
                .getReference(DefaultConfigurations.DB_USERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSS : dataSnapshot.getChildren()) {
                            User user = userSS.getValue(User.class);
                            if (user != null)
                                FirebaseDatabase.getInstance()
                                        .getReference(DefaultConfigurations.DB_USERS_NOTIFICATIONS)
                                        .child(user.getId())
                                        .child(notKey)
                                        .removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {
                    }
                });
    }

    public static void markNotificationsAsRead(String key) {
        FirebaseDatabase.getInstance()
                .getReference(DefaultConfigurations.DB_USERS_NOTIFICATIONS)
                .child(FirebaseAuth.getInstance().getUid())
                .child(key)
                .removeValue();
    }

    public static User getUser() {
        return mUser;
    }

    public static void setUser(final User user) {
        mUser = user;
    }
}

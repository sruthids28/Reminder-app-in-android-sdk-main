package com.example.reminderapp.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.reminderapp.activities.LoginActivity;

/**
 * SessionManager manages user login sessions using SharedPreferences.
 */
public class SessionManager {
    private static final String PREF_NAME = "reminder_app_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    /**
     * Constructor initializes SharedPreferences.
     *
     * @param context Application context.
     */
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Creates a login session.
     *
     * @param userId    User ID.
     * @param userEmail User's email.
     */
    public void createLoginSession(int userId, String userEmail) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.commit();
    }

    /**
     * Checks if the user is logged in.
     *
     * @return true if logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Retrieves the current user's ID.
     *
     * @return User ID, or -1 if not logged in.
     */
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    /**
     * Retrieves the current user's email.
     *
     * @return User's email, or null if not logged in.
     */
    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Logs out the user and clears the session.
     */
    public void logoutUser() {
        editor.clear();
        editor.commit();
        Intent intent = new Intent(context, LoginActivity.class);
        // Clear the activity stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

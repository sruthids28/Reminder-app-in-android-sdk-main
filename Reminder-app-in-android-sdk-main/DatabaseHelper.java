package com.example.reminderapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.reminderapp.models.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper manages the SQLite database, including user and reminder tables.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reminder_app.db";
    private static final int DATABASE_VERSION = 1;

    // User table
    private static final String TABLE_USER = "users";
    private static final String USER_ID = "id";
    private static final String USER_EMAIL = "email";
    private static final String USER_PASSWORD = "password";

    // Reminder table
    private static final String TABLE_REMINDER = "reminders";
    private static final String REMINDER_ID = "id";
    private static final String REMINDER_TITLE = "title";
    private static final String REMINDER_DESCRIPTION = "description";
    private static final String REMINDER_DATE = "date";
    private static final String REMINDER_TIME = "time";
    private static final String REMINDER_USER_ID = "user_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the database tables for users and reminders.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + USER_EMAIL + " TEXT UNIQUE,"
                + USER_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_REMINDER_TABLE = "CREATE TABLE " + TABLE_REMINDER + "("
                + REMINDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + REMINDER_TITLE + " TEXT,"
                + REMINDER_DESCRIPTION + " TEXT,"
                + REMINDER_DATE + " TEXT,"
                + REMINDER_TIME + " TEXT,"
                + REMINDER_USER_ID + " INTEGER,"
                + "FOREIGN KEY(" + REMINDER_USER_ID + ") REFERENCES " + TABLE_USER + "(" + USER_ID + "))";
        db.execSQL(CREATE_REMINDER_TABLE);
    }

    /**
     * Handles database upgrades by dropping existing tables and recreating them.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // ----------------------- User Operations -----------------------

    /**
     * Registers a new user.
     *
     * @param email    User's email.
     * @param password User's password.
     * @return User ID if registration is successful, -1 otherwise.
     */
    public long registerUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_EMAIL, email);
        values.put(USER_PASSWORD, password);

        long id = db.insert(TABLE_USER, null, values);
        db.close();
        return id;
    }

    /**
     * Checks if a user exists with the provided email and password.
     *
     * @param email    User's email.
     * @param password User's password.
     * @return true if user exists, false otherwise.
     */
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {USER_ID};
        String selection = USER_EMAIL + " = ? AND " + USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    /**
     * Retrieves the user ID based on email.
     *
     * @param email User's email.
     * @return User ID if found, -1 otherwise.
     */
    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {USER_ID};
        String selection = USER_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(USER_ID));
        }
        cursor.close();
        db.close();
        return userId;
    }

    // --------------------- Reminder Operations ---------------------

    /**
     * Adds a new reminder to the database.
     *
     * @param reminder Reminder object containing details.
     * @return Row ID of the newly inserted reminder, -1 otherwise.
     */
    public long addReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(REMINDER_TITLE, reminder.getTitle());
        values.put(REMINDER_DESCRIPTION, reminder.getDescription());
        values.put(REMINDER_DATE, reminder.getDate());
        values.put(REMINDER_TIME, reminder.getTime());
        values.put(REMINDER_USER_ID, reminder.getUserId());

        long id = db.insert(TABLE_REMINDER, null, values);
        db.close();
        return id;
    }

    /**
     * Retrieves all reminders for a specific user.
     *
     * @param userId ID of the user.
     * @return List of reminders.
     */
    public List<Reminder> getAllReminders(int userId) {
        List<Reminder> reminderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {REMINDER_ID, REMINDER_TITLE, REMINDER_DESCRIPTION, REMINDER_DATE, REMINDER_TIME};
        String selection = REMINDER_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(TABLE_REMINDER, columns, selection, selectionArgs, null, null, REMINDER_DATE + " ASC, " + REMINDER_TIME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(REMINDER_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_DESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_TIME));

                Reminder reminder = new Reminder(id, title, description, date, time, userId);
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reminderList;
    }

    /**
     * Retrieves a single reminder by ID.
     *
     * @param id Reminder ID.
     * @return Reminder object if found, null otherwise.
     */
    public Reminder getReminder(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {REMINDER_ID, REMINDER_TITLE, REMINDER_DESCRIPTION, REMINDER_DATE, REMINDER_TIME, REMINDER_USER_ID};
        String selection = REMINDER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(TABLE_REMINDER, columns, selection, selectionArgs, null, null, null);
        Reminder reminder = null;
        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_DESCRIPTION));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_DATE));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(REMINDER_TIME));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(REMINDER_USER_ID));

            reminder = new Reminder(id, title, description, date, time, userId);
        }

        cursor.close();
        db.close();
        return reminder;
    }

    /**
     * Updates an existing reminder.
     *
     * @param reminder Reminder object with updated details.
     * @return Number of rows affected.
     */
    public int updateReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(REMINDER_TITLE, reminder.getTitle());
        values.put(REMINDER_DESCRIPTION, reminder.getDescription());
        values.put(REMINDER_DATE, reminder.getDate());
        values.put(REMINDER_TIME, reminder.getTime());

        String whereClause = REMINDER_ID + " = ?";
        String[] whereArgs = {String.valueOf(reminder.getId())};

        int rowsAffected = db.update(TABLE_REMINDER, values, whereClause, whereArgs);
        db.close();
        return rowsAffected;
    }

    /**
     * Deletes a reminder by ID.
     *
     * @param id Reminder ID.
     * @return Number of rows deleted.
     */
    public int deleteReminder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = REMINDER_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        int rowsDeleted = db.delete(TABLE_REMINDER, whereClause, whereArgs);
        db.close();
        return rowsDeleted;
    }
}

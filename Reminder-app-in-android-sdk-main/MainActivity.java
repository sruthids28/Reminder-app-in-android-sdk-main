package com.example.reminderapp.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reminderapp.R;
import com.example.reminderapp.adapters.ReminderAdapter;
import com.example.reminderapp.database.DatabaseHelper;
import com.example.reminderapp.models.Reminder;
import com.example.reminderapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * MainActivity displays the list of reminders and handles logout.
 */
public class MainActivity extends AppCompatActivity implements ReminderAdapter.OnItemClickListener, ReminderAdapter.OnDeleteClickListener {

    private ListView listViewReminders; // Correct ID
    private ReminderAdapter reminderAdapter;
    private DatabaseHelper dbHelper;
    private List<Reminder> reminderList;
    private FloatingActionButton fabAdd;
    private SessionManager sessionManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure this matches your layout XML

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        listViewReminders = findViewById(R.id.listViewReminders); // Correct ID
        fabAdd = findViewById(R.id.fabAdd);

        // Set listener for FAB to add new reminder
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditReminderActivity.class);
                startActivity(intent);
            }
        });

        // Load reminders
        loadReminders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminders();
    }

    /**
     * Loads reminders from the database and sets up the ListView.
     */
    private void loadReminders() {
        int userId = sessionManager.getUserId();
        reminderList = dbHelper.getAllReminders(userId);
        reminderAdapter = new ReminderAdapter(this, reminderList, this, this);
        listViewReminders.setAdapter(reminderAdapter);
    }

    /**
     * Inflates the main menu.
     *
     * @param menu Menu to inflate.
     * @return true if menu is inflated.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu); // Ensure main_menu.xml exists and has 'logout' ID
        return true;
    }

    /**
     * Handles menu item selections.
     *
     * @param item Selected menu item.
     * @return true if handled.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) { // Ensure 'logout' ID exists in menu
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Clear user session
                            sessionManager.logoutUser();

                            // Redirect to LoginActivity
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles reminder item clicks for editing.
     *
     * @param position Position of the clicked item.
     */
    @Override
    public void onItemClick(int position) {
        Reminder reminder = reminderList.get(position);
        Intent intent = new Intent(MainActivity.this, AddEditReminderActivity.class);
        intent.putExtra("REMINDER_ID", reminder.getId());
        startActivity(intent);
    }

    /**
     * Handles reminder item deletions.
     *
     * @param position Position of the item to delete.
     */
    @Override
    public void onDeleteClick(int position) {
        Reminder reminder = reminderList.get(position);
        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Delete Reminder")
                .setMessage("Are you sure you want to delete this reminder?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int result = dbHelper.deleteReminder(reminder.getId());
                        if (result > 0) {
                            Toast.makeText(MainActivity.this, "Reminder deleted.", Toast.LENGTH_SHORT).show();
                            loadReminders();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to delete reminder.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}

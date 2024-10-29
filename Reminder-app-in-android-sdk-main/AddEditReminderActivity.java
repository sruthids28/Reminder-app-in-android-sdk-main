package com.example.reminderapp.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reminderapp.R;
import com.example.reminderapp.database.DatabaseHelper;
import com.example.reminderapp.models.Reminder;
import com.example.reminderapp.utils.SessionManager;

import java.util.Calendar;

/**
 * AddEditReminderActivity allows users to add a new reminder or edit an existing one.
 */
public class AddEditReminderActivity extends AppCompatActivity {

    private EditText editTitle, editDescription, editDate, editTime;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private boolean isEditMode = false;
    private int reminderId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_reminder);

        // Initialize DatabaseHelper and SessionManager
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Get current user ID
        userId = sessionManager.getUserId();

        // Initialize views
        editTitle = findViewById(R.id.editTextTitle);
        editDescription = findViewById(R.id.editTextDescription);
        editDate = findViewById(R.id.editTextDate);
        editTime = findViewById(R.id.editTextTime);
        btnSave = findViewById(R.id.buttonSaveReminder);

        // Check if activity is in edit mode
        Intent intent = getIntent();
        if (intent.hasExtra("REMINDER_ID")) {
            isEditMode = true;
            reminderId = intent.getIntExtra("REMINDER_ID", -1);
            loadReminderData(reminderId);
        }

        // Set listeners for date and time fields
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        // Set listener for Save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveReminder();
            }
        });
    }

    /**
     * Loads existing reminder data into the form for editing.
     *
     * @param id Reminder ID.
     */
    private void loadReminderData(int id) {
        Reminder reminder = dbHelper.getReminder(id);
        if (reminder != null) {
            editTitle.setText(reminder.getTitle());
            editDescription.setText(reminder.getDescription());
            editDate.setText(reminder.getDate());
            editTime.setText(reminder.getTime());
        } else {
            Toast.makeText(this, "Reminder not found!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Displays a DatePicker dialog to select a date.
     */
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddEditReminderActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Format the date and set to EditText
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        editDate.setText(selectedDate);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /**
     * Displays a TimePicker dialog to select a time.
     */
    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddEditReminderActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Format the time and set to EditText
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        editTime.setText(selectedTime);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    /**
     * Validates the input fields and saves or updates the reminder.
     */
    private void saveReminder() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String date = editDate.getText().toString().trim();
        String time = editTime.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(title)) {
            editTitle.setError("Title is required");
            return;
        }

        if (TextUtils.isEmpty(date)) {
            editDate.setError("Date is required");
            return;
        }

        if (TextUtils.isEmpty(time)) {
            editTime.setError("Time is required");
            return;
        }

        if (isEditMode) {
            // Update existing reminder
            Reminder reminder = new Reminder(reminderId, title, description, date, time, userId);
            int result = dbHelper.updateReminder(reminder);
            if (result > 0) {
                Toast.makeText(this, "Reminder updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update reminder.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Add new reminder
            Reminder reminder = new Reminder(title, description, date, time, userId);
            long id = dbHelper.addReminder(reminder);
            if (id != -1) {
                Toast.makeText(this, "Reminder added successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add reminder.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

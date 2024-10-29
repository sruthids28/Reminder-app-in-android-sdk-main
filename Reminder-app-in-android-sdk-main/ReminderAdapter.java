package com.example.reminderapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reminderapp.R;
import com.example.reminderapp.models.Reminder;

import java.util.List;

/**
 * ReminderAdapter binds reminder data to the ListView.
 */
public class ReminderAdapter extends ArrayAdapter<Reminder> {

    private Context context;
    private List<Reminder> reminders;
    private OnItemClickListener itemClickListener;
    private OnDeleteClickListener deleteClickListener;

    /**
     * Interface for handling item clicks.
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * Interface for handling delete clicks.
     */
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    /**
     * Constructor for the adapter.
     *
     * @param context            Context.
     * @param reminders          List of reminders.
     * @param itemClickListener  Listener for item clicks.
     * @param deleteClickListener Listener for delete clicks.
     */
    public ReminderAdapter(@NonNull Context context, @NonNull List<Reminder> reminders, OnItemClickListener itemClickListener, OnDeleteClickListener deleteClickListener) {
        super(context, 0, reminders);
        this.context = context;
        this.reminders = reminders;
        this.itemClickListener = itemClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.item_reminder, parent, false);
        }

        Reminder currentReminder = reminders.get(position);

        TextView textTitle = listItem.findViewById(R.id.reminderTitle);
        TextView textDescription = listItem.findViewById(R.id.reminderDescription);
        TextView textDateTime = listItem.findViewById(R.id.reminderDateTime);
        ImageView imageDelete = listItem.findViewById(R.id.imageDelete);

        textTitle.setText(currentReminder.getTitle());
        textDescription.setText(currentReminder.getDescription());
        textDateTime.setText(currentReminder.getDate() + " " + currentReminder.getTime());

        // Set click listener for the entire item
        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(position);
                }
            }
        });

        // Set click listener for the delete icon
        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(position);
                }
            }
        });

        return listItem;
    }
}

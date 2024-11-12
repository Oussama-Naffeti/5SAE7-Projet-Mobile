package com.example.evenement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    private SQLiteDatabase database;

    public EventDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    // Add a new event to the database
    public long addEvent(Event event) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, event.getName());
        values.put(DatabaseHelper.COLUMN_DATE_START, event.getDateStart());
        values.put(DatabaseHelper.COLUMN_DATE_END, event.getDateEnd());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, event.getDescription());
        values.put(DatabaseHelper.COLUMN_DISCOUNT, event.getDiscount());

        return database.insert(DatabaseHelper.TABLE_EVENTS, null, values);
    }

    // Retrieve all events from the database
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_EVENTS,  // Table name
                null,                         // Select all columns
                null,                         // No WHERE clause
                null,                         // No WHERE arguments
                null,                         // No GROUP BY clause
                null,                         // No HAVING clause
                null                          // Default ORDER BY
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)); // Corrected to match `_id`
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String dateStart = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_START));
                String dateEnd = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_END));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                int discount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DISCOUNT));

                events.add(new Event(id, name, dateStart, dateEnd, description, discount));
            }
            cursor.close();
        }

        return events;
    }

    // Update an existing event in the database
    public void updateEvent(Event event) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, event.getName());
        values.put(DatabaseHelper.COLUMN_DATE_START, event.getDateStart());
        values.put(DatabaseHelper.COLUMN_DATE_END, event.getDateEnd());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, event.getDescription());
        values.put(DatabaseHelper.COLUMN_DISCOUNT, event.getDiscount());

        database.update(
                DatabaseHelper.TABLE_EVENTS,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(event.getId())}
        );
    }

    // Delete an event from the database
    public void deleteEvent(long id) {
        database.delete(
                DatabaseHelper.TABLE_EVENTS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }
}

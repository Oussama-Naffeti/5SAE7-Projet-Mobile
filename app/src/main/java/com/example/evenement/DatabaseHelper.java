package com.example.evenement;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "events.db";
    // Augmentez la version de la base de données à 2 pour inclure la table notifications
    private static final int DATABASE_VERSION = 2;

    // Constantes pour la table des événements
    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE_START = "date_start";
    public static final String COLUMN_DATE_END = "date_end";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DISCOUNT = "discount";

    // Requête pour créer la table des événements
    private static final String CREATE_TABLE_EVENTS =
            "CREATE TABLE " + TABLE_EVENTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_DATE_START + " TEXT, " +
                    COLUMN_DATE_END + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_DISCOUNT + " INTEGER);";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Méthode pour récupérer tous les événements
    public List<Event> getAllEvents() {
        // Implémentez la logique de base de données pour récupérer tous les événements
        return new ArrayList<>();
    }

    // Création des tables lors de la première exécution
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EVENTS);
    }

    // Mise à jour de la base de données lors du changement de version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Ajout de la table notifications si la version de la base de données est inférieure à 2
        }
    }

    // Méthode pour récupérer les événements par date de début
    public List<Event> getEventsByDate(SQLiteDatabase db, String date) {
        List<Event> events = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EVENTS + " WHERE " + COLUMN_DATE_START + " = ?", new String[]{date});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String dateStart = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_START));
                String dateEnd = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_END));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                int discount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DISCOUNT));

                events.add(new Event(id, name, dateStart, dateEnd, description, discount));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return events;
    }
}
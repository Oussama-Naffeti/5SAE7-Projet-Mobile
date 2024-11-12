package com.example.evenement;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText eventNameEditText, eventDateStartEditText, eventDateEndEditText, eventDescriptionEditText, eventDiscountEditText;
    private Button addButton, updateButton, deleteButton, notifyButton;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private EventDAO eventDAO;
    private Event selectedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialiser les vues
        eventNameEditText = findViewById(R.id.eventNameEditText);
        eventDateStartEditText = findViewById(R.id.eventDateStartEditText);
        eventDateEndEditText = findViewById(R.id.eventDateEndEditText);
        eventDescriptionEditText = findViewById(R.id.eventDescriptionEditText);
        eventDiscountEditText = findViewById(R.id.eventDiscountEditText);
        addButton = findViewById(R.id.buttonAdd);
        updateButton = findViewById(R.id.buttonUpdate);
        deleteButton = findViewById(R.id.buttonDelete);
        notifyButton = findViewById(R.id.btn_notify); // Bouton pour la notification
        recyclerView = findViewById(R.id.recyclerViewEvents);

        // Initialiser DAO et RecyclerView
        eventDAO = new EventDAO(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Charger les événements
        loadEvents();

        // Configurer DatePickerDialog pour la date de début
        eventDateStartEditText.setOnClickListener(v -> showDatePickerDialog(eventDateStartEditText));

        // Configurer DatePickerDialog pour la date de fin
        eventDateEndEditText.setOnClickListener(v -> showDatePickerDialog(eventDateEndEditText));

        addButton.setOnClickListener(v -> addEvent());
        updateButton.setOnClickListener(v -> updateEvent());
        deleteButton.setOnClickListener(v -> deleteEvent());

        // Créer le canal de notification
        NotificationHelper.createNotificationChannel(this);

        // Configurer le bouton de notification
        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Titre et message de la notification
                String title = "Nouvelle Notification";
                String message = "Une notification test a été déclenchée.";

                // Afficher la notification
                NotificationHelper.showSimpleNotification(MainActivity.this, title, message);
            }
        });
    }

    private void loadEvents() {
        List<Event> events = eventDAO.getAllEvents();
        eventAdapter = new EventAdapter(events, this::onEventSelected);
        recyclerView.setAdapter(eventAdapter);
    }

    private void showDatePickerDialog(EditText dateEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    dateEditText.setText(formattedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private boolean validateEventName() {
        String name = eventNameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            eventNameEditText.setError("Le nom de l'événement est obligatoire");
            return false;
        }
        return true;
    }

    private boolean validateEventDate() {
        String dateStart = eventDateStartEditText.getText().toString().trim();
        String dateEnd = eventDateEndEditText.getText().toString().trim();

        if (dateStart.isEmpty()) {
            eventDateStartEditText.setError("La date de début est obligatoire");
            return false;
        }
        if (dateEnd.isEmpty()) {
            eventDateEndEditText.setError("La date de fin est obligatoire");
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date startDate = sdf.parse(dateStart);
            Date endDate = sdf.parse(dateEnd);

            if (startDate != null && endDate != null && startDate.after(endDate)) {
                eventDateEndEditText.setError("La date de fin doit être postérieure à la date de début");
                return false;
            }
        } catch (ParseException e) {
            eventDateStartEditText.setError("Format de date invalide");
            eventDateEndEditText.setError("Format de date invalide");
            return false;
        }

        return true;
    }

    private boolean validateEventDiscount() {
        String discountText = eventDiscountEditText.getText().toString().trim();
        if (discountText.isEmpty()) {
            eventDiscountEditText.setError("La réduction est obligatoire");
            return false;
        }
        try {
            int discount = Integer.parseInt(discountText);
            if (discount < 0) {
                eventDiscountEditText.setError("La réduction ne peut pas être négative");
                return false;
            }
        } catch (NumberFormatException e) {
            eventDiscountEditText.setError("Veuillez entrer un nombre valide pour la réduction");
            return false;
        }
        return true;
    }

    private boolean validateAllFields() {
        return validateEventName() && validateEventDate() && validateEventDiscount();
    }

    private void addEvent() {
        if (!validateAllFields()) {
            Toast.makeText(this, "Veuillez corriger les erreurs", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = eventNameEditText.getText().toString().trim();
        String dateStart = eventDateStartEditText.getText().toString().trim();
        String dateEnd = eventDateEndEditText.getText().toString().trim();
        String description = eventDescriptionEditText.getText().toString().trim();
        int discount = Integer.parseInt(eventDiscountEditText.getText().toString().trim());

        Event newEvent = new Event(0, name, dateStart, dateEnd, description, discount);
        long id = eventDAO.addEvent(newEvent);

        if (id != -1) {
            newEvent.setId(id);
            eventAdapter.addEvent(newEvent);
            Toast.makeText(this, "Événement ajouté avec succès", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Échec de l'ajout de l'événement", Toast.LENGTH_SHORT).show();
        }

        clearInputFields();
    }

    private void updateEvent() {
        if (selectedEvent != null) {
            if (!validateAllFields()) {
                Toast.makeText(this, "Veuillez corriger les erreurs", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedEvent.setName(eventNameEditText.getText().toString().trim());
            selectedEvent.setDateStart(eventDateStartEditText.getText().toString().trim());
            selectedEvent.setDateEnd(eventDateEndEditText.getText().toString().trim());
            selectedEvent.setDescription(eventDescriptionEditText.getText().toString().trim());
            selectedEvent.setDiscount(Integer.parseInt(eventDiscountEditText.getText().toString().trim()));

            eventDAO.updateEvent(selectedEvent);
            eventAdapter.updateEvent(selectedEvent);
            Toast.makeText(this, "Événement mis à jour avec succès", Toast.LENGTH_SHORT).show();

            clearInputFields();
        } else {
            Toast.makeText(this, "Aucun événement sélectionné pour la mise à jour", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteEvent() {
        if (selectedEvent != null) {
            eventDAO.deleteEvent(selectedEvent.getId());
            eventAdapter.deleteEvent(selectedEvent);
            Toast.makeText(this, "Événement supprimé avec succès", Toast.LENGTH_SHORT).show();

            clearInputFields();
        } else {
            Toast.makeText(this, "Aucun événement sélectionné pour la suppression", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputFields() {
        eventNameEditText.setText("");
        eventDateStartEditText.setText("");
        eventDateEndEditText.setText("");
        eventDescriptionEditText.setText("");
        eventDiscountEditText.setText("");
        selectedEvent = null;
    }

    private void onEventSelected(Event event) {
        selectedEvent = event;
        eventNameEditText.setText(event.getName());
        eventDateStartEditText.setText(event.getDateStart());
        eventDateEndEditText.setText(event.getDateEnd());
        eventDescriptionEditText.setText(event.getDescription());
        eventDiscountEditText.setText(String.valueOf(event.getDiscount()));
    }
}

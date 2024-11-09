// RegisterActivity.java
package tn.esprit.user_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    DatabaseHelper dbHelper; // Instance de la classe DatabaseHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText emailInput = findViewById(R.id.registerEmailInput);
        EditText passwordInput = findViewById(R.id.registerPasswordInput);
        RadioGroup roleGroup = findViewById(R.id.roleGroup);
        Button registerButton = findViewById(R.id.registerButton);

        dbHelper = new DatabaseHelper(this); // Initialisation de DatabaseHelper

        registerButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            int selectedRoleId = roleGroup.getCheckedRadioButtonId();

            String role = "";
            if (selectedRoleId == R.id.radioClient) {
                role = "Client";
            } else if (selectedRoleId == R.id.radioEmployee) {
                role = "Employee";
            } else {
                Toast.makeText(RegisterActivity.this, "Please select a role", Toast.LENGTH_SHORT).show();
                return;
            }

            // Vérifier que les champs ne sont pas vides
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Vérifier si l'utilisateur existe déjà
            if (dbHelper.checkUserExists(email)) {
                Toast.makeText(RegisterActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            // Enregistrer l'utilisateur dans la base de données
            long result = dbHelper.addUser(email, password, role);

            if (result > 0) {
                Toast.makeText(RegisterActivity.this, "Registered successfully. Please log in.", Toast.LENGTH_SHORT).show();

                // Rediriger vers LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

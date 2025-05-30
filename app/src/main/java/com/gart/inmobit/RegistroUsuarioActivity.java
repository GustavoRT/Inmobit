package com.gart.inmobit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import modelo.Usuario;


public class RegistroUsuarioActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    EditText nombre, fechaNacimiento, telefono, nickname, correo, contrasenia, confirmarContrasenia;

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_usuario);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        nombre = findViewById(R.id.nameInput);
        fechaNacimiento = findViewById(R.id.birthdayInput);
        telefono = findViewById(R.id.phoneInput);
        nickname = findViewById(R.id.usernameInput);
        correo = findViewById(R.id.emailInput);
        contrasenia = findViewById(R.id.passwordInput);
        confirmarContrasenia = findViewById(R.id.confirmPasswordInput);

        TextView inicioText = findViewById(R.id.inicioText);
        inicioText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroUsuarioActivity.this, InicioSesionActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    public void registrarUsuario(View view) {

        if (nombre.getText().toString().trim().isEmpty() ||
                fechaNacimiento.getText().toString().trim().isEmpty() ||
                telefono.getText().toString().trim().isEmpty() ||
                nickname.getText().toString().trim().isEmpty() ||
                correo.getText().toString().trim().isEmpty() ||
                contrasenia.getText().toString().trim().isEmpty() ||
                confirmarContrasenia.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_LONG).show();
        } else {

            if (contrasenia.getText().toString().trim().equals(confirmarContrasenia.getText().toString().trim())) {

                // Crear usuario
                mAuth.createUserWithEmailAndPassword(
                                correo.getText().toString().trim(),
                                contrasenia.getText().toString().trim())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser();

                                // Enviar correo de verificación
                                if (user != null) {
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(verifyTask -> {
                                                if (verifyTask.isSuccessful()) {

                                                    // Crear objeto Usuario
                                                    Usuario usuario = new Usuario();
                                                    usuario.setId(user.getUid());
                                                    usuario.setNombre(nombre.getText().toString().trim());
                                                    usuario.setNombreUsuario(nickname.getText().toString().trim());
                                                    usuario.setContrasenia(contrasenia.getText().toString().trim());
                                                    usuario.setCorreo(correo.getText().toString().trim());
                                                    usuario.setTelefono(telefono.getText().toString().trim());
                                                    usuario.setFechaNacimiento(fechaNacimiento.getText().toString().trim());

                                                    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                                    String fechaFormateada = formatoFecha.format(new Date());
                                                    usuario.setFechaAlta(fechaFormateada);

                                                    // Guardar datos en Firestore
                                                    db.collection("usuarios")
                                                            .document(user.getUid())
                                                            .set(usuario)
                                                            .addOnSuccessListener(aVoid -> {
                                                                Toast.makeText(getApplicationContext(), "Registro exitoso. Verifica tu correo electrónico antes de iniciar sesión.", Toast.LENGTH_LONG).show();
                                                                limpiarCampos();
                                                                mAuth.signOut(); // Importante: cerrar sesión después del registro
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(getApplicationContext(), "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                            });

                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Error al enviar verificación: " + verifyTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }

                            } else {
                                Log.e("Auth", "Error al crear usuario: " + task.getException().getMessage());
                                Toast.makeText(this, "Error al registrar: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Las contraseñas deben coincidir.", Toast.LENGTH_LONG).show();
            }

        }
    }


    public void limpiarCampos(){
        nombre.setText("");
        fechaNacimiento.setText("");
        telefono.setText("");
        nickname.setText("");
        correo.setText("");
        contrasenia.setText("");
        confirmarContrasenia.setText("");
    }


}
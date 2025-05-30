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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import modelo.Inmueble;
import modelo.Usuario;


public class InicioSesionActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText correo, contrasenia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_sesion);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        correo = findViewById(R.id.correoInicioInput);
        contrasenia = findViewById(R.id.contraseniaInicioInput);


        TextView registerText = findViewById(R.id.registerText);
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioSesionActivity.this, RegistroUsuarioActivity.class);
                startActivity(intent);
            }
        });

        TextView inicioInvitado = findViewById(R.id.inicioInvitado);
        inicioInvitado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(InicioSesionActivity.this, InicioInvitadoActivity.class);
                startActivity(intent);
            }
        });
    }

    public void iniciarSesion(View view) {
        if (correo.getText().toString().trim().isEmpty() || contrasenia.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Introduce los campos.", Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(
                            correo.getText().toString().trim(),
                            contrasenia.getText().toString().trim())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null && user.isEmailVerified()) {
                                String usuarioId = user.getUid();
                                db.collection("usuarios").document(usuarioId).get()
                                        .addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                DocumentSnapshot document = task2.getResult();
                                                Usuario usuario = document.toObject(Usuario.class);
                                                Toast.makeText(this, "Hola " + usuario.getNombreUsuario(), Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(InicioSesionActivity.this, InicioUsuarioActivity.class);
                                                intent.putExtra("usuarioId", usuarioId);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(this, "Error al cargar datos del usuario.", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(this, "Debes verificar tu correo electrónico antes de iniciar sesión.", Toast.LENGTH_LONG).show();
                                mAuth.signOut(); // Importante: cerrar sesión si no está verificado
                            }

                        } else {
                            Toast.makeText(this, "Error de autenticación: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

}

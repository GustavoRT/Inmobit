package com.gart.inmobit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;
import modelo.Usuario;

public class MiPerfilActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView nombre, nombreUsuario, correo, telefono, fechaNacimiento;
    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mi_perfil);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializar vistas
        nombre = findViewById(R.id.nombre);
        nombreUsuario = findViewById(R.id.nombreUsuario);
        correo = findViewById(R.id.correo);
        telefono = findViewById(R.id.telefono);
        fechaNacimiento = findViewById(R.id.fechaNacimiento);
        profileImage = findViewById(R.id.profileImage);

        cargarDatosUsuario();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_inicio){
                Intent intent2 = new Intent(MiPerfilActivity.this, InicioUsuarioActivity.class);
                startActivity(intent2);
                return true;
            }
            if(item.getItemId() == R.id.nav_inmueble){
                Intent intent2 = new Intent(MiPerfilActivity.this, MiInmuebleActivity.class);
                startActivity(intent2);
                return true;
            }
            if(item.getItemId() == R.id.nav_perfil){
                Intent intent = new Intent(MiPerfilActivity.this, MiPerfilActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void cargarDatosUsuario() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Usuario usuario = documentSnapshot.toObject(Usuario.class);
                        if (usuario != null) {
                            nombre.setText(usuario.getNombre());
                            nombreUsuario.setText("@" + usuario.getNombreUsuario());
                            correo.setText(usuario.getCorreo());
                            telefono.setText(usuario.getTelefono());
                            fechaNacimiento.setText(usuario.getFechaNacimiento());

                            if (usuario.getFotoUrl() != null && !usuario.getFotoUrl().isEmpty()) {
                                Glide.with(this).load(usuario.getFotoUrl()).into(profileImage);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    public void editarUsuario(View view){
        Intent intent = new Intent(MiPerfilActivity.this, EditarUsuarioActivity.class);
        startActivity(intent);
    }

    public void cerrarSesion(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MiPerfilActivity.this, InicioSesionActivity.class);
        startActivity(intent);

    }
}

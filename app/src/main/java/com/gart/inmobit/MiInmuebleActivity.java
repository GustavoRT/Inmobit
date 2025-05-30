package com.gart.inmobit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import modelo.Inmueble;
import modelo.InmuebleAdapter;

public class MiInmuebleActivity extends AppCompatActivity {

    private RecyclerView recyclerInmuebles;
    private InmuebleAdapter adapter;
    private List<Inmueble> inmuebleList;

    private FirebaseFirestore db;
    private String usuarioId;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mi_inmueble);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
        }

        recyclerInmuebles = findViewById(R.id.recyclerInmuebles);
        recyclerInmuebles.setLayoutManager(new LinearLayoutManager(this));

        inmuebleList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        usuarioId = mAuth.getCurrentUser().getUid();
        adapter = new InmuebleAdapter(this, inmuebleList, usuarioId);
        recyclerInmuebles.setAdapter(adapter);

        cargarInmuebles();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_inicio){
                Intent intent2 = new Intent(MiInmuebleActivity.this, InicioUsuarioActivity.class);
                startActivity(intent2);
                return true;
            }
            if(item.getItemId() == R.id.nav_inmueble){
                Intent intent2 = new Intent(MiInmuebleActivity.this, MiInmuebleActivity.class);
                startActivity(intent2);
                return true;
            }
            if(item.getItemId() == R.id.nav_perfil){
                Intent intent = new Intent(MiInmuebleActivity.this, MiPerfilActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void cargarInmuebles() {
        db.collection("usuarios")
                .document(usuarioId)
                .collection("inmuebles")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    inmuebleList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Inmueble inmueble = doc.toObject(Inmueble.class);
                        inmuebleList.add(inmueble);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "No tienes inmuebles registrados", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error al obtener inmuebles", e);
                });
    }


}

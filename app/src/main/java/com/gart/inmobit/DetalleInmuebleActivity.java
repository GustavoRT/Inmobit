package com.gart.inmobit;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import modelo.ImagenAdapter;
import modelo.Inmueble;
import modelo.Usuario;

public class DetalleInmuebleActivity extends AppCompatActivity {

    Inmueble inmueble;
    private RecyclerView rvImagenes;
    private TextView tvDireccion, tvTipo, tvOperacion,tvOperacion2, tvPrecio, tvSuperficie,tvAmueblado,tvAmueblado2;
    private TextView tvHabitaciones,tvHabitaciones2, tvBanos, tvComentarios, tvFechaPublicacion;
    private TextView tvCalefaccion, tvAireAcondicionado, tvAscensor, tvParking,tvParking2, tvTrastero,tvTrastero2;
    private FirebaseAuth mAuth;
    private MaterialButton loginButton;
    private BottomNavigationView bottomNavigation;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_inmueble);

        loginButton = findViewById(R.id.loginButton);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            bottomNavigation.setVisibility(View.GONE);
        }else{
            loginButton.setVisibility(View.GONE);
        }

         inmueble = (Inmueble) getIntent().getSerializableExtra("inmueble");

        // Galería de imágenes
        rvImagenes = findViewById(R.id.rv_imagenes);

        // TextViews principales
        tvDireccion = findViewById(R.id.tv_direccion);
        tvTipo = findViewById(R.id.tv_tipo);
        tvOperacion = findViewById(R.id.tv_operacion);
        tvOperacion2 = findViewById(R.id.tv_operacion2);
        tvPrecio = findViewById(R.id.tv_precio);
        tvSuperficie = findViewById(R.id.tv_superficie);
        tvHabitaciones = findViewById(R.id.tv_habitaciones);
        tvHabitaciones2 = findViewById(R.id.tv_habitaciones2);
        tvBanos = findViewById(R.id.tv_banos);
        tvComentarios = findViewById(R.id.tv_comentarios);
        tvFechaPublicacion = findViewById(R.id.tv_fecha_publicacion);

        // Características
        tvCalefaccion = findViewById(R.id.tv_calefaccion);
        tvAireAcondicionado = findViewById(R.id.tv_aire_acondicionado);
        tvAscensor = findViewById(R.id.tv_ascensor);
        tvParking = findViewById(R.id.tv_parking);
        tvParking2 = findViewById(R.id.tv_parking2);
        tvTrastero = findViewById(R.id.tv_trastero);
        tvTrastero2 = findViewById(R.id.tv_trastero2);
        tvAmueblado  = findViewById(R.id.tv_amueblado);
        tvAmueblado2  = findViewById(R.id.tv_amueblado2);

        cargarDatos(inmueble);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_inicio){
                Intent intent = new Intent(DetalleInmuebleActivity.this, InicioUsuarioActivity.class);
                startActivity(intent);
                return true;
            }
            if(item.getItemId() == R.id.nav_inmueble){
                Intent intent = new Intent(DetalleInmuebleActivity.this, MiInmuebleActivity.class);
                startActivity(intent);
                return true;
            }
            if(item.getItemId() == R.id.nav_perfil){
                Intent intent = new Intent(DetalleInmuebleActivity.this, MiPerfilActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        if(inmueble.getTipoInmueble().equalsIgnoreCase("Habitación")){
            tvOperacion.setVisibility(View.GONE);
            tvOperacion2.setVisibility(View.GONE);
            tvHabitaciones.setVisibility(View.GONE);
            tvHabitaciones2.setVisibility(View.GONE);
            tvParking.setVisibility(View.GONE);
            tvParking2.setVisibility(View.GONE);
            tvTrastero.setVisibility(View.GONE);
            tvTrastero2.setVisibility(View.GONE);
            tvAmueblado.setVisibility(View.VISIBLE);
            tvAmueblado2.setVisibility(View.VISIBLE);
        }

        aumentarVisualizacion();
    }

    private void cargarDatos(Inmueble inmueble) {

        tvDireccion.setText(inmueble.getDireccion());
        tvTipo.setText(inmueble.getTipoInmueble());
        tvOperacion.setText(inmueble.getOperacion());
        tvPrecio.setText(inmueble.getPrecio()+" €");
        tvSuperficie.setText(String.valueOf(inmueble.getSuperficie()));
        tvHabitaciones.setText(String.valueOf(inmueble.getNumHabitaciones()));
        tvBanos.setText(String.valueOf(inmueble.getNumBanos()));
        tvComentarios.setText(inmueble.getComentarios());
        tvCalefaccion.setText(inmueble.isCalefaccion() ? "Si" : "No");
        tvAireAcondicionado.setText(inmueble.isAireAcondicionado() ? "Si" : "No");
        tvAscensor.setText(inmueble.isAscensor() ? "Si" : "No");
        tvParking.setText(inmueble.isParking() ? "Si" : "No");
        tvTrastero.setText(inmueble.isTrastero() ? "Si" : "No");
        tvAmueblado.setText(inmueble.isAmueblado() ? "Si" : "No");
        tvFechaPublicacion.setText(inmueble.getFechaRegistro());


        ArrayList<Uri> imagenesLocales = cargarImagenesDesdeListaRutas(inmueble.getImagenes());
       // ArrayList<Uri> imagenesLocales = cargarImagenesDesdeMemoria();

        ImagenAdapter imagenAdapter = new ImagenAdapter(imagenesLocales);
        rvImagenes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvImagenes.setAdapter(imagenAdapter);
        imagenAdapter.notifyDataSetChanged();



    }

    public void contactarPropietario(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .document(inmueble.getUsuarioId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Usuario usuario = document.toObject(Usuario.class);
                            Log.d("Firestore", "Usuario obtenido: " + usuario.getNombre());

                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                    .setTitle("Propietario: " + usuario.getNombre())
                                    .setMessage("Telefono: " + usuario.getTelefono()
                                            +"\n"+"Correo: " + usuario.getCorreo())
                                    .setNegativeButton("Cerrar", null).show();

                        } else {
                            Log.d("Firestore", "No se encontró el usuario");
                        }
                    } else {
                        Log.e("Firestore", "Error al obtener usuario", task.getException());
                    }
                });
    }

    private ArrayList<Uri> cargarImagenesDesdeMemoria() {
        ArrayList<Uri> imagenUris = new ArrayList<>();

        File directorio;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            directorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MisInmuebles");
        } else {
            directorio = new File(Environment.getExternalStorageDirectory(), "MisInmuebles");
        }

        if (directorio.exists() && directorio.isDirectory()) {
            File[] archivos = directorio.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    if (archivo.getAbsolutePath().endsWith(".jpg")) {
                        Uri uri = Uri.fromFile(archivo);
                        imagenUris.add(uri);
                    }
                }
            }
        }

        return imagenUris;
    }
    private ArrayList<Uri> cargarImagenesDesdeListaRutas(List<String> listaRutasImagenes) {
        ArrayList<Uri> imagenUris = new ArrayList<>();

        if (listaRutasImagenes == null || listaRutasImagenes.isEmpty()) {
            return imagenUris; // Retorna lista vacía si no hay rutas
        }

        for (String ruta : listaRutasImagenes) {
            if (ruta != null && !ruta.isEmpty()) {
                File archivo = new File(ruta);

                // Verificar si el archivo existe y es una imagen (opcional)
                if (archivo.exists() && (ruta.toLowerCase().endsWith(".jpg") ||
                        ruta.toLowerCase().endsWith(".png") ||
                        ruta.toLowerCase().endsWith(".jpeg"))) {
                    try {
                        Uri uri = Uri.fromFile(archivo);
                        imagenUris.add(uri);
                    } catch (Exception e) {
                        // Manejar error si no se puede crear la URI
                        Log.e("CargaImagenes", "Error al crear URI para: " + ruta, e);
                    }
                }
            }
        }

        return imagenUris;
    }
    public void irInicioSesion(View view){
        Intent intent = new Intent(DetalleInmuebleActivity.this,InicioSesionActivity.class);
        startActivity(intent);
    }

    public void aumentarVisualizacion(){
        inmueble.setVisualizacion();
        Toast.makeText(this, inmueble.getUsuarioId() +"-" + inmueble.getId()+ "-"+ inmueble.getVisualizar(), Toast.LENGTH_SHORT).show();
        FirebaseFirestore.getInstance().collection("usuarios")
                .document(inmueble.getUsuarioId())
                .collection("inmuebles")
                .document(inmueble.getId())
                .set(inmueble)
                .addOnSuccessListener(unused -> {
                  //  Toast.makeText(this, "Inmueble actualizado", Toast.LENGTH_SHORT).show();
                    Log.i("Firebase", "Inmueble actualizado");
                })
                .addOnFailureListener(e -> {
                  //  Toast.makeText(this, "Error al actualizar inmueble", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Error al actualizar inmueble", e);
                });
    }

}
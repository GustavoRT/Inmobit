package com.gart.inmobit;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import modelo.ImagenAdapter;
import modelo.Inmueble;

public class RegistroInmuebleActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    String[] tipoInmueble = { "Piso", "Casa","Chalet","Habitación" };
    String[] tipoOperacion = { "Venta", "Alquiler"};
    String[] habitaciones = {"1", "2", "3", "4", "5", "6+"};
    String[] banos = {"1", "2", "3+"};
    AutoCompleteTextView autoCompleteTipoInmueble, autoCompleteTipoOperacion;
    TextInputLayout autoCompleteTipoOperacionn;
    ArrayAdapter<String> adapterTipoInmueble, adapterTipoOperacion;
    TextView spinner_habitacioness;
    private Spinner numHabitaciones, numBanos;
    private TextInputEditText etPrecio, etSuperficie, etComentarios, etDireccion;
    private SwitchCompat swCalefaccion, swAire, swAscensor, swParking, swTrastero, swAmueblado;
    Double latitud;
    Double longitud;
    String usuarioId;

    private static final int PICK_IMAGES_REQUEST = 1;
    private RecyclerView rvImagenes;
    public ArrayList<Uri> imagenUris = new ArrayList<>();
    public ImagenAdapter imagenAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_inmueble);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
        }

        if( mAuth.getCurrentUser() != null) usuarioId = mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        latitud = intent.getDoubleExtra("latitud",0);
        longitud =intent.getDoubleExtra("longitud",0);

        etDireccion = findViewById(R.id.et_direccion);
        autoCompleteTipoInmueble = findViewById(R.id.auto_complete_tipo);
        autoCompleteTipoOperacion = findViewById(R.id.auto_complete_operacion);
        autoCompleteTipoOperacionn = findViewById(R.id.auto_complete_operacionn);
        numHabitaciones = findViewById(R.id.spinner_habitaciones);
        spinner_habitacioness = findViewById(R.id.spinner_habitacioness);
        numBanos = findViewById(R.id.spinner_banos);
        etPrecio = findViewById(R.id.et_precio);
        etSuperficie = findViewById(R.id.et_superficie);
        etComentarios = findViewById(R.id.et_comentarios);
        swCalefaccion = findViewById(R.id.switch_calefaccion);
        swAire = findViewById(R.id.switch_aire_acondicionado);
        swAscensor = findViewById(R.id.switch_ascensor);
        swParking = findViewById(R.id.switch_parking);
        swTrastero = findViewById(R.id.switch_trastero);
        swAmueblado  = findViewById(R.id.switch_amueblado);



        adapterTipoInmueble = new ArrayAdapter<String>(this,R.layout.list_item_tipo,tipoInmueble);
        autoCompleteTipoInmueble.setAdapter(adapterTipoInmueble);

        autoCompleteTipoInmueble.setOnItemClickListener((parent, view, position, id) -> {
            String seleccion = parent.getItemAtPosition(position).toString();
            if (seleccion.equalsIgnoreCase("Habitación")) {
                autoCompleteTipoOperacion.setVisibility(View.GONE);
                autoCompleteTipoOperacionn.setVisibility(View.GONE);
                numHabitaciones.setVisibility(View.GONE);
                swParking.setVisibility(View.GONE);
                swTrastero.setVisibility(View.GONE);
                spinner_habitacioness.setVisibility(View.GONE);
                swAmueblado.setVisibility(View.VISIBLE);
            }else{
                autoCompleteTipoOperacion.setVisibility(View.VISIBLE);
                autoCompleteTipoOperacionn.setVisibility(View.VISIBLE);
                numHabitaciones.setVisibility(View.VISIBLE);
                swParking.setVisibility(View.VISIBLE);
                swTrastero.setVisibility(View.VISIBLE);
                spinner_habitacioness.setVisibility(View.VISIBLE);
                swAmueblado.setVisibility(View.GONE);

            }
        });


        adapterTipoOperacion = new ArrayAdapter<String>(this,R.layout.list_item_tipo,tipoOperacion);
        autoCompleteTipoOperacion.setAdapter(adapterTipoOperacion);

        ArrayAdapter<String> adapterHabitaciones = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, habitaciones);
        adapterHabitaciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numHabitaciones.setAdapter(adapterHabitaciones);

        ArrayAdapter<String> adapterBanos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, banos);
        adapterBanos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numBanos.setAdapter(adapterBanos);

        // Configurar RecyclerView
        rvImagenes = findViewById(R.id.rv_imagenes);
        imagenAdapter = new ImagenAdapter(imagenUris);
        rvImagenes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvImagenes.setAdapter(imagenAdapter);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_inicio){
                Intent intent2 = new Intent(RegistroInmuebleActivity.this, InicioUsuarioActivity.class);
                startActivity(intent2);
                return true;
            }
            if(item.getItemId() == R.id.nav_inmueble){
                Intent intent2 = new Intent(RegistroInmuebleActivity.this, MiInmuebleActivity.class);
                startActivity(intent2);
                return true;
            }
            if(item.getItemId() == R.id.nav_perfil){
                Intent intent2 = new Intent(RegistroInmuebleActivity.this, MiPerfilActivity.class);
                startActivity(intent2);
                return true;
            }
            return false;
        });

    }

    public void registrarInmueble(View view){

//        if (etDireccion.getText().toString().trim().isEmpty() || autoCompleteTipoInmueble.getText().toString().trim().isEmpty() || autoCompleteTipoOperacion.getText().toString().trim().isEmpty()
//                || etPrecio.getText().toString().trim().isEmpty() || etSuperficie.getText().toString().trim().isEmpty() || etComentarios.getText().toString().trim().isEmpty()) {
//            Toast.makeText(this,"Introduce los capos alfanumericos",Toast.LENGTH_LONG).show();
//        }else{
            Inmueble inmueble = new Inmueble();
            inmueble.setDireccion(etDireccion.getText().toString().trim());
            inmueble.setTipoInmueble(autoCompleteTipoInmueble.getText().toString().trim());
            inmueble.setOperacion(autoCompleteTipoOperacion.getText().toString().trim());
            inmueble.setPrecio(Double.parseDouble(etPrecio.getText().toString().trim()));
            inmueble.setSuperficie(Integer.parseInt(etSuperficie.getText().toString().trim()));
            inmueble.setNumHabitaciones(numHabitaciones.getSelectedItem().toString());
            inmueble.setNumBanos(numBanos.getSelectedItem().toString());
            inmueble.setComentarios(etComentarios.getText().toString().trim());
            inmueble.setLatitud(latitud.toString());
            inmueble.setLongitud(longitud.toString());

            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String fechaFormateada = formatoFecha.format(new Date());
            inmueble.setFechaRegistro(fechaFormateada);

            if(swCalefaccion.isChecked())inmueble.setCalefaccion(true);
            if(swAire.isChecked())inmueble.setAireAcondicionado(true);
            if(swParking.isChecked())inmueble.setParking(true);
            if(swTrastero.isChecked())inmueble.setTrastero(true);
            if(swAscensor.isChecked())inmueble.setAscensor(true);
            if(swAmueblado.isChecked())inmueble.setAmueblado(true);


            FirebaseFirestore db = FirebaseFirestore.getInstance();

            String inmuebleId = db.collection("usuarios")
                    .document(usuarioId)
                    .collection("inmuebles")
                    .document().getId();
            inmueble.setId(inmuebleId);
            inmueble.setUsuarioId(usuarioId);

            ArrayList<String> rutasImagenes = guardarImagenesLocalmente(inmuebleId);
            inmueble.setImagenes(rutasImagenes);

            db.collection("usuarios")
                    .document(usuarioId)
                    .collection("inmuebles")
                    .document(inmuebleId)
                    .set(inmueble)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Inmueble registrado correctamente");
                        Toast.makeText(this,"Inmueble registrado correctamente",Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        Intent intent = new Intent(RegistroInmuebleActivity.this, InicioUsuarioActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error al registrar inmueble", e);
                        Toast.makeText(this,"Error al registrar inmueble",Toast.LENGTH_SHORT).show();
                    });
//        }
    }

    public void limpiarCampos() {
        etDireccion.setText("");
        etPrecio.setText("");
        etSuperficie.setText("");
        etComentarios.setText("");

        autoCompleteTipoInmueble.setText("");
        autoCompleteTipoOperacion.setText("");

        numHabitaciones.setSelection(0);
        numBanos.setSelection(0);

        swCalefaccion.setChecked(false);
        swAire.setChecked(false);
        swAscensor.setChecked(false);
        swParking.setChecked(false);
        swTrastero.setChecked(false);
        swAmueblado.setChecked(false);
    }
    public void abrirGaleria(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Selecciona imágenes"), PICK_IMAGES_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    // Múltiples imágenes
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imagenUris.add(imageUri);
                    }
                } else if (data.getData() != null) {
                    // Solo una imagen
                    imagenUris.add(data.getData());
                }
                imagenAdapter.notifyDataSetChanged();
            }
        }
    }

    public ArrayList<String> guardarImagenesLocalmente(String inmuebleID) {
        ArrayList<String> rutasGuardadas = new ArrayList<>();

        File directorio;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            directorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MisInmuebles");
        } else {
            directorio = new File(Environment.getExternalStorageDirectory(), "MisInmuebles");
        }
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        for (int i = 0; i < imagenUris.size(); i++) {
            Uri uri = imagenUris.get(i);
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                String fileName = "inmueble_img_" + inmuebleID + "_" + i + ".jpg";

                File file = new File(directorio, fileName);

                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                rutasGuardadas.add(file.getAbsolutePath());
            } catch (Exception e) {
                Log.e("GuardarImagen", "Error al guardar imagen", e);
            }
        }
        return rutasGuardadas;
    }

}
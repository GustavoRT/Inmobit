package com.gart.inmobit;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import modelo.ImagenAdapter;
import modelo.Inmueble;

public class EditarInmuebleActivity extends AppCompatActivity {
    String[] tipoInmueble = { "Piso", "Casa","Chalet","Habitación" };
    String[] tipoOperacion = { "Venta", "Alquiler"};
    String[] habitaciones = {"1", "2", "3", "4", "5", "6+"};
    String[] banos = {"1", "2", "3+"};
    Inmueble inmueble;
    private RecyclerView rvImagenes;
    TextView spinner_habitacioness;
    AutoCompleteTextView autoCompleteTipoInmueble, autoCompleteTipoOperacion;
    TextInputLayout autoCompleteTipoOperacionn;
    private Spinner numHabitaciones, numBanos;
    private TextInputEditText etPrecio, etSuperficie, etComentarios, etDireccion;
    private SwitchCompat swCalefaccion, swAire, swAscensor, swParking, swTrastero, swAmueblado;
    private FirebaseAuth mAuth;
    private MaterialButton loginButton;
    private BottomNavigationView bottomNavigation;
    MaterialButton editButton, updateButton, btn_add_image;
    private static final int PICK_IMAGES_REQUEST = 1;

    ArrayAdapter<String> adapterTipoInmueble, adapterTipoOperacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_inmueble);

        inmueble = (Inmueble) getIntent().getSerializableExtra("inmueble");

        rvImagenes = findViewById(R.id.rv_imagenes);
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
        btn_add_image = findViewById(R.id.btn_add_image);
        editButton = findViewById(R.id.editButton);
        updateButton = findViewById(R.id.updateButton);
        habilitarCampos(false);
        updateButton.setBackgroundColor(Color.parseColor("#d8d8d8"));
        updateButton.setEnabled(false);
        btn_add_image.setEnabled(false);

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

        if(inmueble.getTipoInmueble().equalsIgnoreCase("Habitación")){
            autoCompleteTipoOperacion.setVisibility(View.GONE);
            autoCompleteTipoOperacionn.setVisibility(View.GONE);
            numHabitaciones.setVisibility(View.GONE);
            spinner_habitacioness.setVisibility(View.GONE);
            swParking.setVisibility(View.GONE);
            swTrastero.setVisibility(View.GONE);
            swAmueblado.setVisibility(View.VISIBLE);
        }

        adapterTipoOperacion = new ArrayAdapter<String>(this,R.layout.list_item_tipo,tipoOperacion);
        autoCompleteTipoOperacion.setAdapter(adapterTipoOperacion);

        ArrayAdapter<String> adapterHabitaciones = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, habitaciones);
        adapterHabitaciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numHabitaciones.setAdapter(adapterHabitaciones);

        ArrayAdapter<String> adapterBanos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, banos);
        adapterBanos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numBanos.setAdapter(adapterBanos);

        cargarDatos(inmueble);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_inicio){
                Intent intent2 = new Intent(EditarInmuebleActivity.this, InicioUsuarioActivity.class);
                startActivity(intent2);
                return true;
            }
            if(item.getItemId() == R.id.nav_inmueble){
                Intent intent2 = new Intent(EditarInmuebleActivity.this, MiInmuebleActivity.class);
                startActivity(intent2);
                return true;
            }
            if(item.getItemId() == R.id.nav_perfil){
                Intent intent2 = new Intent(EditarInmuebleActivity.this, MiPerfilActivity.class);
                startActivity(intent2);
                return true;
            }
            return false;
        });
    }
    private void cargarDatos(Inmueble inmueble) {

        etDireccion.setText(inmueble.getDireccion());
        autoCompleteTipoInmueble.setText(inmueble.getTipoInmueble());
        autoCompleteTipoOperacion.setText(inmueble.getOperacion());
        etPrecio.setText(inmueble.getPrecio()+" €");
        etSuperficie.setText(String.valueOf(inmueble.getSuperficie()));


        if(inmueble.getNumHabitaciones().equalsIgnoreCase("1"))  numHabitaciones.setSelection(0);
        if(inmueble.getNumHabitaciones().equalsIgnoreCase("2"))  numHabitaciones.setSelection(1);
        if(inmueble.getNumHabitaciones().equalsIgnoreCase("3"))  numHabitaciones.setSelection(2);
        if(inmueble.getNumHabitaciones().equalsIgnoreCase("4"))  numHabitaciones.setSelection(3);
        if(inmueble.getNumHabitaciones().equalsIgnoreCase("5"))  numHabitaciones.setSelection(4);
        if(inmueble.getNumHabitaciones().equalsIgnoreCase("6+"))  numHabitaciones.setSelection(5);

        if(inmueble.getNumBanos().equalsIgnoreCase("1"))  numBanos.setSelection(0);
        if(inmueble.getNumBanos().equalsIgnoreCase("2"))  numBanos.setSelection(1);
        if(inmueble.getNumBanos().equalsIgnoreCase("3"))  numBanos.setSelection(2);

        etComentarios.setText(inmueble.getComentarios());
        swCalefaccion.setChecked(inmueble.isCalefaccion());
        swAire.setChecked(inmueble.isAireAcondicionado());
        swAscensor.setChecked(inmueble.isAscensor());
        swParking.setChecked(inmueble.isParking());
        swTrastero.setChecked(inmueble.isTrastero());
        swAmueblado.setChecked(inmueble.isAmueblado());

        ArrayList<Uri> imagenesLocales = cargarImagenesDesdeListaRutas(inmueble.getImagenes());
        // ArrayList<Uri> imagenesLocales = cargarImagenesDesdeMemoria();

        ImagenAdapter imagenAdapter = new ImagenAdapter(imagenesLocales);
        rvImagenes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvImagenes.setAdapter(imagenAdapter);
        imagenAdapter.notifyDataSetChanged();
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
    public void abrirGaleria(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Selecciona imágenes"), PICK_IMAGES_REQUEST);
    }

    private void habilitarCampos(boolean habilitar) {
        // RecyclerView
        rvImagenes.setEnabled(habilitar);

        // EditText
        etDireccion.setEnabled(habilitar);
        etPrecio.setEnabled(habilitar);
        etSuperficie.setEnabled(habilitar);
        etComentarios.setEnabled(habilitar);

        // AutoCompleteTextView
        autoCompleteTipoInmueble.setEnabled(habilitar);
        autoCompleteTipoOperacion.setEnabled(habilitar);
        autoCompleteTipoOperacionn.setEnabled(habilitar);

        // Spinners
        numHabitaciones.setEnabled(habilitar);
        spinner_habitacioness.setEnabled(habilitar);
        numBanos.setEnabled(habilitar);

        // Switches
        swCalefaccion.setEnabled(habilitar);
        swAire.setEnabled(habilitar);
        swAscensor.setEnabled(habilitar);
        swParking.setEnabled(habilitar);
        swTrastero.setEnabled(habilitar);
        swAmueblado.setEnabled(habilitar);
        btn_add_image.setEnabled(habilitar);
    }
    public void editarCampos(View view){
        habilitarCampos(true);
        editButton.setEnabled(false);
        editButton.setBackgroundColor(Color.parseColor("#d8d8d8"));

        updateButton.setEnabled(true);
        updateButton.setBackgroundColor(Color.parseColor("#6200EE"));
    }
    public void actualizarInmueble(View view){
        habilitarCampos(false);
        editButton.setEnabled(true);
        editButton.setBackgroundColor(Color.parseColor("#6200EE"));

        updateButton.setEnabled(false);
        updateButton.setBackgroundColor(Color.parseColor("#d8d8d8"));

       // String uid = userId.getUid();
    }
}
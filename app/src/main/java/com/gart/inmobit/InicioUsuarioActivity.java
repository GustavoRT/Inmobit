package com.gart.inmobit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import modelo.Inmueble;

public class InicioUsuarioActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    LatLng ies = new LatLng(40.0294868,-3.6119778);
    private FirebaseAuth mAuth;
     EditText searchInput;
     ImageButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_usuario);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapContainer);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_inicio){
                Intent intent = new Intent(InicioUsuarioActivity.this, InicioUsuarioActivity.class);
                startActivity(intent);
                return true;
            }
            if(item.getItemId() == R.id.nav_inmueble){
                Intent intent = new Intent(InicioUsuarioActivity.this, MiInmuebleActivity.class);
                startActivity(intent);
                return true;
            }
            if(item.getItemId() == R.id.nav_perfil){
                Intent intent = new Intent(InicioUsuarioActivity.this, MiPerfilActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(v -> {
            String location = searchInput.getText().toString().trim();
            if (!location.isEmpty()) {
                buscarYZoomUbicacion(location);
            } else {
                Toast.makeText(this, "Introduce una ubicación", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private BitmapDescriptor getCircleMarkerWithSoftBorder(int fillColor, int strokeColor, int radius, int strokeWidth) {
        // Configurar el tamaño del bitmap con espacio extra para el difuminado
        int padding = strokeWidth * 2;
        int bitmapSize = radius * 2 + padding * 2;

        // Crear el bitmap con configuración ARGB para transparencia
        Bitmap bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Pintar el círculo principal con relleno
        Paint fillPaint = new Paint();
        fillPaint.setColor(fillColor);
        fillPaint.setAntiAlias(true);
        fillPaint.setStyle(Paint.Style.FILL);

        // Pintar para el efecto de borde suavizado
        Paint softEdgePaint = new Paint();
        softEdgePaint.setColor(strokeColor);
        softEdgePaint.setAntiAlias(true);
        softEdgePaint.setStyle(Paint.Style.FILL);

        // Configurar el difuminado (blur) para el borde
        BlurMaskFilter blurFilter = new BlurMaskFilter(strokeWidth, BlurMaskFilter.Blur.NORMAL);
        softEdgePaint.setMaskFilter(blurFilter);

        // Coordenadas del centro
        float center = bitmapSize / 2f;

        // Dibujar el borde suavizado (primero para que quede detrás)
        canvas.drawCircle(center, center, radius, softEdgePaint);

        // Dibujar el círculo principal encima
        canvas.drawCircle(center, center, radius - strokeWidth, fillPaint);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
            return;
        }

        BitmapDescriptor softEdgeMarker = getCircleMarkerWithSoftBorder(Color.parseColor("#4285F4"), Color.WHITE, 30, 7);

        mMap.addMarker(new MarkerOptions().position(ies).title("Mi ubicacion").icon(softEdgeMarker).flat(true).anchor(0.5f, 0.5f));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ies, 13f));
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collectionGroup("inmuebles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Log.d("Firestore", "La consulta no devolvió resultados");
                            return;
                        }
                        BitmapDescriptor iconoPersonalizado = null;
                        for (DocumentSnapshot document : task.getResult()) {
                            try {
                                Inmueble inmueble = document.toObject(Inmueble.class);
                                if (inmueble != null && inmueble.getLatitud() != null && inmueble.getLongitud() != null) {
                                    if(inmueble.getTipoInmueble().equalsIgnoreCase("casa")) iconoPersonalizado = BitmapDescriptorFactory.fromResource(R.drawable.casa);
                                    if(inmueble.getTipoInmueble().equalsIgnoreCase("chalet")) iconoPersonalizado = BitmapDescriptorFactory.fromResource(R.drawable.chalet);
                                    if(inmueble.getTipoInmueble().equalsIgnoreCase("piso")) iconoPersonalizado = BitmapDescriptorFactory.fromResource(R.drawable.piso);
                                    if(inmueble.getTipoInmueble().equalsIgnoreCase("habitacion")) iconoPersonalizado = BitmapDescriptorFactory.fromResource(R.drawable.habitacion);

                                    LatLng ubicacion = new LatLng(Double.parseDouble(inmueble.getLatitud()),Double.parseDouble(inmueble.getLongitud()));

                                    Marker marker = mMap.addMarker(new MarkerOptions().position(ubicacion)
                                            .title(inmueble.getTipoInmueble())
                                            .icon(iconoPersonalizado)
                                            .snippet(inmueble.getDireccion()));
                                    marker.setTag(inmueble);
                                    Log.d("Firestore_Debug", "Inmueble encontrado: " + inmueble.getDireccion());
                                }
                            } catch (Exception e) {
                                Log.e("Firestore_Error", "Error procesando documento", e);
                            }
                        }
                    } else {
                        Log.e("Firestore", "Error en la consulta", task.getException());
                    }
                });

        mMap.setOnMarkerClickListener(marker -> {
            Object tag = marker.getTag();
            if (tag instanceof Inmueble) {
                mostrarDialogo(marker, (Inmueble) tag);
                return true; // Consumimos el evento
            }
            return false;
        });

    }

    private void mostrarDialogo(Marker marker, Inmueble inmueble) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Inmueble: " + inmueble.getTipoInmueble())
                .setMessage("Dirección: " + inmueble.getDireccion())
                .setPositiveButton("Ver", (dialog, which) -> {
                    Intent intent = new Intent(InicioUsuarioActivity.this, DetalleInmuebleActivity.class);
                    intent.putExtra("inmueble", inmueble);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", null).show();
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
    }
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Intent intent = new Intent(InicioUsuarioActivity.this, RegistroInmuebleActivity.class);
        intent.putExtra("latitud",latLng.latitude);
        intent.putExtra("longitud",latLng.longitude);
        startActivity(intent);
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void ubicacionActual(View view){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ies, 15f));
    }

    private void buscarYZoomUbicacion(String locationName) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            } else {
                Toast.makeText(this, "No se encontró la ubicación", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al buscar la ubicación", Toast.LENGTH_SHORT).show();
        }
    }

}
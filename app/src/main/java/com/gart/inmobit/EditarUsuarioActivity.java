package com.gart.inmobit;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import modelo.Usuario;

public class EditarUsuarioActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseUser currentUser;
    EditText nombre, fechaNacimiento, telefono, nombreUsuario, correo, contrasenia;
    TextView fechaAlta, id, cambiarFoto;
    MaterialButton editButton, updateButton;
    private FirebaseAuth mAuth;
    FirebaseUser userId;
    Usuario usuario;
    CircleImageView profileImage;

    Uri selectedImageUri;
    String fotoUrlFirebase = "";

    ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
        }

        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser();

        // Inicialización de vistas
        profileImage = findViewById(R.id.profileImage);
        cambiarFoto = findViewById(R.id.cambiarFoto);

        nombre = findViewById(R.id.nameInput);
        fechaNacimiento = findViewById(R.id.birthdayInput);
        telefono = findViewById(R.id.phoneInput);
        nombreUsuario = findViewById(R.id.usernameInput);
        correo = findViewById(R.id.emailInput);
        contrasenia = findViewById(R.id.passwordInput);
        editButton = findViewById(R.id.editButton);
        updateButton = findViewById(R.id.updateButton);
        fechaAlta = findViewById(R.id.fechaAlta);
        id = findViewById(R.id.id);

        habilitarCampos(false);
        updateButton.setBackgroundColor(Color.parseColor("#d8d8d8"));
        updateButton.setEnabled(false);

        // Obtener datos del usuario
        db.collection("usuarios").document(userId.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        usuario = documentSnapshot.toObject(Usuario.class);
                        if (usuario != null) {
                            nombre.setText(usuario.getNombre());
                            fechaNacimiento.setText(usuario.getFechaNacimiento());
                            telefono.setText(usuario.getTelefono());
                            nombreUsuario.setText(usuario.getNombreUsuario());
                            correo.setText(usuario.getCorreo());
                            contrasenia.setText(usuario.getContrasenia());
                            fechaAlta.setText(usuario.getFechaAlta());
                            id.setText(usuario.getId());

                            if (usuario.getFotoUrl() != null && !usuario.getFotoUrl().isEmpty()) {
                                Glide.with(this).load(usuario.getFotoUrl()).into(profileImage);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Error al obtener usuario", e));

        // Selector de imagen
        cambiarFoto.setOnClickListener(view -> abrirGaleria());

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        profileImage.setImageURI(selectedImageUri);
                    }
                });

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_inicio){
                Intent intent2 = new Intent(EditarUsuarioActivity.this, InicioUsuarioActivity.class);
                startActivity(intent2);
                return true;
            }
            if(item.getItemId() == R.id.nav_inmueble){
                Intent intent2 = new Intent(EditarUsuarioActivity.this, MiInmuebleActivity.class);
                startActivity(intent2);
                return true;
            }
            if(item.getItemId() == R.id.nav_perfil){
                Intent intent = new Intent(EditarUsuarioActivity.this, MiPerfilActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void habilitarCampos(boolean habilitar) {
        nombre.setEnabled(habilitar);
        fechaNacimiento.setEnabled(habilitar);
        telefono.setEnabled(habilitar);
        nombreUsuario.setEnabled(habilitar);
        cambiarFoto.setEnabled(habilitar);
        correo.setEnabled(false);
        contrasenia.setEnabled(false);
    }

    public void editarCampos(View view) {
        habilitarCampos(true);
        editButton.setEnabled(false);
        editButton.setBackgroundColor(Color.parseColor("#d8d8d8"));

        updateButton.setEnabled(true);
        updateButton.setBackgroundColor(Color.parseColor("#6200EE"));
    }

    public void actualizarUsuario(View view) {
        habilitarCampos(false);
        editButton.setEnabled(true);
        editButton.setBackgroundColor(Color.parseColor("#6200EE"));

        updateButton.setEnabled(false);
        updateButton.setBackgroundColor(Color.parseColor("#d8d8d8"));

        String uid = userId.getUid();

        // Guardar imagen localmente si hay nueva selección
        if (selectedImageUri != null) {
            String rutaLocal = guardarImagenLocal(uid);
            fotoUrlFirebase = rutaLocal;
        } else {
            fotoUrlFirebase = usuario.getFotoUrl();
        }

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(uid);
        usuarioActualizado.setNombre(nombre.getText().toString());
        usuarioActualizado.setFechaNacimiento(fechaNacimiento.getText().toString());
        usuarioActualizado.setTelefono(telefono.getText().toString());
        usuarioActualizado.setNombreUsuario(nombreUsuario.getText().toString());
        usuarioActualizado.setCorreo(correo.getText().toString());
        usuarioActualizado.setContrasenia(contrasenia.getText().toString());
        usuarioActualizado.setFechaAlta(fechaAlta.getText().toString());
        usuarioActualizado.setFotoUrl(fotoUrlFirebase);

        db.collection("usuarios").document(uid).set(usuarioActualizado)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("Firebase", "Error al actualizar usuario", e));
        habilitarCampos(false);
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private String guardarImagenLocal(String nombreArchivoUnico) {
        try {
            Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
            File directorio;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                directorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MisUsuarios");
            } else {
                directorio = new File(Environment.getExternalStorageDirectory(), "MisUsuarios");
            }
            if (!directorio.exists()) {
                directorio.mkdirs();
            }
            File archivo = new File(directorio, "perfil_" + nombreArchivoUnico + ".jpg");
            FileOutputStream fos = new FileOutputStream(archivo);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return archivo.getAbsolutePath();
        } catch (Exception e) {
            Log.e("GuardarImagen", "Error al guardar imagen de perfil", e);
            return "";
        }
    }

}

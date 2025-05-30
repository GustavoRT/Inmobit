package modelo;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gart.inmobit.DetalleInmuebleActivity;
import com.gart.inmobit.EditarInmuebleActivity;
import com.gart.inmobit.InicioUsuarioActivity;
import com.gart.inmobit.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.List;

public class InmuebleAdapter extends RecyclerView.Adapter<InmuebleAdapter.ViewHolder> {

    private Context context;
    private List<Inmueble> inmuebleList;
    private String usuarioId;
    private FirebaseFirestore db;

    public InmuebleAdapter(Context context, List<Inmueble> inmuebleList, String usuarioId) {
        this.context = context;
        this.inmuebleList = inmuebleList;
        this.usuarioId = usuarioId;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inmueble, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inmueble inmueble = inmuebleList.get(position);
        holder.titulo.setText(inmueble.getTipoInmueble());
        holder.descripcion.setText(inmueble.getDireccion());

        // Cargar imagen desde ruta local
        if (inmueble.getImagenes() != null && !inmueble.getImagenes().isEmpty()) {
            String ruta = inmueble.getImagenes().get(0); // Primera imagen
            File imgFile = new File(ruta);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.imagen.setImageBitmap(bitmap);
            }
        }

        // Botón eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            db.collection("usuarios")
                    .document(usuarioId)
                    .collection("inmuebles")
                    .document(inmueble.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        //Toast.makeText(context, "Inmueble eliminado", Toast.LENGTH_SHORT).show();
                        inmuebleList.remove(position);
                        notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> {
                        //Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    });
        });

        // Botón editar (puedes expandir esto con una nueva actividad)
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditarInmuebleActivity.class);
            intent.putExtra("inmueble", inmueble);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return inmuebleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion;
        ImageView imagen;
        ImageButton  btnEditar, btnEliminar;


        public ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textTitulo);
            descripcion = itemView.findViewById(R.id.textDescripcion);
            imagen = itemView.findViewById(R.id.imageInmueble);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}

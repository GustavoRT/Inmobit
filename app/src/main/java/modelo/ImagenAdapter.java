package modelo;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gart.inmobit.R;

import java.util.List;

public class ImagenAdapter extends RecyclerView.Adapter<ImagenAdapter.ImagenViewHolder> {

    private final List<Uri> imagenes;

    public ImagenAdapter(List<Uri> imagenes) {
        this.imagenes = imagenes;
    }

    @NonNull
    @Override
    public ImagenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imagen, parent, false);
        return new ImagenViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagenViewHolder holder, int position) {
        holder.imageView.setImageURI(imagenes.get(position));
    }

    @Override
    public int getItemCount() {
        return imagenes.size();
    }

    static class ImagenViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImagenViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_preview);
        }
    }
}

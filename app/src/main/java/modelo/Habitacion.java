package modelo;

import java.io.Serializable;
import java.util.List;

public class Habitacion implements Serializable {

    private String id;
    private List<String> imagenes;
    private double precio;
    private double superficie;
    private String numBanos;
    private String comentarios;
    private boolean calefaccion;
    private boolean aireAcondicionado;
    private boolean ascensor;
    private boolean amueblado;
    private String usuarioId;
    private String fechaRegistro;
    private String latitud;
    private String longitud;
    private String direccion;

    public Habitacion() {
    }

    public Habitacion(String id, List<String> imagenes, double precio, double superficie, String numBanos, String comentarios, String usuarioId, String fechaRegistro, String latitud, String longitud, String direccion) {
        this.id = id;
        this.imagenes = imagenes;
        this.precio = precio;
        this.superficie = superficie;
        this.numBanos = numBanos;
        this.comentarios = comentarios;
        this.calefaccion = false;
        this.aireAcondicionado = false;
        this.ascensor = false;
        this.amueblado = false;
        this.usuarioId = usuarioId;
        this.fechaRegistro = fechaRegistro;
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccion = direccion;
    }

    public boolean isAmueblado() {
        return amueblado;
    }

    public void setAmueblado(boolean amueblado) {
        this.amueblado = amueblado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<String> imagenes) {
        this.imagenes = imagenes;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getSuperficie() {
        return superficie;
    }

    public void setSuperficie(double superficie) {
        this.superficie = superficie;
    }

    public String getNumBanos() {
        return numBanos;
    }

    public void setNumBanos(String numBanos) {
        this.numBanos = numBanos;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public boolean isCalefaccion() {
        return calefaccion;
    }

    public void setCalefaccion(boolean calefaccion) {
        this.calefaccion = calefaccion;
    }

    public boolean isAireAcondicionado() {
        return aireAcondicionado;
    }

    public void setAireAcondicionado(boolean aireAcondicionado) {
        this.aireAcondicionado = aireAcondicionado;
    }

    public boolean isAscensor() {
        return ascensor;
    }

    public void setAscensor(boolean ascensor) {
        this.ascensor = ascensor;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}

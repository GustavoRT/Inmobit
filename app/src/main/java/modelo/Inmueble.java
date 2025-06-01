package modelo;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class Inmueble implements Serializable {
    private String id;
    private List<String> imagenes;
    private String tipoInmueble;
    private String operacion;
    private double precio;
    private double superficie;
    private String numHabitaciones;
    private String numBanos;
    private String comentarios;
    private boolean calefaccion;
    private boolean aireAcondicionado;
    private boolean ascensor;
    private boolean parking;
    private boolean trastero;
    private boolean amueblado;
    private String usuarioId;
    private String fechaRegistro;
    private String latitud;
    private String longitud;
    private String direccion;
    private int visualizar;

    public Inmueble() {
    }

    public Inmueble(String direccion, String id, List<String> imagenes, String tipoInmueble, String operacion, double precio, double superficie, String numHabitaciones, String numBanos, String comentarios, String usuarioId, String fechaRegistro, String latitud, String longitud) {
        this.id = id;
        this.imagenes = imagenes;
        this.tipoInmueble = tipoInmueble;
        this.operacion = operacion;
        this.precio = precio;
        this.superficie = superficie;
        this.numHabitaciones = numHabitaciones;
        this.numBanos = numBanos;
        this.comentarios = comentarios;
        this.calefaccion = false;
        this.aireAcondicionado = false;
        this.ascensor = false;
        this.parking = false;
        this.trastero = false;
        this.amueblado = false;
        this.usuarioId = usuarioId;
        this.fechaRegistro = fechaRegistro;
        this.direccion =direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.visualizar = 0;
    }

    public void setVisualizacion(){
        this.visualizar = this.visualizar + 1;
    }
    public int getVisualizar(){
        return visualizar;
    }
    public boolean isAmueblado() {
        return amueblado;
    }

    public void setAmueblado(boolean amueblado) {
        this.amueblado = amueblado;
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

    public String getTipoInmueble() {
        return tipoInmueble;
    }

    public void setTipoInmueble(String tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
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

    public String getNumHabitaciones() {
        return numHabitaciones;
    }

    public void setNumHabitaciones(String numHabitaciones) {
        this.numHabitaciones = numHabitaciones;
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

    public boolean isParking() {
        return parking;
    }

    public void setParking(boolean parking) {
        this.parking = parking;
    }

    public boolean isTrastero() {
        return trastero;
    }

    public void setTrastero(boolean trastero) {
        this.trastero = trastero;
    }
}

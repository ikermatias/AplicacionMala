package com.angular.gerardosuarez.carpoolingapp.mvp.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sebastian on 18/01/18.
 */

public class Notificacion {


    private String idPasajero;
    private String nombreConductor;
    private String texto;
    private String tokenPasajero;
    private String estado;


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("idPasajero", idPasajero);
        result.put("nombreConductor", nombreConductor);
        result.put("texto", texto);
        result.put("tokenPasajero", tokenPasajero);
        result.put("estado", estado);
        return result;
    }


    public void setIdPasajero(String idPasajero) {
        this.idPasajero = idPasajero;
    }

    public void setNombreConductor(String nombreConductor) {
        this.nombreConductor = nombreConductor;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setTokenPasajero(String tokenPasajero) {
        this.tokenPasajero = tokenPasajero;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

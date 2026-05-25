package modelo;
import utilidades.IdPersona;
import utilidades.Nombre;
import utilidades.Rut;

import java.util.ArrayList;
 // Autor: Yamilet Arias
public class Empresa {
    private String rut;
    private String nombre;
    private String url;

    private ArrayList<Bus> buses;
    private ArrayList<Conductor> conductores;
    private ArrayList<Auxiliar> auxiliares;

     public Empresa(Rut rut, String nombre, String url) {
         this.rut = rut;
         this.nombre = nombre;
         this.url = url;
         this.buses = new ArrayList<>();
         this.conductores = new ArrayList<>();
         this.auxiliares = new ArrayList<>();
     }

    public String getRut() {
        return rut;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUrl() {
        return url;
    }

    public void addBus(Bus bus) {
        buses.add(bus);
    }
     public Bus[] getBuses() {
         return buses.toArray(new Bus[0]);
     }

     public boolean addConductor(IdPersona id, Nombre nom, Direccion dir) {
         for (Conductor c : conductores) {
             if (c.getIdPersona().equals(id)) return false;
         }
         for (Auxiliar a : auxiliares) {
             if (a.getIdPersona().equals(id)) return false;
         }
         conductores.add(new Conductor(id, nom, dir));
         return true;
     }

     public boolean addAuxiliar(IdPersona id, Nombre nom, Direccion dir) {
         for (Conductor c : conductores) {
             if (c.getIdPersona().equals(id)) return false;
         }
         for (Auxiliar a : auxiliares) {
             if (a.getIdPersona().equals(id)) return false;
         }
         auxiliares.add(new Auxiliar(id, nom, dir));
         return true;
     }

     public Tripulante[] getTripulantes() {
         Tripulante[] lista = new Tripulante[conductores.size() + auxiliares.size()];
         int i = 0;
         for (Conductor c : conductores) lista[i++] = c;
         for (Auxiliar a : auxiliares)   lista[i++] = a;
         return lista;
     }

     public Venta[] getVentas() {
         ArrayList<Venta> ventas = new ArrayList<>();
         for (Bus b : buses) {
             for (Viaje v : b.getViajes()) {
                 for (Venta venta : v.getVentas()) {
                     if (!ventas.contains(venta)) ventas.add(venta);
                 }
             }
         }
         return ventas.toArray(new Venta[0]);
     }
 }

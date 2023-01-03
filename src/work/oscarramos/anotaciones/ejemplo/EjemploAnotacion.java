package work.oscarramos.anotaciones.ejemplo;

import work.oscarramos.anotaciones.models.Producto;
import work.oscarramos.anotaciones.utils.JsonSerializador;

import java.time.LocalDate;

public class EjemploAnotacion {
    public static void main(String[] args) {
        Producto p = new Producto();
        p.setFecha(LocalDate.now());
        p.setNombre("Centro de mesa");
        p.setPrecio(350000L);

        System.out.println("json = " + JsonSerializador.convertirJson(p));
    }
}

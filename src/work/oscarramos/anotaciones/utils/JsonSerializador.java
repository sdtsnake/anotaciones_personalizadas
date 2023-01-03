package work.oscarramos.anotaciones.utils;

import work.oscarramos.anotaciones.Init;
import work.oscarramos.anotaciones.JsonAtributo;
import work.oscarramos.anotaciones.excepciones.JsonSerializadorException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonSerializador {

    public static void  inicializarObjeto(Object object){
        if (Objects.isNull(object)) {
            throw new JsonSerializadorException("El objeto no puede ser null");
        }


        Method[] metodos = object.getClass().getDeclaredMethods();
        Arrays.stream(metodos).filter(m -> m.isAnnotationPresent(Init.class))
                .forEach(m-> {
                    m.setAccessible(true);
                    try {
                        m.invoke(object);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new JsonSerializadorException("Error al serailizar, no se puede iniciar el objeto" + e.getMessage());
                    }
                });

    }
    public static String convertirJson(Object object) {
        if (Objects.isNull(object)) {
            throw new JsonSerializadorException("El objeto no puede ser null");
        }
        inicializarObjeto(object);

        Field[] atributos = object.getClass().getDeclaredFields();

        return Arrays.stream(atributos)
                .filter(field -> field.isAnnotationPresent(JsonAtributo.class))
                .map(field -> {
                    field.setAccessible(true);
                    String nombre = field.getAnnotation(JsonAtributo.class).nombre().equals("")
                            ? field.getName()
                            : field.getAnnotation(JsonAtributo.class).nombre();
                    try {
                        Object valor = field.get(object);
                        if (field.getAnnotation(JsonAtributo.class).capitalizar() && valor instanceof String) {
                            String nuevoValor = (String) valor;
                            nuevoValor = Arrays
                                    .stream(nuevoValor.split(" "))
                                    .map(palabra -> palabra.substring(0, 1).toUpperCase() + palabra.substring(1).toLowerCase())
                                    .collect(Collectors.joining(" "));
                            field.set(object, nuevoValor);
                        }
                        return "\"" + nombre + "\":\"" + field.get(object) + "\"";
                    } catch (IllegalAccessException e) {
                        throw new JsonSerializadorException("Error al serializar a json : " + e.getMessage());
                    }
                })
                .reduce("{", (a, b) -> {
                    if ("{".equals(a)) {
                        return a + b;
                    }
                    return a + ", " + b;
                }).concat("}");
    }
}

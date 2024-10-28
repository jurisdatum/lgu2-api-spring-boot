package uk.gov.legislation.data.virtuoso.rdf;

import java.lang.reflect.*;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RdfMapper {

    public <T> T read(Map<URI, List<TypedValue>> properties, Class<T> clazz) {
        T t;
        try {
            t = clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (Field field: clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(RdfProperty.class))
                continue;
            RdfProperty annotation = field.getAnnotation(RdfProperty.class);
            URI prop = URI.create(annotation.value());
            List<TypedValue> values = properties.get(prop);
            if (values == null)
                continue;
//            field.setAccessible(true);
            try {
                if (field.getType().equals(String.class))
                    field.set(t, values.getFirst().value());
                else if (field.getType().equals(int.class))
                    field.setInt(t, Integer.parseInt(values.getFirst().value()));
                else if (field.getType().equals(List.class)) {
//                    Type t2 = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
//                    if (t2.equals(String.class))
                        field.set(t, values.stream().map(TypedValue::value).collect(Collectors.toList()));
                } else
                    throw new RuntimeException();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return t;
    }

}

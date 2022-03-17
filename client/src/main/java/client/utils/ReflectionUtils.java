package client.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import jdk.jfr.Description;
import jdk.jfr.Name;
import lombok.NonNull;



/**
 * Helper methods for reflection.
 */
public class ReflectionUtils {

    /**
     * Helper method to extract all fields
     * from an object that have the specified
     * annotation class.
     *
     * @param obj The object the filtering will be done upon.
     * @param annotation The annotation type we want the fields to have.
     * @return A list of all the fields that have the specified annotation.
     */
    public static List<Field> getAnnotatedFields(@NonNull Object obj,
                                                 Class<? extends Annotation> annotation) {
        var fields = new ArrayList<Field>();
        Class currentClass = obj.getClass();
        while (currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }

        return fields.stream()
                .filter(field -> field.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    /**
     * Helper method to extract all methods
     * from an object that have the specified
     * annotation class.
     *
     * @param obj The object the filtering will be done upon.
     * @param annotation The annotation type we want the methods to have.
     * @return A list of all the methods that have the specified annotation.
     */
    public static List<Method> getAnnotatedMethods(@NonNull Object obj,
                                                 Class<? extends Annotation> annotation) {
        var fields = new ArrayList<Method>();
        Class currentClass = obj.getClass();
        while (currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredMethods()));
            currentClass = currentClass.getSuperclass();
        }

        return fields.stream()
                .filter(method -> method.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    /**
     * Helper method to get the name
     * from a method by accessing the
     * Name annotation.
     *
     * @param method the requested method.
     * @return the string name of the method.
     */
    public static String getTextName(@NonNull Method method) {
        if (!method.isAnnotationPresent(Name.class)) {
            throw new IllegalArgumentException();
        }
        return method.getAnnotation(Name.class).value();
    }

    /**
     * Helper method to get the text
     * description from a field by accessing the
     * Description annotation.
     *
     * @param field the requested field.
     * @return the string description of the field.
     */
    public static String getTextDescription(@NonNull Field field) {
        if (!field.isAnnotationPresent(Description.class)) {
            throw new IllegalArgumentException();
        }
        return field.getAnnotation(Description.class).value();
    }

    /**
     * Helper method to get the minimum value
     * that a field can hold by accessing the
     * DecimalMin annotation.
     *
     * @param field the requested field.
     * @return the minimum value that this field can hold.
     */
    public static Float getMinValue(@NonNull Field field) {
        if (!field.isAnnotationPresent(DecimalMin.class)) {
            throw new IllegalArgumentException();
        }
        return Float.parseFloat(field.getAnnotation(DecimalMin.class).value());
    }

    /**
     * Helper method to get the maximum value
     * that a field can hold by accessing the
     * DecimalMax annotation.
     *
     * @param field the requested field.
     * @return the maximum value that this field can hold.
     */
    public static Float getMaxValue(@NonNull Field field) {
        if (!field.isAnnotationPresent(DecimalMax.class)) {
            throw new IllegalArgumentException();
        }
        return Float.parseFloat(field.getAnnotation(DecimalMax.class).value());
    }
}

package commons.entities.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jdk.jfr.MetadataDefinition;

/**
 * This is a custom annotation that is to be used for
 * configuration fields which are too granualar for the user.
 * The value will be multiplied by the value specified and divided back.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@MetadataDefinition
public @interface Multiplier {
    /**
     * The value to multiply/ divide by.
     *
     * @return the value.
     */
    int value();
}
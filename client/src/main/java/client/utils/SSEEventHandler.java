package client.utils;

import commons.SSEMessage;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jdk.jfr.MetadataDefinition;

/**
 * This is a custom annotation that is to be used for
 * sse event handlers.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@MetadataDefinition
public @interface SSEEventHandler {
    /**
     * Returns the sse event enums.
     *
     * @return the enum.
     */
    SSEMessage value();
}
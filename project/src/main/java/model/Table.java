package model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/**
 * Annotation used for storing table information
 */
public @interface Table {
    String tableName();
    String pkField();
    boolean autoIncrement();
}

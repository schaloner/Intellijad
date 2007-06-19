package net.stevechaloner.intellijad.config;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to indicate a {@link javax.swing.JComponent} is considered
 * part of the general control set, as defined by whatever class is using this.
 *
 * @author Steve Chaloner
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.FIELD)
public @interface Control
{
}

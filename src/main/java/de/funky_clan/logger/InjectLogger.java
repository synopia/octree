package de.funky_clan.logger;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author synopia
 */
@Target({ FIELD, ElementType.PARAMETER })
@Retention(RUNTIME)
public @interface InjectLogger {
}

package de.funky_clan.logger;

import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * @author synopia
 */
public class InjectLoggerTypeListener implements TypeListener {
    private static class MyInjector<T> implements MembersInjector<T> {
        private final Field field;
        private final Logger logger;

        private MyInjector(Field field) {
            this.field = field;
            this.logger = LoggerFactory.getLogger(field.getDeclaringClass());
            field.setAccessible(true);
        }

        @Override
        public void injectMembers(T instance) {
            try {
                field.set(instance, logger);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        for (Field field : type.getRawType().getDeclaredFields()) {
            if( field.getType()==Logger.class && field.isAnnotationPresent(InjectLogger.class)) {
                encounter.register(new MyInjector<I>(field));
            }
        }
    }
}

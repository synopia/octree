package de.funky_clan.coregl;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import de.funky_clan.logger.InjectLoggerModule;

/**
 * @author synopia
 */
public class CoreGlModule extends AbstractModule {
    @Override
    protected void configure() {
        install( new InjectLoggerModule() );
    }
}

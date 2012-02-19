package de.funky_clan.logger;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;

/**
 * @author synopia
 */
public class InjectLoggerModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bindListener(Matchers.any(), new InjectLoggerTypeListener());
    }
}

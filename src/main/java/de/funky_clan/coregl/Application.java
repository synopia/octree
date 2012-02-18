package de.funky_clan.coregl;

import java.io.IOException;

/**
 * @author synopia
 */
public interface Application {
    void init(ApplicationController ctrl);
    void update( int delta );
    void render();
}

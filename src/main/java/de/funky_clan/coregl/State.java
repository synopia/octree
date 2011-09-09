package de.funky_clan.coregl;

import java.io.IOException;

/**
 * @author paul.fritsche@objectfab.de
 */
public interface State {
    public String getName();

    public void init( GameWindow window ) throws IOException;
    public void enter( GameWindow window );
    public void leave( GameWindow window );

    public void update( GameWindow window, int delta );
    public void render( GameWindow window, int delta );
}

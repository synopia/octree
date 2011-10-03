package de.funky_clan.octree;

import de.funky_clan.coregl.GameWindow;
import de.funky_clan.coregl.renderer.MappedVertex;
import org.lwjgl.util.mapped.MappedObjectClassLoader;
import org.lwjgl.util.mapped.MappedObjectTransformer;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author paul.fritsche@objectfab.de
 */
public class Main {
    public static void main(String[] args) throws IOException {
        MappedObjectTransformer.register(MappedVertex.class);
        if(MappedObjectClassLoader.fork(Main.class, args)) {
            return;
        }
        GameWindow gameWindow = new GameWindow(800, 600);
        gameWindow.addState( new TestState() );
        gameWindow.startGame();
    }
}

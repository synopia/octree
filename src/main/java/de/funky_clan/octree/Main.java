package de.funky_clan.octree;

import de.funky_clan.coregl.GameWindow;

import java.io.IOException;

/**
 * @author paul.fritsche@objectfab.de
 */
public class Main {
    public static void main(String[] args) throws IOException {
      System.out.println(System.getProperty("java.library.path"));
        GameWindow gameWindow = new GameWindow(800, 600);
        gameWindow.addState( new TestState() );
        gameWindow.startGame();
    }

}

package de.funky_clan.coregl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author synopia
 */
public class Lighting {
    private List<LightSource> lightSources = new ArrayList<LightSource>();
    private LightSource off;
    private HashMap<Integer, LightSource> lightIds=new HashMap<Integer, LightSource>();
    private int nextId=1;

    public Lighting() {
        off = new LightSource(0,0,0,0,0,0,0,0,0,0,0,0,1,1,1);
    }

    public LightSource createLight( float x, float y, float z,
                       float Dr, float Dg, float Db,
                       float Ar, float Ag, float Ab,
                       float cA, float lA, float qA ) {
        LightSource lightSource = new LightSource(x, y, z, Dr, Dg, Db, Ar, Ag, Ab, 1,1,1,cA, lA, qA);
        addLight(lightSource);
        return lightSource;
    }

    public void addLight( LightSource lightSource ) {
        if( !lightSources.contains(lightSource) ) {
            lightSources.add(lightSource);
        }
    }
    public void removeLight( LightSource lightSource ) {
        lightSources.remove(lightSource);
    }

    public void update( int delta ) {
        Iterator<LightSource> it = lightSources.iterator();
        while (it.hasNext()) {
            LightSource next = it.next();
            if( !next.update(delta) ) {
                it.remove();
            }
        }
    }

    public void doLighting( Vector3f pos) {
        for (LightSource source : lightSources) {
            source.doLighting(pos);
        }
        Collections.sort( lightSources );
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        int noLights = Math.min(8, lightSources.size());
        for( int i=0; i<noLights; i++ ) {
            LightSource lightSource = lightSources.get(i);
            lightSource.bind( i );
        }
        for( int i=noLights; i<8; i++ ) {
            off.bind(i);
        }
        GL11.glPopMatrix();
    }

}

package de.funky_clan.coregl.renderer;


import org.lwjgl.util.mapped.MappedObject;
import org.lwjgl.util.mapped.MappedType;

/**
 * @author synopia
 */
@MappedType()
public class MappedVertex extends MappedObject {
    public float x;
    public float y;
    public float z;
    public float tx;
    public float ty;
    public int color;
    public float nx;
    public float ny;
    public float nz;
}

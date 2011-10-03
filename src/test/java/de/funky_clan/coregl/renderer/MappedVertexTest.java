package de.funky_clan.coregl.renderer;

import com.sun.deploy.util.BufferUtil;
import org.junit.Test;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.mapped.MappedObject;
import org.lwjgl.util.mapped.MappedObjectClassLoader;
import org.lwjgl.util.mapped.MappedObjectTransformer;

import java.nio.ByteBuffer;

/**
 * @author synopia
 */
public class MappedVertexTest {
    public static void main(String[] args) {
        MappedObjectTransformer.register(MappedVertex.class);
        if(MappedObjectClassLoader.fork(MappedVertexTest.class, args)) {
            return;
        }

        ByteBuffer bb = BufferUtils.createByteBuffer(4096);
        MappedVertex vertex = MappedVertex.map(bb);

        vertex.x = 10;
        vertex.y = 20;
        vertex.z = 30;

        float x = bb.getFloat(0<<2);
        System.out.println(x);
    }
}

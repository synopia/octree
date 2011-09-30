package de.funky_clan.coregl.renderer;

import de.funky_clan.coregl.geom.Vertex;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author synopia
 */
public class BufferedRenderer {
    public static final int NUMBER_OF_BUFFERS = 8;
    public static final int BUFFER_SIZE = 0x10000;

    protected VBO[] buffers;
    protected int bufferIndex = 0;

    private IntBuffer vboIds;

    private float[]     translation = new float[]{0,0,0};
    private float[]     scale = new float[]{1,1,1};
    private float[]     textureCoords = new float[]{ 0,0, 1/16.f, 1/16.f };

    private int texCoordFormat;
    private int colorFormat;
    private int normalFormat;
    private int strideSize;
    private int texCoordOffset;
    private int colorOffset;
    private int normalOffset;

    protected int trianglesTotal;

    public BufferedRenderer( int texCoordFormat, int colorFormat, int normalFormat) {
        vboIds = BufferUtils.createIntBuffer(1);
        this.texCoordFormat = texCoordFormat;
        this.colorFormat    = colorFormat;
        this.normalFormat   = normalFormat;

        strideSize = 3*4;
        texCoordOffset = strideSize; strideSize += 2*sizeOfFormat(texCoordFormat);
        colorOffset    = strideSize; strideSize += 4*sizeOfFormat(colorFormat);
        normalOffset   = strideSize; strideSize += 3*sizeOfFormat(normalFormat);

        buffers = new VBO[NUMBER_OF_BUFFERS];
        for (int i = 0; i < NUMBER_OF_BUFFERS; i++) {
            buffers[i] = createVBOBuffer();
        }
    }

    public void prepare() {
        trianglesTotal = 0;
    }

    protected ByteBuffer createBuffer() {
        return BufferUtils.createByteBuffer(BUFFER_SIZE);
    }

    protected int genVBOId() {
        GL15.glGenBuffers(vboIds);
        return vboIds.get(0);
    }

    protected VBO createVBOBuffer() {
        return new VBOBuffer2(this);
    }

    public void addVertex(float x, float y, float z, float tx, float ty, int color, float nx, float ny, float nz) {
        getCurrentBuffer().addVertex(
            x * scale[0] + translation[0],
            y * scale[1] + translation[1],
            z * scale[2] + translation[2],
            textureCoords[0] + textureCoords[2]*tx,
            textureCoords[1] + textureCoords[3]*ty,
            color, nx, ny, nz);
    }

    protected VBO getCurrentBuffer() {
        return buffers[bufferIndex];
    }
    public boolean begin() {
        trianglesTotal = 0;
        return true;
    }

    public void onBufferFull() {
        VBO buffer = buffers[bufferIndex];
        if( buffer.getVertices()==0 ) {
            return;
        }
        buffer.upload();
        buffer.render();
        trianglesTotal += buffer.getVertices()/3;
        buffer.clear();
        bufferIndex ++;
        bufferIndex %= NUMBER_OF_BUFFERS;
    }

    public void render() {
        onBufferFull();
    }

    public void ensureSpace(int vertices) {
        VBO buffer = getCurrentBuffer();
        buffer.ensureSpace(vertices);
    }

    private static int sizeOfFormat( int format ) {
        switch( format ) {
            case GL11.GL_BYTE: case GL11.GL_UNSIGNED_BYTE:
                return 1;
            case GL11.GL_FLOAT:
                return 4;
            case GL11.GL_SHORT:
                return 2;
        }
        return 0;
    }

    public void setupPointers() {
        GL11.glEnableClientState( GL11.GL_VERTEX_ARRAY );
        GL11.glVertexPointer( 3, GL11.GL_FLOAT, strideSize, 0);

        if( texCoordFormat!=0 ) {
            GL11.glEnableClientState( GL11.GL_TEXTURE_COORD_ARRAY );
            GL11.glTexCoordPointer( 2, texCoordFormat, strideSize, texCoordOffset );
        }

        if( colorFormat!=0 ) {
            GL11.glEnableClientState( GL11.GL_COLOR_ARRAY );
            GL11.glColorPointer( 4, colorFormat, strideSize, colorOffset );
        }

        if( normalFormat!=0 ) {
            GL11.glEnableClientState( GL11.GL_NORMAL_ARRAY );
            GL11.glNormalPointer( normalFormat, strideSize, normalOffset );
        }
    }

    public int getTrianglesTotal() {
        return trianglesTotal;
    }

    public int getStrideSize() {
        return strideSize;
    }
    public void setTranslation(float x, float y, float z) {
        translation[0] = x;
        translation[1] = y;
        translation[2] = z;
    }

    public void setScale(float x, float y, float z) {
        scale[0] = x;
        scale[1] = y;
        scale[2] = z;
    }

    public void addVertex(Vertex vertex) {
        addVertex(
            vertex.getX(), vertex.getY(), vertex.getZ(),
            vertex.getTx(), vertex.getTy(),
            vertex.getColor(),
            vertex.getNx(), vertex.getNy(), vertex.getNz()
        );
    }

    public void addVertex(float[] position, float[] texCoord, int color, float[] normal) {
        addVertex(position[0], position[1], position[2], texCoord[0], texCoord[1], color, normal[0], normal[1], normal[2]);
    }

    public ArrayList<String> getDebugInfos() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(String.format("Triangles: %d", trianglesTotal));
        result.add( String.format("Mem: %d/%d kB",
            (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024, Runtime.getRuntime().totalMemory()/1024
            ));
        return result;
    }

    public int getTexCoordFormat() {
        return texCoordFormat;
    }

    public int getColorFormat() {
        return colorFormat;
    }

    public int getNormalFormat() {
        return normalFormat;
    }

    public void setTextureCoords(float tx, float ty) {
        textureCoords[0] = tx;
        textureCoords[1] = ty;
    }
}

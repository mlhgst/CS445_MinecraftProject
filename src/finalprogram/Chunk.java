package finalprogram;
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
//
public class Chunk {
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX, StartY, StartZ;    
    private Random r;
    //for texture mapping
    private int VBOTextureHandle;
    private Texture texture;
    
    public void render(){
        glPushMatrix();
            glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
            glColorPointer(3,GL_FLOAT, 0, 0L);
            //texture mapping
            glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
            glBindTexture(GL_TEXTURE_2D, 1);
            glTexCoordPointer(2,GL_FLOAT,0,0L);
            glDrawArrays(GL_QUADS, 0, CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    public void rebuildMesh(float startX, float startY, float startZ) {
        //SimplexNoise noise = new SimplexNoise(20, 0.5, 100);        
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE *CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE* CHUNK_SIZE *CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);
        
        for (float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                //int i=(int)(startX+x*((CHUNK_SIZE - startX)/xResolution));
                //int j=(int)(startY+*((CHUNK_SIZE - startX)/xResolution));
                //int k=(int)(startY+*((CHUNK_SIZE - startX)/xResolution));
                //float height = (startY + (int)(100*noise.getNoise(i,j,k))*CUBE_LENGTH);
                for(float y = 0; y < CHUNK_SIZE; y++){                                       
                    VertexPositionData.put(createCube((float) (startX + x* CUBE_LENGTH),(float)(y*CUBE_LENGTH+
                        (int)(CHUNK_SIZE*.8)),(float) (startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z])));
                    VertexTextureData.put(createTexCube((float) 0, (float) 0,Blocks[(int)(x)][(int) (y)][(int) (z)]));
                }
            }
        }
        
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

    }
    
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f/16)/1024f;
        switch (block.GetID()) {
            //grass
            case 0:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) (row 9, column 2)
                x + offset*3, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*3, y + offset*9,
                // TOP! (row 0, column 2)
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD (row 0, column 3)
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1, 
                x + offset*3, y + offset*1,
                // BACK QUAD (same as front, left, right but inverted)
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                // LEFT QUAD (row 0, column 3)
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // RIGHT QUAD (row 0, column 3)
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1};
            //sand
            case 1:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) (row 9, column 2)
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // TOP! (row 0, column 2)                
                x + offset*1, y + offset*14,
                x + offset*0, y + offset*14,
                x + offset*0, y + offset*13,
                x + offset*1, y + offset*13,
                // FRONT QUAD (row 0, column 3)
                x + offset*15, y + offset*10,
                x + offset*16, y + offset*10,
                x + offset*16, y + offset*11, 
                x + offset*15, y + offset*11,
                // BACK QUAD (same as front, left, right but inverted)
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                // LEFT QUAD (row 0, column 3)
                x + offset*15, y + offset*10,
                x + offset*16, y + offset*10,
                x + offset*16, y + offset*11, 
                x + offset*15, y + offset*11,
                // RIGHT QUAD (row 0, column 3)
                x + offset*15, y + offset*10,
                x + offset*16, y + offset*10,
                x + offset*16, y + offset*11, 
                x + offset*15, y + offset*11};
            //water
            case 2:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) (row 9, column 2)
                x + offset*3, y + offset*12,
                x + offset*2, y + offset*12,
                x + offset*2, y + offset*11,
                x + offset*3, y + offset*11,
                // TOP! (row 0, column 2)                                               
                x + offset*2, y + offset*14,
                x + offset*1, y + offset*14,
                x + offset*1, y + offset*13,
                x + offset*2, y + offset*13,
                // FRONT QUAD (row 11, column 2)
                x + offset*1, y + offset*11,
                x + offset*2, y + offset*11,
                x + offset*2, y + offset*12,
                x + offset*1, y + offset*12,
                // BACK QUAD (same as front, left, right but inverted)                
                x + offset*2, y + offset*12,
                x + offset*1, y + offset*12,              
                x + offset*1, y + offset*11,
                x + offset*2, y + offset*11,
                // LEFT QUAD (row 11, column 2)
                x + offset*1, y + offset*11,
                x + offset*2, y + offset*11,
                x + offset*2, y + offset*12,
                x + offset*1, y + offset*12,
                // RIGHT QUAD (row 11, column 2)
                x + offset*1, y + offset*11,
                x + offset*2, y + offset*11,
                x + offset*2, y + offset*12,
                x + offset*1, y + offset*12}; 
            //dirt
            case 3:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) (row 9, column 2)
                x + offset*9, y + offset*7,
                x + offset*8, y + offset*7,
                x + offset*8, y + offset*6,
                x + offset*9, y + offset*6,
                // TOP! (row 0, column 2)                                                               
                x + offset*5, y + offset*8,
                x + offset*4, y + offset*8,
                x + offset*4, y + offset*7,
                x + offset*5, y + offset*7,
                // FRONT QUAD (row 11, column 2)
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*11,
                x + offset*1, y + offset*11,
                // BACK QUAD (same as front, left, right but inverted)                
                x + offset*2, y + offset*11,
                x + offset*1, y + offset*11,                
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,                
                // LEFT QUAD (row 11, column 2)
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*11,
                x + offset*1, y + offset*11,
                // RIGHT QUAD (row 11, column 2)
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*11,
                x + offset*1, y + offset*11}; 
            //stone    
            case 4:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) (row 9, column 2)
                x + offset*6, y + offset*7,
                x + offset*5, y + offset*7,
                x + offset*5, y + offset*6,
                x + offset*6, y + offset*6,                
                // TOP! (row 0, column 2)  
                x + offset*7, y + offset*4,
                x + offset*6, y + offset*4,
                x + offset*6, y + offset*3,
                x + offset*7, y + offset*3,
                // FRONT QUAD (row 11, column 2)
                x + offset*4, y + offset*6,
                x + offset*5, y + offset*6,
                x + offset*5, y + offset*7,
                x + offset*4, y + offset*7,
                // BACK QUAD (same as front, left, right but inverted)                                
                x + offset*5, y + offset*7,
                x + offset*4, y + offset*7,
                x + offset*4, y + offset*6,
                x + offset*5, y + offset*6,
                // LEFT QUAD (row 11, column 2)
                x + offset*4, y + offset*6,
                x + offset*5, y + offset*6,
                x + offset*5, y + offset*7,
                x + offset*4, y + offset*7,
                // RIGHT QUAD (row 11, column 2)
                x + offset*4, y + offset*6,
                x + offset*5, y + offset*6,
                x + offset*5, y + offset*7,
                x + offset*4, y + offset*7};
            //bedrock
            case 5:
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) (row 9, column 2)
                x + offset*5, y + offset*3,
                x + offset*4, y + offset*3,
                x + offset*4, y + offset*2,
                x + offset*5, y + offset*2,
                // TOP! (row 0, column 2)                                                               
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // FRONT QUAD (row 11, column 2)
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                // BACK QUAD (same as front, left, right but inverted)                
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,                
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,                               
                // LEFT QUAD (row 11, column 2)
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                // RIGHT QUAD (row 11, column 2)
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2};
            //default
            default:    
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) (row 9, column 2)
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                // TOP! (row 0, column 2)                                               
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                // FRONT QUAD (row 11, column 2)
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                // BACK QUAD (same as front, left, right but inverted)                                
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                // LEFT QUAD (row 11, column 2)
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                // RIGHT QUAD (row 11, column 2)
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3};
        }
    }
    
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i %
            CubeColorArray.length];
        }
        return cubeColors;
    }
    
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        
        return new float[] {
        // TOP QUAD
        x + offset, y + offset, z,
        x - offset, y + offset, z,
        x - offset, y + offset, z - CUBE_LENGTH,
        x + offset, y + offset, z - CUBE_LENGTH,
        // BOTTOM QUAD
        x + offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z,
        x + offset, y - offset, z,
        // FRONT QUAD
        x + offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        // BACK QUAD
        x + offset, y - offset, z,
        x - offset, y - offset, z,
        x - offset, y + offset, z,
        x + offset, y + offset, z,
        // LEFT QUAD
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z,
        x - offset, y - offset, z,
        x - offset, y - offset, z - CUBE_LENGTH,
        // RIGHT QUAD
        x + offset, y + offset, z,
        x + offset, y + offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z 
        };
    }
    
    private float[] getCubeColor(Block block) {
//        switch (block.GetID()) {
//            case 1:
//                return new float[] { 0, 1, 0 };
//            case 2:
//                return new float[] { 1, 0.5f, 0 };
//            case 3:
//                return new float[] { 0, 0f, 1f };
//        }
        return new float[] { 1, 1, 1 };
    }
    
    public Chunk(int startX, int startY, int startZ) {
        try{
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        }
        catch(Exception e)
        {
            System.out.print("ER-ROAR!");
        }        
        
        r= new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    if(r.nextFloat() > 0.85f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    }else if(r.nextFloat()>0.75f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                    }else if(r.nextFloat()>0.6f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                    }else if(r.nextFloat()>0.45f){ 
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    }else if(r.nextFloat()>0.3f){ 
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                    }else if(r.nextFloat()>0.15f){ 
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    }else{
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Default);
                    }
                }
            }
        }
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();         
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
}

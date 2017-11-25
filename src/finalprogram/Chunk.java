/***************************************************************
* file: Chunk.java
* author: Vincent Zhu
* class: CS 445 – Computer Graphics
*
* assignment: final program
* date last modified: 11/16/2017
*
* purpose: this class allows the creation of Chunk objects, large consolidations
* of Blocks in 30x30x30 size in 3D space. In the final project, a chunk will be
* rendered with noise generation, lighting, and at each Chunk location 
* certain block types can only appear, depending on the relative height of terrain.
*
****************************************************************/ 
package finalprogram;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

//Chunk class is a congregation of Blocks in 30x30x30 units in 3D space
public class Chunk {
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    static final int NUM_TERRAIN_LVLS = 3;  //blocktype appearances at different levels
    static final int WATER_LVL = NUM_TERRAIN_LVLS;
    static final int SAND_LVL = NUM_TERRAIN_LVLS+1;
    
    protected Block[][][] Blocks;
    protected int VBOVertexHandle;
    protected int VBOColorHandle;
    protected int VBOTextureHandle;
    private int StartX, StartY, StartZ;    
    private Random r;    
    private Texture texture;
    
    //parameterized Chunk constructor
    public Chunk(int startX, int startY, int startZ, int worldType) {
        try{
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        }
        catch(Exception e)
        {
            System.out.print("ER-ROAR!");
            e.printStackTrace();
        }        
        
        r = new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
                
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();         
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        if(worldType == 0)
            rebuildMesh(startX, startY, startZ);
        else
            rebuildMeshOther(startX, startY, startZ);
    }
    
    //method renders the Chunk in OpenGL, using VBOs for vertices, colors and textures
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
    
    //method rebuilds the Chunk in 3D space, depending on where the camera is 
    public void rebuildMesh(float startX, float startY, float startZ) {
        //glClearColor(0.9f, 1.0f, 1.5f, 0.0f);
        SimplexNoise noise = new SimplexNoise(100, 0.3, (int)System.nanoTime());
        float maxY;
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE *CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE* CHUNK_SIZE *CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);
        
        //creates nxnxn cube based on the CHUNK_SIZE
        //includes noise generation so terrain elevation changes appear reasonable
        for (float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                
                float noiseVal = (float)noise.getNoise((int)x, (int)z);
                maxY = (Math.abs(noiseVal)*CHUNK_SIZE)+NUM_TERRAIN_LVLS;  // Needs to have a height of at least 3 (3 levels of terrain)
                                                    
                for(float y = 0; y <= maxY; y++){                     
                    // Set block type based on relative height 
                    if(y > maxY-1 ){ // TOP LEVEL - water,sand, grass
                        if(y <= WATER_LVL){
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Water);
                        }else if(y <= SAND_LVL && y > WATER_LVL){
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Sand);
                        }else{
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Grass);
                        }
                    }else if(y <= maxY-1 && y > maxY-NUM_TERRAIN_LVLS){// MIDDLE LAYER - dirt, stone
                        if(y >= maxY-(NUM_TERRAIN_LVLS-1))
                            Blocks[(int)x][(int)y][(int)z]  = new Block(Block.BlockType.BlockType_Dirt);
                        else
                            Blocks[(int)x][(int)y][(int)z]  = new Block(Block.BlockType.BlockType_Stone);
                    }else if(y <= maxY-NUM_TERRAIN_LVLS){ // BOTTOM LAYER - bedrock
                        Blocks[(int)x][(int)y][(int)z]  = new Block(Block.BlockType.BlockType_Bedrock);
                    }
                    VertexPositionData.put(createCube((float) (startX + x* CUBE_LENGTH),(float)(y*CUBE_LENGTH+
                        (int)(CHUNK_SIZE*.8)),(float) (startZ + z * CUBE_LENGTH)));                    
                    VertexColorData.put(createCubeVertexCol(getCubeColor()));
                    VertexTextureData.put(createTexCube((float)0, (float)0, Blocks[(int)(x)][(int)(y)][(int)(z)]));
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
    
    //method rebuilds the Chunk in 3D space, depending on where the camera is 
    public void rebuildMeshOther(float startX, float startY, float startZ) {
        //glClearColor(0.63f, 0.0f, 0.0f, 0.0f);
        SimplexNoise noise = new SimplexNoise(100, 0.3, (int)System.nanoTime());
        float maxY;
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE *CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE* CHUNK_SIZE *CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);
        
        //creates nxnxn cube based on the CHUNK_SIZE
        //includes noise generation so terrain elevation changes appear reasonable
        for (float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                
                float noiseVal = (float)noise.getNoise((int)x, (int)z);
                maxY = (Math.abs(noiseVal)*CHUNK_SIZE)+NUM_TERRAIN_LVLS;  // Needs to have a height of at least 3 (3 levels of terrain)
                                                    
                for(float y = 0; y <= maxY; y++){                     
                    // Set block type based on relative height 
                    if(y > maxY-1 ){ // TOP LEVEL - water,sand, grass
                        if(y <= WATER_LVL){
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Pumpkin);
                        }else if(y <= SAND_LVL && y > WATER_LVL){
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_SpiderWeb);
                        }else{
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_DeadTree1);
                        }
                    }else if(y <= maxY-1 && y > maxY-NUM_TERRAIN_LVLS){// MIDDLE LAYER - dirt, stone
                        if(y >= maxY-(NUM_TERRAIN_LVLS-1))
                            Blocks[(int)x][(int)y][(int)z]  = new Block(Block.BlockType.BlockType_Lava1);
                        else
                            Blocks[(int)x][(int)y][(int)z]  = new Block(Block.BlockType.BlockType_Lava2);
                    }else if(y <= maxY-NUM_TERRAIN_LVLS){ // BOTTOM LAYER - bedrock
                        Blocks[(int)x][(int)y][(int)z]  = new Block(Block.BlockType.BlockType_MoltenRock);
                    }
                    VertexPositionData.put(createCube((float) (startX + x* CUBE_LENGTH),(float)(y*CUBE_LENGTH+
                        (int)(CHUNK_SIZE*.8)),(float) (startZ + z * CUBE_LENGTH)));                    
                    VertexColorData.put(createCubeVertexCol(getCubeColor()));
                    VertexTextureData.put(createTexCubeOther((float)0, (float)0, Blocks[(int)(x)][(int)(y)][(int)(z)]));
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
    
    //method defines the texture of each side of a Block depending on blockType
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f/16)/1024f; //offset based on the pixel size of input file

        switch (block.GetID()) {        
            case 0: //grass
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) 
                x + offset*3, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*3, y + offset*9,
                // TOP QUAD
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1, 
                x + offset*3, y + offset*1,
                // BACK QUAD 
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                // LEFT QUAD 
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // RIGHT QUAD 
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1};            
            case 1: //sand
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) 
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                // TOP QUAD
                x + offset*1, y + offset*14,
                x + offset*0, y + offset*14,
                x + offset*0, y + offset*13,
                x + offset*1, y + offset*13,
                // FRONT QUAD
                x + offset*15, y + offset*10,
                x + offset*16, y + offset*10,
                x + offset*16, y + offset*11, 
                x + offset*15, y + offset*11,
                // BACK QUAD 
                x + offset*16, y + offset*11, 
                x + offset*15, y + offset*11,
                x + offset*15, y + offset*10,
                x + offset*16, y + offset*10,
                // LEFT QUAD 
                x + offset*15, y + offset*10,
                x + offset*16, y + offset*10,
                x + offset*16, y + offset*11, 
                x + offset*15, y + offset*11,
                // RIGHT QUAD 
                x + offset*15, y + offset*10,
                x + offset*16, y + offset*10,
                x + offset*16, y + offset*11, 
                x + offset*15, y + offset*11};            
            case 2: //water
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) 
                x + offset*3, y + offset*12,
                x + offset*2, y + offset*12,
                x + offset*2, y + offset*11,
                x + offset*3, y + offset*11,
                // TOP QUAD
                x + offset*2, y + offset*14,
                x + offset*1, y + offset*14,
                x + offset*1, y + offset*13,
                x + offset*2, y + offset*13,
                // FRONT QUAD
                x + offset*1, y + offset*11,
                x + offset*2, y + offset*11,
                x + offset*2, y + offset*12,
                x + offset*1, y + offset*12,
                // BACK QUAD 
                x + offset*2, y + offset*12,
                x + offset*1, y + offset*12,              
                x + offset*1, y + offset*11,
                x + offset*2, y + offset*11,
                // LEFT QUAD 
                x + offset*1, y + offset*11,
                x + offset*2, y + offset*11,
                x + offset*2, y + offset*12,
                x + offset*1, y + offset*12,
                // RIGHT QUAD 
                x + offset*1, y + offset*11,
                x + offset*2, y + offset*11,
                x + offset*2, y + offset*12,
                x + offset*1, y + offset*12};             
            case 3: //dirt
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) 
                x + offset*9, y + offset*7,
                x + offset*8, y + offset*7,
                x + offset*8, y + offset*6,
                x + offset*9, y + offset*6,
                // TOP QUAD
                x + offset*5, y + offset*8,
                x + offset*4, y + offset*8,
                x + offset*4, y + offset*7,
                x + offset*5, y + offset*7,
                // FRONT QUAD
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*11,
                x + offset*1, y + offset*11,
                // BACK QUAD 
                x + offset*2, y + offset*11,
                x + offset*1, y + offset*11,                
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,                
                // LEFT QUAD 
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*11,
                x + offset*1, y + offset*11,
                // RIGHT QUAD 
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*11,
                x + offset*1, y + offset*11};                 
            case 4: //stone
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) 
                x + offset*6, y + offset*7,
                x + offset*5, y + offset*7,
                x + offset*5, y + offset*6,
                x + offset*6, y + offset*6,                
                // TOP QUAD
                x + offset*7, y + offset*4,
                x + offset*6, y + offset*4,
                x + offset*6, y + offset*3,
                x + offset*7, y + offset*3,
                // FRONT QUAD
                x + offset*4, y + offset*6,
                x + offset*5, y + offset*6,
                x + offset*5, y + offset*7,
                x + offset*4, y + offset*7,
                // BACK QUAD 
                x + offset*5, y + offset*7,
                x + offset*4, y + offset*7,
                x + offset*4, y + offset*6,
                x + offset*5, y + offset*6,
                // LEFT QUAD 
                x + offset*4, y + offset*6,
                x + offset*5, y + offset*6,
                x + offset*5, y + offset*7,
                x + offset*4, y + offset*7,
                // RIGHT QUAD 
                x + offset*4, y + offset*6,
                x + offset*5, y + offset*6,
                x + offset*5, y + offset*7,
                x + offset*4, y + offset*7};            
            case 5: //bedrock
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) 
                x + offset*5, y + offset*3,
                x + offset*4, y + offset*3,
                x + offset*4, y + offset*2,
                x + offset*5, y + offset*2,
                // TOP QUAD
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // FRONT QUAD 
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                // BACK QUAD 
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,                
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,                               
                // LEFT QUAD 
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                // RIGHT QUAD
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2};      
            case 6: //bedrock
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) 
                x + offset*5, y + offset*3,
                x + offset*4, y + offset*3,
                x + offset*4, y + offset*2,
                x + offset*5, y + offset*2,
                // TOP QUAD
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                // FRONT QUAD 
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                // BACK QUAD 
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,                
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,                               
                // LEFT QUAD 
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                // RIGHT QUAD
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2};  
           default: //default
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                // TOP QUAD
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                // FRONT QUAD
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                // BACK QUAD                                 
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                // LEFT QUAD 
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                // RIGHT QUAD
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3};
        }
    }
    
    //method defines the texture of each side of a Block depending on blockType
    public static float[] createTexCubeOther(float x, float y, Block block) {
        float offset = (1024f/16)/1024f; //offset based on the pixel size of input file
        System.out.println(block.GetID());
        switch (block.GetID()) {            
            case 7: //pumpkin
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) 
                x + offset*7, y + offset*7,
                x + offset*6, y + offset*7,
                x + offset*6, y + offset*6,
                x + offset*7, y + offset*6,
                // TOP QUAD                
                x + offset*7, y + offset*8,
                x + offset*6, y + offset*8,
                x + offset*6, y + offset*7,
                x + offset*7, y + offset*7,
                // FRONT QUAD
                x + offset*8, y + offset*7, 
                x + offset*9, y + offset*7,
                x + offset*9, y + offset*8,
                x + offset*8, y + offset*8,                
                // BACK QUAD 
                x + offset*9, y + offset*8,
                x + offset*8, y + offset*8, 
                x + offset*8, y + offset*7, 
                x + offset*9, y + offset*7,                               
                // LEFT QUAD 
                x + offset*7, y + offset*7,
                x + offset*8, y + offset*7,
                x + offset*8, y + offset*8,
                x + offset*7, y + offset*8,
                // RIGHT QUAD 
                x + offset*7, y + offset*7,
                x + offset*8, y + offset*7,
                x + offset*8, y + offset*8,
                x + offset*7, y + offset*8};            
            case 8: //lava
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) 
                x + offset*14, y + offset*15,
                x + offset*13, y + offset*15,
                x + offset*13, y + offset*14,
                x + offset*14, y + offset*14,
                // TOP QUAD
                x + offset*14, y + offset*15,
                x + offset*13, y + offset*15,
                x + offset*13, y + offset*14,
                x + offset*14, y + offset*14,
                // FRONT QUAD
                x + offset*13, y + offset*14,
                x + offset*14, y + offset*14,
                x + offset*14, y + offset*15, 
                x + offset*13, y + offset*15,
                // BACK QUAD 
                x + offset*14, y + offset*15, 
                x + offset*13, y + offset*15,
                x + offset*13, y + offset*14,
                x + offset*14, y + offset*14,                
                // LEFT QUAD 
                x + offset*13, y + offset*14,
                x + offset*14, y + offset*14,
                x + offset*14, y + offset*15, 
                x + offset*13, y + offset*15,
                // RIGHT QUAD 
                x + offset*13, y + offset*14,
                x + offset*14, y + offset*14,
                x + offset*14, y + offset*15, 
                x + offset*13, y + offset*15};            
            case 9: //molten rock
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) 
                x + offset*8, y + offset*7,
                x + offset*7, y + offset*7,
                x + offset*7, y + offset*6,
                x + offset*8, y + offset*6,
                // TOP QUAD
                x + offset*8, y + offset*7,
                x + offset*7, y + offset*7,
                x + offset*7, y + offset*6,
                x + offset*8, y + offset*6,
                // FRONT QUAD
                x + offset*7, y + offset*6,
                x + offset*8, y + offset*6,
                x + offset*8, y + offset*7,
                x + offset*7, y + offset*7,
                // BACK QUAD 
                x + offset*8, y + offset*7,
                x + offset*7, y + offset*7,                
                x + offset*7, y + offset*6,
                x + offset*8, y + offset*6,                
                // LEFT QUAD 
                x + offset*7, y + offset*6,
                x + offset*8, y + offset*6,
                x + offset*8, y + offset*7,
                x + offset*7, y + offset*7,
                // RIGHT QUAD 
                x + offset*7, y + offset*6,
                x + offset*8, y + offset*6,
                x + offset*8, y + offset*7,
                x + offset*7, y + offset*7};             
            case 10: //lava2
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) 
                x + offset*3, y + offset*9,
                x + offset*2, y + offset*9,
                x + offset*2, y + offset*8,
                x + offset*3, y + offset*8,
                // TOP QUAD                
                x + offset*2, y + offset*9,
                x + offset*1, y + offset*9,
                x + offset*1, y + offset*8,
                x + offset*2, y + offset*8,
                // FRONT QUAD
                x + offset*1, y + offset*8,
                x + offset*2, y + offset*8,
                x + offset*2, y + offset*9,
                x + offset*1, y + offset*9,
                // BACK QUAD 
                x + offset*2, y + offset*9,
                x + offset*1, y + offset*9,
                x + offset*1, y + offset*8,
                x + offset*2, y + offset*8,                
                // LEFT QUAD 
                x + offset*1, y + offset*8,
                x + offset*2, y + offset*8,
                x + offset*2, y + offset*9,
                x + offset*1, y + offset*9,
                // RIGHT QUAD 
                x + offset*1, y + offset*8,
                x + offset*2, y + offset*8,
                x + offset*2, y + offset*9,
                x + offset*1, y + offset*9};                 
            case 11: //spider web
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) 
                x + offset*12, y + offset*1,
                x + offset*11, y + offset*1,
                x + offset*11, y + offset*0,
                x + offset*12, y + offset*0,                
                // TOP QUAD
                x + offset*12, y + offset*1,
                x + offset*11, y + offset*1,
                x + offset*11, y + offset*0,
                x + offset*12, y + offset*0,
                // FRONT QUAD
                x + offset*11, y + offset*0,
                x + offset*12, y + offset*0,
                x + offset*12, y + offset*1,
                x + offset*11, y + offset*1,
                // BACK QUAD 
                x + offset*11, y + offset*0,
                x + offset*12, y + offset*0,
                x + offset*12, y + offset*1,
                x + offset*11, y + offset*1,
                // LEFT QUAD 
                x + offset*11, y + offset*0,
                x + offset*12, y + offset*0,
                x + offset*12, y + offset*1,
                x + offset*11, y + offset*1,
                // RIGHT QUAD 
                x + offset*11, y + offset*0,
                x + offset*12, y + offset*0,
                x + offset*12, y + offset*1,
                x + offset*11, y + offset*1};            
            case 12: //bedrock
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y) 
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                // TOP QUAD
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                // FRONT QUAD 
                x + offset*15, y + offset*7,
                x + offset*16, y + offset*7,
                x + offset*16, y + offset*8,
                x + offset*15, y + offset*8,
                // BACK QUAD 
                x + offset*15, y + offset*7,
                x + offset*16, y + offset*7,
                x + offset*16, y + offset*8,
                x + offset*15, y + offset*8,
                // LEFT QUAD 
                x + offset*15, y + offset*8,
                x + offset*16, y + offset*8,
                x + offset*16, y + offset*9,
                x + offset*15, y + offset*9,
                // RIGHT QUAD
                x + offset*15, y + offset*8,
                x + offset*16, y + offset*8,
                x + offset*16, y + offset*9,
                x + offset*15, y + offset*9};              
            default: //default
                return new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                // TOP QUAD
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                // FRONT QUAD
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                // BACK QUAD                                 
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                // LEFT QUAD 
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3,
                // RIGHT QUAD
                x + offset*0, y + offset*2,
                x + offset*1, y + offset*2,
                x + offset*1, y + offset*3,
                x + offset*0, y + offset*3};
        }
    }
    
    //method determines the color of each side of a Block, but it is determined by textures
    protected float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i %
            CubeColorArray.length];
        }
        return cubeColors;
    }
    
    //method forms a Block in 3D space
    protected static float[] createCube(float x, float y, float z) {
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
        x + offset, y - offset, z};
    }
    
    //not used, since texture is used as opposed to color
    protected float[] getCubeColor() {
        return new float[] { 1, 1, 1 };
    }        
}

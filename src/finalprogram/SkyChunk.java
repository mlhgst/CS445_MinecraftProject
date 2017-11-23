
package finalprogram;

import static finalprogram.Chunk.CHUNK_SIZE;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;


public class SkyChunk extends Chunk {
    
    private static final int NUM_SECTORS_1D = 3;
    private static final int NUM_SECTORS_2D = NUM_SECTORS_1D*NUM_SECTORS_1D;
    private static final int SECTOR_SIZE = CHUNK_SIZE/NUM_SECTORS_1D;
    private static final int MAX_SIDES = 6;
    
    public SkyChunk(int startX, int startY, int startZ) {
        super(startX, startY, startZ);
    }
    
    @Override
    //method rebuilds the Chunk in 3D space, depending on where the camera is 
    public void rebuildMesh(float startX, float startY, float startZ) {
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE *CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE* CHUNK_SIZE *CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);
       
        SimplexNoise noise = new SimplexNoise(100, 0.3, (int)System.nanoTime());
        float maxY;
        for (float x = 0; x < CHUNK_SIZE; x += 1) {
           
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                 
                float noiseVal = (float)noise.getNoise((int)x, (int)z);
                maxY = Math.abs(noiseVal)*10;  
                
                for(float y = 0; y <= maxY; y++){                                    
                    if(y >= 1){    
                        Blocks[(int)x][(int)y][(int)z]  = new Block(Block.BlockType.BlockType_Water);
                        VertexPositionData.put(createCube((float) (startX + x* CUBE_LENGTH),(float)(startY + y* CUBE_LENGTH),(float) (startZ + z * CUBE_LENGTH)));        
                        VertexColorData.put(createCubeVertexCol(getCubeColor()));
                        VertexTextureData.put(createTexCube((float)0, (float)0, Blocks[(int)(x)][(int)(y)][(int)(z)]));
                    }
                }
            }
        }
        
        // Process sectors in sky
        /*for(int sector = 1; sector <= NUM_SECTORS_2D; sector++){
        
            // Should we place a cloud in this sector? - 80% chance of cloud
            if(Math.random() <= 0.8){
                
                // Generate a new random shape (1-6 sided shape)
                int numVertices = (int)(Math.random()*MAX_SIDES)+1;
                RandomShape r = new RandomShape(numVertices);
                
                switch(sector){
                    case(1): r.generateVertices(SECTOR_SIZE,0,0);
                        break;
                    case(2): r.generateVertices(SECTOR_SIZE,10,0);
                        break;
                    case(3): r.generateVertices(SECTOR_SIZE,20,0);
                        break;
                    case(4): r.generateVertices(SECTOR_SIZE,0,10);
                        break;
                    case(5): r.generateVertices(SECTOR_SIZE,10,10);
                        break;
                    case(6): r.generateVertices(SECTOR_SIZE,20,10);
                        break;
                    case(7): r.generateVertices(SECTOR_SIZE,0,20);
                        break;
                    case(8): r.generateVertices(SECTOR_SIZE,10,20);
                        break;
                    case(9): r.generateVertices(SECTOR_SIZE,20,20);
                        break;
                }
                
                int [][] fillAr = r.fill();
                // TEST
                System.out.println("Cloud in sector: " + sector);
                r.printVertices();
                for(int i = 0; i < fillAr.length; i++){
                    int x = fillAr[i][0];
                    int z = fillAr[i][1];
                    for (float y = (float)(CHUNK_SIZE*0.75); y < (float)(CHUNK_SIZE*0.75)+1; y += 1) {
                        Blocks[x][(int)y][z] = new Block(Block.BlockType.BlockType_Water);

                        VertexPositionData.put(createCube((float) (startX + x* CUBE_LENGTH),(float)(y*CUBE_LENGTH+
                            (int)(CHUNK_SIZE*.8)),(float) (startZ + z * CUBE_LENGTH)));                    
                        VertexColorData.put(createCubeVertexCol(getCubeColor()));
                        VertexTextureData.put(createTexCube((float)0, (float)0, Blocks[(int)(x)][(int)(y)][(int)(z)]));
                    }
                }
            }
        }*/
        
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
}
        
/***************************************************************
* file: FinalProgram.java
* author: Vincent Zhu
* class: CS 445 – Computer Graphics
*
* assignment: final program
* date last modified: 11/16/2017
*
* purpose: main class file responsible for initializing the objects
* for the final project. The final project will use openGL to create
* a 3D minecraft-esque world with various terrain based on elevation.
* Based on the height of each location in the "Chunk" the unit that 
* forms the world, individual "Block"s in the Chunk will appear differently.
* Using noise generation, the elevation will not contain areas that have
* sudden peaks and valleys, this way, the world appears more realistic.
* This revision includes a fixed light source.
*
****************************************************************/
package finalprogram;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

//main FinalProgram class
public class FinalProgram {
    private FPCameraController fp = new FPCameraController(0f,0f,0f);
    private DisplayMode displayMode;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    private Texture texture;

    //sets up an OpenGL window, initializes the camera and renders a 3D chunk
    private void start(){
        draw();
        try{
            createWindow();
            initGL();
            fp.gameLoop();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //attempts to create an openGL window a size of 640x480
    private void createWindow()throws Exception{
        Display.setFullscreen(false); //do not want our window to take up full screen        
        DisplayMode d[] = Display.getAvailableDisplayModes();
        //selects appropriate displayMode based on available modes in d[]
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("glHF();");
        Display.create();
    }
    
    //initializes openGL parameters for the openGL window
    private void initGL(){
        glClearColor(0f, 0f, 0f, 0f);
        //glClearColor(0.9f, 1.0f, 1.5f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();        
        //sets up perspective projection matrix
        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);     
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);        
        //for texture mapping
        glEnable(GL_TEXTURE_2D);
        glEnableClientState (GL_TEXTURE_COORD_ARRAY);
        //for lighting
        glEnable(GL_LIGHTING);//enables our lighting
        glEnable(GL_LIGHT0);//enables light0                        
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); //sets our light’s position
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);//sets our specular light
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);//sets our diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);//sets our ambient light        
    }
    
    private void initLightArrays() {
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }
    
    private void draw(){
//        try{
//            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("sky.png"));
//            glColor3f(255, 255, 255);
//            glBegin(GL_QUADS);
//            {
//                glTexCoord2d(0.0, 0.0);
//                glVertex2d(0, 0);
//
//                glTexCoord2d(1.0, 0.0);
//                glVertex2d(1024, 0);
//
//                glTexCoord2d(1.0, 1.0);
//                glVertex2d(1024, 1024);
//
//                glTexCoord2d(0.0, 1.0);
//                glVertex2d(0, 1024);
//            }
//            glEnd();
//        }
//        catch(Exception e){
//            
//        }
    }       
           
    //main function
    public static void main(String[] args) {    
        FinalProgram fp = new FinalProgram();
        fp.start();
    }             
}

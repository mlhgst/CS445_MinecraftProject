/***************************************************************
* file: FinalProgram.java
* author: Vincent Zhu
* class: CS 445 – Computer Graphics
*
* assignment: final program
* date last modified: 11/8/2017
*
* purpose: main class file responsible for initializing
* an openGL window and the first person camera
*
****************************************************************/
package finalprogram;
//
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

//main FinalProgram class
public class FinalProgram {
    private FPCameraController fp = new FPCameraController(0f,0f,0f);
    private DisplayMode displayMode;

    //sets up an OpenGL window, initializes the camera and renders the 3d cube
    private void start(){
        try{
            createWindow();
            initGL();
            fp.gameLoop();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //attempts to create an openGL window with a size of 640x480
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
        glClearColor(0.9f, 1.0f, 1.5f, 0.0f);
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
    }

    
    private void initLightArrays() {
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        FloatBuffer whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }
           
    //main function
    public static void main(String[] args) {    
        FinalProgram fp = new FinalProgram();
        fp.start();
    }    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalprogram;

import java.util.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.glu.GLU;

public class FinalProgram {
    private FPCameraController fp = new FPCameraController(0f,0f,0f);
    private DisplayMode displayMode;

    private void start(){
        try{
            createWindow();
            initGL();
            fp.gameLoop();
            //render();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
     //attempts to create an openGL window with a size of 640x480
    private void createWindow()throws Exception{
        Display.setFullscreen(false); //do not want our window to take up full screen        
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("OpenGL Window of Size 640x480");
        Display.create();
    }
    
    //initializes openGL parameters for the openGL window, size: 640x480
    private void initGL(){
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 300.0f);
        //glOrtho(-320, 320, -240, 240, 1, -1); //origin is on center of window
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        
        //added for chunk class
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
    }
    
     private void render(){      
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            try{
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();                                                                                                                                                                                                             
                
                glBegin(GL_QUADS);
                    //Top
                    glColor3f(1.0f, 0.0f, 0.0f);
                    glVertex3f(1.0f, 1.0f, -1.0f);
                    glVertex3f(-1.0f, 1.0f, -1.0f);
                    glVertex3f(-1.0f, 1.0f, 1.0f);
                    glVertex3f(1.0f, 1.0f, 1.0f);
                    //Bottom
                    glColor3f(0.0f, 1.0f, 0.0f);
                    glVertex3f(1.0f, -1.0f, 1.0f);
                    glVertex3f(-1.0f, -1.0f, 1.0f);
                    glVertex3f(-1.0f, -1.0f, -1.0f);
                    glVertex3f(1.0f, -1.0f, -1.0f);
                    //Front
                    glColor3f(0.0f, 0.0f, 1.0f);
                    glVertex3f(1.0f, 1.0f, 1.0f);
                    glVertex3f(-1.0f, 1.0f, 1.0f);
                    glVertex3f(-1.0f, -1.0f, 1.0f);
                    glVertex3f(1.0f, -1.0f, 1.0f);
                    //Back
                    glColor3f(0.5f, 0.0f, 0.0f);
                    glVertex3f(1.0f, -1.0f, 1.0f);
                    glVertex3f(-1.0f, -1.0f, -1.0f);
                    glVertex3f(-1.0f, 1.0f, -1.0f);
                    glVertex3f(1.0f, 1.0f, -1.0f);
                    //Left
                    glColor3f(0.5f, 0.5f, 0.0f);
                    glVertex3f(-1.0f, 1.0f, 1.0f);
                    glVertex3f(-1.0f, 1.0f, -1.0f);
                    glVertex3f(-1.0f, -1.0f, -1.0f);
                    glVertex3f(-1.0f, -1.0f, 1.0f);
                    //Right
                    glColor3f(0.5f, 0.5f, 0.5f);
                    glVertex3f(1.0f, 1.0f, -1.0f);
                    glVertex3f(1.0f, 1.0f, 1.0f);
                    glVertex3f(1.0f, -1.0f, 1.0f);
                    glVertex3f(1.0f, -1.0f, -1.0f);
                glEnd();
                
                glBegin(GL_LINE_LOOP);
                    //Top
                    glColor3f(1.0f, 0.0f, 0.0f);
                    glVertex3f(1.0f, 1.0f, -1.0f);
                    glVertex3f(-1.0f, 1.0f, -1.0f);
                    glVertex3f(-1.0f, 1.0f, 1.0f);
                    glVertex3f(1.0f, 1.0f, 1.0f);
                glEnd();
                glBegin(GL_LINE_LOOP);
                    //Bottom
                    glColor3f(0.0f, 1.0f, 0.0f);
                    glVertex3f(1.0f, -1.0f, 1.0f);
                    glVertex3f(-1.0f, -1.0f, 1.0f);
                    glVertex3f(-1.0f, -1.0f, -1.0f);
                    glVertex3f(1.0f, -1.0f, -1.0f);
                glEnd();
                glBegin(GL_LINE_LOOP);
                    //Front
                    glColor3f(0.0f, 0.0f, 1.0f);
                    glVertex3f(1.0f, 1.0f, 1.0f);
                    glVertex3f(-1.0f, 1.0f, 1.0f);
                    glVertex3f(-1.0f, -1.0f, 1.0f);
                    glVertex3f(1.0f, -1.0f, 1.0f);
                glEnd();
                glBegin(GL_LINE_LOOP);
                    //Back
                    glColor3f(0.5f, 0.0f, 0.0f);
                    glVertex3f(1.0f, -1.0f, 1.0f);
                    glVertex3f(-1.0f, -1.0f, -1.0f);
                    glVertex3f(-1.0f, 1.0f, -1.0f);
                    glVertex3f(1.0f, 1.0f, -1.0f);
                glEnd();
                glBegin(GL_LINE_LOOP);
                    //Left
                    glColor3f(0.5f, 0.5f, 0.0f);
                    glVertex3f(-1.0f, 1.0f, 1.0f);
                    glVertex3f(-1.0f, 1.0f, -1.0f);
                    glVertex3f(-1.0f, -1.0f, -1.0f);
                    glVertex3f(-1.0f, -1.0f, 1.0f);
                glEnd();
                glBegin(GL_LINE_LOOP);
                    //Right
                    glColor3f(0.5f, 0.5f, 0.5f);
                    glVertex3f(1.0f, 1.0f, -1.0f);
                    glVertex3f(1.0f, 1.0f, 1.0f);
                    glVertex3f(1.0f, -1.0f, 1.0f);
                    glVertex3f(1.0f, -1.0f, -1.0f);
                glEnd();
                
                
                Display.update();                   
                Display.sync(60);  
            }catch(Exception e){
                e.printStackTrace();
            }
        }          
        Display.destroy();
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        FinalProgram fp = new FinalProgram();
        fp.start();
    }
    
}

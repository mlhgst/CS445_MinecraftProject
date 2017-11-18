/***************************************************************
* file: FPCameraController.java
* author: Vincent Zhu
* class: CS 445 – Computer Graphics
*
* assignment: final program
* date last modified: 11/16/2017
*
* purpose: this is the first person camera controller class that allows
* the creation of a camera that allows the perspective view of a 3D
* Chunk from any angle. Initial fixed light source is included.
*
****************************************************************/ 
package finalprogram;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

//first person camera controller class
public class FPCameraController {
    //3d vector to store the camera's position initially
    private Vector3Float position = null; //camera position
    private Vector3Float lposition = null; //lighting position    
    
    //the rotation around the Y axis of the camera
    private float yaw = 0.0f;
    //the rotation around the X axis of the camera
    private float pitch = 0.0f;    
    
    //parameterized FPCameraController constructor
    public FPCameraController(float x, float y, float z){
        //instantiate position to x,y,z parameters
        position = new Vector3Float(x, y, z);
        position.x = 18f;
        position.y = -60f;
        position.z = -30f;
        //instantiate lighting position to x,y,z parameters
        lposition = new Vector3Float(x, y, z);
        //still light source
        lposition.x = 20f;
        lposition.y = 0f;
        lposition.z = 50f;               
    }
    
    //increment the camera's current yaw rotation
    public void yaw(float amount){
        //increment the yaw by the amount parameter
        yaw += amount;
    }
    
    //increment the camera's current pitch rotation
    public void pitch(float amount){
        //increment the pitch by the amount parameter
        pitch -= amount;
    }
    
    //moves the camera forward relative to its current rotation (yaw)
    public void walkForward(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
        System.out.println("Camera position! x: " + position.x + " y: " + position.y + " z: " + position.z);

        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lposition.x+=xOffset).put(lposition.y).put(lposition.z-=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        //System.out.println("Light position! x: " + lposition.x + " y: " + lposition.y + " z: " + lposition.z);
    }

    //moves the camera backward relative to its current rotation (yaw)
    public void walkBackwards(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
        System.out.println("Camera position! x: " + position.x + " y: " + position.y + " z: " + position.z);

        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lposition.x-=xOffset).put(lposition.y).put(lposition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        //System.out.println("Light position! x: " + lposition.x + " y: " + lposition.y + " z: " + lposition.z);
    }
    
    //strafes the camera left relative to its current rotation (yaw)
    public void strafeLeft(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw-90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw-90));
        position.x -= xOffset;
        position.z += zOffset;
        System.out.println("Camera position! x: " + position.x + " y: " + position.y + " z: " + position.z);
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lposition.x+=xOffset).put(lposition.y).put(lposition.z-=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        //System.out.println("Light position! x: " + lposition.x + " y: " + lposition.y + " z: " + lposition.z);
    }
    
    //strafes the camera right relative to its current rotation (yaw)
    public void strafeRight(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw+90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw+90));
        position.x -= xOffset;
        position.z += zOffset;
        System.out.println("Camera position! x: " + position.x + " y: " + position.y + " z: " + position.z);
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lposition.x+=xOffset).put(lposition.y).put(lposition.z-=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        //System.out.println("Light position! x: " + lposition.x + " y: " + lposition.y + " z: " + lposition.z);
    }
    
    //moves the camera up
    public void moveUp(float distance)
    {
        position.y -= distance;
        //System.out.println("Camera position! x: " + position.x + " y: " + position.y + " z: " + position.z);
        //System.out.println("Light position! x: " + lposition.x + " y: " + lposition.y + " z: " + lposition.z);
    }
    
    //moves the camera down
    public void moveDown(float distance)
    {
        position.y += distance;
        //System.out.println("Camera position! x: " + position.x + " y: " + position.y + " z: " + position.z);
        //System.out.println("Light position! x: " + lposition.x + " y: " + lposition.y + " z: " + lposition.z);
    }
    
    //translates and rotate the matrix so that it looks through the camera
    public void lookThrough()
    {
        //roatate the pitch around the X axis
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        //roatate the yaw around the Y axis
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        //translate to the position vector's location
        glTranslatef(position.x, position.y, position.z);
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lposition.x).put(lposition.y).put(lposition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //maintains camera functionality until user closes window
    public void gameLoop()
    {
        FPCameraController camera = new FPCameraController(0, 0, 0);
        Chunk chunk = new Chunk(0, 0, 0);
        float dx = 0.0f;
        float dy = 0.0f;
        float mouseSensitivity = 0.09f;
        float movementSpeed = .35f;      
        Mouse.setGrabbed(true);  //hide the mouse
        
        // keep looping till the display window is closed or the ESC key is down
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            //distance in mouse movement from the last getDX() call.
            dx = Mouse.getDX();
            //distance in mouse movement from the last getDY() call.
            dy = Mouse.getDY();

            //control camera yaw from x movement fromt the mouse
            camera.yaw(dx * mouseSensitivity);
            //control camera pitch from y movement fromt the mouse
            camera.pitch(dy * mouseSensitivity);
            
            if (Keyboard.isKeyDown(Keyboard.KEY_W))//move forward
            {
                camera.walkForward(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S))//move backwards
            {
                camera.walkBackwards(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A))//strafe left 
            {
                camera.strafeLeft(movementSpeed); 
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D))//strafe right 
            {
                camera.strafeRight(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))//move up 
            {
                camera.moveUp(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) //move down
            {
                camera.moveDown(movementSpeed);
            }
            
            //set the modelview matrix back to the identity
            glLoadIdentity();
            //look through the camera before you draw anything
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            //draw scene here, rendering the Chunk
            chunk.render();
            //draw the buffer to the screen
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }                       
}

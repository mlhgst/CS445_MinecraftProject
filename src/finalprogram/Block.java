/***************************************************************
* file: Block.java
* author: Vincent Zhu
* class: CS 445 – Computer Graphics
*
* assignment: final program
* date last modified: 11/16/2017
*
* purpose: this class allows the creation of Block objects, individual 6-sided
* shapes in 3D space which together in 30x30x30 units form one complete Chunk. 
* Each block has an associated type, indicating its appearance on all 6 sides.
*
****************************************************************/ 
package finalprogram;

//Block class that allows the creation of individual 3D blocks
public class Block {
    private BlockType Type; //type of the block
    
    public enum BlockType{                
        BlockType_Grass(0),        
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5),
        BlockType_Default(6);
        
        private int BlockID; //ID to determine the block appearance by type
        
        BlockType(int i){
            BlockID = i;
        }
        
        public int GetID(){
            return BlockID;
        }               
    }
   
    //parameterized constructor for Block class
    public Block(BlockType type){
        Type = type;
    }          
    
    //retrieve the type of block encountered
    public int GetID(){
        return Type.GetID();
    }
}

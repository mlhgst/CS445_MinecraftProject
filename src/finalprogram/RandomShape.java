
package finalprogram;


public class RandomShape {
    private int numVertices;
    private int [][]coordinateAr;
    private int [][] fillAr;
    
    // Creates a random shape with given number of vertices
    RandomShape(int numVertices){
        this.numVertices = numVertices;
        this.coordinateAr = new int [this.numVertices][2];
    }
    
    // Generate vertices within given range
    public void generateVertices(int range,int xMin ,int zMin){
        this.fillAr = new int [range+1][range+1];
        for(int vertex = 0; vertex < this.numVertices; vertex++){
            this.coordinateAr[vertex][0] = (int)(Math.random()*range)+xMin;
            this.coordinateAr[vertex][1] = (int)(Math.random()*range)+zMin;
        }
    }
    
    // Given coordinates - fill array
    public int [][] fill(){
        return this.coordinateAr;
    }
    
    public void printVertices(){
       for(int vertex = 0; vertex < this.numVertices; vertex++){
            System.out.println(this.coordinateAr[vertex][0]+","+this.coordinateAr[vertex][1]);
        } 
    }
    
}

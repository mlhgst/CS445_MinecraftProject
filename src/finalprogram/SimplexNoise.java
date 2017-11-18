/***************************************************************
* file: SimplexNoise.java
* author: Vincent Zhu
* class: CS 445 – Computer Graphics
*
* assignment: final program
* date last modified: 11/16/2017
*
* purpose: this is a class file that allows the creation of noises, which
* contain parameters used to exhibit realistic appearing terrain features
* when creating the world
*
****************************************************************/ 
package finalprogram;

import java.util.Random;

//SimplexNoise class
public class SimplexNoise {
    private int largestFeature;
    private double persistence;
    private int seed;    
    private SimplexNoise_octave[] octaves;
    private double[] frequencys;
    private double[] amplitudes;    

    //SimplexNoise parameterized constructor
    public SimplexNoise(int largestFeature,double persistence, int seed){
        this.largestFeature = largestFeature;
        this.persistence = persistence;
        this.seed = seed;

        //recieves a number (eg 128) and calculates what power of 2 it is (eg 2^7)
        int numberOfOctaves = (int)Math.ceil(Math.log10(largestFeature)/Math.log10(2));

        octaves = new SimplexNoise_octave[numberOfOctaves];
        frequencys = new double[numberOfOctaves];
        amplitudes = new double[numberOfOctaves];

        Random rnd = new Random(seed); //use a seed to determine the octaves, frequencies and amplitudes of SimplexNoise

        for(int i = 0; i < numberOfOctaves; i++){
            octaves[i] = new SimplexNoise_octave(rnd.nextInt());
            frequencys[i] = Math.pow(2,i);
            amplitudes[i] = Math.pow(persistence, octaves.length-i);
        }
    }

    //returns a noise value used as a factor in determining the max height of each location of a chunk in x-z plane
    public double getNoise(int x, int y){
        double result=0;

        for(int i=0;i<octaves.length;i++){
            result=result+octaves[i].noise(x/frequencys[i], y/frequencys[i])* amplitudes[i];
        }
        return result;
    }   
} 
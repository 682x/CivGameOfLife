/*
Tile
Individual tiles on the map
Ryan Yan and Xingming Xu
ICS4U1
Jan. 21
 */

import java.util.*;

public class Tile {

    // on each tile, there are multiple "levels" of terrain
    // lv. 1 is the base terrain
    /*grassland, plains, wasteland, water*/
    // lv. 2 are terrain features
    /*hills, mountains, river*/
    // lv. 3 is removeable terrain features
    /**/
    // each tile will have a movement cost
    // open terrain costs 1 movement point
    // rough terrain costs 2

    String lv1 = "NA", lv2 = "NA", lv3 = "NA", owner = "NA";

    /*
    // lv 1 terrain
    GRASS, PLAIN, DESERT, SNOW, WATER, NA
    // lv 2 terrain
    HILL, MOUNTAIN, ICE, NA
    // lv 3 terrain
    FOREST, JUNGLE, NA
     */

    public Tile (String t1, String t2, String t3)
    {
        lv1 = t1;
        lv2 = t2;
        lv3 = t3;
    }

    public Tile (ArrayList <Tile> neighbours)//generate tile
    {
        //arraylist of possibilities for rng
        //chances of specific terrain based on neighbours
        ArrayList <String> rand = new ArrayList <String> (), rand2 = new ArrayList <String> (), rand3 = new ArrayList <String> ();
        boolean desert = false, snow = false;//conditional booleans
        //setup lv1 random arraylist
        rand.add ("GRASS");
        rand.add ("PLAIN");
        rand.add ("DESERT");
        rand.add ("SNOW");
        rand.add ("WATER");
        //setup lv2 arraylist
        rand2.add ("HILL");
        rand2.add ("MOUNTAIN");
        for (int i2 = 0; i2 < 5; i2++) rand2.add ("NA");
        //setup lv3 arraylist
        rand3.add ("FOREST");
        rand3.add ("JUNGLE");
        for (int i2 = 0; i2 < 3; i2++) rand3.add ("NA");
        
        for (int i = 0; i < neighbours.size (); i++)//check neighbours
        {            
            //cases for lv1 of neighbours
                //change rng arraylists based on neighbour
                //cell is more likely to be like neighbours
                //grass increases plain chance and vice versa
            if (neighbours.get (i).lv1.equals ("GRASS"))
            {
                for (int i2 = 0; i2 < 5; i2++) rand.add ("GRASS");
                for (int i2 = 0; i2 < 2; i2++) rand.add ("PLAIN");
            }
            else if (neighbours.get (i).lv1.equals ("PLAIN"))
            {
                for (int i2 = 0; i2 < 5; i2++) rand.add ("PLAIN");
                for (int i2 = 0; i2 < 2; i2++) rand.add ("GRASS");
            }
            else if (neighbours.get (i).lv1.equals ("DESERT"))
            {
                for (int i2 = 0; i2 < 5; i2++) rand.add ("DESERT");
                desert = true;
            }
            else if (neighbours.get (i).lv1.equals ("SNOW"))
            {
                for (int i2 = 0; i2 < 5; i2++) rand.add ("SNOW");
                snow = true;
            }
            else if (neighbours.get (i).lv1.equals ("WATER"))
                for (int i2 = 0; i2 < 5; i2++) rand.add ("WATER");
            
            //change lv2 arraylist based on neighbours
            if (neighbours.get (i).lv2.equals ("HILL"))
                for (int i2 = 0; i2 < 2; i2++) rand2.add ("HILL");
            else if (neighbours.get (i).lv2.equals ("MOUNTAIN"))
                for (int i2 = 0; i2 < 3; i2++) rand2.add ("MOUNTAIN");
            else if (neighbours.get (i).lv2.equals ("ICE"))
                for (int i2 = 0; i2 < 3; i2++) rand2.add ("ICE");
            
            //change lv3 arraylist based on neighbours
            if (neighbours.get (i).lv3.equals ("FOREST"))
                for (int i2 = 0; i2 < 3; i2++) rand3.add ("FOREST");
            else if (neighbours.get (i).lv3.equals ("JUNGLE"))
                for (int i2 = 0; i2 < 3; i2++) rand3.add ("JUNGLE");
        }
        
        //desert and snow cannot be neighbours
        if (desert) while (rand.remove ("SNOW"));
        if (snow) 
        {
            while (rand.remove ("DESERT"));
            for (int i2 = 0; i2 < 5; i2++) rand2.add ("ICE");
        }
        lv1 = rand.get ((int) (Math.random () * rand.size ()));//get lv1 from arraylist
        
        if (lv1.equals ("WATER")) //only ice can be on water
        {
            while (rand2.remove ("HILL"));
            while (rand2.remove ("MOUNTAIN"));
            if (!snow) while (rand2.remove ("ICE"));//ice only near snow tiles
            if (desert) while (rand2.remove ("ICE"));//no ice near deserts
        }
        else while (rand2.remove ("ICE"));//ice cannot be on non-water tiles
        lv2 = rand2.get ((int) (Math.random () * rand2.size ()));//get lv2
        
        if (lv1.equals ("DESERT") || lv1.equals ("WATER"))
            while (rand3.remove ("FOREST"));//no forest on water or in desert
        if (lv1.equals ("SNOW") || lv1.equals ("DESERT") || lv1.equals ("WATER"))
            while (rand3.remove ("JUNGLE"));//no jungle in certain tiles
        lv3 = rand3.get ((int) (Math.random () * rand3.size ()));//get lv3
    }

    public int[] yields (int [] bonus) //calculate yields of tile
    {
        int [] y = new int [2]; // food, materials
        //see guide for tile details
        //increases yields based on research bonuses
        if (lv1.equals ("GRASS"))
        {
            y [0] = 2 + bonus [0];
            y [1] = bonus [10];
        }
        else if (lv1.equals ("PLAIN"))
        {
            y [0] = 1 + bonus [1];
            y [1] = 1 + bonus [11];
        }
        else if (lv1.equals ("DESERT"))
        {
            y [0] = bonus [2];
            y [1] = bonus [12];
        }
        else if (lv1.equals ("SNOW"))
        {
            y [0] = bonus [3];
            y [1] = 1 + bonus [13];
        }
        else if (lv1.equals ("WATER"))
        {
            y [0] = 1 + bonus [4];
            y [1] = 1 + bonus [14];
        }
        if (lv2.equals ("HILL"))
        {
            y [0] += bonus [5];
            y [1] += 1 + bonus [15];
        }
        else if (lv2.equals ("MOUNTAIN"))
        {
            y [0] = 0 + bonus [6];
            y [1] += 2 + bonus [16];
        }
        else if (lv2.equals ("ICE"))
        {
            y [0] = 0 + bonus [7];
            y [1] = 0 + bonus [17];
        }
        if (lv3.equals ("FOREST"))
        {
            y [0] += 1 + bonus [8];
            y [1] += 1 + bonus [18];
        }
        else if (lv3.equals ("JUNGLE"))
        {
            y [0] += 1 + bonus [9];
            y [1] += bonus [19];
        }
        
        y [0] += bonus [20];
        y [1] += bonus [21];
        return y;//return yields
    }
    
    public int cost ()//calculate cost of expanding to tile
    {
        int c = 10;//base cost
        //certain tiles have increased costs
        if (lv1.equals ("DESERT")) c += 5;
        else if (lv1.equals ("SNOW")) c += 5;
        if (lv2.equals ("MOUNTAIN")) c += 5;
        return c;//return cost
    }
}

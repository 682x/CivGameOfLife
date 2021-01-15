/*
TechTree
Stores techs for a civ
Ryan Yan and Xingming Xu
ICS4U1
Jan. 21
 */

import java.util.*;

public class TechTree {

    ArrayList<String> discovered = new ArrayList<String>();//discovered tech
    
    //initialize all tech, see manual for tech tree
    Tech agriculture = new Tech(10, "Agriculture");

    Stack <Tech> line1 = new Stack <Tech> ();
    Tech pottery = new Tech(10, "Pottery");
    Tech records = new Tech(15, "Records");
    Tech phil = new Tech(25, "Philosophy");
    Tech law = new Tech(40, "Code of Law");
    Tech bureau = new Tech(60, "Bureaucracy");
    Tech edu = new Tech(100, "Education");

    Stack <Tech> line2 = new Stack <Tech> ();
    Tech animal = new Tech(10, "Animal Husbandry");
    Tech wheel = new Tech(15, "The Wheel");
    Tech trade = new Tech(25, "Trade");
    Tech horse = new Tech(40, "The Stirrup");
    Tech currency = new Tech(60, "Currency");
    Tech guilds = new Tech(100, "Guilds");

    Stack <Tech> line3 = new Stack <Tech> ();
    Tech mining = new Tech(10, "Mining");
    Tech bronze = new Tech(15, "Bronze Working");
    Tech construct = new Tech(25, "Construction");
    Tech iron = new Tech(40, "Iron Working");
    Tech engi = new Tech(60, "Engineering");
    Tech metal = new Tech(100, "Metal Working");

    Tech gun = new Tech (225, "Gunpowder");

    public TechTree() 
    {
        //Insert tech into stack in reverse order, first tech is on top
        //Create research path 1
        line1.push (edu);
        line1.push (bureau);
        line1.push (law);
        line1.push (phil);
        line1.push (records);
        line1.push (pottery);

        //Create research path 2
        line2.push (guilds);
        line2.push (currency);
        line2.push (horse);
        line2.push (trade);
        line2.push (wheel);
        line2.push (animal);

        //Create research path 3
        line3.push (metal);
        line3.push (engi);
        line3.push (iron);
        line3.push (construct);
        line3.push (bronze);
        line3.push (mining);
    }

    public int research (int materials, int line)//research a tech
    {
        //line 0: agriculture and gunpowder
        //line 1-3: research paths
        if (line == 0)//check if agriculture or gunpowder
        {
            if (!agriculture.researched) 
            {
                if (agriculture.research (materials))//research agriculture if possible
                {
                    discovered.add ("Agricultue");//add researched to discovered
                    return agriculture.beakers;//return cost if successful
                }
                else return -1;//return if insufficient materials
            }
            else 
            {
                if (gun.research (materials))//research gunpowder if possible
                {
                    discovered.add ("Gunpowder");
                    return gun.beakers;
                }
                else return -1;
            }
        }
        //get path to research
        else if (line == 1)
        {
            if (line1.peek ().research (materials)) //check if top of stack can be researched
            {
                Tech t = line1.pop ();//remove tech from stack
                discovered.add (t.name);//add to discovered
                return t.beakers;//return cost
            }
            else return -1;//insufficient materials
        }
        else if (line == 2)
        {
            if (line2.peek ().research (materials)) //check top of stack
            {
                Tech t = line2.pop ();//remove and return cost
                discovered.add (t.name);
                return t.beakers;
            }
            else return -1;//insufficient materials
        }
        else if (line == 3)
        {
            if (line3.peek ().research (materials)) //check top of stack
            {
                Tech t = line3.pop ();//remove and return cost
                discovered.add (t.name);
                return t.beakers;
            }
            else return -1;//insufficient materials
        }
        else return -1;//case if all research complete
    }

    public ArrayList <String> show ()//show available options for research
    {
        ArrayList <String> avail = new ArrayList <String> ();//arraylist of possibilities
        if (discovered.size () == 19) avail.add ("Gunpowder");//case all other research complete
        else if (discovered.size () == 20) avail.add ("All Research Complete");//case all research complete
        else
        {
            //add top of each stack, if research path has not been completed
            if (line1.size () > 0) avail.add (line1.peek ().name);
            if (line2.size () > 0) avail.add (line2.peek ().name);
            if (line3.size () > 0) avail.add (line3.peek ().name);
        }
        return (avail);//return available research
    }

    public void discount (int rate)//get discounts (from research bonuses)
    {
        int disc = 100;//initialize disc
        if (rate == 1) disc = 10;//first discount, 10%
        else if (rate == 2) disc = 4;//second discount, 25%
        //cost of tech divided by discount is subtracted from cost
        gun.beakers -= gun.beakers / disc;//adjust gunpowder
        //adjust all remaining research in all 3 paths
        for (int i = 0; i < line1.size (); i++) 
            line1.get (i).beakers -= line1.get (i).beakers / disc;
        for (int i = 0; i < line2.size (); i++) 
            line2.get (i).beakers -= line2.get (i).beakers / disc;
        for (int i = 0; i < line3.size (); i++) 
            line3.get (i).beakers -= line3.get (i).beakers / disc;
    }

}

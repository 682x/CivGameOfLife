/*
Tech
Researchable technologies for a civ
Ryan Yan and Xingming Xu
ICS4U1
Jan. 21
 */

public class Tech 
{
    
    int beakers; // required research
    boolean researched = false;//stores if tech has been researched
    String name;//name of tech
    
    public Tech (int beakers, String name)//contructor
    {
        this.beakers = beakers;//set cost
        researched = false;//set as unresearched
        this.name = name;//set name
    }
    
    public boolean research (int materials)//research tech
    {
        if (materials >= beakers) //check if sufficient materials
        {
            researched = true;//set as researched
            return true;//return success
        }
        else return false;//insufficient materials
    }
}

/*
Menu
Draws a menu when player clicks on a tile
Ryan Yan and Xingming Xu
ICS4U1
Jan. 21
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Menu//basic menu
{
    Tile tile;//Selected tile
    
    public Menu (Tile t)//constructor
    {
        tile = t;//get tile
    }
    
    public void show (Graphics g, int x, int y)//display menu
    {
        g.setColor (Color.gray);//draw border
        g.drawRect (x, y, 180, 105);
        g.setColor (new Color (80, 80, 40));//fill in menu
        g.fillRect (x + 1, y + 1, 179, 104);
        
        //display owner and terrain details
        g.setFont (new Font ("Century Gothic", 0, 14));
        g.setColor (new Color (240, 240, 240));
        if (tile.owner.equals ("NA"))
            g.drawString ("Unoccupied Tile", x + 5, y + 18);
        else
            g.drawString (tile.owner, x + 5, y + 18);
        g.drawString ("Lv1 terrain: " + tile.lv1, x + 5, y + 43);
        g.drawString ("Lv2 terrain: " + tile.lv2, x + 5, y + 68);
        g.drawString ("Lv3 terrain: " + tile.lv3, x + 5, y + 93);
    }
    
    public int selected (int y)//placeholder method, nothing to select
    {
        return -1;
    }
    
    public int getX ()//return x-length of menu
    {
        return 180;
    }
    
    public int getY ()//return y-length of menu
    {
        return 105;
    }
}

class ExpandMenu extends Menu//menu with expand option
{   
    public ExpandMenu (Tile t)//constructor
    {
        super (t);
    }
    
    public void show (Graphics g, int x, int y)//display menu
    {
        g.setColor (Color.gray);//draw border
        g.drawRect (x, y, 180, 145);
        g.setColor (new Color (80, 80, 40));//fill in menu
        g.fillRect (x + 1, y + 1, 179, 144);
        
        //display tile details
        g.setFont (new Font ("Century Gothic", Font.PLAIN, 14));
        g.setColor (new Color (240, 240, 240));
        g.drawString ("Unoccupied Tile", x + 5, y + 18);
        g.drawString ("Lv1 terrain: " + tile.lv1, x + 5, y + 43);
        g.drawString ("Lv2 terrain: " + tile.lv2, x + 5, y + 68);
        g.drawString ("Lv3 terrain: " + tile.lv3, x + 5, y + 93);
        
        //draw button using gradient
        for (int i = 0; i < 10; i++)
        {
            g.setColor (new Color (150 + i * 5, 150 + i * 5, 150 + i * 5));
            g.fillRect (x + i + 2, y + i + 105, 177 - i * 2, 39 - i * 2);
        }
        g.setColor (new Color (30, 40, 30));//display "expand"
        g.setFont (new Font ("Century Gothic", Font.BOLD, 16));
        g.drawString ("EXPAND", x + 58, y + 130);
    }
    
    public int selected (int y)//checks if user has clicked expand button
    {
        if (y >= 105 && y <= 145) return 1;
        else return -1;
    }
    
    public int getY ()//return y-length of menu
    {
        return 145;
    }
}

class AIMenu extends Menu//menu for tiles controlled by AICivs
{
    int manpow;//manpower of AICiv
    
    public AIMenu (Tile t, int Manpow)//constructor
    {
        super (t);
        manpow = Manpow;//store manpower
    }
    
    public void show (Graphics g, int x, int y)//display menu
    {
        g.setColor (Color.gray);//draw border
        g.drawRect (x, y, 180, 130);
        g.setColor (new Color (80, 80, 40));//fill menu
        g.fillRect (x + 1, y + 1, 179, 129);
        
        //display tile details, with manpower of AICiv
        g.setFont (new Font ("Century Gothic", Font.PLAIN, 14));
        g.setColor (new Color (240, 240, 240));
        g.drawString (tile.owner, x + 5, y + 18);
        g.drawString ("Manpower: " + manpow, x + 5, y + 43); 
        g.drawString ("Lv1 terrain: " + tile.lv1, x + 5, y + 68);
        g.drawString ("Lv2 terrain: " + tile.lv2, x + 5, y + 93);
        g.drawString ("Lv3 terrain: " + tile.lv3, x + 5, y + 118);
    }
    
    public int getY ()//return y-length of menu
    {
        return 130;
    }
}

class WarMenu extends Menu//menu with war option
{
    int manpow;//manpower of AICiv
    
    public WarMenu (Tile t, int Manpow)//contructor
    {
        super (t);
        manpow = Manpow;//store manpower
    }
    
    public void show (Graphics g, int x, int y)//display menu
    {
        g.setColor (Color.gray);//draw border
        g.drawRect (x, y, 180, 170);
        g.setColor (new Color (80, 80, 40));//fill menu
        g.fillRect (x + 1, y + 1, 179, 169);
        
        //display details with anpower
        g.setFont (new Font ("Century Gothic", Font.PLAIN, 14));
        g.setColor (new Color (240, 240, 240));
        g.drawString (tile.owner, x + 5, y + 18);
        g.drawString ("Manpower: " + manpow, x + 5, y + 43); 
        g.drawString ("Lv1 terrain: " + tile.lv1, x + 5, y + 68);
        g.drawString ("Lv2 terrain: " + tile.lv2, x + 5, y + 93);
        g.drawString ("Lv3 terrain: " + tile.lv3, x + 5, y + 118);
        
        //draw button using gradients
        for (int i = 0; i < 10; i++)
        {
            g.setColor (new Color (150 + i * 5, 150 + i * 5, 150 + i * 5));
            g.fillRect (x + i + 2, y + i + 130, 177 - i * 2, 39 - i * 2);
        }
        g.setColor (new Color (30, 40, 30));
        g.setFont (new Font ("Century Gothic", Font.BOLD, 16));//display "war"
        g.drawString ("WAR", x + 72, y + 155);
    }
    
    public int selected (int y)//check if war button has been selected
    {
        if (y >= 130 && y <= 170) return 2;
        else return -1;
    }
    
    public int getY ()//return y-length of menu
    {
        return 170;
    }
}

class ColMenu extends Menu//menu with war and colonize options
{
    int manpow;//manpower of AICiv
    
    public ColMenu (Tile t, int Manpow)//constructor
    {
        super (t);
        manpow = Manpow;//store manpower
    }
    
    public void show (Graphics g, int x, int y)//display menu
    {
        g.setColor (Color.gray);//draw border
        g.drawRect (x, y, 180, 210);
        g.setColor (new Color (80, 80, 40));//fill menu
        g.fillRect (x + 1, y + 1, 179, 209);
        
        //display tile details, with manpower
        g.setFont (new Font ("Century Gothic", Font.PLAIN, 14));
        g.setColor (new Color (240, 240, 240));
        g.drawString (tile.owner, x + 5, y + 18);
        g.drawString ("Manpower: " + manpow, x + 5, y + 43); 
        g.drawString ("Lv1 terrain: " + tile.lv1, x + 5, y + 68);
        g.drawString ("Lv2 terrain: " + tile.lv2, x + 5, y + 93);
        g.drawString ("Lv3 terrain: " + tile.lv3, x + 5, y + 118);
        
        //war button
        for (int i = 0; i < 10; i++)
        {
            g.setColor (new Color (150 + i * 5, 150 + i * 5, 150 + i * 5));
            g.fillRect (x + i + 2, y + i + 130, 177 - i * 2, 39 - i * 2);
        }
        g.setColor (new Color (30, 40, 30));
        g.setFont (new Font ("Century Gothic", Font.BOLD, 16));
        g.drawString ("WAR", x + 72, y + 155);
        
        //colonize button
        for (int i = 0; i < 10; i++)
        {
            g.setColor (new Color (150 + i * 5, 150 + i * 5, 150 + i * 5));
            g.fillRect (x + i + 2, y + i + 170, 177 - i * 2, 39 - i * 2);
        }
        g.setColor (new Color (30, 40, 30));
        g.drawString ("COLONIZE", x + 52, y + 195);
    }
    
    public int selected (int y)//check if any button has been selected
    {
        if (y >= 130 && y < 170) return 2;
        else if (y >= 170 && y <= 210) return 3;
        else return -1;
    }
    
    public int getY ()//return y-length of menu
    {
        return 210;
    }
}

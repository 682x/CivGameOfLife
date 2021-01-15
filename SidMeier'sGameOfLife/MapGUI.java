/*
MapGUI
GUI of Game
Ryan Yan and Xingming Xu
ICS4U1
Jan. 21
 */

import java.io.*;
import java.lang.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.sound.sampled.*;

class MapGUI extends JFrame implements ActionListener
{
    private JComboBox rsCB = new JComboBox ();//combobox for research
    boolean menuExists = false, gameover = false;//stores if menu is open and if game is over
    //x and y store co-ordinates of the centre displayed tile
    //menux and meuy store absolute co-ordinates of menu, if any
    //openx and openy store location on map where menu is open
    //conscripts stores consciptions used during a turn
    int x = 25, y = 25, menux, menuy, openx, openy, turn = 1, conscripts = 0;
    JTextArea outTA = new JTextArea ();//display messages to player
    Map map;//map used for game
    Menu menu;//menu when selecting a tile
    private JTextField turnTF, foodTF, matsTF, manTF;//display current turn and resources

    //======================================================== constructor
    public MapGUI (String name)
    {
        map = new Map (name);
        
        // 1... Create/initialize components
        //Initialize button
        JButton saveBtn = new JButton ("Save");
        saveBtn.addActionListener (this);
        JButton loadBtn = new JButton ("Load");
        loadBtn.addActionListener (this);
        JButton rsBtn = new JButton ("Research");
        rsBtn.addActionListener (this);
        JButton conscriptBtn = new JButton ("Conscript");
        conscriptBtn.addActionListener (this);
        JButton twBtn = new JButton ("Total War");
        twBtn.addActionListener (this);
        JButton ntBtn = new JButton ("Next Turn");
        ntBtn.addActionListener (this);
        
        //initialize combobox and textfields
        rsCB.addItem ("Agriculture");
        turnTF = new JTextField (2);
        turnTF.setText (turn + "");
        turnTF.setEditable (false);
        foodTF = new JTextField (6);
        foodTF.setEditable (false);
        matsTF = new JTextField (6);
        matsTF.setEditable (false);
        manTF = new JTextField (6);
        manTF.setEditable (false);

        // 2... Create content pane, set layout
        JPanel content = new JPanel ();        // Create a content pane
        content.setLayout (new BoxLayout (content, BoxLayout.PAGE_AXIS)); // Use BorderLayout for panel
        JPanel top = new JPanel ();//buttons and turn counter
        top.setLayout (new FlowLayout ());
        JPanel mid = new JPanel ();//resources textfields
        mid.setLayout (new FlowLayout ());

        DrawArea board = new DrawArea (825, 825);//create display area
        //write instructions
        update ();
        
        //Audio
        try
        {
            File file = new File ("Baba_Yetu.wav");
            AudioInputStream as = AudioSystem.getAudioInputStream (file);
            Clip clip = AudioSystem.getClip ();
            clip.open (as);
            clip.loop (Clip.LOOP_CONTINUOUSLY);
            clip.start ();
        }
        catch (UnsupportedAudioFileException baba_yeetu) {
        System.out.println ("E1");}
        catch (IOException baba_yeetu) {
        System.out.println ("E2");}
        catch (LineUnavailableException baba_yeetu) {}

        // 3... Add the components to the input area.
        //buttons and turn counter
        top.add (new JLabel ("Turn:"));
        top.add (turnTF);
        top.add (rsCB);
        top.add (rsBtn);
        top.add (conscriptBtn);
        top.add (twBtn);
        top.add (ntBtn);
        
        //resources
        mid.add (new JLabel ("Food:"));
        mid.add (foodTF);
        mid.add (new JLabel ("Materials:"));
        mid.add (matsTF);
        mid.add (new JLabel ("Manpower:"));
        mid.add (manTF);
        
        //add components to master jpanel
        content.add (top);
        content.add (mid);
        content.add (board); // Output area
        content.add (outTA);

        // 4... Set this window's attributes.
        setContentPane (content);
        pack ();
        setTitle ("Sid Meier's Game of Life");
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo (null);           // Center window.
    }

    public void actionPerformed (ActionEvent e)//button listener
    {
        if (e.getActionCommand ().equals ("Research"))//research button pressed
        {
            //get index of selected tech, research if possible
            if (map.civ.research (rsCB.getSelectedIndex () + 1))//success
            {
                update ();//update resources and display message
                outTA.append ("\nResearch Successful. Obtained: " + rsCB.getSelectedItem ());
                rsCB.removeAllItems ();//reset combobox and replace with available research
                ArrayList <String> t = map.civ.tree.show ();
                for (int i = 0; i < t.size (); i++) rsCB.addItem (t.get (i));
            }
            else 
            {
                update ();//failure message
                outTA.append ("\nInsufficient Materials");
            }
        }
        else if (e.getActionCommand ().equals ("Conscript"))//conscript button pressed
        {
            //max 5 conscriptions per turn
            if (conscripts >= 5) 
            {
                update ();//show message
                outTA.append ("\nYou cannot perform anymore consciptions this turn");
            }
            else
            {
                if (map.civ.conscript ())//check if civ can conscript
                {
                    update ();//success message
                    outTA.append ("\nConscription Successful");
                    conscripts++;//increase consciptions counter
                }
                else 
                {
                    update ();//failure message
                    outTA.append ("\nInsufficient Materials");
                }
            }
        }
        else if (e.getActionCommand ().equals ("Total War"))//total war button pressed
        {
            if (map.civ.totalWar ())//check if civ can enter total war
            {
                update ();//success message
                outTA.append ("\nCivilization in Total War");
            }
            else 
            {
                update ();//failure message
                outTA.append ("\nInsufficient Materials");
            }
        }
        else if (e.getActionCommand ().equals ("Next Turn"))//next turn button pressed
        {
            //new thread ensure repaint finishes before window exits from game over
            new Thread (new Runnable ()
            {
                public void run ()
                {
                    map.advance ();//advance AICivs
                    map.civ.turn ();//advance player civ
                    //possible disaster
                    String dis = map.civ.disaster ();
                    update ();//update resources
                    //display disaster, if any
                    if (!dis.equals ("")) 
                    {
                        outTA.append ("\n" + dis);
                        if (dis.equals ("Revolution: Game over")) gameover = true;
                    }
                    if (map.civ.size == 0)//check if civ has no land
                    {
                        outTA.append ("\nCiv conquered: Game over");//game over message
                        gameover = true;//set gameover
                    }
                    repaint ();//redraw map
                    conscripts = 0;//reset consciptions counter
                    turn++;//increment and display turn counter
                    turnTF.setText (turn + "");
                }
            }).start ();
        }
        if (gameover) gameOver ();//close game if gameover
    }

    public void update ()//update resources
    {
        //update resource textfields
        foodTF.setText (map.civ.food + "");
        matsTF.setText (map.civ.materials + "");
        manTF.setText (map.civ.manpower + "");
        //clear bottom textarea
        outTA.setText ("");
    }

    public void gameOver ()//exits when gameover
    {
        setVisible (false);//close game
    }

    public class Mouse implements MouseListener//implements mouse in game
    {
        int x1, y1;//stores co-ordinates of click

        public void mouseEntered (MouseEvent e){}

        public void mouseExited (MouseEvent e){}

        public void mousePressed (MouseEvent e)
        {
            if (e.isShiftDown ())//shift + mouse allows user to traverse map
            {
                x1 = e.getX () / 75; //find mouse x-location
                y1 = e.getY () / 75; //find mouse y-location
                menuExists = false;//close menu, if any
            }
        }

        public void mouseReleased (MouseEvent e)
        {
            if (e.isShiftDown ())//mouse released during traverse
            {
                x -= e.getX () / 75 - x1;//change x and y based on mouse location
                y -= e.getY () / 75 - y1;
                //expand map if user is close to edge
                if (x < 15)//close to left edge
                {
                    //check if closer to top or bottom and expand accordingly
                    if (y < map.size / 2) 
                    {
                        map.expand ("NW");
                        x += 20;//adjust co-ordinates along with expansion
                        y += 20;
                    }
                    else 
                    {
                        map.expand ("SW");
                        x += 20;
                    }
                }
                //check if near right edge
                else if (x > map.size - 15)
                {
                    //check top or bottom
                    if (y < map.size / 2) 
                    {
                        map.expand ("NE");
                        y += 20;
                    }
                    else map.expand ("SE");
                }
                
                //check if near top
                else if (y < 15)
                {
                    //check if map is closer to left or right
                    if (x < map.size / 2) 
                    {
                        map.expand ("NW");
                        x += 20;
                        y += 20;
                    }
                    else 
                    {
                        map.expand ("NE");
                        y += 20;
                    }
                }
                //check if near bottom
                else if (y > map.size - 15)
                {
                    if (y < map.size / 2) 
                    {
                        map.expand ("SW");
                        x += 20;
                    }
                    else map.expand ("SE");
                }
                repaint ();//update menu
            }
        }

        public void mouseClicked (MouseEvent e)//processes click when shift not down
        {
            if (e.isShiftDown ()) return;//return if shift is down
            if (menuExists)//menu currently exists
            {
                x1 = e.getX ();//get co-ordinates of click
                y1 = e.getY ();
                //check if click is outside menu
                if (menu.selected (y1 - menuy) == 1) //check if expand is selected
                {
                    map.civ.expand (openx, openy);//exapnd civ
                    menuExists = false;//close menu
                    update ();//update resources
                }
                else if (menu.selected (y1 - menuy) == 2)//check if war is selected
                {
                    //declare war, get result
                    boolean b = map.civ.war (openx, openy, map.getCiv (map.map [openx] [openy].owner));
                    menuExists = false;//close menu
                    update ();//update resources
                    if (b) outTA.append ("\nVictory");//if victorius
                    else outTA.append ("\nDefeat");//if defeated
                }
                else if (menu.selected (y1 - menuy) == 3)//check if colonize is selected
                    //colonize AICiv
                    map.colonize (map.getCiv (map.map [openx] [openy].owner));
                menuExists = false;//close menu
            }
            else//menu is not open
            {
                menuExists = true;//open menu
                openx = e.getX () / 75 + x - 5;//get co-ordinates of selected tile
                openy = e.getY () / 75 + y - 5;
                menux = e.getX ();//get co-ordinates of click
                menuy = e.getY ();
                
                //if expansion is possible, open expand menu
                if (map.canExpand (openx, openy, map.civ.tree))
                    menu = new ExpandMenu (map.map [openx] [openy]);
                //if colonizing is possible, open colonize menu
                else if (map.canCol (openx, openy, map.civ.tree))
                {
                    int manpow = map.getCiv (map.map [openx] [openy].owner).manpower;
                    menu = new ColMenu (map.map [openx] [openy], manpow);
                }
                //if war is possible, open war menu
                else if (map.canWar (openx, openy, map.civ.tree))
                {
                    int manpow = map.getCiv (map.map [openx] [openy].owner).manpower;
                    menu = new WarMenu (map.map [openx] [openy], manpow);
                }
                //if AICiv tile is selected, open AICiv menu
                else if (!map.map [openx] [openy].owner.equals ("NA") && !map.map [openx] [openy].owner.equals (map.civ.name))
                {
                    int manpow = map.getCiv (map.map [openx] [openy].owner).manpower;
                    menu = new AIMenu (map.map [openx] [openy], manpow);
                }
                //otherwise, open normal menu
                else
                    menu = new Menu (map.map [openx] [openy]);
            }
            
            if (gameover) gameOver ();//close game if gameover
            repaint ();//update map
        }
    }

    class DrawArea extends JPanel//display map
    {
        public DrawArea (int width, int height)//constructor
        {
            this.setPreferredSize (new Dimension (width, height)); // size
            this.addMouseListener (new Mouse ());//add mouse
        }

        public void paintComponent (Graphics g)//display
        {
            map.show (g, x, y);//display tiles
            if (menuExists) menu.show (g, menux, menuy);//display menu, if any
            if (gameover)//display game over banner if gameover
            {
                g.setColor (new Color (10, 10, 100));//draw border
                g.drawRect (225, 300, 375, 225);
                g.setColor (new Color (40, 40, 120));//fill banner
                g.fillRect (226, 301, 374, 224);
                g.setColor (Color.white);//write game over
                g.setFont (new Font ("Century Gothic", Font.BOLD, 20));
                g.drawString ("GAME OVER", 350, 420);
            }
        }
    }

    //======================================================== method main
    public static void main (String[] args)//start game
    {
        MapGUI window = new MapGUI ("");
        window.setVisible (true);
    }
}


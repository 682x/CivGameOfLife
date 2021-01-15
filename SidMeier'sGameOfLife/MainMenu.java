/*
MainMenu
Launch game from menu
Ryan Yan and Xingming Xu
ICS4U1
Jan. 21
 */

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.*;

public class MainMenu extends JFrame implements ActionListener
{
    JTextField nameTF = new JTextField (10);
    
    public MainMenu ()
    {
        // 1... Create/initialize components
        JButton startBtn = new JButton ("Start Game");//Start game button
        startBtn.addActionListener (this);
        JButton htpBtn = new JButton ("How to Play");//How to play button
        htpBtn.addActionListener (this);

        // 2... Create content pane, set layout
        JPanel content = new JPanel();//main panel
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        JPanel top = new JPanel();//individual sections
        top.setLayout(new FlowLayout());
        
        DrawArea board = new DrawArea (600, 400);
        
        // 3... Add the components to the content pane.
        top.add (new JLabel ("Name of Civilization"));
        top.add(nameTF);
        top.add(startBtn);
        //top.add(htpBtn);
        content.add(top);
        content.add (board);

        // 4... Set this window's attributes.
        setContentPane(content);
        pack();
        setTitle("Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);           // Center window.
    }

    public void actionPerformed (ActionEvent e)//button listener
    {
        if (e.getActionCommand ().equals ("Start Game"))//start game pressed
        {
            MapGUI window = new MapGUI (nameTF.getText ());//start game with name from textfield
            window.setVisible (true);//open game and close menu
            this.setVisible (false);
        }
    }

    public static void main (String[] args) throws java.lang.Exception//initialize and show menu
    {
        MainMenu window = new MainMenu ();
        window.setVisible (true);
    }
    
    class DrawArea extends JPanel//display map
    {
        public DrawArea (int width, int height)//constructor
        {
            this.setPreferredSize (new Dimension (width, height)); // size
        }

        public void paintComponent (Graphics g)//display
        {
            try
            {
                Image img = ImageIO.read (new File ("MENU.png"));
                g.drawImage (img, 0, 0, 600, 400, null);
            }
            catch (IOException ohme){}
        }
    }
}
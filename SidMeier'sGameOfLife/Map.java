/*
Map
Stores all tiles and civs
Ryan Yan and Xingming Xu
ICS4U1
Jan. 21
 */

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class Map
{
    /*
     * Map is always square
     * Edge cells empty (Will never be seen in-game)
     */

    Tile [] [] map;//array of tiles
    int size;//size
    private final int centre = 25;//starting point of civ
    PlayerCiv civ;//player civ
    ArrayList <AICiv> civs = new ArrayList <AICiv> ();//ai civs

    public Map (String name) //create map
    {
        map = new Tile [51] [51]; //alllocate space for tiles
        size = 51;//set default size
        //fill array with empty tiles
        for (int i = 0; i < 51; i++) Arrays.fill (map [i], new Tile ("NA", "NA", "NA"));
        map [centre] [centre] = new Tile ("GRASS", "NA", "FOREST");//ideal starting point
        civ = new PlayerCiv (25, 25, name);//create player civ
        map [centre] [centre].owner = name;
        generate ();
    }

    private void generate () //generate tiles of map
    {
        for (int i = 1; i < Integer.max (centre, size - centre - 1); i++)//array traverses outwards from centre
        //finds maximum distance form centre to edge of map
        {
            int edge1 = centre - i, edge2 = centre + i;//creates edge 
            if (edge1 < 1) edge1 = 1;//fixes edges if beyond array index
            if (edge2 > size - 2) edge2 = size - 2;
            for (int i2 = edge1; i2 <= edge2; i2++) //traverses cells with edges
            {
                for (int i3 = edge1; i3 <= edge2; i3++)
                {
                    if (!map [i2] [i3].lv1.equals ("NA")) continue;
                    else if (!map [i2] [i3].lv2.equals ("NA")) continue;
                    else if (!map [i2] [i3].lv3.equals ("NA")) continue;
                    ArrayList <Tile> neighbours = new ArrayList <Tile> ();//adds neighbours to array list
                    neighbours.add (map [i2 - 1] [i3 - 1]);
                    neighbours.add (map [i2 - 1] [i3]);
                    neighbours.add (map [i2 - 1] [i3 + 1]);
                    neighbours.add (map [i2] [i3 - 1]);
                    neighbours.add (map [i2] [i3 - 1]);
                    neighbours.add (map [i2 + 1] [i3 - 1]);
                    neighbours.add (map [i2 + 1] [i3]);
                    neighbours.add (map [i2 + 1] [i3 + 1]);
                    map [i2] [i3] = new Tile (neighbours);//generates new tile based on neighbours
                    if (edge1 > 3 && edge2 < size - 4) generateCiv (i2, i3);
                }
            }
        }
    }

    private void generateCiv (int x, int y)
    {
        if (Math.random () < 0.75) return;
        for (int i = x - 4; i <= x + 4; i++)
            for (int i2 = y - 4; i2 <= y + 4; i2++)
                if (!map [i] [i2].owner.equals ("NA")) return;
        civs.add (new AICiv (x, y, "Civ" + x + "-" + y));
        map [x] [y].owner = "Civ" + x + "-" + y;
    }

    public void expand (String direction) //expand map
    {
        size += 20;//increase size
        Tile [] [] t = new Tile [size] [size];//create temporary expanded array and fill
        for (int i = 0; i < size; i++) Arrays.fill (t [i], new Tile ("NA", "NA", "NA"));
        int a = 0, b = 0;//paramaters for shifting map
        //If north, shift existing map 20 cells down
        //If west, shift exsting map 20 cells right
        //If south or east, no shift
        if (direction.equals ("NW"))
        {
            a = 20;
            b = 20;
        }
        else if (direction.equals ("NE")) b = 20;
        else if (direction.equals ("SW")) a = 20;

        //transfer existing cells to temporary with necessary shift
        for (int i = 0; i < size - 20; i++)
            for (int i2 = 0; i2 < size - 20; i2++)
                t [i + a] [i2 + b] = map [i] [i2];
        map = new Tile [size] [size];
        map = t;//set temporary to map
        generate ();//fill empty cells

        civ.shiftland (a, b);
        for (int i = 0; i < civs.size (); i++)
            civs.get (i).shiftland (a, b);
    }

    public void show (Graphics g, int x, int y)//display map
    {
        Image img = null;//declare image
        String imageFile;
        int countx = 0, county = 0;//counters
        for (int i = x - 5; i <= x + 5; i++)//traverse on-screen portion of map
        {
            for (int i2 = y - 5; i2 <= y + 5; i2++)
            {
                //set border
                //Player civ: Blue, AI civ: Red, Colony: Green, None: Light gray
                if (map [i] [i2].owner.equals (civ.name))
                    g.setColor (Color.blue);
                else if (map [i] [i2].owner.equals ("Colony of " + civ.name))
                    g.setColor (Color.green);
                else if (!map [i] [i2].owner.equals ("NA"))
                    g.setColor (Color.red);
                else g.setColor (new Color (195, 195, 155));
                for (int j = 0; j < 3; j++)
                    g.drawRect (countx * 75 + j, county * 75 + j, 74 - j * 2, 74 - j * 2);
                
                //case for mountain
                if (map [i] [i2].lv2.equals ("MOUNTAIN")) imageFile = "MOUNTAIN";
                else//get all existing levels, add to string
                {
                    imageFile = map [i] [i2].lv1;//filename for lv1
                    if (!map [i] [i2].lv2.equals ("NA"))//add lvl2, if it exists
                        imageFile += "_" + map [i] [i2].lv2;
                    if (!map [i] [i2].lv3.equals ("NA"))//add lvl3, if it exists
                        imageFile += "_" + map [i] [i2].lv3;
                }

                try//browse and draw image
                {
                    img = ImageIO.read (new File ("terrain/" + imageFile + ".png"));
                    g.drawImage (img, countx * 75 + 3, county * 75 + 3, 69, 69, null);
                }
                catch (IOException owo){}
                county++;//increment y-counter
            }
            county = 0;//reset y-counter
            countx++;//increment x-counter
        }
    }

    public void advance ()//advance all AICivs
    {
        for (int i = 0; i < civs.size (); i++) civs.get (i).turn ();
    }

    public boolean canExpand (int x, int y, TechTree tree)//check if civ can expand to a tile
    {
        if (!map [x] [y].owner.equals ("NA")) return false;//ensure tile is unoccupied
        if (map [x] [y].cost () > civ.food) return false;//ensure there is enough food
        //cannot expand over water without trade
        if (map [x] [y].lv1.equals ("WATER") && !tree.discovered.contains ("Trade")) return false;
        //check if selected tile neighbours controlled tile
        if (map [x - 1] [y].owner.equals (civ.name)) return true;
        if (map [x + 1] [y].owner.equals (civ.name)) return true;
        if (map [x] [y - 1].owner.equals (civ.name)) return true;
        if (map [x] [y + 1].owner.equals (civ.name)) return true;
        return false;//conditions not met
    }

    public boolean canWar (int x, int y, TechTree tree)//check if civ can declare war
    {
        //ensure tile is owned by non-colony AICiv
        if (map [x] [y].owner.equals ("NA") || map [x] [y].owner.equals (civ.name) || map [x] [y].owner.equals ("Colony of " + civ.name)) return false;
        if (civ.food < 10) return false;//ensure there is enough food
        //cannot declare war on water without trade
        if (map [x] [y].lv1.equals ("WATER") && !tree.discovered.contains ("Trade")) return false;
        //check if selected tile neighbours controlled tile
        if (map [x - 1] [y].owner.equals (civ.name)) return true;
        if (map [x + 1] [y].owner.equals (civ.name)) return true;
        if (map [x] [y - 1].owner.equals (civ.name)) return true;
        if (map [x] [y + 1].owner.equals (civ.name)) return true;
        return false;//conditions not met
    }
    
    public boolean canCol (int x, int y, TechTree tree)//check if civ can colonize AICiv
    {
        if (!canWar (x, y, tree)) return false;//must be able to declare war to colonize
        AICiv c = getCiv (map [x] [y].owner);//get AICiv on tile
        //To colonize, civ needs over 10 times as much as AICiv, as well as over 100 manpower
        if (civ.manpower > c.manpower * 10 && civ.manpower > 100) return true;
        return false;//condition not met
    }

    public AICiv getCiv (String name)//get AICIv given name
    {
        for (int i = 0; i < civs.size (); i++)//traverse AICivs arraylist
            if (civs.get (i).name.equals (name)) return civs.get (i);//check name of civ, return if match
        return new AICiv (1, 1, "");//failsafe in case civ not found, should never occur
    }
    
    public void colonize (AICiv c)//colonize an AICiv
    {
        civs.remove (c);//remove from AICiv arraylist 
        Colony col = new Colony (c);//create colony from AICiv
        civs.add (col);//add back to arraylist
    }
    
    //Civilization class
    //Stores resources and land controlled
    //Contains method to facilitate interaction between civs
    public class Civ
    {
        String name;//name and starting resources
        int food = 15, materials = 15, size = 1, manpower = 10, base = 10;
        //Food, Materials, Manpower: resources of civ
        //Size: tracks size of civ
        //Base: base limit for manpower
        
        //2-item array for every square civ controls, contains co-ordinate
        ArrayList <int []> land = new ArrayList <int []> ();
        TechTree tree = new TechTree ();//civ techtree
        int[] bonuses = new int [23];//bonuses from techtree
        //0 - 9: terrain food bonuses
        //10 - 19: terrain material bonuses
        //20 - 21: universal

        public Civ (int startx, int starty, String Name)//create civ
        {
            int [] t = {startx, starty};//add starting position to land
            land.add (t);
            name = Name;//set name
            map [startx] [starty].owner = name;//register ownership of tile
        }

        public String disaster ()
        {
            String message = "";
            int x = (int) (Math.random () * 30);
            if (x == 1 || x == 2)//plague
            {
                //reduce manpower by 10 - 50%
                int per = ((int) (Math.random () * 5) + 1) * 10;
                manpower = (int) (manpower * (100 - per) / 100.0);
                message = "Plague: Manpower reduced by " + per + "%";
            }
            else if (x == 3 || x == 4)//famine
            {
                //reduce food by 10 - 50%
                int per = ((int) (Math.random () * 5) + 1) * 10;
                food = (int) (food * (100 - per) / 100.0);
                message = "Famine: Food reduced by " + per + "%";
            }
            else if (x == 5 || x == 6)//fire
            {
                //reduce materials by 10 - 50%
                int per = ((int) (Math.random () * 5) + 1) * 10;
                materials = (int) (materials * (100 - per) / 100.0);
                message = "Fire: Materials reduced by " + per + "%";
            }
            else if (x == 7 || x == 8)//drought
            {
                //no food yield for current turn
                //calculates and subtracts yield to counter normal yield
                for (int i = 0; i < land.size (); i++) 
                {
                    Tile t = map [land.get (i) [0]] [land.get (i) [1]];
                    food -= t.yields (bonuses) [0];
                }
                message = "Drought: No food for this turn";
            }
            else if (x == 9 || x == 10)//rebellion
            {
                //no materials yield for current turn
                //calculates and subtracts yield to counter normal yield
                for (int i = 0; i < land.size (); i++)
                {
                    Tile t = map [land.get (i) [0]] [land.get (i) [1]];
                    materials -= t.yields (bonuses) [1];
                }
                message = "Rebellion: No materials for this turn";
            }
            else if (x == 11)//civil war
            {
                //halve all resources
                food -= food / 2;
                materials -= materials / 2;
                manpower -= manpower / 2;
                message = "Civil War: Food, materials, and manpower halved";
            }
            else if (x == 12)//revolution
            {
                //instantly end the game is there is less than 1 food per tile
                if (food < size) message = "Revolution: Game over";
            }
            return message;
        }

        public void shiftland (int a, int b)//shift land when map expands
        {
            //offset each co-ordinate based on shift values
            for (int i = 0; i < land.size (); i++)
            {
                land.get (i) [0] += a;
                land.get (i) [1] += b;
            }
        }

        public boolean research (int line)//research new items
        {
            //special cases for agriculture and gunpowder
            if (!tree.agriculture.researched || tree.discovered.size () == 19) 
            {
                int r = tree.research (materials, 0);
                if (r == -1) return false;//return false if insufficient materials
                else materials -= r;//deduct cost if successful
            }
            else //standard case
            {
                //increment line if one branch is fully researched
                //line 2 tech will be in position 1 of combobox, must be adjusted
                if (tree.line1.isEmpty ()) line++;
                if (tree.line2.isEmpty ()) line++; 
                try
                {
                    int r = tree.research (materials, line);//attempt research
                    if (r == -1) return false;//insufficient materials
                    else materials -= r;//deduct spent materials
                }
                catch (EmptyStackException owo)//prevents error if all research is complete
                {
                    return false;
                }
            }
            //get bonus for researched tile if successful
            getBonus (tree.discovered.get (tree.discovered.size () - 1));
            return true;//return success
        }

        private void getBonus (String branch)//give bonus from research
        {
            //check user's manual for list of bonuses
            if (branch.equals ("Agriculture")) bonuses [20]++;
            else if (branch.equals ("Pottery")) bonuses [2]++;
            else if (branch.equals ("Records")) 
            {
                bonuses [9]++;
                tree.discount (1);
            }
            else if (branch.equals ("Philosophy")) bonuses [0]++;
            else if (branch.equals ("Code of Law")) 
            {
                base += 10;
                bonuses [15]++;
                bonuses [18]++;
            }
            else if (branch.equals ("Bureaucracy")) 
            {
                bonuses [0]++;
                bonuses [1]++;
            }
            else if (branch.equals ("Education")) tree.discount (2);
            else if (branch.equals ("Animal Husbandry")) 
            {
                bonuses [10]++;
                bonuses [11]++;
            }
            else if (branch.equals ("The Wheel")) bonuses [12]++;
            else if (branch.equals ("The Stirrup")) 
            {
                bonuses [11]++;
                bonuses [12]++;
            }
            else if (branch.equals ("Currency")) 
            {
                bonuses [0]++;
                bonuses [21]++;
            }
            else if (branch.equals ("Guilds")) 
            {
                bonuses [10]++;
                bonuses [11]++;
            }
            else if (branch.equals ("Mining")) 
            {
                bonuses [15]++;
                bonuses [18]++;
            }
            else if (branch.equals ("Bronze Working")) base += 10;
            else if (branch.equals ("Construction")) bonuses [18]++;
            else if (branch.equals ("Iron Working")) base += 20;
            else if (branch.equals ("Engineering")) 
            {
                bonuses [5]++;
                bonuses [6]++;
            }
            else if (branch.equals ("Metal Working")) 
            {
                manpower += 100;
                base += 20;
            }
            else if (branch.equals ("Gunpowder")) 
            {
                bonuses [15] += 2;
                base += 30;
            }
        }

        public void turn ()//advance civ
        {
            manpower -= 10;//deduct manpower
            if (manpower < base) manpower = base;//restore manpower to base if needed
            for (int i = 0; i < land.size (); i++)//calculate yields and add to resources
            {
                Tile t = map [land.get (i) [0]] [land.get (i) [1]];
                food += t.yields (bonuses) [0];
                materials += t.yields (bonuses) [1];
            }
        }

        public boolean conscript ()//use materials to strengthen army
        {
            if (materials >= 10)//10 materials needed
            {
                manpower += 10;//deduct 10 from materials, add 10 to manpower
                materials -= 10;
                return true;//return success
            }
            return false;//insufficient materials
        }

        public boolean war (int x, int y, Civ c)//war between two civs
        {
            manpower -= 10;//deduct 10 manpower from both civs
            if (manpower < 1) manpower = 1;//manpower never flls below 1
            c.manpower -= 10;
            if (c.manpower < 1) c.manpower = 1;
            
            //calculate "warpower" for each civ, similar to dice rolls in risk
            //greater manpower means greater odds of victory
            int rand1 = (int) (Math.random () * (manpower + materials / 100));
            int rand2 = (int) (Math.random () * (c.manpower + c.materials / 100));
            
            //deduct 10 food from both civs
            if (food < 10)
            {
                food = 0;//if food is fewer than 10, food becomes 0, warpower is halved
                rand1 /= 2;
            }
            else food -= 10;
            if (c.food < 10)
            {
                c.food = 0;
                rand2 /= 2;
            }
            else c.food -= 10;
            
            if (rand1 >= rand2)//case victory
            {
                //tranfer ownership of tile
                int [] temp = {x, y};
                land.add (temp);//add tile to attacking civ
                for (int i = 0; i < c.land.size (); i++)//find and remove land in defending civ
                    if (Arrays.equals (c.land.get (i), temp)) c.land.remove (i);
                map [x] [y].owner = name;//change ownership of tile
                
                //spoils of war
                //gain food and materials based on average per tile
                food += c.food / c.size;
                c.food -= c.food / c.size;
                materials += c.materials / c.size;
                c.materials -= c.materials / c.size;
                
                //change sizes, done after spoils for accuracy
                size++;
                c.size--;
                //remove civ from arraylist if land becomes 0
                if (c.size == 0) civs.remove (getCiv (c.name));
                return true;//return victory
            }
            else if (rand1 < rand2)//case defeat
            {
                //spoils, goes from attacker to defender
                c.food += food / size;
                food -= food / size;
                c.materials += materials / size;
                materials -= materials / size;
            }
            return false;//return defeat
        }
    }

    class PlayerCiv extends Civ//Player controlled civ
    {
        public PlayerCiv (int startx, int starty, String name)
        {
            super (startx, starty, name);//same constructor as parent
        }

        public void expand (int x, int y)//expand civ using co-ordinates
        {
            food -= map [x] [y].cost ();//deduct cost from food
            map [x] [y].owner = name;//change ownership of tile
            size++;//increment size
            int [] temp = {x, y};//add tile co-ordinates to land
            land.add (temp);
        }

        public boolean totalWar ()//put civ in state of total war
        {
            //check if sufficient resources
            //requires 5 materials per tile and over 50 materials
            if (materials > size * 5 && materials > 50)
            {
                manpower += (materials - size * 5) / 5;//gain 1 manpower for every 5 materials used
                materials = size * 5;//deduct materials
                return true;//return success
            }
            return false;//insufficient materials
        }
    }

    class AICiv extends Civ//AI controlled civ
    {
        private int agro, stock;//factors affecting AI behaviour
        //Agressiveness (agro): chance to declare war when possible
        //Stockpiling (stock): minimum resource amount, calculated per 5 tiles

        public AICiv (int startx, int starty, String name)//generate AICiv
        {
            super (startx, starty, name);//parent constuctor
            agro = (int) (Math.random () * 5);//randomly generate agro and stock
            stock = (int) (Math.random () * 4) + 2;
        }

        public void turn ()//advance AICiv
        {
            super.turn ();//perform turn in parent class
            disaster ();//possible disaster
            for (int i = 0; i < 3; i++)//attempt expand three times
            //AICiv can only expand one tile out if called once
                if (food > stock * (size / 2)) expand ();
            //attempt consciption to the maximum of 5 times
            for (int i = 0; i < 5; i++) if (materials > 50) conscript ();
            //attempt to research a random available tech three times
            for (int i = 0 ; i < 3; i++)
                if (materials > stock * (size / 5)) 
                    research ((int) (Math.random () * 3) + 1);
            //restore manpower again, needed if civ declares war during turn
            if (manpower < base) manpower = base;
        }

        public void expand ()//expand civ
        {
            for (int i = 0; i < land.size (); i++)//check all neighbours
            {
                int [] tile = land.get (i);
                check (tile [0] - 1, tile [1]);
                check (tile [0] + 1, tile [1]);
                check (tile [0], tile [1] - 1);
                check (tile [0], tile [1] + 1);
                if (food <= stock * (size / 2)) return;//immediately end if insufficient food
            }
        }

        public void check (int x, int y)//check tile for expansion or war
        {
            Tile t = map [x] [y];//get tile
            if (t.lv1.equals ("NA")) return;//prevent civ from expanding past edge of map
            //trade needed for expansion over water
            if (t.lv1.equals ("WATER") && !tree.trade.researched) return;
            
            //possibly declare war if tile if occupied
            if (!t.owner.equals ("NA"))
            {
                //ensures civ does not attack itself, then use rng with agro
                if (!t.owner.equals (name) && (int) (Math.random () * 5) < agro && size != 0)
                {
                    if (t.owner.equals (civ.name)) war (x, y, civ);//case for attacking player civ
                    else war (x, y, getCiv (map [x] [y].owner));//case for attacking AICivs
                }
            }
            //check if sufficient food, then 1 in 4 chance of expanding
            else if (food > t.cost () & Math.random () < 0.25)
            {
                food -= t.cost ();//deduct food spent
                t.owner = name;//change ownership
                size++;//increment size
                int [] temp = {x, y};//add tile to land
                land.add (temp);
            }
        }
    }

    class Colony extends AICiv//Colonies of player civ
    {
        public Colony (int x, int y, String name)//default constuctor, never used
        {
            super (x, y, name);
        }
        
        public Colony (AICiv c)//create colony from existing AICiv
        {
            //parent constructor
            super (c.land.get (0) [0], c.land.get (0) [1], "Colony of " + civ.name);
            //transfer land of AICiv and change ownership
            for (int i = 1; i < c.land.size (); i++) 
            {
                land.add (c.land.get (i));
                map [c.land.get (i) [0]] [c.land.get (i) [1]].owner = name;
            }
            //transfer all details of AICiv over
            size = c.size;
            food = c.food;
            materials = c.materials;
            manpower = c.manpower;
            bonuses = c.bonuses;
            tree = c.tree;
            super.agro = c.agro;
            super.stock = c.stock;
        }
        
        public void turn ()//advance colony
        {
            super.turn ();//normal turn of AICiv
            //donate a quarter of food and materials to player civ
            civ.food += food / 4;
            food -= food / 4;
            civ.materials += materials / 4;
            materials -= materials / 4;
        }
        
        public void check (int x, int y)//same as AICiv check, but cannot declare war on player
        {
            Tile t = map [x] [y];
            if (t.lv1.equals ("NA")) return;
            if (t.lv1.equals ("WATER") && !tree.trade.researched) return;
            if (!t.owner.equals ("NA"))
            {
                if (!t.owner.equals (name) && (int) (Math.random () * 5) < super.agro && size != 0)
                {
                    if (!t.owner.equals (civ.name)) 
                        war (x, y, getCiv (map [x] [y].owner));
                }
            }
            else if (food > t.cost () & Math.random () < 0.25)
            {
                food -= t.cost ();
                t.owner = name;
                size++;
                int [] temp = {x, y};
                land.add (temp);
            }
        }
    }
}

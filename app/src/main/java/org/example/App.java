package org.example;

import org.graalvm.polyglot.*; // Import GraalVM Polyglot API "FOR PYTHON INTERPRETER"

import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;
// import java.awt.FlowLayout; // not used (using null layout for absolute positioning)
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

public class App extends JFrame implements KeyListener {

    ArrayList<ArrayList<Integer>> chunklist = new ArrayList<>();
    public static Random random = new Random();
    int moveEnemyX = 0;
    int moveEnemyY = 0;
    int movePlayerX = 0;
    int movePlayerY = 0;
    public static JFrame frame = new JFrame();
    public static Integer currentChunkX = 0;
    public static Integer currentChunkY = 0;
    static JLabel player;
    static JLabel enemy;
    public static ArrayList<Integer> ChunkmapX = new ArrayList<>();
    public static ArrayList<Integer> ChunkmapY = new ArrayList<>();
    public static void main(String[] args) {
        App myGame = new App(); // Create an instance of the App class to access non-static methods

        // Run Graal Python snippets (if available) but continue regardless of outcome
        try (Context context = Context.create("python")) {
            Value string = context.eval("python", "" 
                + "name = \"name\"\n"
                + "print(f'Hello, {name}!')\n"
            );
            System.out.println(string);

            // Run a simple script
            context.eval("python", "print('Hello from Python!')");

            // Or get a value back to Java
            Value result = context.eval("python", "10 + 5");
            System.out.println("Result from Python: " + result.asInt());
        } catch (Exception ex) {
            // Don't fail the app if Graal/Python isn't available; just log it
            System.err.println("Graal/Python context failed: " + ex.getMessage());
        }

        // Build the Swing UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Start the Swing application by configuring the frame
            frame.setTitle("Last Light");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            // Use absolute positioning so we can move components with setLocation
            frame.setLayout(null);
            // Make sure the frame is focusable and will receive key events
            frame.setFocusable(true);
            frame.addKeyListener(myGame); // Add key listener to the frame

            // Create player label (Object no longer adds it to the frame)
            player = Object("defaultPlayer", Color.RED);

            enemy = Object("defaultEnemy", Color.BLUE);

            // Load image from classpath (resource files in src/main/resources are on the classpath root)
            URL url = App.class.getResource("/idle0.png");
            if (url == null) {
                System.err.println("Missing resource: /idle0.png");
            } else {
                ImageIcon playerimg = new ImageIcon(url);
                player.setIcon(playerimg);
                // Ensure the label has the correct size so setLocation/setBounds work
                player.setSize(playerimg.getIconWidth(), playerimg.getIconHeight());
                player.setLocation(0, 0);
            }

            // Load image from classpath (resource files in src/main/resources are on the classpath root)
            URL urlenemy = App.class.getResource("/idle90.png");
            if (urlenemy == null) {
                System.err.println("Missing resource: /idle90.png");
            } else {
                ImageIcon enemyimg = new ImageIcon(urlenemy);
                enemy.setIcon(enemyimg);
                // Ensure the label has the correct size so setLocation/setBounds work
                enemy.setSize(enemyimg.getIconWidth(), enemyimg.getIconHeight());
                enemy.setLocation(0, 0);
            }

            // Add player and enemy to frame and show
            //frame.add(player);
            // make sure enemy is added too and doesn't overlap the player
            enemy.setLocation(100, 0);
            //frame.add(enemy);
            frame.setVisible(true);
            frame.revalidate();
            frame.repaint();
            InitializeTiles();
        });
    }

    public static void InitializeTiles() {
        System.out.println("InitializeTiles method called");
        int i = 0;
        int j = 0;
        int[][] chunk = Generate();
        for (i = 0; i < 9; i++) {
            for (j = 0; j < 9; j++) {
                System.out.print(chunk[i][j] + " ");
            }
            System.out.println();
        }
        for (i = 0; i < 9; i++) {
            for (j = 0; j < 9; j++) {
                URL url = App.class.getResource("/" + chunk[i][j] + ".png");
                if (url == null) {
                    System.err.println("Missing resource: /idle90.png");
                } else {
                    JLabel tile = Object("tile" + i + j, null);
                    ImageIcon tileimg = new ImageIcon(url);
                    tile.setIcon(tileimg);
                    // Ensure the label has the correct size so setLocation/setBounds work
                    int tileWidth = tileimg.getIconWidth();
                    int tileHeight = tileimg.getIconHeight();
                    tile.setBounds(i * tileWidth, j * tileHeight, tileWidth, tileHeight);
                    frame.add(tile);
                }
            }    
        }
    }
    public static int[][] Generate() {
            int[][] gerneratedchunk = new int[9][9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    int temprand = random.nextInt(9);
                    gerneratedchunk[i][j] = temprand;
                }
            }
            return gerneratedchunk;
    }

    public static void MoveObject(int movex, int movey, JLabel obj) {
        System.out.println("MoveObject method called");
        obj.setLocation(movex, movey);
        System.out.println("OBJECT:" + obj);
        System.out.println("Object moved to: (" + movex + ", " + movey + ")");
    }

    public static void player(){
        System.out.println("Player method called");
        
    }

    /*public static void RemoveObject(JLabel obj) {
        System.out.println("RemoveObject method called");
        frame.remove(obj);
        frame.revalidate();
        frame.repaint();
        System.out.println("Object removed from frame");
    } */

    public static JLabel Object(String playername, Color color) {
        System.out.println("Player method called");
        System.out.println("Player name: " + playername);
        JLabel label = new JLabel(playername);
        label.setOpaque(true);
        label.setBackground(color);
        frame.add(label);
        return label;
    }

    //Store different maps/chunks of the map
    private void storeMaps(int chunkx, int chunky) {
        ChunkmapX.add(chunkx);
        ChunkmapY.add(chunky);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // keyTyped reports a character; use keyPressed for movement logic
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // example: print key code
        System.out.println("Key pressed: " + e.getKeyCode());
                int c = e.getKeyCode();
        switch (c) {
            case KeyEvent.VK_A:
                System.out.println("Key typed: a");
                movePlayerX -= 10;
                MoveObject(movePlayerX, movePlayerY, player);
                break;
            case KeyEvent.VK_D:
                System.out.println("Key typed: d");
                movePlayerX += 10;
                MoveObject(movePlayerX, movePlayerY, player);
                break;
            case KeyEvent.VK_W:
                System.out.println("Key typed: w");
                movePlayerY -= 10;
                MoveObject(movePlayerX, movePlayerY, player);
                break;
            case KeyEvent.VK_S:
                System.out.println("Key typed: s");
                movePlayerY += 10;
                MoveObject(movePlayerX, movePlayerY, player);
                break;
            case KeyEvent.VK_LEFT: // 37
                System.out.println("Key pressed: left arrow");
                moveEnemyX -= 10;
                MoveObject(moveEnemyX, moveEnemyY, enemy);
                break;
            case KeyEvent.VK_RIGHT: // 39
                System.out.println("Key pressed: right arrow");
                moveEnemyX += 10;
                MoveObject(moveEnemyX, moveEnemyY, enemy);
                break;
            case KeyEvent.VK_UP: // 38
                System.out.println("Key pressed: up arrow");
                moveEnemyY -= 10;
                MoveObject(moveEnemyX, moveEnemyY, enemy);
                break;
            case KeyEvent.VK_DOWN: // 40
                System.out.println("Key pressed: down arrow");
                moveEnemyY += 10;
                MoveObject(moveEnemyX, moveEnemyY, enemy);
                break;
            default:
                // no-op
        }
 
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // no-op
    }

    // left, right, up, down methods
    // KeyEvent.VK_RIGHT    37    38   40
}
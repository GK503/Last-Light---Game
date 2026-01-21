package org.example;

import org.graalvm.polyglot.*; // Import GraalVM Polyglot API "FOR PYTHON INTERPRETER"

import java.util.List;
import java.util.Iterator;
import javax.swing.Timer;
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
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class App extends JFrame implements KeyListener {

    public static Map<Point, int[][]> chunkmap = new HashMap<>();

    public static List<JLabel> tiles = new ArrayList<>();
    public static List<JLabel> projectiles = new ArrayList<>();
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
    public static void main(String[] args) {
        App myGame = new App(); // Create an instance of the App class to access non-static methods

        // Run Graal Python snippets (if available) but continue regardless of outcome
        try (Context context = Context.create("python")) {
            Value string = context.eval("python", "" 
                + "name = \"f u\"\n"
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

            enemy.setLocation(100, 0);
            //frame.add(enemy);
            frame.setVisible(true);
            frame.revalidate();
            frame.repaint();

            //CreateTiles();

            // Start a timer to update projectile positions
            new Timer(50, e -> {
                    UpdateProjectile(0);
                }).start();
        });
    }

    public static void UpdateProjectile(int direction) {
        //System.out.println("UpdateProjectile method called");
        Iterator<JLabel> it = projectiles.iterator();
        while (it.hasNext()) {
            JLabel p = it.next();
            int x = p.getX();
            if (x < frame.getWidth()) {
                p.setLocation(x + 10, p.getY());
            } else {
                frame.remove(p);
                it.remove();
                System.out.println(projectiles.size());
            }
        }
        frame.revalidate();
        frame.repaint();
    }

    public static void UpdateAnimation(char c) {
    System.out.println("UpdateAnimation method called");
    String base;
    switch (c) {
        case 'a': base = "/walk-90.";  updateicon("/idle-90.png", player); break;
        case 'd': base = "/walk90.";   updateicon("/idle90.png",  player); break;
        case 'w': base = "/walk0.";    updateicon("/idle0.png",   player); break;
        case 's': base = "/walk180.";  updateicon("/idle180.png", player); break;
        default:  updateicon("/idle0.png", player); return;
    }

    final int[] frameIndex = {1};
    int delay = 50; // ms

    new Timer(delay, e -> {
        if (frameIndex[0] <= 5) {
            updateicon(base + frameIndex[0] + ".png", player);
            frameIndex[0]++;
        } else {
            ((Timer) e.getSource()).stop();
        }
    }).start();
}


    public static void updateicon(String resource, JLabel object) {
        System.out.println("UpdatePlayerIcon method called");
        if (resource == null) {
            return; // No update if resource is null
        }
            URL url = App.class.getResource(resource);
            ImageIcon img = new ImageIcon(url);
            object.setIcon(img);
            object.setSize(img.getIconWidth(), img.getIconHeight());
    }

    public static void CreateTiles() {
        System.out.println("CreateTiles method called");
        int i = 0;
        int j = 0;
        for (JLabel t : tiles) {
            frame.remove(t);
        }
        tiles.clear();
        int[][] chunk = Generate();
        for (i = 0; i < 9; i++) {
            for (j = 0; j < 9; j++) {
                System.out.print(chunk[i][j] + " ");
            }
            System.out.println();
        }
        storeMaps(chunk);
        for (i = 0; i < 9; i++) {
            for (j = 0; j < 9; j++) {
                URL url = App.class.getResource("/" + chunk[i][j] + ".png");
                if (url == null) {
                    System.err.println("Missing resource: /idle" + chunk[i][j] + ".png");
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
                    int temprand = random.nextInt(1, 10);
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

    public static void UpdateMap(){
        System.out.println("UpdateMap method called");
    }

    //Store different maps/chunks of the map
    public static void storeMaps(int[][] chunk) {
        System.out.println("storeMaps method called");
        chunkmap.put(new Point(currentChunkX, currentChunkY), chunk);
        System.out.println("Map stored at chunk (" + currentChunkX + ", " + currentChunkY + ")");
        for (Map.Entry<Point, int[][]> entry : chunkmap.entrySet()) {
            Point key = entry.getKey();
            int[][] value = entry.getValue();
            System.out.println("Chunk at (" + key.x + ", " + key.y + "):");
            for (int i = 0; i < value.length; i++) {
                for (int j = 0; j < value[i].length; j++) {
                    System.out.print(value[i][j] + " ");
                }
                System.out.println();
            }
        }
        //currentChunkX++;
        //currentChunkY++;
    }

    public static void CreateProjectile(int movePlayerX, int movePlayerY) {
        System.out.println("CreateProjectile method called");
        JLabel projectile = Object("projectile", Color.YELLOW);
        projectile.setBounds(movePlayerX + 20, movePlayerY + 10, 10, 10);
        projectiles.add(projectile);
        frame.repaint();
        // Example size and position will change this after adding images
        updateicon(null, projectile);
        // Simple animation to move the projectile to the right

        // Each projectile is overloading the queue thread making the projectile laggy and slower
        /*new Timer(50, e -> {
            int x = projectile.getX();
            if (x < frame.getWidth()) {
                projectile.setLocation(x + 10, projectile.getY());
            } else {
                ((Timer) e.getSource()).stop();
                frame.remove(projectile);
                frame.revalidate();
                frame.repaint();
            }
        }).start();*/
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
                UpdateAnimation('a');
                MoveObject(movePlayerX, movePlayerY, player);
                break;
            case KeyEvent.VK_D:
                System.out.println("Key typed: d");
                movePlayerX += 10;
                UpdateAnimation('d');
                MoveObject(movePlayerX, movePlayerY, player);
                break;
            case KeyEvent.VK_W:
                System.out.println("Key typed: w");
                movePlayerY -= 10;
                UpdateAnimation('w');
                MoveObject(movePlayerX, movePlayerY, player);
                break;
            case KeyEvent.VK_S:
                System.out.println("Key typed: s");
                movePlayerY += 10;
                UpdateAnimation('s');
                MoveObject(movePlayerX, movePlayerY, player);
                break;
            case KeyEvent.VK_F:
                System.out.println("Key typed: f");
                CreateProjectile(movePlayerX, movePlayerY);
                break;
            case KeyEvent.VK_Q:
                System.out.println("Key typed: q");
                CreateTiles();
                System.out.println(currentChunkX);
                System.out.println(currentChunkY);
                break;
            /*case KeyEvent.VK_LEFT: // 37
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
                break;*/
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
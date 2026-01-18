package org.example;

import org.graalvm.polyglot.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class App extends JFrame implements KeyListener {

    /*String chunksmap[][] = {
            {"chunk1", "chunk2", "chunk3"},
            {"chunk4", "chunk5", "chunk6"},
            {"chunk7", "chunk8", "chunk9"}
        };*/
    public static JFrame frame = new JFrame();
    public static Integer currentChunkX = 0;
    public static Integer currentChunkY = 0;
    public static ArrayList<Integer> ChunkmapX = new ArrayList<>();
    ArrayList<Integer> ChunkmapY = new ArrayList<>();
    public static void main(String[] args) {
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
        }
    }
    /*
        // Start the Swing application by creating the
        //  frame instance
        frame.setTitle("My Swing Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        // Make sure the frame is focusable and will receive key events
        frame.setFocusable(true);
        frame.addKeyListener(KeyListener.class.cast(new App()));
        frame.setVisible(true);
        player("defaultPlayer", Color.RED);
        player("defaultPlayer", Color.black);
    */

    public static void player(String playername, Color color) {
        System.out.println("Player method called");
        System.out.println("Player name: " + playername);
        JLabel label = new JLabel(playername);
        label.setOpaque(true);
        label.setBounds(0,0,100,100);
        label.setBackground(color);
        frame.add(label);
    }

    //Store different maps/chunks of the map
    private void storeMaps() {
        // Implementation for storing maps
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        switch (c) {
            case 'a':
                System.out.println("Key typed: a");
                break;
            default:
                // ignore
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // example: print key code
        System.out.println("Key pressed: " + e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // no-op
    }
}
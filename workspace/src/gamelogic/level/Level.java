package gamelogic.level;

import java.awt.Graphics;
import java.util.List;

import gameengine.PhysicsObject;
import gameengine.graphics.Camera;
import gameengine.loaders.Mapdata;
import gameengine.loaders.Tileset;
import gamelogic.GameResources;
import gamelogic.Main;
import gamelogic.player.Player;
import gamelogic.tiledMap.Map;
import gamelogic.tiles.Flag;
import gamelogic.tiles.Flower;
import gamelogic.tiles.SolidTile;
import gamelogic.tiles.Spikes;
import gamelogic.tiles.Tile;
import gamelogic.key.Key;
import gamelogic.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import gameengine.*;
import gamelogic.clientHandling.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class Level {

	private LevelData leveldata;
	private Map map;
	private Key[] key;
	public Player player;
	public ArrayList<Player> otherPlayers;
	private Camera camera;

	private boolean active;
	private boolean playerDead;
	private boolean playerWin;
	private ArrayList<Key> keys = new ArrayList<>();

	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();

	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 70;

	

	public Level(LevelData leveldata) {
		this.leveldata = leveldata;
		mapdata = leveldata.getMapdata();
		width = mapdata.getWidth();
		height = mapdata.getHeight();
		tileSize = mapdata.getTileSize();
		restartLevel();

        
	}

	public LevelData getLevelData(){
		return leveldata;
	}

	public void restartLevel() {
		int[][] values = mapdata.getValues();
		Tile[][] tiles = new Tile[width][height];

		for (int x = 0; x < width; x++) {
			int xPosition = x;
			for (int y = 0; y < height; y++) {
				int yPosition = y;

				tileset = GameResources.tileset;

				tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				if (values[x][y] == 0){
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this); // Air
				}else if (values[x][y] == 1){
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid"), this);

				}else if (values[x][y] == 2){
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_DOWNWARDS, this);
				}else if (values[x][y] == 3){
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_UPWARDS, this);
				}else if (values[x][y] == 4){
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_LEFTWARDS, this);
				}else if (values[x][y] == 5){
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_RIGHTWARDS, this);
				}else if (values[x][y] == 6){
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Dirt"), this);
				}else if (values[x][y] == 7){
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Grass"), this);
				}else if (values[x][y] == 9){
					tiles[x][y] = new Flag(xPosition, yPosition, tileSize, tileset.getImage("Flag"), this);
				}else if (values[x][y] == 10) {
					tiles[x][y] = Key(xPosition, yPosition, null);
					keys.add((Key) tiles[x][y]);
				} else if (values[x][y] == 12){
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_down"), this);
				}else if (values[x][y] == 13){
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_up"), this);
				}else if (values[x][y] == 14){
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_middle"), this);
			}

		}

		key = new Key[keys.size()];
		map = new Map(width, height, tileSize, tiles);
		camera = new Camera(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, 0, map.getFullWidth(), map.getFullHeight());
		for (int i = 0; i < keysList.size(); i++) {
			key[i] = new Key(keys.get(i).getX(), keys.get(i).getY(), this);
		}
		player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(),
				this);
		camera.setFocusedObject(player);

		active = true;
		playerDead = false;
		playerWin = false;
	}

	public void onPlayerDeath() {
		active = false;
		playerDead = true;
		throwPlayerDieEvent();
	}

	public void onPlayerWin() {
		active = false;
		playerWin = true;
		throwPlayerWinEvent();
	}

	public void update(float tslf) {
		if (active) {
			// Update the player
			player.update(tslf);
			
			// Player death
			if (map.getFullHeight() + 100 < player.getY())
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.BOT] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.LEF] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.RIG] instanceof Spikes)
				onPlayerDeath();
			
			for (int i = 0; i < key.length; i++) {
				key[i].update(tslf);
				if (player.getHitbox().isIntersecting(key[i].getHitbox())&&player.getX()<key[i].getX()) {
					player.hasKey=true;
					key[i].pickedUp=true;
				}
			}

			

			// Update the map
			map.update(tslf);

			// Update the camera
			camera.update(tslf);
		}
	}
	
	




	public void draw(Graphics g) {
	   	 g.translate((int) -camera.getX(), (int) -camera.getY());
	   	 // Draw the map
	   	 for (int x = 0; x < map.getWidth(); x++) {
	   		 for (int y = 0; y < map.getHeight(); y++) {
	   			 Tile tile = map.getTiles()[x][y];
	   			 if (tile == null)
	   				 continue;
	   			
	   			 if (camera.isVisibleOnCamera(tile.getX(), tile.getY(), tile.getSize(), tile.getSize()))
	   				 tile.draw(g);
	   		 }
	   	 }


	   	 for (int i = 0; i < key.length; i++) {
	   		 key[i].draw(g);
	   	 }

	   	 // Draw the player
	   	 player.draw(g);

		 for (int i = 0; i < otherPlayers.size(); i++) {
			otherPlayers.get(i).draw(g);
		 }

	   	 // used for debugging
	   	 if (Camera.SHOW_CAMERA)
	   		 camera.draw(g);
	   	 g.translate((int) +camera.getX(), (int) +camera.getY());
	    }


	// --------------------------Die-Listener
	public void throwPlayerDieEvent() {
		for (PlayerDieListener playerDieListener : dieListeners) {
			playerDieListener.onPlayerDeath();
		}
	}

	public void addPlayerDieListener(PlayerDieListener listener) {
		dieListeners.add(listener);
	}

	// ------------------------Win-Listener
	public void throwPlayerWinEvent() {
		for (PlayerWinListener playerWinListener : winListeners) {
			playerWinListener.onPlayerWin();
		}
	}

	public void addPlayerWinListener(PlayerWinListener listener) {
		winListeners.add(listener);
	}

	// ---------------------------------------------------------Getters
	public boolean isActive() {
		return active;
	}

	public boolean isPlayerDead() {
		return playerDead;
	}

	public boolean isPlayerWin() {
		return playerWin;
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}

	public void addPlayer(Player newPlayer) {
		otherPlayers.add(newPlayer);
	}
	
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        final Socket socket = new Socket(host, 9876);
        final ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        final ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        
        
      
        System.out.println("Sending request to Socket Server");
        AtomicBoolean running = new AtomicBoolean(true);

        JFrame gui= new JFrame();
        gui.setSize(500, 500);
        //gui.setBackground(new Color(200,200,150));
        JTextField topText = new JTextField("Type your messages bellow (type 'exit' to quit):", 40);
        JTextField input = new JTextField("", 40);
        JTextField bottomText = new JTextField("Recieved message displayed bellow:", 40);
        JTextField output = new JTextField("", 40);
        topText.setPreferredSize(new Dimension(500, 50));
        input.setPreferredSize(new Dimension(500, 150));
        bottomText.setPreferredSize(new Dimension(500, 50));
        output.setPreferredSize(new Dimension(500, 150));
        topText.setBackground(new Color(215, 220, 250));
        bottomText.setBackground(new Color(215, 220, 250));
        input.setBackground(new Color(250, 235, 215));
        output.setBackground(new Color(215, 250, 220));
        gui.setLayout(new FlowLayout());	


        // pre: none
        // post: the GUI is set up and visible, and a thread is running that listens
        // for user input and sends it to the server, and another thread is running 
        // that listens for messages from the server and displays them in the GUI.
        new Thread (() -> {input.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("tried to send message "+input.getText());
                try{
                //System.out.println("Enter a message to send to the server (type 'exit' to quit):");

                    String message = input.getText();
                    if (message.equalsIgnoreCase("exit")) {
                        running.set(false);
                        oos.writeObject("exit");
                        if (ois != null) ois.close();
                        if (oos != null) oos.close();
                        if (socket != null) socket.close();
                        gui.setVisible(false);
                        gui.dispose();
                        System.out.println("Exiting client...");
                    } else {
                        oos.writeObject(message);
                        oos.flush();
                        input.setText("");
                        }
                    
                } catch (IOException k) {
                    System.out.println("Error sending message to server: " + k);
                }
            
            }});
        }).start();
        topText.setEditable(false);
        bottomText.setEditable(false);
        output.setEditable(false);
        gui.add(topText);
        gui.add(input);
        gui.add(bottomText);
        gui.add(output);
        gui.setVisible(true);



        // pre: none
        // post: a thread is running that listens for messages from the server and displays them in the GUI. 
        // If the connection is closed, the thread stops and a message is printed.
    //     new Thread (() -> {while (running.get()) {
    //         if (ois != null) {
    //             String message = "";
    //             try {
    //                 message = (String) ois.readObject();
    //                 output.setText(message);
    //                 System.out.println("Someone said: " + message);
    //             } catch (ClassNotFoundException e) {
    //                 // TODO Auto-generated catch block
    //                 e.printStackTrace();
    //             } catch (IOException e) {
    //                 if (e instanceof java.net.SocketException && "Socket closed".equals(e.getMessage())) {
    //                     running.set(false);
    //                     System.out.println("Connection closed.");
    //                 } else {
    //                     e.printStackTrace();
    //                 }
    //             }
    //         }
            
        
    //         //read the server response message
    //         //if (running){
    //         /*
    //         try {
    //             Thread.sleep(100);
    //         } catch (InterruptedException e) {
    //             // TODO Auto-generated catch block
    //             e.printStackTrace();
    //         }
    //              */
                
    //     //}
        
    // }
    
        
    // }).start();
}



}
package gamelogic.level;

import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import gameengine.PhysicsObject;
import gameengine.graphics.Camera;
import gameengine.loaders.Mapdata;
import gameengine.loaders.Tileset;
import gamelogic.GameResources;
import gamelogic.Main;
import gamelogic.key.Key;
import gamelogic.player.Player;
import gamelogic.tiledMap.Map;
import gamelogic.tiles.Flag;
import gamelogic.tiles.Flower;
import gamelogic.tiles.SolidTile;
import gamelogic.tiles.Spikes;
import gamelogic.tiles.Tile;
import gamelogic.player.PlayerInput;

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

        

		//get the localhost IP address, if server is running on some other IP, you need to use that
		try{
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = new Socket(host, 9876);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        
        
      
        System.out.println("Sending request to Socket Server");
        AtomicBoolean running = new AtomicBoolean(true);

        // Wait for any player input to start the sending thread
        while ((PlayerInput.isLeftKeyDown() || PlayerInput.isRightKeyDown() || PlayerInput.isJumpKeyDown())) {
            
				Level me = this;
                new Thread(() -> {
				while (running.get()) {
					try {
						oos.writeObject(me);
						oos.flush();
					} catch (IOException e) {
						System.out.println("something whent wrong when trying to send");
				}
            }
        }).start();
	}
        
            

        // Thread for receiving messages
        new Thread(() -> {
            while (running.get()) {
                if (ois != null) {
                    Level message = null;
                    try {
                        message = (Level) ois.readObject();
                        System.out.println("Received: " + message);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (e instanceof java.net.SocketException && "Socket closed".equals(e.getMessage())) {
                            running.set(false);
                            System.out.println("Connection closed.");
                        } else {
                            e.printStackTrace();
                        }
                    }
					
					ArrayList<Player> newPlayers = message.otherPlayers;
					for (int i = 0; i < newPlayers.size(); i++) {
						if (newPlayers.get(i) == player) {
							Player newPlayer = message.player;
							player = newPlayers.get(i);
							for (int j = 0; j < otherPlayers.size(); j++) {
								if (otherPlayers.get(j) == newPlayers.get(i)) {
									otherPlayers.set(j, newPlayers.get(i));
								}
							}
						}
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
	}).start();
	
}
catch (UnknownHostException e) {
		System.out.println("Host not found: " + e.getMessage());
	} catch (IOException e) {
		System.out.println( "problems connecting to input from server" + e.getMessage());
	
	}
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
				// }else if (values[x][y] == 10) {
				// 	tiles[x][y] = Key(xPosition, yPosition, null);
				// 	keys.add((Key) tiles[x][y]);
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
			// for (int i = 0; i < keysList.size(); i++) {
			// 	key[i] = new Key(keys.get(i).getX(), keys.get(i).getY(), this);
			// }
			player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(),
					this);
			camera.setFocusedObject(player);

			active = true;
			playerDead = false;
			playerWin = false;
		}
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
					// player.hasKey=true; // HasKey gives the player a key, or somethin'????
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
	
	
	



}
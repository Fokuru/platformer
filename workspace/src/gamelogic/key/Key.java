package gamelogic.key;

import java.awt.Color;
import java.awt.Graphics;

import gameengine.PhysicsObject;
import gameengine.graphics.MyGraphics;
import gameengine.hitbox.RectHitbox;
import gameengine.maths.Vector2D;
import gamelogic.Main;
import gamelogic.clientHandling.Information;
import gamelogic.level.Level;
import gamelogic.tiles.Tile;
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

public class Key extends PhysicsObject {
    private BufferedImage image;
	public boolean pickedUp;

    public Key (float x, float y, Level level){
		super(x, y, 120, 120, level);
		movementVector.x = 0;
		this.hitbox = new RectHitbox(this, 10, 10, width - 10, height - 10);
    }


    public void update(float tslf) {
		if(this.level.getPlayer().getKey()!=null&&pickedUp) {
			position.x=this.level.getPlayer().getX()-30;
			position.y=this.level.getPlayer().getY()-100;
		}
	}
	@Override
	public void draw(Graphics g) {
		g.drawImage(this.image, (int)position.x, (int)position.y, width,height, null);
		
		
		hitbox.draw(g);
	}
}

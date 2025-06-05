package gameengine.hitbox;

import java.awt.Color;
import java.awt.Graphics;

import gameengine.GameObject;
import gameengine.maths.Vector2D;

public class RectHitbox extends Hitbox{

	private GameObject object; //This is the object the hitbox is attached to;
	private Vector2D position; 
	private float offsetX;
	private float offsetY;
	private int width;
	private int height;

	public RectHitbox(GameObject object, float offsetX, float offsetY, int width, int height) {
		this.object = object;
		this.position = new Vector2D(object.getX() + offsetX, object.getY() + offsetY);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = (int) (width - offsetX);
		this.height = (int) (height - offsetY);
	}

	public RectHitbox(float x, float y, float offsetX, float offsetY, int width, int height) {
		this.object = null;
		this.position = new Vector2D(x + offsetX, y + offsetY);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = (int) (width - offsetX);
		this.height = (int) (height - offsetY);
	}

	@Override
	public void update() {
		if(object != null) {
			position.x = object.getX() + offsetX;
			position.y = object.getY() + offsetY;
		}
	}

	@Override
	public void draw(Graphics g) {
		if(!SHOW_HITBOXES) return;
		g.setColor(Color.GREEN);
		g.drawRect((int)position.x, (int)position.y, width, height);
	}

	public boolean isIntersecting(RectHitbox hitbox) {
		if(position.x < hitbox.getX() + hitbox.getWidth() && position.x + width > hitbox.getX() && position.y < hitbox.getY() + hitbox.getHeight() && position.y + height > hitbox.getY()) return true;
		return false;
	}
	
	//-------------------------------------------------------------Getters
	public float getOffsetX() {
		return offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public float getX() {
		return position.x;
	}

	public float getY() {
		return position.y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	// Pre: newPos is a vector inside the level and is not null.
	// Post: Sets new poition based on newPos.
	public void setPosition(Vector2D newPos){
		position = newPos;
	}
}

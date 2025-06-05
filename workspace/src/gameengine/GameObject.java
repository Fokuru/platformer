package gameengine;

import java.awt.Graphics;

import gameengine.maths.Vector2D;

public class GameObject {

	protected Vector2D position;
	protected int width;
	protected int height;
	
	public GameObject() {
		this.position = new Vector2D();
	}
	
	public GameObject(float x, float y) {
		this.position = new Vector2D(x, y);
	}
	
	public GameObject(float x, float y, int width, int height) {
		this.position = new Vector2D(x, y);
		this.width = width;
		this.height = height;
	}
	

	public void update (float tslf) {};
	
	public void draw (Graphics g) {};
	
	
	//------------------------------------Getters
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

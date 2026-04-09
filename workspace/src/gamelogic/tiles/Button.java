package gamelogic.tiles;

import java.awt.image.BufferedImage;

import gameengine.hitbox.RectHitbox;
import gamelogic.level.Level;

public class Button extends Tile {

    private boolean pressed;
	public Button(float x, float y, int size, BufferedImage image, Level level, boolean pressed) {
		super(x, y, size, image, false, level);
		this.pressed = pressed;
		this.hitbox = new RectHitbox(x*size , y*size, 0, 10, size, size);
	}
	
	public boolean getPressed() {
		return pressed;
	}
	
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
        if (pressed){
            //image=;
        } else {
            //image=;
        }
	}

}
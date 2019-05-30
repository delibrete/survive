import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class GameObject {

	protected double x, y;
	protected ID id;
	protected double velX, velY, acceleration, deceleration;
	protected boolean[] dirKeys = {false, false, false, false}; //0: up, 1: right, 2: down, 3: left

	public GameObject(int x, int y, ID id) {
		this.x = x;
		this.y = y;
		this.id = id;

		this.velX = 0;
		this.velY = 0;
		this.acceleration = 0.1d;
		this.deceleration = 0.1d;
	}

	public abstract void tick(double dt);
	public abstract void render(Graphics g);

	public abstract void keyPressed(KeyEvent e);
	public abstract void keyReleased(KeyEvent e);

	public abstract void mousePressed(MouseEvent e);
	public abstract void mouseReleased(MouseEvent e);

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public void setVelX(double velX) {
		this.velX = velX;
	}

	public void setVelY(double velY) {
		this.velY = velY;
	}

	public double getVelX() {
		return this.velX;
	}

	public double getVelY() {
		return this.velY;
	}

	public void setId(ID id){
		this.id = id;
	}

	public ID getId() {
		return this.id;
	}

	public void move(int vx, int vy) {
		this.velX = vx;
		this.velY = vy;
	}

	public void setKey(int n, boolean pressed) {
		dirKeys[n] = pressed;
	}
}
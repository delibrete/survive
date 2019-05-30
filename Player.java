import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Point;
import java.util.Random;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Player extends GameObject {

	public enum UpgradeEnum {
		Speed,
		Rate,
		Aim,
		Armour,
		Health,
		Damage,
		Bullets;
	}

 public class FireLocation {
  private float x, y, damage;

  public FireLocation(float x, float y, float damage) {
   this.x = x;
   this.y = y;
   this.damage = damage;
  }

  public float getX(){
   return this.x;
  }

  public float getY(){
   return this.y;
  }

  public float getDamage(){
	return this.damage;
   }
 }

 private double speed = 96;
 private int width = 16;

 private LinkedList<FireLocation> fireLocations = new LinkedList<FireLocation>();

 private BufferedImage img;
 private BufferedImage damaged;
 private BufferedImage current;

 private Random rand;

 private double damageTimer = 0;

 public int health = 100;

 public boolean dead = false;
 private boolean fireDown = false;
 private double fireTimer = 0;
 private float fireRate = 1;

 private int randAim = 16;

 private int bullets = 1;

 private double armour = 1;
 public int power = 20;

 private LinkedList<NotifyMessage> notifications = new LinkedList<NotifyMessage>();

	public class NotifyMessage {
		public int x, y;
		public String message;
		public Color color;
		public double timer = 0;

		private boolean finished = false;

		public NotifyMessage (int x, int y, String message, Color color) {
			this.x = x;
			this.y = y;
			this.message = message;
			this.color = color;
			this.timer = 0.5;
		}

		public void tick(double dt) {
			if (timer > 0) {
				timer -= dt;
			} else {
				finished = true;
			}
		}

		public void render(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g.setColor(Color.white);
			Font font = new Font("Serif", Font.PLAIN, 12);
			g2d.setFont(font);
			g2d.drawString(message, (int)x-16, ((int)y-(int)(16 * -(timer/1)))-16);
		}

		public boolean isFinished() {
			return finished;
		}
	}

 public Player(int x, int y, ID id) {
  super(x, y, id);

	try {
		img = ImageIO.read(new File("player1.png"));
		damaged = ImageIO.read(new File("zombie1_damage.png"));
		current = img;
	} catch (IOException e) {

	}

  rand = new Random();
 }

	public void tick(double dt) {
		health = (int)Game.clamp(health, 0, 100);

		for (int i = 0; i < notifications.size(); i++) {
			NotifyMessage notification = notifications.get(i);
			notification.tick(dt);

			if (notification.isFinished()) {
				notifications.remove(notification);
			}
		}

		if (damageTimer > 0) {
			damageTimer -= dt;
		}

		if (damageTimer <= 0) {
			damageTimer = 0;
			current = img;
		}

		if (dirKeys[0]) {
		velY = -speed;
		}
		if (dirKeys[1]) {
		velX = speed;
		}
		if (dirKeys[2]) {
		velY = speed;
		}
		if (dirKeys[3]) {
		velX = -speed;
		}

		velX *= 0.9;
		velY *= 0.9;

		x += velX * dt;
		y += velY * dt;

		x = Game.clamp(x, 32, Game.WIDTH-32);
		y = Game.clamp(y, 32, Game.HEIGHT-64);

		if (fireDown) {
			fireTimer += dt;

			if (fireTimer > fireRate) {
				int mouseX = MouseInfo.getPointerInfo().getLocation().x - (Game.window.frame.getLocationOnScreen().x+3);
				int mouseY = MouseInfo.getPointerInfo().getLocation().y - (Game.window.frame.getLocationOnScreen().y+26);
				Fire(mouseX, mouseY);

				fireTimer = 0;
			}
		}
	}

 public void render(Graphics g) {
	Graphics2D g2d = (Graphics2D) g;
	AffineTransform transform = g2d.getTransform();

	if (id == ID.Player) {
		g.setColor(Color.blue);
	} else if (id == ID.Player2) {
		g.setColor(Color.red);
	}

	// g.fillRect((int)x-(width/2), (int)y-(width/2), width, width);

	int mouseX = MouseInfo.getPointerInfo().getLocation().x - (Game.window.frame.getLocationOnScreen().x+3);
	int mouseY = MouseInfo.getPointerInfo().getLocation().y - (Game.window.frame.getLocationOnScreen().y+26);

	float[] dirs = Game.lookAt((float)this.x, (float)this.y, (float)(mouseX), (float)(mouseY));
	double rotationAngle = Math.atan2(dirs[1], dirs[0]) - Math.PI / 180;

	g.setColor(Color.white);

	if(!dead) {
		g2d.rotate(rotationAngle, x, y);
	}
	g2d.drawImage(current, null, (int)x-8, (int)y-16);
	// g2d.draw(new Line2D.Float((float)this.x, (float)this.y, (float)this.x+100, (float)this.y));

	g2d.setTransform(transform);

	g.setColor(Color.white);

	for (int i = 0; i < fireLocations.size(); i++) {
		FireLocation tempFireLocation = fireLocations.get(i);
		g2d.draw(new Line2D.Float((float)this.x, (float)this.y, (float)this.x+tempFireLocation.getX(), (float)this.y+tempFireLocation.getY()));
	}

	fireLocations.clear();

	for (int i = 0; i < notifications.size(); i++) {
		NotifyMessage notification = notifications.get(i);
		notification.render(g);
	}
 }

 public void Fire (double posX, double posY) {
	if(!dead) {
		for (int i = 0; i < bullets; i++){
			int aim = rand.nextInt(randAim + 1 + randAim) - randAim;
			// System.out.println(aim);
			posX += aim;
			posY += aim;

			float[] dirs = Game.lookAt((float)this.x, (float)this.y, (float)posX, (float)posY);
			float dirX = dirs[0];
			float dirY = dirs[1];
			float length = 1000;
			fireLocations.add(new FireLocation(dirX * length, dirY * length, power));
		}
	}
 }

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		switch (key) {
			case KeyEvent.VK_W:
				setKey(0, true);
				break;
			case KeyEvent.VK_D:
				setKey(1, true);
				break;
			case KeyEvent.VK_S:
				setKey(2, true);
				break;
			case KeyEvent.VK_A:
				setKey(3, true);
				break;
			default:
				break;
		}
	}

    public void keyReleased(KeyEvent e) {
  int key = e.getKeyCode();

  switch (key) {
   case KeyEvent.VK_W:
    setKey(0, false);
    break;
   case KeyEvent.VK_D:
    setKey(1, false);
    break;
   case KeyEvent.VK_S:
    setKey(2, false);
    break;
   case KeyEvent.VK_A:
    setKey(3, false);
                break;
            default:
                break;
        }
 }
 
	public void mousePressed(MouseEvent e) {
		int mouseButton = e.getButton();

		switch (mouseButton) {
			case MouseEvent.BUTTON1:
				Point p = e.getPoint();
				Fire(p.getX(), p.getY());
				fireDown = true;
				break;
			case MouseEvent.BUTTON2:
				break;
			case MouseEvent.BUTTON3:
				break;
			default:
				break;
		}
	}

    public void mouseReleased(MouseEvent e) {
		int mouseButton = e.getButton();

		switch (mouseButton) {
			case MouseEvent.BUTTON1:
				fireDown = false;
				fireTimer = 0;
				break;
			case MouseEvent.BUTTON2:
				break;
			case MouseEvent.BUTTON3:
				break;
			default:
				break;
		}
	}
	
	public LinkedList<FireLocation> getFireLocations() {
		return fireLocations;
	}

	public void Notify (double x, double y, String text, Color color) {
		notifications.add(new NotifyMessage((int)x, (int)y, text, color));
	}

	public void takeDamage (int damage) {
		if (damageTimer <= 0) {
			health -= damage/armour;
			damageTimer = 0.25;
			current = damaged;
		}
	}

	public void Upgrade(UpgradeEnum upgradeEnum, float value) {
		switch(upgradeEnum) {
			case Speed:
				speed = speed * 1.5;
				System.out.println("Speed is now: "+speed);
				break;
			case Rate:
				fireRate = fireRate/2.25f;
				System.out.println("Fire Rate is now: "+fireRate);
				break;
			case Aim:
				randAim = randAim/2;
				System.out.println("Aim is now: "+randAim);
				break;
			case Bullets:
				bullets++;
				System.out.println("Bullets is now: "+bullets);
				break;
			case Armour:
				armour += 4;
				System.out.println("Armour is now: "+armour);
				break;
			case Damage:
				power *= 2;
				System.out.println("Damage is now: "+power);
				break;
			default:
				break;
		}
	}
}
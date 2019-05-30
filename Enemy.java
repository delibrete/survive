import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.Math;
import java.util.Random;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Enemy extends GameObject {

	private double speed = 64;
	private int width = 16;

	private double dirX = 0, dirY = 0;

	private int health = 0;

	private Player player;

	private double damageTimer = 0;

	private double spawnTimer = 1;

	private BufferedImage img;
	private BufferedImage damaged;
	private BufferedImage current;

	public int reward = 0;
	public int damage = 0;
	private String[] imageNames = {"zombie1.png", "zombie2.png", "zombie3.png", "boss.png", "blade.png"};
	private String[] damagedImageNames = {"zombie1_damage.png", "zombie3_hurt.png", "boss_damaged.png"};

	//getting sloppy now, but I'm near the end
	private boolean spinning = false;
	private double spinDirX = -1;
	private double spinDirY = -1;
	private double spinTimer = 0;

	private double projectileTimer = 0;
	private double projectileShootTime = 1; //every x seconds, shoot something;

	private S_Game game;

	public Enemy(int x, int y, ID id, int wave, Player player, S_Game game) {
		super(x, y, id);

		double[] stats = getWaveStats(id, wave);

		this.health = (int)stats[0];
		this.reward = (int)stats[1];
		this.damage = (int)stats[2];
		this.speed = stats[4];
		this.player = player;
		this.width = (int)stats[6];
		this.spawnTimer = stats[7];

		try {
			img = ImageIO.read(new File(imageNames[(int)stats[3]]));
			damaged = ImageIO.read(new File(damagedImageNames[(int)stats[5]]));
			current = img;
		} catch (IOException e) {
	
		}

		this.game = game;
	}

	public double[] getWaveStats(ID id, int wave) {
		double[] stats = new double[8];
		stats[7] = 1; // spawnTimer
		switch (id) {
			case Enemy:
				stats[0] = 30*wave ; //health
				stats[1] = 3+(2*wave); //reward
				stats[2] = 10+(2*wave); //damage
				stats[3] = 0; //image
				stats[4] = 64; //speed
				stats[5] = 0; //damaged image
				stats[6] = 16; //width
				break;
			case EnemyFast:
				stats[0] = 30*wave ; //health
				stats[1] = 10+(2*wave); //reward
				stats[2] = 5+(2*wave); //damage
				stats[3] = 1; //image
				stats[4] = 192; //speed
				stats[5] = 0; //damaged image
				stats[6] = 16; //width
				stats[7] = 0.5; //spawn timer
				break;
			case EnemyHeavy:
				stats[0] = 100*wave ; //health
				stats[1] = 50*wave; //reward
				stats[2] = 10+(2*wave); //damage
				stats[3] = 2; //image
				stats[4] = 32; //speed
				stats[5] = 1; //damaged image
				stats[6] = 32; //width
				break;
			case EnemyBoss:
				stats[0] = 100*wave ; //health
				stats[1] = 100*wave; //reward
				stats[2] = 30+(2*wave); //damage
				stats[3] = 3; //image
				stats[4] = 32; //speed
				stats[5] = 2; //damaged image
				stats[6] = 96; //width
				break;
			case EnemySpinner:
				stats[0] = 1 ; //health
				stats[1] = 50*wave; //reward
				stats[2] = 5+(2*wave); //damage
				stats[3] = 4; //image
				stats[4] = 256; //speed
				stats[5] = 1; //damaged image
				stats[6] = 64; //width
				stats[7] = 0.5; //spawn timer
				break;
			case EnemyShooter:
				stats[0] = 50*wave ; //health
				stats[1] = 7+(2*wave); //reward
				stats[2] = 10+(2*wave); //damage
				stats[3] = 0; //image
				stats[4] = 48; //speed
				stats[5] = 0; //damaged image
				stats[6] = 16; //width
				break;
			default:
				break;
		}

		return stats;
	}

	public void tick(double dt) {
		if (damageTimer > 0) {
			damageTimer -= dt;
		}

		if (damageTimer <= 0) {
			damageTimer = 0;
			current = img;
		}

		//I know this is a bit lazy
		if (spawnTimer <= 0) {
			if (id != ID.EnemySpinner) {
				double distX = this.x - player.x;
				double distY = this.y - player.y;
				double distLength = Math.abs(Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2)));

				double dirX = -((distX)/distLength);
				double dirY = -((distY)/distLength);

				x += dirX * speed * dt;
				y += dirY * speed * dt;

				x = Game.clamp(x, 0, Game.WIDTH);
				y = Game.clamp(y, 0, Game.HEIGHT);

				if (id == ID.EnemyShooter || id == ID.EnemyBoss) {
					if (projectileTimer < projectileShootTime) {
						projectileTimer += dt;
					} else {
						System.out.println("PROJECTILE");
						game.addProjectile(new EnemyProjectile((int)x, (int)y, player, 256, (int)damage/3, "flame.png"));
						projectileTimer = 0;
					}
				}

			} else { //if they're a spinner
				if (spinning == false) {
					spinning = true;
					spinDirX = -1;
					spinDirY = -1;
				}

				if (x < 0) {
					spinDirX = 1;
					// x += 8;
				} else if (x > Game.WIDTH) {
					spinDirX = -1;
					// x -= 8;
				}

				if (y < 0) {
					spinDirY = 1;
					// y += 8;
				} else if (y > Game.HEIGHT) {
					spinDirY = -1;
					// y -= 8;
				}

				//Enemy Spinner code
				x += spinDirX * speed * dt;
				y += spinDirY * speed * dt;

				// x = Game.clamp(x, 0, Game.WIDTH);
				// y = Game.clamp(y, 0, Game.HEIGHT);

				spinTimer += dt;
			}
		} else { //if they're still entering the screen
			double distX = this.x - (Game.WIDTH/2);
			double distY = this.y - (Game.HEIGHT/2);
			double distLength = Math.abs(Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2)));

			double dirX = -((distX)/distLength);
			double dirY = -((distY)/distLength);

			x += dirX * speed * dt;
			y += dirY * speed * dt;

			x = Game.clamp(x, 0, Game.WIDTH);
			y = Game.clamp(y, 0, Game.HEIGHT);

			spawnTimer -= dt;
		}
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform transform = g2d.getTransform();
	
		// g.setColor(Color.yellow);

		if (damageTimer > 0) {
			// g.setColor(Color.red);
			// current.set(col, row, new Color(255, 0, 0));
		}
		
		// g.fillRect((int)x-(width/2), (int)y-(width/2), width, width);

		if (id == ID.EnemySpinner) {
			g2d.rotate(-(spinTimer * 8), x, y);
		} else {
			double playerX = this.player.getX();
			double playerY = this.player.getY();
	
			float[] dirs = Game.lookAt((float)this.x, (float)this.y, (float)(playerX), (float)(playerY));
			double rotationAngle = Math.atan2(dirs[1], dirs[0]) - Math.PI / 180;
			
			g2d.rotate(rotationAngle, x, y);
		}

		int offsetX = 8;
		int offsetY = 16;

		if (id == ID.EnemyHeavy) {
			offsetX = 32;
			offsetY = 32;
		} else if (id == ID.EnemySpinner) {
			offsetX = 32;
			offsetY = 32;
		} else if (id == ID.EnemyBoss) {
			offsetX = 48;
			offsetY = 48;
		}
		// g.drawRect((int)x-width/2,(int)y-width/2, width, width);
		g2d.drawImage(current, null, (int)x-offsetX, (int)y-offsetY);
		// g2d.draw(new Line2D.Float((float)this.x, (float)this.y, (float)this.x+100, (float)this.y));

		g2d.setTransform(transform);
	}

	public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
	}
	
	public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
	}
	
	public int getWidth(){
		return this.width;
	}

	public int getHealth() {
		return this.health;
	}

	public void takeDamage (int damage) {
		health -= damage;
		damageTimer = 0.05;
		current = damaged;
	}

	public void kill(){
		health = 0;
	}
}
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.lang.Math;
import java.io.File;
import java.io.IOException;


public class EnemyProjectile {
	public int x, y, speed;
	private BufferedImage img;
	public boolean removed = false;
	private Player player;
	private double aimTimer = 0.5;
	private double dirX, dirY;
	private float[] dirs;
	public int damage = 0;

	public EnemyProjectile(int x, int y, Player player, int speed, int damage, String image) {
		this.x = x;
		this.y = y;
		this.player = player;
		this.speed = speed;
		this.damage = damage;

		try {
			img = ImageIO.read(new File(image));
		} catch (IOException e) {
	
		}
	}

	public void tick(double dt) {
		if (aimTimer > 0) {
			double distX = this.x - player.x;
			double distY = this.y - player.y;
			double distLength = Math.abs(Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2)));

			dirX = -((distX)/distLength);
			dirY = -((distY)/distLength);

			aimTimer -= dt;
		}

		x += dirX * speed * dt;
		y += dirY * speed * dt;

		if (x < -16 ||
			x > Game.WIDTH + 16 ||
			y < -16 || 
			y > Game.HEIGHT + 16) {
				removed = true;
			}
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform transform = g2d.getTransform();
		if (aimTimer > 0) {
			double playerX = this.player.getX();
			double playerY = this.player.getY();
			dirs = Game.lookAt((float)this.x, (float)this.y, (float)(playerX), (float)(playerY));
		}

		double rotationAngle = Math.atan2(dirs[1], dirs[0]) - Math.PI / 180;
		g2d.rotate(rotationAngle, (int)x, (int)y);
		g2d.drawImage(img, null, (int)x-8, (int)y-8);
		g2d.setTransform(transform);
	}
}
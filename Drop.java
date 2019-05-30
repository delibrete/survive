import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.lang.Math;
import java.io.File;
import java.io.IOException;


public class Drop {
	public int x, y;
	private BufferedImage img;
	public boolean removed = false;
	private double aliveTimer = 15;

	public Drop(int x, int y, String image) {
		this.x = x;
		this.y = y;

		try {
			img = ImageIO.read(new File(image));
		} catch (IOException e) {
	
		}
	}

	public void tick(double dt) {
		aliveTimer -= dt;

		if (aliveTimer < 0) {
			removed = true;
		}
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(img, null, (int)x-8, (int)y-8);
	}
}
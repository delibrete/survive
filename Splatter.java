import java.awt.*;
import java.awt.Graphics;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.AlphaComposite;
import java.util.Random;

public class Splatter {
	private int x, y;
	private double maxTimeout = 60;
	private double timeout = 60;
	public boolean removed = false;
	private BufferedImage img;
	private double rotationAngle;
	private Random rand;

	public Splatter(int x, int y) {
		this.x = x;
		this.y = y;

		try {
            img = ImageIO.read(new File("splatter.png"));
        } catch (IOException e) {

		}
		rand = new Random();
		rotationAngle = rand.nextInt(359);
	}

	public void tick(double dt) {
		timeout -= dt;

		if (timeout < 0) {
			removed = true;
		}
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform transform = g2d.getTransform();
		double alpha = Game.clamp((double)timeout/maxTimeout, 0, maxTimeout);
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)alpha);
		g2d.setComposite(ac);
		g2d.rotate(Math.toRadians(rotationAngle), x, y);
		g2d.drawImage(img, null, x-16, y-16);

		ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f);
		g2d.setComposite(ac);
		g2d.setTransform(transform);
	}
}
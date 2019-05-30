import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.lang.Math;
import java.io.File;
import java.io.IOException;

public class S_Menu extends State {

    private BufferedImage img;

    public S_Menu() {
        super();

        try {
			img = ImageIO.read(new File("menu.png"));
		} catch (IOException e) {
	
		}
    }
    
    public void enter() {
        System.out.println("Entered the Menu state.");
    }

    public void tick(double dt) {
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(img, null, 0, 0);
    }

    public void exit() {
        System.out.println("Exited the Menu state.");
    }

    public String getName() {
        return "S_Menu";
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();


        switch (key) {
            case KeyEvent.VK_ENTER:
                StateManager.getInstance().changeState(new S_Game());
                break;
            default:
                break;
        }
    }
}
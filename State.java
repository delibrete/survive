import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

public abstract class State {
	LinkedList<GameObject> objects = new LinkedList<GameObject>();

	public State() {
	}

    public abstract void enter();
    public void tick(double dt) {
		for (int i = 0; i < objects.size(); i++) {
			GameObject tempObject = objects.get(i);
			tempObject.tick(dt);
		}
	}
    public void render(Graphics g) {
		for (int i = 0; i < objects.size(); i++) {
			GameObject tempObject = objects.get(i);
			tempObject.render(g);
		}
	}

    public abstract void exit();
	public abstract String getName();

	public void keyPressed(KeyEvent e){
		for (int i = 0; i < objects.size(); i++) {
			GameObject tempObject = objects.get(i);
			tempObject.keyPressed(e);
		}
	}

	public void keyReleased(KeyEvent e){
		for (int i = 0; i < objects.size(); i++) {
			GameObject tempObject = objects.get(i);
			tempObject.keyReleased(e);
		}
	}

	public void mousePressed(MouseEvent e) {
		for (int i = 0; i < objects.size(); i++) {
			GameObject tempObject = objects.get(i);
			tempObject.mousePressed(e);
		}
	}

	public void mouseReleased(MouseEvent e) {
		for (int i = 0; i < objects.size(); i++) {
			GameObject tempObject = objects.get(i);
			tempObject.mouseReleased(e);
		}
	}

	public void addObject (GameObject object) {
		this.objects.add(object);
	}

	public void removeObject (GameObject object) {
		this.objects.remove(object);
	}
}
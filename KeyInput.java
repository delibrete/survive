import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

	private Handler handler;

	public KeyInput (Handler handler) {
		this.handler = handler;
	}

	public void keyTyped(KeyEvent e) {
		int key = e.getKeyCode();
	}

	public void keyPressed(KeyEvent e) {
		StateManager.getInstance().getState().keyPressed(e);

		// int key = e.getKeyCode();

		// for (int i = 0; i < handler.object.size(); i++) {
		// 	GameObject tempObject = handler.object.get(i);
			
		// 	if (tempObject.getId() == ID.Player) {
		// 		switch (key) {
		// 			case KeyEvent.VK_W:
		// 				tempObject.setKey(0, true);
		// 				break;
		// 			case KeyEvent.VK_S:
		// 				tempObject.setKey(2, true);
		// 				break;
		// 			case KeyEvent.VK_A:
		// 				tempObject.setKey(3, true);
		// 				break;
		// 			case KeyEvent.VK_D:
		// 				tempObject.setKey(1, true);
		// 				break;
		// 			default:
		// 				break;
		// 		}
		// 	}
		// }
	}

	public void keyReleased(KeyEvent e) {
		StateManager.getInstance().getState().keyReleased(e);

		// int key = e.getKeyCode();

		// for (int i = 0; i < handler.object.size(); i++) {
		// 	GameObject tempObject = handler.object.get(i);
			
		// 	switch (key) {
		// 		case KeyEvent.VK_W:
		// 			tempObject.setKey(0, false);
		// 			break;
		// 		case KeyEvent.VK_S:
		// 			tempObject.setKey(2, false);
		// 			break;
		// 		case KeyEvent.VK_A:
		// 			tempObject.setKey(3, false);
		// 			break;
		// 		case KeyEvent.VK_D:
		// 			tempObject.setKey(1, false);
		// 			break;
		// 		default:
		// 			break;
		// 	}
		// }
	}
}
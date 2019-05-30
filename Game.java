import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Canvas;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;

import java.awt.geom.Line2D;
import java.awt.Rectangle;

public class Game extends Canvas implements Runnable{

  public static final int WIDTH = 640, HEIGHT = 508;
 public static final long ONE_LONG_SECOND = 1000000000;
 public static final double ONE_SECOND = 100;

 public double locX = 0;
 public double locY = 0;

 private Thread thread;
 private boolean running = false;

 private Handler handler;
 private HUD hud;

 public static Window window;

 public Game(){
  KeyListener keyListener = new GameKeyListener();
  addKeyListener(keyListener);
  MouseListener mouseListener = new GameMouseListener();
  addMouseListener(mouseListener);

  StateManager.getInstance().setState(new S_Menu());

  window = new Window(WIDTH, HEIGHT, "Game", this);

  // hud = new HUD();

  // handler.addObject(new Player(100, 100, ID.Player));
  // handler.addObject(new Player(400, 100, ID.Player2));
 }

 public synchronized void start(){
  thread = new Thread(this);
  thread.start();
  running = true;
 }

 public synchronized void stop(){
  try {
   thread.join();
   running = false;
  }catch (Exception e) {
   e.printStackTrace();
  }
 }

 //Loop code taken from: http://www.java-gaming.org/index.php?topic=24220.0
 public void run(){
  this.requestFocus();

  boolean gameRunning = true;
  long lastLoopTime = System.nanoTime();
  final int TARGET_FPS = 60;
  final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
  long lastFpsTime = 0;
  int fps = 0;

    // keep looping round til the game ends
  while (gameRunning) {
    // work out how long its been since the last update, this
    // will be used to calculate how far the entities should
    // move this loop
    long now = System.nanoTime();
    long updateLength = now - lastLoopTime;
    lastLoopTime = now;
    double delta = updateLength / ((double)OPTIMAL_TIME);

    // update the frame counter
    lastFpsTime += updateLength;
    fps++;
    
    // update our FPS counter if a second has passed since
    // we last recorded
    if (lastFpsTime >= 1000000000)
    {
      // System.out.println("(FPS: "+fps+")");
      lastFpsTime = 0;
      fps = 0;
    }
    
    // update the game logic
    tick(delta/100);
    
    // draw everyting
    render();
    
    // we want each frame to take 10 milliseconds, to do this
    // we've recorded when we started the frame. We add 10 milliseconds
    // to this and then factor in the current time to give 
    // us our final value to wait for
    // remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
    try {
      Thread.sleep( Math.abs((lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000));
    }catch (Exception e){
      System.out.println(e);
    };
  }
 }

 public static double clamp(double n, double min, double max) {
  if (n <= min) {
   return min;
  }
  if (n >= max) {
   return max;
  }
  return n;
 }

 public static float[] lookAt(float x1, float y1, float x2, float y2) {
  float distX = x1 - x2;
  float distY = y1 - y2;
  float distLength = (float)Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));

  float dirX = -((distX)/distLength);
  float dirY = -((distY)/distLength);

  float[] dirs = {dirX, dirY};

  return dirs;
 }

 private void tick(double dt){
  locX = this.getLocationOnScreen().x;
  locY = this.getLocationOnScreen().y;
  StateManager.getInstance().getState().tick(dt);
  // handler.tick();
  // hud.tick();
 }

 private void render(){
	  BufferStrategy bs = this.getBufferStrategy();
	  if (bs == null) {
	   this.createBufferStrategy(3);
	   return;
	  }

	Graphics g = bs.getDrawGraphics();

	g.setColor(Color.black);
	g.fillRect(0, 0, WIDTH, HEIGHT);

	StateManager.getInstance().getState().render(g);

	g.dispose();
	bs.show();
 }

 public static void main(String args[]) {
  new Game();
 }

  public static boolean LineBoxIntersection(float lx1, float ly1, float lx2, float ly2, float bx, float by, float bw) {
    Rectangle r1 = new Rectangle((int)bx, (int)by, (int)bw, (int)bw);
    Line2D l1 = new Line2D.Float(lx1, ly1, lx2, ly2);
    return l1.intersects(r1);
  }

  //this is axis aligned
  public static boolean BoxBoxIntersection(float bx1, float by1, float bw1, float bx2, float by2, float bw2) {
    Rectangle r1 = new Rectangle((int)bx1, (int)by1, (int)bw1, (int)bw1);
    Rectangle r2 = new Rectangle((int)bx2, (int)by2, (int)bw2, (int)bw2);
    return r1.intersects(r2);
  }

  public static boolean PointBoxIntersection(float lx, float ly, float bx, float by, float bwx, float bwy) {
    Line2D l1 = new Line2D.Float(lx, ly, lx, ly);
    Rectangle r1 = new Rectangle((int)bx, (int)by, (int)bwx, (int)bwy);
    return l1.intersects(r1);
  }

  public class GameKeyListener implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
      StateManager.getInstance().getState().keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
      StateManager.getInstance().getState().keyReleased(e);
    }
  }

  public class GameMouseListener implements MouseListener {
    @Override
    public void mousePressed(MouseEvent e) {
      StateManager.getInstance().getState().mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      StateManager.getInstance().getState().mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      // System.out.println(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }
  }
}
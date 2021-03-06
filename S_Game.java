import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
import java.util.Random;

public class S_Game extends State {

    public Player player;
    private double enemyTimer = 0;
    private double spawnTime = 1;
    private LinkedList<Enemy> enemies = new LinkedList<Enemy>();

    private BufferedImage img;
    private Random rand;

    private HUD hud;
    public int wave = 1;
    public int enemyCounter = 5;
    public int nextWaveAmount = 5;
    public int remainingEnemies = 5;

    private boolean gameOver = false;

    private LinkedList<Splatter> splatters = new LinkedList<Splatter>();
    private LinkedList<EnemyProjectile> projectiles = new LinkedList<EnemyProjectile>();
    private LinkedList<Drop> drops = new LinkedList<Drop>();

    private boolean completed = false;

    public S_Game() {
        super();

        player = new Player(Game.WIDTH/2, Game.HEIGHT/2, ID.Player);
        addObject(player);

        try {
            img = ImageIO.read(new File("background.png"));
        } catch (IOException e) {

        }

        rand = new Random();

        hud = new HUD(this);
    }
    
    public void enter() {
        System.out.println("Entered the Game state.");
    }

    public void tick(double dt) {
        if (!gameOver) {
            super.tick(dt);

            checkDrops();

            checkEnemyHit(player.getFireLocations());

            checkPlayerDamage();

            resolveEnemies();

            hud.tick(dt);

            enemyTimer += dt;

            if (enemyTimer >= spawnTime && remainingEnemies > 0) {
                int spawnX = 0;
                int spawnY = 0;
                double centerX = Game.WIDTH/2;
                double centerY = Game.HEIGHT/2;

                int n = rand.nextInt(4);

                ID enemyType;

                if (wave % 6 == 0) {
                    // printObjects();
                    Enemy spinner1 = new Enemy(Game.WIDTH/2, Game.HEIGHT/2, ID.EnemySpinner, wave, player, this);
                    spinner1.setY(-32);
                    addObject(spinner1);
                    enemies.add(spinner1);

                    Enemy spinner2 = new Enemy(Game.WIDTH/2, Game.HEIGHT/2, ID.EnemySpinner, wave, player, this);
                    spinner2.setY(Game.HEIGHT+32);
                    addObject(spinner2);
                    enemies.add(spinner2);

                    // Enemy spinner3 = new Enemy(Game.WIDTH/2, Game.HEIGHT/2, ID.EnemySpinner, wave, player, this);
                    // spinner3.setX(-32);
                    // addObject(spinner3);
                    // enemies.add(spinner3);

                    // Enemy spinner4 = new Enemy(Game.WIDTH/2, Game.HEIGHT/2, ID.EnemySpinner, wave, player, this);
                    // spinner4.setX(Game.WIDTH+32);
                    // addObject(spinner4);
                    // enemies.add(spinner4);

                    Enemy tempEnemy = new Enemy(Game.WIDTH/2, Game.HEIGHT/2, ID.EnemyBoss, wave, player, this);
                    tempEnemy.setX(-32);
                    addObject(tempEnemy);
                    enemies.add(tempEnemy);
                    // printObjects();
                } else {
                    if (remainingEnemies % 3 == 0 && remainingEnemies != nextWaveAmount) {
                        enemyType = ID.EnemyFast;
                    } else if (remainingEnemies % 5 == 0 && remainingEnemies != nextWaveAmount) {
                        enemyType = ID.EnemyShooter;
                    } else if (remainingEnemies % 7 == 0 && remainingEnemies != nextWaveAmount) {
                        enemyType = ID.EnemyHeavy;
                    } else {
                        enemyType = ID.Enemy;
                    }
    
                    Enemy tempEnemy = new Enemy(Game.WIDTH/2, Game.HEIGHT/2, enemyType, wave, player, this);
    
                    switch (n) {
                        case 0: //top
                            tempEnemy.setY(-32);
                            break;
                        case 1: //right
                            tempEnemy.setX(Game.WIDTH+32);
                            break;
                        case 2: //bottom
                            tempEnemy.setY(Game.HEIGHT+32);
                            break;
                        case 3: //left
                            tempEnemy.setX(-32);
                            break;
                        default:
                            break;
                    }
    
                    addObject(tempEnemy);
                    enemies.add(tempEnemy);
                }

                enemyTimer = 0;
                remainingEnemies--;
            }

            for (int i = 0; i < splatters.size(); i++) {
                Splatter tempSplatter = splatters.get(i);
    
                tempSplatter.tick(dt);

                if (tempSplatter.removed) {
                    splatters.remove(tempSplatter);
                }
            }

            for (int i = 0; i < drops.size(); i++) {
                Drop tempDrop = drops.get(i);
    
                tempDrop.tick(dt);
                if (tempDrop.removed) {
                    drops.remove(tempDrop);
                }
            }

            for (int i = 0; i < projectiles.size(); i++) {
                EnemyProjectile tempProjectile = projectiles.get(i);
    
                tempProjectile.tick(dt);

                if (tempProjectile.removed) {
                    projectiles.remove(tempProjectile);
                }
            }
        }
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        //render background
        g2d.drawImage(img, null, 0, 0);

        for (int i = 0; i < splatters.size(); i++) {
            Splatter tempSplatter = splatters.get(i);

            tempSplatter.render(g);
        }

        for (int i = 0; i < drops.size(); i++) {
            Drop tempDrop = drops.get(i);

            tempDrop.render(g);
        }

        for (int i = 0; i < projectiles.size(); i++) {
            EnemyProjectile tempProjectile = projectiles.get(i);

            tempProjectile.render(g);
        }

        super.render(g);

        hud.render(g);

        if (gameOver) {
            g.setColor(Color.white);
            Font font2 = new Font("Serif", Font.PLAIN, 72);
            g2d.setFont(font2);
            FontMetrics metrics = g.getFontMetrics(font2);
            String text = "GAME OVER";
            g2d.drawString(text, Game.WIDTH/2-(metrics.stringWidth(text)/2), 240);
        }

        if (completed) {
            g.setColor(Color.white);
            Font font2 = new Font("Serif", Font.PLAIN, 24);
            g2d.setFont(font2);
            FontMetrics metrics = g.getFontMetrics(font2);
            String text = "Game Complete!";
            g2d.drawString(text, Game.WIDTH/2-(metrics.stringWidth(text)/2), 128);
        }
    }

    public void exit() {
        System.out.println("Exited the Game state.");
    }

    public String getName() {
        return "S_Game";
    }

    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_E:
                hud.toggleUpgradeMenu();
                break;
            case KeyEvent.VK_F:
                hud.nextWaveTimer = 0;
                break;
            case KeyEvent.VK_K:
                removeEnemies();
                break;
            default:
                break;
        }
    }

    // public void keyReleased(KeyEvent e) {
    //     super.keyReleased(e);

    //     int key = e.getKeyCode();

 //  switch (key) {
    //         case KeyEvent.VK_F:
 //    hud.nextWaveTimer = 0;
 //    break;
 //   default:
 //    break;
 //  }
    // }

    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        hud.mousePressed(e);
    }

    public void checkEnemyHit(LinkedList<Player.FireLocation> fireLocations) {
        for (int i = 0; i < fireLocations.size(); i++) {
            Player.FireLocation tempFireLocation = fireLocations.get(i);

            for (int j = 0; j < enemies.size(); j++) {
                Enemy tempEnemy = enemies.get(j);

                if (tempEnemy.getId() != ID.EnemySpinner) {
                    float lx1 = (float)player.getX();
                    float ly1 = (float)player.getY();
                    float lx2 = (float)player.getX()+(float)tempFireLocation.getX();
                    float ly2 = (float)player.getY()+(float)tempFireLocation.getY();
                    float bw = (float)tempEnemy.getWidth();
                    float bx = (float)tempEnemy.getX()-(bw/2);
                    float by = (float)tempEnemy.getY()-(bw/2);

                    if (Game.LineBoxIntersection(lx1, ly1, lx2, ly2, bx, by, bw)) {
                        // System.out.println("hit!");
                        tempEnemy.takeDamage((int)tempFireLocation.getDamage());

                        //calculate knockback
                        float[] dirs = Game.lookAt((float)tempEnemy.getX(), (float)tempEnemy.getY(), (float)player.getX(), (float)player.getY());
                        double knockbackX = tempEnemy.getX()-(dirs[0]*10);
                        double knockbackY = tempEnemy.getY()-(dirs[1]*10);

                        System.out.println(tempEnemy.getId());
                        if (tempEnemy.getId() != ID.EnemyHeavy &&
                            tempEnemy.getId() != ID.EnemyBoss) {
                            tempEnemy.setX((int)knockbackX);
                            tempEnemy.setY((int)knockbackY);
                        }
                        
                    }
                }
            }
        }
    }

    public void checkDrops() {
        for (int j = 0; j < drops.size(); j++) {
            Drop tempDrop = drops.get(j);

            float dropWidth = 16;

            if (Game.BoxBoxIntersection((float)player.getX()-8, (float)player.getY()-8, 16, (float)tempDrop.x-(dropWidth/2), (float)tempDrop.y-(dropWidth/2), dropWidth)) {
                player.Notify(tempDrop.x, tempDrop.y, "+ 15HP", Color.white);
                player.health += 15;
                drops.remove(tempDrop);
            }
        }
    }

    public void checkPlayerDamage(){
        for (int j = 0; j < enemies.size(); j++) {
            Enemy tempEnemy = enemies.get(j);

            float enemyWidth = (float)tempEnemy.getWidth();

            if (Game.BoxBoxIntersection((float)player.getX()-8, (float)player.getY()-8, 16, (float)tempEnemy.getX()-(enemyWidth/2), (float)tempEnemy.getY()-(enemyWidth/2), enemyWidth)) {
                player.takeDamage(tempEnemy.damage);
            }
        }

        for (int j = 0; j < projectiles.size(); j++) {
            EnemyProjectile tempProjectile = projectiles.get(j);
            float projectileWidth = 16;

            if (Game.BoxBoxIntersection((float)player.getX()-8, (float)player.getY()-8, 16, (float)tempProjectile.x-8, (float)tempProjectile.y-8, projectileWidth)) {
                player.takeDamage(tempProjectile.damage);
                projectiles.remove(tempProjectile);
            }

            //lets resolve projectiles here too
            if (tempProjectile.removed) {
                projectiles.remove(tempProjectile);
            }
        }

        if (player.health <= 0) {
            gameOver = true;
            player.dead = true;
        }
    }

    public void resolveEnemies() {
        for (int j = 0; j < enemies.size(); j++) {
            Enemy tempEnemy = enemies.get(j);

            if (tempEnemy.getHealth() <= 0) {
                // System.out.println("BEFORE:");
                // printObjects();

                if (tempEnemy.getId() == ID.EnemyBoss) {
                    removeEnemies();
                    drops.add(new Drop((int)tempEnemy.getX(), (int)tempEnemy.getY(), "health.png"));
                }

                removeObject(tempEnemy);
                enemies.remove(tempEnemy);
                
                hud.cash += tempEnemy.reward;
                hud.nextWave--;
                player.Notify(tempEnemy.x, tempEnemy.y, "+ $"+tempEnemy.reward, Color.white);

                if (enemyCounter > 0) {
                    enemyCounter--;
                }

                splatters.add(new Splatter((int)tempEnemy.x, (int)tempEnemy.y));

                if (tempEnemy.getId() == ID.EnemyFast) {
                    drops.add(new Drop((int)tempEnemy.getX(), (int)tempEnemy.getY(), "health.png"));
                }

                // System.out.println("AFTER:");
                // printObjects();
            }
        }

        if (hud.nextWaveTimer <= 0 && hud.waveIntermission == true && enemyCounter <= 0) {
            wave++;

            if (wave > 30) {
                completed = true;
                hud.cash = 999999999;
                player.health = 100;
            }

            spawnTime = 1-((double)wave/15);
            System.out.println(spawnTime);
            
            if (wave % 6 == 0) {
                //BOSS TIME
                enemyCounter = 1;
                remainingEnemies = enemyCounter;
            } else {
                enemyCounter = nextWaveAmount;
                // if (wave % 5 == 0) {
                    nextWaveAmount += 1;
                    enemyCounter += 1;
                // }
                remainingEnemies = enemyCounter;
            }

            hud.waveIntermission = false;
        }

        if (enemyCounter <= 0 && hud.nextWaveTimer <= 0) {
            hud.nextWaveTimer = 5;
            hud.waveIntermission = true;
        }
    }

    public void Upgrade(Player.UpgradeEnum upgradeEnum, float value) {
        player.Upgrade(upgradeEnum, value);
    }

    public void removeEnemies() {
        for (int k = 0; k < enemies.size(); k++) {
            Enemy tempEnemy = enemies.get(k);

            tempEnemy.kill();
        }
    }

    public void printObjects() {    
        System.out.println("PRINTING OBJECTS");

        for (int k = 0; k < objects.size(); k++) {
            GameObject obj = objects.get(k);

            System.out.println(obj.id);
        }

        System.out.println("----------");
    }

    public void addProjectile(EnemyProjectile enemyProjectile) {
        this.projectiles.add(enemyProjectile);
    }
}
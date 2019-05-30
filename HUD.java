import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;
import java.util.LinkedList;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;

public class HUD {
	public static double HEALTH = 100;
	public int currentWave = 1;
	public int nextWave = 10;
	public int cash = 0;

	public S_Game game;

	private boolean showUpgrades = false;

	private LinkedList<UpgradeButton> upgradeButtons = new LinkedList<UpgradeButton>();

	public double nextWaveTimer = 0;
	public boolean waveIntermission = false;

	public enum UpgradeType {
		Multiply,
		Add,
		Divide
	}

	public class UpgradeButton {
		int x, y, initCost, cost, base, modifier, upgradeMax, upgradeLevel;
		String text;
		Player.UpgradeEnum upgradeEnum;
		UpgradeType upgradeType;
		S_Game game;
		private boolean highlighted = false;
		private HUD hud;

		public UpgradeButton(int x, int y, String text, Player.UpgradeEnum upgradeEnum, UpgradeType upgradeType, int base, int modifier, int upgradeMax, int cost, S_Game game, HUD hud) {
			this.x = x;
			this.y = y;
			this.text = text;
			this.upgradeEnum = upgradeEnum;
			this.upgradeType = upgradeType;
			this.base = base;
			this.modifier = modifier;
			this.upgradeMax = upgradeMax;
			this.initCost = cost;
			this.cost = cost;
			this.game = game;
			this.hud = hud;
		}

		public void render(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g.setColor(Color.gray);
			g.fillRect(x, y, 64, 40);
			g.setColor(Color.white);
			Font font = new Font("Serif", Font.PLAIN, 11);
			FontMetrics metrics = g.getFontMetrics(font);
			g2d.setFont(font);
			String buttonText;
			String costText;
			if (upgradeLevel != upgradeMax) {
				buttonText = text+" Lv. "+(upgradeLevel+1);
				costText = "$"+cost;
			} else {
				buttonText = text+" Max";
				costText = "";
			}
			g2d.drawString(buttonText, x+32-(metrics.stringWidth(buttonText)/2), y+16);
			g2d.drawString(costText, x+32-(metrics.stringWidth(costText)/2), y+32);
			if (highlighted) {
				g.setColor(Color.white);
			} else {
				g.setColor(Color.black);
			}
			g.drawRect(x, y, 64, 40);
		}

		public void Upgrade() {
			if (hud.cash >= cost){
				hud.cash -= cost;
				if (upgradeLevel < upgradeMax) {
					switch(upgradeType) {
						case Multiply:
							game.Upgrade(upgradeEnum, base*(modifier+upgradeLevel));
							break;
						case Divide:
							game.Upgrade(upgradeEnum, (float)((float)base/(float)(modifier+upgradeLevel)));
							break;
						case Add:
							game.Upgrade(upgradeEnum, upgradeLevel);
							break;
						default:
							break;
					}

					upgradeLevel++;
					cost = cost*3;
				}
			}
		}

		public void TestHighlight(int px, int py) {
			if (Game.PointBoxIntersection(px, py, x, y, 64, 40)) {
				highlighted = true;
			} else {
				highlighted = false;
			}
		}

		public void CheckClick(){
			if (highlighted) {
				Upgrade();
			}
		}
	}

	public class UpgradeOptions {


		public UpgradeOptions(){

		}
	}

	public HUD(S_Game game) {
		this.game = game;

		int windX = Game.WIDTH/4;
		int windY = (Game.HEIGHT/4)+32;
		upgradeButtons.add(new UpgradeButton(windX+32, windY+32, "Speed", Player.UpgradeEnum.Speed, UpgradeType.Add, 128, 2, 5, 50, game, this));
		upgradeButtons.add(new UpgradeButton(windX+128, windY+32, "Rate", Player.UpgradeEnum.Rate, UpgradeType.Add, 1, 1, 5, 25, game, this));
		upgradeButtons.add(new UpgradeButton(windX+224, windY+32, "Aim", Player.UpgradeEnum.Aim, UpgradeType.Add, 1, 1, 3, 100, game, this));
		upgradeButtons.add(new UpgradeButton(windX+32, windY+96, "Bullets", Player.UpgradeEnum.Bullets, UpgradeType.Add, 1, 1, 3, 1000, game, this));
		upgradeButtons.add(new UpgradeButton(windX+128, windY+96, "Armour", Player.UpgradeEnum.Armour, UpgradeType.Add, 1, 1, 3, 400, game, this));
		upgradeButtons.add(new UpgradeButton(windX+224, windY+96, "Damage", Player.UpgradeEnum.Damage, UpgradeType.Add, 1, 1, 4, 600, game, this));
	}

	public void tick(double dt) {
		// HEALTH--;
		// HEALTH = Game.clamp(HEALTH, 0, 100);

		int mouseX = MouseInfo.getPointerInfo().getLocation().x - (Game.window.frame.getLocationOnScreen().x+3);
		int mouseY = MouseInfo.getPointerInfo().getLocation().y - (Game.window.frame.getLocationOnScreen().y+26);

		checkButtonCollisions(mouseX, mouseY);

		if (nextWaveTimer > 0) {
			nextWaveTimer -= dt;
		} else {
			nextWaveTimer = 0;
			waveIntermission = false;
		}
	}

	public void render (Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		//Health
		g.setColor(Color.white);
		Font font = new Font("Serif", Font.PLAIN, 16);
  		g2d.setFont(font);
		g2d.drawString("Health", 16, 16);
		g.setColor(Color.gray);
		g.drawRect(16,24,200,16);
		g.setColor(Color.green);
		g.fillRect(16,24,(int) Game.clamp(game.player.health * 2, 0, 200),16);
		g.setColor(Color.white);
		g.drawRect(16,24,200,16);

		//Wave
		g.setColor(Color.white);
		Font font2 = new Font("Serif", Font.PLAIN, 24);
		g2d.setFont(font2);
		FontMetrics metrics = g.getFontMetrics(font2);
		String text = "Wave: "+game.wave;
		g2d.drawString(text, Game.WIDTH/2-(metrics.stringWidth(text)/2), 24);
		Font fontSmall = new Font("Serif", Font.PLAIN, 12);
		g2d.setFont(fontSmall);
		metrics = g.getFontMetrics(fontSmall);
		String text2 = "Next Wave: "+game.enemyCounter+" Enemies";
		g2d.drawString(text2, Game.WIDTH/2-(metrics.stringWidth(text2)/2), 40);

		//Money
		metrics = g.getFontMetrics(font);
		g2d.setFont(font);
		String text3 = "Cash: $"+cash;
		g2d.drawString(text3, Game.WIDTH-(metrics.stringWidth(text3))-16, 24);

		if (showUpgrades) {
			int windX = Game.WIDTH/4;
			int windY = Game.HEIGHT/4;
			int windWidth = Game.WIDTH/2;
			int windHeight = Game.HEIGHT/3;

			g.setColor(new Color(128, 192, 255, 128));
			g2d.fillRect(windX, windY+16, windWidth, windHeight+16);

			for (int i = 0; i < upgradeButtons.size(); i++) {
				UpgradeButton tempUpgradeButton = upgradeButtons.get(i);
	
				tempUpgradeButton.render(g);
			}

			g.setColor(Color.white);
			metrics = g.getFontMetrics(font);
			g2d.setFont(font);
			g2d.drawString("Upgrades", Game.WIDTH/2-(metrics.stringWidth("Upgrades")/2), windY+48);
		}

		if (nextWaveTimer > 0) {
			int colourVal = (int)Math.abs(255*Math.sin(nextWaveTimer*4));
			g.setColor(new Color(255, colourVal, colourVal));
			Font font3 = new Font("Serif", Font.PLAIN, 24);
			metrics = g.getFontMetrics(font3);
			g2d.setFont(font3);
			String waveText = "Next wave in: "+String.format("%.2f", nextWaveTimer);
			g2d.drawString(waveText, Game.WIDTH/2-(metrics.stringWidth(waveText)/2), 96);
			metrics = g.getFontMetrics(fontSmall);
			g2d.setFont(fontSmall);
			String startText = "(or press F to start)";
			g2d.drawString(startText, Game.WIDTH/2-(metrics.stringWidth(startText)/2), 96+16);
		}
	}

	public void toggleUpgradeMenu() {
		showUpgrades = !showUpgrades;
	}

	public void checkButtonCollisions(int x, int y) {
		for (int i = 0; i < upgradeButtons.size(); i++) {
			UpgradeButton tempUpgradeButton = upgradeButtons.get(i);

			tempUpgradeButton.TestHighlight(x, y);
		}
	}

	public void mousePressed(MouseEvent e) {
		if (showUpgrades) {
			for (int i = 0; i < upgradeButtons.size(); i++) {
				UpgradeButton tempUpgradeButton = upgradeButtons.get(i);

				tempUpgradeButton.CheckClick();
			}
		}
	}
}
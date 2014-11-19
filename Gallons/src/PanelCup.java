import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

/**
 * Swing Komponente fuer die graphische Darstellung der einzelnen Becher.
 */
public class PanelCup extends JPanel
{
	Simulation simulation;
	Cup cup;
	Player player;
	
	/** Global ausgeweahlter Becher **/
	public static Cup current;
	
	public PanelCup(final Simulation simulation, final Cup cup)
	{
		this.simulation = simulation;
		this.player = cup.getPlayer();
		this.cup = cup;
		//Zeichne neu
		this.updateUI();
		
		//Mouse Listener, reagiert falls eine Mauseingabe erfolgt.
		this.addMouseListener(new MouseListener() 
		{		
			@Override public void mouseReleased(MouseEvent e) {}
			@Override public void mouseClicked(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent e) {}
			@Override public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) 
			{
				PanelCup panel = (PanelCup)e.getComponent();
				//Falls noch kein Becher fuer die Interaktion ausgewaehlt wurde, nehme diesen.
				if(current == null) current = cup;
				else
				{
					//Andernfalls versuche den ausgeweahlten Becher in diesen hier zu fuellen,
					//bei Erfolg wirde der "reload" button aktiviert da eine Aenderung erfolgt ist.
					if(panel.cup.fill(current)) Main.reload.setEnabled(true);
					//Setze den ausgeweahlten Becher wieder zurueck auf "null".
					current = null;	
				}
				//Zeichne saemtliche PanelCups neu.
				e.getComponent().getParent().repaint();
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) 
	{
		//Veraendere den Hintergrund, 
		//gruen falls der momentan ausgeweahlte Becher dieser hier ist,
		//gelb falls die Simulation erfolgreich war,
		//rot falls der Spieler die id 0 traegt und blau falls nicht.
		if(current == this.cup) setBackground(Color.green);
		else if(simulation.state.isDone()) setBackground(Color.yellow);
		else setBackground(player.getId() == 0 ? Color.red : Color.blue);
		
		//Super callback, damit der Hintergrund auch gezeichnet wird
		super.paintComponent(g);
		//Zeichne den momentanen Fuellstand des Bechers und die Bechergroesse.
		g.drawString("Max: " + cup.getMaxAmount(), 5, 15);
		g.drawString("Cur: " + cup.getAmount(), 5, 30);
		
		//Fuellstand, relativ gesehen zum groessten Becher der Simulation.
		int height = getHeight() - 10;
		if(height < 0) return;
		int cupHeight = (int)(cup.getMaxAmount() / (float)simulation.biggest * height);
		int fillHeight = (int)(cup.getAmount() / (float)simulation.biggest * height);
		g.drawRect(getWidth() - 25, getHeight() - cupHeight - 5, 20, cupHeight);
		g.fillRect(getWidth() - 25, getHeight() - fillHeight - 5, 20, fillHeight);
	}
}

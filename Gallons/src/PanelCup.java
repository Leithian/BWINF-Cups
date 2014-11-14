import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class PanelCup extends JPanel
{
	Simulation simulation;
	Cup cup;
	Player player;
	
	public static Cup current;
	
	public PanelCup(final Simulation simulation, final Cup cup)
	{
		this.simulation = simulation;
		this.player = cup.getPlayer();
		this.cup = cup;
		this.setBackground(player.getId() == 0 ? Color.red : Color.blue);
		this.updateUI();
		
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
				if(current == null) current = cup;
				else
				{
					if(panel.cup.fill(current)) Main.reload.setEnabled(true);
					current = null;	
				}
				e.getComponent().getParent().repaint();
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) 
	{
		if(current == this.cup) setBackground(Color.green);
		else if(simulation.state.isDone()) setBackground(Color.yellow);
		else setBackground(player.getId() == 0 ? Color.red : Color.blue);
		
		super.paintComponent(g);
		g.drawString("Max: " + cup.getMaxAmount(), 5, 15);
		g.drawString("Cur: " + cup.getAmount(), 5, 30);
		
		int height = getHeight() - 10;
		if(height < 0) return;
		int cupHeight = (int)(cup.getMaxAmount() / (float)simulation.biggest * height);
		int fillHeight = (int)(cup.getAmount() / (float)simulation.biggest * height);
		g.drawRect(getWidth() - 25, getHeight() - cupHeight - 5, 20, cupHeight);
		g.fillRect(getWidth() - 25, getHeight() - fillHeight - 5, 20, fillHeight);
	}
}

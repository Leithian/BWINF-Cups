import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class PanelCup extends JPanel
{
	Simulation simulation;
	Simulation.Cup cup;
	Simulation.Player player;
	
	public static Simulation.Cup current;
	
	public PanelCup(final Simulation simulation, final Simulation.Cup cup)
	{
		this.simulation = simulation;
		this.player = cup.getPlayer();
		this.cup = cup;
		this.setBackground(player.getId() == 0 ? Color.red : Color.blue);
		this.updateUI();
		this.setPreferredSize(new Dimension(476 / simulation.getCups().size(), 191));
		
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
					panel.cup.fill(current);
					current = null;	
				}
				((JPanel)e.getComponent().getParent()).updateUI();
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) 
	{
		if(current == this.cup) setBackground(Color.green);
		else if(simulation.isDone()) setBackground(Color.yellow);
		else setBackground(player.getId() == 0 ? Color.red : Color.blue);
		
		super.paintComponent(g);
		g.drawString("Maximum: " + cup.getMaxAmount(), 5, 25);
		g.drawString("Current: " + cup.getAmount(), 5, 40);	
	}
}

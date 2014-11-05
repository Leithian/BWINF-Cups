import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

public class Main 
{
	public static JFrame frame;
	public static JPanel panel;
	public static JToolBar bar;
	public static JButton open, reload, run;
	
	public static Simulation simulation;
	
	public static void main(String[] args) 
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 250);
		frame.setTitle("Gallons");
		frame.setLocationRelativeTo(null);
		
		bar = new JToolBar();
		bar.setFloatable(false);
		open = new JButton("Open");
		reload = new JButton("Reload");
		reload.setEnabled(false);
		run = new JButton("Run");
		panel = new JPanel();
		
		open.addActionListener(new ActionListener() 
		{		
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				JFileChooser fchooser = new JFileChooser();
				fchooser.setFileFilter(new FileFilter() 
				{	
					@Override
					public String getDescription() 
					{
						return "please only Textfiles";
					}
					
					@Override
					public boolean accept(File f) 
					{
						return f.getName().endsWith("");
					}
				});
				fchooser.setDialogTitle("Load...");
				fchooser.setVisible(true);
				int response = fchooser.showOpenDialog(fchooser);
				
				if(response == JFileChooser.APPROVE_OPTION)
				{
					try {
						panel.removeAll();
						panel.setLayout(new GridBagLayout());
						simulation = new Simulation(fchooser.getSelectedFile());
						GridBagConstraints constr = new GridBagConstraints();
						ArrayList<Simulation.Cup> list = simulation.getCups();
						
						constr.weightx = 1;
						constr.weighty = 1;
						constr.insets = new Insets(5, 5, 5, 5);
						constr.fill = GridBagConstraints.BOTH;
						constr.gridheight = list.size();
						
						for(int i = 0; i < list.size(); i++)
						{
							Simulation.Cup cup = list.get(i);
							constr.gridy = i;
							panel.add(new PanelCup(simulation, cup), constr);
						}
						panel.updateUI();
					} catch (Exception e2) {
						e2.printStackTrace();
						Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(frame, e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		reload.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(simulation != null)
				{
					for(Simulation.Cup cup : simulation.getCups()) cup.reset();
					panel.repaint();
					reload.setEnabled(false);
				}
			}
		});
		
		bar.add(open);
		bar.add(reload);
		frame.add(bar, BorderLayout.NORTH);
		frame.add(panel, BorderLayout.CENTER);
		
		frame.setVisible(true);
	}
}

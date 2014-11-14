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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;

public class Main 
{
	public static JFrame frame;
	public static JPanel panel;
	public static JToolBar bar;
	public static JButton open, reload, run, eval;
	public static JSplitPane split;
	public static JTextArea area;
	public static JScrollPane scrollPane;
	
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
		
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setDividerLocation(150);
		
		bar = new JToolBar();
		bar.setFloatable(false);
		
		area = new JTextArea();
		area.setEditable(false);
		area.setLineWrap(true);
		
		open = new JButton("Open");
		reload = new JButton("Reload");
		eval = new JButton("Solve");
		eval.setEnabled(false);
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
						return ".txt / plaintext";
					}
					
					@Override
					public boolean accept(File f) 
					{
						return f.getName().endsWith(".txt");
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
						ArrayList<Cup> list = simulation.state.getCups();
						
						constr.weightx = 1;
						constr.weighty = 1;
						constr.insets = new Insets(5, 5, 5, 5);
						constr.fill = GridBagConstraints.BOTH;
						constr.gridheight = list.size();
						
						for(int i = 0; i < list.size(); i++)
						{
							Cup cup = list.get(i);
							constr.gridy = i;
							panel.add(new PanelCup(simulation, cup), constr);
						}
						
						panel.updateUI();
						eval.setEnabled(true);
						area.setText("");
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
					for(Cup cup : simulation.state.getCups()) cup.reset();
					panel.updateUI();
					reload.setEnabled(false);
				}
			}
		});
		
		eval.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(simulation == null) return;
				simulation.evaluate();
			}
		});
		
		bar.add(open);
		bar.add(reload);
		bar.add(eval);

		split.add(panel);
		split.add(scrollPane = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		
		frame.add(bar, BorderLayout.NORTH);
		frame.add(split, BorderLayout.CENTER);
		
		frame.setVisible(true);
	}
	
	public static void output(String s)
	{
		try {
			area.getDocument().insertString(area.getDocument().getLength(), s + "\n", null);
			JScrollBar vertical = scrollPane.getVerticalScrollBar();
			vertical.setValue(vertical.getMaximum());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}

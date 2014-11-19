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
	//Fields
	public static JFrame frame;
	public static JPanel panel;
	public static JToolBar bar;
	public static JButton open, reload, run, eval;
	public static JSplitPane split;
	public static JTextArea area;
	public static JScrollPane scrollPane;
	
	/** Momentan geladene Simulation **/
	public static Simulation simulation;
	
	public static void main(String[] args) 
	{
		try {
			//Um das ganze etwas besser aussehen zu lassen, die UI sollte sich dem
			//Betriebssystem anpassen.
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Neues Fenster
		frame = new JFrame();
		//Damit das Programm beendet wird falls das Fenster geschlossen wird.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 250);
		frame.setTitle("Gallons");
		frame.setLocationRelativeTo(null);
		
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setDividerLocation(150);
		
		//Menueleiste
		bar = new JToolBar();
		bar.setFloatable(false);
		
		//Output an den Benutzer
		area = new JTextArea();
		area.setEditable(false);
		area.setLineWrap(true);
		
		//Verschiedene Buttons
		open = new JButton("Open");
		reload = new JButton("Reload");
		eval = new JButton("Solve");
		
		//eval & reload sind am Anfang nicht freigegeben
		//da noch keine Simulation geladen wurde.
		eval.setEnabled(false);
		reload.setEnabled(false);
		run = new JButton("Run");
		//Simulationspanel
		panel = new JPanel();
		
		//Action Listener fuer "open".
		open.addActionListener(new ActionListener() 
		{		
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//Ein Explorerfenster um eine neue Simulationsdatei auszuwaehlen, 
				//standardmaessig das Benutzerverzeichnis.
				JFileChooser fchooser = new JFileChooser();
				//Filter damit nur Textdateien dateien akzeptiert werden.
				fchooser.setFileFilter(new FileFilter() 
				{	
					@Override
					public String getDescription() 
					{
						//Beschreibung des Dateityps.
						return ".txt / plaintext";
					}
					
					@Override
					public boolean accept(File f) 
					{
						//Wenn der Dateiname mit .txt endet ist es eine Textdatei und wird akzeptiert.
						return f.getName().endsWith(".txt");
					}
				});
				fchooser.setDialogTitle("Load...");
				fchooser.setVisible(true);
				//Zeige den Explorerdialog, mit der Option eine Datei auszuwaehlen.
				int response = fchooser.showOpenDialog(fchooser);
				
				//Falls die Dateiangabe akzeptiert wurde...
				if(response == JFileChooser.APPROVE_OPTION)
				{
					try {
						//Entferne seamtlichen Inhalt
						panel.removeAll();
						//Ein neues Layout
						panel.setLayout(new GridBagLayout());
						//Kreieren einer neuen Simulation basierend auf der ausgeawaehlten Datei.
						simulation = new Simulation(fchooser.getSelectedFile());
						GridBagConstraints constr = new GridBagConstraints();
						//Liste der Becher aus der Simulation.
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
							//Fuege fuer jeden Becher ein neuse PanelCup hinzu, fuer die visuelle Darstellung.
							panel.add(new PanelCup(simulation, cup), constr);
						}
						
						//Setze das Simulationspanel zurueck.
						panel.updateUI();
						//Nun kann auch die Simulation ausgefuehrt werden.
						eval.setEnabled(true);
						//Benutzerausgabe zuruecksetzen.
						area.setText("");
					} catch (Exception e2) {
						//Falls bei der Erstellung der Simulation eine Fehlermeldung zurueckgegeben wurde,
						//oeffne einen Dialog der den Fehler anzeigt.
						e2.printStackTrace();
						//BEEP!
						Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(frame, e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		//Action Listener fuer "reload".
		reload.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//null check, sollte eigentlich nicht passieren aber lieber auf Nummer sicher.
				if(simulation != null)
				{
					//Setze saemtliche Becher auf den Anfangszustand zurueck
					for(Cup cup : simulation.state.getCups()) cup.reset();
					//Setze das Simulationspanel zurueck.
					panel.updateUI();
					//Einmal reloaden reicht
					reload.setEnabled(false);
				}
			}
		});
		
		//Action Listener fuer "eval".
		eval.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(simulation == null) return;
				//Starte die Simulation
				simulation.evaluate();
			}
		});
		
		//Hinzufuegen den Swing Komponenten.
		bar.add(open);
		bar.add(reload);
		bar.add(eval);

		split.add(panel);
		//Eine Scrollpane fuer den Benutzeroutput.
		split.add(scrollPane = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		
		frame.add(bar, BorderLayout.NORTH);
		frame.add(split, BorderLayout.CENTER);
		
		//Fertig, das Fenster kann nun angezeigt werden.
		frame.setVisible(true);
	}
	
	/**
	 * Gibt eine Nachricht and den Benutzer aus, in der dafuer vorgesehenen "Konsole".
	 * @param s - Nachricht
	 */
	public static void output(String s)
	{
		try {
			//Fuege der Textarea eine neue Zeichenkette hinzu, inklusive Zeilenumbruch.
			area.getDocument().insertString(area.getDocument().getLength(), s + "\n", null);
			JScrollBar vertical = scrollPane.getVerticalScrollBar();
			//Scrolle bis zum Ende damit die Ausgabe auch angezeigt wird.
			vertical.setValue(vertical.getMaximum());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}

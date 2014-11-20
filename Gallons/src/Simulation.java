import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/** Simulation, simuliert... **/
public class Simulation 
{
	/** Momentaner Simulationsstand **/
	public SimulationState state;
	private int totalAmount;
	public int biggest;
	private SolverTree tree;
	private Thread thread;
	
	/** Kreiere eine Neue Simulation aus einer Simulationsdatei **/
	public Simulation(File file) throws SimulationException
	{
		try {
			//Reader
			BufferedReader reader = new BufferedReader(new FileReader(file));
			//Erste Zeile, gibt die Anzahl der Becher and die dem ersten Spieler zugeordnet werden.
			String l1 = reader.readLine();
			int amount1 = Integer.parseInt(l1);
			//Zweite Zeile, gibt die Anzahl der Becher und deren Groesse an.
			String l2 = reader.readLine();
			//Dritte Zeile, gibt die Momentane Fuellmenge der Becher an.
			String l3 = reader.readLine();
			//Explode!
			String[] s1 = l2.split(" ");
			String[] s2 = l3.split(" ");
			//Schliesse den Reader.
			reader.close();
			
			int total = s1.length;		
			if(s1.length != total || s2.length != total) //Error checking and message if number of Cups and number of declared amounts of fluid for Cups didn't match
				throw new SimulationException("Declared number of cups didn't match input!");
			if(total - amount1 < 1)	//Error checking and message if player2 has less than 1 cup (or player 1 has more cups than total declared)
				throw new SimulationException("Number of cups for player1 exceeded the boundery!");
			
			//Zwei neue Spieler
			Player player1 = new Player(0);
			Player player2 = new Player(1);
			
			//Fuer alle Becher...
			for(int i = 0; i < total; i++)
			{
				int max = Integer.parseInt(s1[i]);
				int cur = Integer.parseInt(s2[i]);
				//Falls der Becher groesser sein sollte als der momentan groesste, aendere diesen Wert.
				if(max > biggest) biggest = max;
				//Aendere den Gesamtfuellstand.
				totalAmount += cur;
				//Wenn die Bechernummer kleiner als die Anzahl der Becher des ersten Spielers ist, fuege den Becher dort hinzu,
				//andernfalls beim zweiten.
				if(i < amount1) player1.addCup(new Cup(cur, max, player1));
				else player2.addCup(new Cup(cur, max, player2));
			}
			
			if(totalAmount % 2 != 0) 
				throw new SimulationException("Total amount of cups has to be even!");	//Error checking and message if amount of fluid cannot be splitted
			
			//Erstellung des momentanen Simulationsstatus.
			this.state = new SimulationState(player1, player2);
		} catch (IOException e) {
			//Sollte nicht passieren.
			e.printStackTrace();
		} catch (SimulationException e2) {
			//Eine SimulationException wird weitergegeben.
			throw e2;
		} catch (Exception e3) {
			throw new SimulationException("Filetype invalid.");	//Error message if filetype is different than expected
		}
	}
	
	/**
	 * Versuche das Raetsel zu loesen.
	 */
	public void evaluate()
	{
		//Neuer SolverTree mit dem Momentanen Status
		tree = new SolverTree(state);
		//Versuche eine Loesung zu finden
		tree.evaluate();
		//Falls eine Loesung moeglich ist, gebe diese aus und starte den Playback,
		//andernfalls Fehlermeldung.
		if(tree.isPossible()) 
		{
			Main.output("Found a solution in " + tree.getPath().size() + " steps! " + state);
			start();
		}
		else Main.output("No solution found!");
	}
	
	/**
	 * Playback, veranschaulicht den Loesungsweg in mehreren Fuellschritten.
	 */
	public void start()
	{
		//Falls schon ein Thread gestartet wurde und dieser noch laeuft, return.
		if(thread != null && thread.isAlive()) return;
		//Neuer Thread, wollen ja nicht die event queue blockieren...
		thread = new Thread()
		{
			@Override
			public void run() 
			{
				//Falls eine Loesung in mehr als 0 Schritten erfolgt, aktiviere den Reload Button da sich etwas veraendern wird.
				if(tree.getPath().size() > 0) Main.reload.setEnabled(true);
				try {
					//Fuer seamtliche Fuelloperationen der Loesung...
					for(int i = 0; i < tree.getPath().size(); i++)
					{
						FillOperation fop = tree.getPath().get(i);
						//Fuehre die Fuelloperation durch.
						fop.apply(state);
						//Zeichne das Simulationspanel neu.
						Main.panel.repaint();
						//Gib aus welche fuelloperation durchgefuehrt wurde.
						Main.output((i + 1) + ": From " + fop.from + " into " + fop.to + " " + state);
						//Warte eine Sekunde
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.setDaemon(true);
		//Starte den Thread
		thread.start();
	}

	/**
	 * Der SolverTree findet eine Loesung falls eine solche existiert und speichert die schnellstmoegliche.
	 */
	public static class SolverTree
	{
		/** Liste saemtlicher Fuellkombinationen die bereits erreicht wurden **/
		private ArrayList<SimulationState> states = new ArrayList<SimulationState>();
		/** Queue an Nodes zur weiteren Bearbeitung **/
		private ArrayList<SolverNode> layer = new ArrayList<SolverNode>();
		/** Der Endzustand **/
		private SolverNode solution;
		/** Loesungsweg **/
		private ArrayList<FillOperation> path;
		/** Der Startzustand **/
		public SolverNode parent;
		
		/** Kreirt einen neuen SolverTree mit einem Startzustand **/
		public SolverTree(SimulationState state)
		{
			//Startnode.
			this.parent = new SolverNode(this, null, state);
			parent.isValid = true;
			states.add(state);
		}
		
		/** Gibt den Loesungsweg zurueck. **/
		public ArrayList<FillOperation> getPath()
		{
			return path;
		}

		@Override
		public String toString() 
		{
			return parent.toString();
		}
		
		/** Versuche diesen SolverTree zu loesen **/
		public void evaluate()
		{
			//Falls der Startzustand bereits die Loesung ist muss nichts passieren.
			if(parent.state.isDone()) 
			{
				//Leerer Loesungsweg
				path = new ArrayList<FillOperation>();
				solution = parent;
				return;
			}
			//Ansonsten wird an die Startnode weitergegeben.
			parent.populate();
			goDeeper();
			
			//Falls eine Loesung gefunden wurde...
			if(isPossible())
			{
				//Fuelle den Loesungsweg
				path = new ArrayList<FillOperation>();
				SolverNode node = solution;
				//So lange wie kein parent mehr existiert...
				while(node.parent != null)
				{
					//Einfuegen der Fuelloperation an Position 0, da die Hierarchie von unten nach oben verlaeuft.
					path.add(0, node.operation);
					//Eine Ebene nach oben.
					node = node.parent;
				}
			}	
		}
		
		/** Arbeite den Layer ab, fuer jede Node wird {@link SolverNode#populate()} ausgefuehrt **/
		public void goDeeper()
		{
			@SuppressWarnings("unchecked")
			ArrayList<SolverNode> tmp = (ArrayList<SolverNode>)layer.clone();
			layer.clear();
			for(SolverNode node : tmp)
				node.populate();
			//Wenn der Layer nicht leer ist, muss er auch bearbeitet werden
			if(layer.size() > 0) goDeeper();
		}
		
		/**
		 * Gibt zurueck ob eine Loesung gefunden wurde.
		 * @return isPossible
		 */
		public boolean isPossible()
		{
			return solution != null;
		}
	}
	
	/** Eine SolverNode, ein Baumabschnitt der Mehrere children und einen parent besitzt **/
	public static class SolverNode
	{
		/** Der momentane Simulationsstatus **/
		private SimulationState state;

		/** Der dazugehoerige {@link SolverTree} **/
		private SolverTree tree;
		/** Ob der momentane Zustand schonmal vorkam **/
		private boolean isValid;
		/** Fuelloperation um hierher zu gelangen **/
		private FillOperation operation;
		/** Parent node **/
		private SolverNode parent;

		/** Kreiert eine neue SolverNode mit einem SolverTree, einer anderen SolverNode als parent und dem momentanen Simulationsstatus **/
		public SolverNode(SolverTree tree, SolverNode parent, SimulationState state)
		{
			this.tree = tree;
			this.state = state;
			this.parent = parent;
		}
		
		/** Fuehrt saemtliche Kombinationen durch und fuegt sie dem SolverTree Layer hinzu falls die Node gueltig ist **/
		public void populate()
		{
			//Falls dieser Zustand schonmal vorkam muss nichts unternommen werden
			if(!isValid) return;
			//Ansonsten kommt er nun auf jeden Fall vor.
			tree.states.add(this.state);
			if(tree.solution != null) return;
			
			//Fuelle jeden Becher in jeden Becher
			for(int i = 0; i < state.getCups().size(); i++)
			{
				for(int j = 0; j < state.getCups().size(); j++)
				{
					//Na, nicht den selben bitte.
					if(i == j) continue;
					//Klone den Zustand dieser Node.
					SimulationState state = this.state.clone();
					//Die zwei Becher
					Cup cup1 = state.getCups().get(i);
					Cup cup2 = state.getCups().get(j);
					//Falls beide keinen Inhalt haben passiert sowieso nichts, also weiter.
					if(cup1.getAmount() == 0 && cup2.getAmount() == 0) continue;
					
					//Ansonsten fuelle nun Becher 2 in Becher 1
					if(cup1.fill(cup2))
					{	
						//Es ist ein neuer Zustand entstanden, also her mit einer neuen Node.
						SolverNode node = new SolverNode(tree, this, state);
						//Fuelloperation, welcher Becher in welchen gefuellt wurde.
						node.operation = new FillOperation(j, i);
						//Fuege die Node dem momentanen Layer hinzu.
						tree.layer.add(node);
						
						//Falls dieser Zustand die Loesung ist, setze die Loesung im SimulationTree
						if(state.isDone() && !tree.isPossible())
							tree.solution = node;
						//Falls dieser Zustand noch nicht vorkam ist es eine gueltige Operation
						//(Brute force ist zwar langsam aber so kann man dann doch einiges einsparen,
						//ausserdem wuerde das hier nie aufhoeren falls keine Loesung moeglich ist)
						if(!tree.states.contains(node.state)) 
						{
							node.isValid = true;
							//Dieser Zustand existiert nun, also fuege ihn der Liste hinzu.
							tree.states.add(state);
						}
					}
				}
			}
		}
	}
	
	/** SimulationState enthealt beide Spieler und deren Becherkombinationen **/
	public static class SimulationState implements Cloneable
	{
		public Player player1, player2;
		
		public SimulationState(Player player1, Player player2)
		{
			this.player1 = player1.clone();
			this.player2 = player2.clone();
		}
		
		/**
		 * Gibt eine Liste saemtliche Becher zurueck.
		 * @return ArrayList<Cup>
		 */
		public ArrayList<Cup> getCups()
		{
			ArrayList<Cup> list = new ArrayList<Cup>();
			list.addAll(player1.cups);
			list.addAll(player2.cups);
			return list;
		}
		
		/**
		 * Gebe zurueck falls der Zustand der Endzustand ist, d. h. beide Spieler haben gleich viel.
		 * @return
		 */
		public boolean isDone()
		{
			return player1.getCurrentAmount() == player2.getCurrentAmount();
		}
		
		@Override
		protected SimulationState clone()
		{
			return new SimulationState(player1.clone(), player2.clone());
		}

		@Override
		public boolean equals(Object obj) 
		{
			if(obj instanceof SimulationState)
			{
				SimulationState state = (SimulationState)obj;
				for(int i = 0; i < getCups().size(); i++)
					if(getCups().get(i).getAmount() != state.getCups().get(i).getAmount()) 
						return false;
				return true;
			}
			return false;
		}
		
		@Override
		public String toString() 
		{
			String s = "(";
			for(Cup c : player1.cups) 
				s += c.getAmount() + " ";
			s += "|";
			for(Cup c : player2.cups) 
				s += " " + c.getAmount();
			s += ")";
			return s;
		}
	}
	
	/** Eine Fuelloperation, von Becher a nach Becher b. **/
	public static class FillOperation
	{
		public int from, to;
		public FillOperation(int from, int to)
		{
			this.from = from;
			this.to = to;
		}
		
		@Override
		public String toString() 
		{
			return "(" + from + " -> " + to + ")";
		}
		
		/** Fuehrt diese Operation durch **/
		public void apply(SimulationState state)
		{
			state.getCups().get(to).fill(state.getCups().get(from));
		}
	}
}

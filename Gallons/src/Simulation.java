import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Simulation 
{
	public SimulationState state;
	private int totalAmount;
	public int biggest;
	private SolverTree tree;
	private Thread thread;
	
	public Simulation(File file) throws SimulationException
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String l1 = reader.readLine();
			int amount1 = Integer.parseInt(l1);
			String l2 = reader.readLine();
			String l3 = reader.readLine();
			String[] s1 = l2.split(" ");
			String[] s2 = l3.split(" ");
			reader.close();
			
			if(totalAmount % 2 != 0) throw new SimulationException("Total amount of cups has to be even!");	//Error checking and message if amount of fluid cannot be splitted
			
			int total = s1.length;		
			if(s1.length != total || s2.length != total) //Error checking and message if number of Cups and number of declared amounts of fluid for Cups didn't match
				throw new SimulationException("Declared number of cups didn't match input!");
			if(total - amount1 < 1)	//Error checking and message if player2 has less than 1 cup (or player 1 has more cups than total declared)
				throw new SimulationException("Number of cups for player1 exceeded the boundery!");
			
			Player player1 = new Player(0);
			Player player2 = new Player(1);
			
			for(int i = 0; i < total; i++)
			{
				int max = Integer.parseInt(s1[i]);
				int cur = Integer.parseInt(s2[i]);
				if(max > biggest) biggest = max;
				totalAmount += cur;
				if(i < amount1) player1.addCup(new Cup(cur, max, player1));
				else player2.addCup(new Cup(cur, max, player2));
			}
			this.state = new SimulationState(player1, player2);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SimulationException e2) {
			throw e2;
		} catch (Exception e3) {
			throw new SimulationException("Filetype invalid.");	//Error message if filetype is different than expected
		}
	}
	
	public void evaluate()
	{
		tree = new SolverTree(state);
		tree.evaluate();
		if(tree.isPossible()) 
		{
			Main.output("Found a solution in " + tree.getPath().size() + " steps! " + state);
			start();
		}
		else Main.output("No solution found!");
	}
	
	public void start()
	{
		if(thread != null && thread.isAlive()) return;
		thread = new Thread()
		{
			@Override
			public void run() 
			{
				if(tree.getPath().size() > 0) Main.reload.setEnabled(true);
				try {
					for(int i = 0; i < tree.getPath().size(); i++)
					{
						FillOperation fop = tree.getPath().get(i);
						fop.apply(state);
						Main.panel.repaint();
						Main.output((i + 1) + ": From " + fop.from + " into " + fop.to + " " + state);
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	public static class SolverTree
	{
		private ArrayList<SimulationState> states = new ArrayList<SimulationState>();
		private SolverNode solution;
		private ArrayList<FillOperation> path;
		public SolverNode parent;
		
		public SolverTree(SimulationState state)
		{
			this.parent = new SolverNode(this, null, state);
			parent.isValid = true;
			states.add(state);
		}
		
		public ArrayList<FillOperation> getPath()
		{
			return path;
		}

		@Override
		public String toString() 
		{
			return parent.toString();
		}
		
		public void evaluate()
		{
			if(parent.state.isDone()) 
			{
				path = new ArrayList<FillOperation>();
				solution = parent;
				return;
			}
			parent.evaluate();
			if(isPossible())
			{
				path = new ArrayList<FillOperation>();
				SolverNode node = solution;
				while(node.parent != null)
				{
					path.add(0, node.operation);
					node = node.parent;
				}
			}	
		}
		
		public boolean isPossible()
		{
			return solution != null;
		}
	}
	
	public static class SolverNode
	{
		private SimulationState state;
		private ArrayList<SolverNode> sub = new ArrayList<SolverNode>();
		private SolverTree tree;
		private boolean isValid;
		private FillOperation operation;
		private SolverNode parent;
		
		public SolverNode(SolverTree tree, SolverNode parent, SimulationState state)
		{
			this.tree = tree;
			this.state = state;
			this.parent = parent;
		}
		
		public void evaluate()
		{
			if(!isValid) return;
			tree.states.add(this.state);
			
			for(int i = 0; i < state.getCups().size(); i++)
			{
				for(int j = 0; j < state.getCups().size(); j++)
				{
					if(i == j) continue;
					SimulationState state = this.state.clone();
					Cup cup1 = state.getCups().get(i);
					Cup cup2 = state.getCups().get(j);
					if(cup1.getAmount() == 0 && cup2.getAmount() == 0) continue;
					
					if(cup1.fill(cup2))
					{	
						SolverNode node = new SolverNode(tree, this, state);
						node.operation = new FillOperation(j, i);
						sub.add(node);
						if(state.isDone() && !tree.isPossible())
							tree.solution = node;
						if(!tree.states.contains(node.state)) 
						{
							node.isValid = true;
							tree.states.add(state);
						}
					}
				}
			}
			
			for(SolverNode node : sub)
			{
				node.evaluate();
				if(tree.isPossible()) return;
			}
		}
	}
	
	public static class SimulationState implements Cloneable
	{
		public Player player1, player2;
		
		public SimulationState(Player player1, Player player2)
		{
			this.player1 = player1.clone();
			this.player2 = player2.clone();
		}
		
		public ArrayList<Cup> getCups()
		{
			ArrayList<Cup> list = new ArrayList<Cup>();
			list.addAll(player1.cups);
			list.addAll(player2.cups);
			return list;
		}
		
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
		
		public void apply(SimulationState state)
		{
			state.getCups().get(to).fill(state.getCups().get(from));
		}
	}
}

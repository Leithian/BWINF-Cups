import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Simulation 
{
	public Player player1 = new Player(0);
	public Player player2 = new Player(1);
	private int totalAmount;
	
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
			
			if(totalAmount % 2 != 0) throw new SimulationException("Total amount of cups has to be even!");			//Error checking and message if amount of fluid cannot be splitted
			
			int total = s1.length;		
			if(s1.length != total || s2.length != total)			//Error checking and message if number of Cups and number of declared amounts of fluid for Cups didn't match
				throw new SimulationException("Declared number of cups didn't match input!");
			if(total - amount1 < 1)									//Error checking and message if player2 has less than 1 cup (or player 1 has more cups than total declared)
				throw new SimulationException("Number of cups for player1 exceeded the boundery!");
			
			for(int i = 0; i < total; i++)
			{
				int max = Integer.parseInt(s1[i]);
				int cur = Integer.parseInt(s2[i]);
				totalAmount += cur;
				if(i < amount1) player1.addCup(new Cup(cur, max, player1));
				else player2.addCup(new Cup(cur, max, player2));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SimulationException e2) {
			throw e2;
		} catch (Exception e3) {
			throw new SimulationException("Filetype invalid.");		//Error message if filetype is different than expected
		}
	}
	
	public void start()
	{
		Thread thread = new Thread()
		{
			@Override
			public void run()
			{		
				try {					
					while(isDone())
					{
						
						Thread.sleep(2000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	public boolean isDone()
	{
		return player1.getCurrentAmount() == player2.getCurrentAmount();
	}
	
	public ArrayList<Cup> getCups()
	{
		ArrayList<Cup> list = new ArrayList<Cup>();
		list.addAll(player1.cups);
		list.addAll(player2.cups);
		return list;
	}
	
	public static class Cup 
	{
		private int max, cur, def;
		private Player player;
		
		public Cup(int cur, int max, Player player)
		{
			if(cur > max) throw new SimulationException("You can not obey the law of physics!");			//Error checking and message if a cup holds more fluid than it can handle
			this.def = cur;
			this.cur = cur;
			this.max = max;
			this.player = player;
		}
		
		public int getAmount()
		{
			return cur;
		}
		
		public int getMaxAmount()
		{
			return max;
		}
		
		public Player getPlayer()
		{
			return player;
		}
		
		public boolean fill(Cup cup)
		{
			if(cup == this) return false;
			int tcur = this.cur;
			int ncur = this.cur + cup.cur;
			int over = ncur - this.max;
			this.cur = ncur > this.max ? this.max : ncur;
			cup.cur = over > 0 ? over : 0;
			return tcur != this.cur;
		}
		
		public void reset()
		{
			this.cur = def;
		}
	}
	
	public static class Player 
	{
		private int id;
		private ArrayList<Cup> cups = new ArrayList<Cup>();
		
		public Player(int id)
		{
			this.id = id;
		}
		
		public int getId()
		{
			return id;
		}
		
		public void addCup(Cup cup)
		{
			cups.add(cup);
		}
		
		public int getCurrentAmount()
		{
			int a = 0;
			for(Cup cup : cups) a += cup.getAmount();
			return a;
		}
	}
	
	public static class SimulationException extends RuntimeException
	{
		public SimulationException(String exc)
		{
			super(exc);
		}
	}
}

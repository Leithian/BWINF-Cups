import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Simulation 
{
	public Player player1 = new Player();
	public Player player2 = new Player();
	
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
			int total = s1.length;
			
			for(int i = 0; i < amount1; i++)
			{
				int max = Integer.parseInt(s1[i]);
				int cur = Integer.parseInt(s2[i]);
				player1.addCup(new Cup(cur, max));
			}
			for(int i = amount1; i < total; i++)
			{
				int max = Integer.parseInt(s1[i]);
				int cur = Integer.parseInt(s2[i]);
				player2.addCup(new Cup(cur, max));
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e2) {
			throw new SimulationException("Filetype invalid.");
		}
	}
	
	public boolean isDone()
	{
		return player1.getCurrentAmount() == player2.getCurrentAmount();
	}
	
	public static class Cup 
	{
		private int max, cur;
		
		public Cup(int cur, int max)
		{
			this.cur = cur;
			this.max = max;
		}
		
		public int getAmount()
		{
			return cur;
		}
		
		public int getMaxAmount()
		{
			return max;
		}
		
		public void fill(Cup cup)
		{
			int ncur = this.cur + cup.cur;
			int over = ncur - this.max;
			this.cur = ncur > this.max ? this.max : ncur;
			cup.cur = over;
		}
	}
	
	public class Player 
	{
		private ArrayList<Cup> cups = new ArrayList<Cup>();
		
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

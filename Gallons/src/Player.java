import java.util.ArrayList;

public class Player implements Cloneable
{
	private int id;
	ArrayList<Cup> cups = new ArrayList<Cup>();
	
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

	@Override
	protected Player clone()
	{
		Player clone = new Player(id);
		for(Cup c : cups)
			clone.cups.add(c.clone());
		return clone;
	}
}
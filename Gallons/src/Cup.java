public class Cup implements Cloneable
{
	private int max, cur, def;
	private Player player;
	
	public Cup(int cur, int max, Player player)
	{
		if(cur > max) throw new SimulationException("You can not obey the law of physics!"); //Error checking and message if a cup holds more fluid than it can handle
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
	
	@Override
	protected Cup clone()
	{
		return new Cup(cur, max, player);
	}
}
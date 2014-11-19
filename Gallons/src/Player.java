import java.util.ArrayList;

/** Ein "Spieler", Holder fuer die Becher **/
public class Player implements Cloneable
{
	private int id;
	ArrayList<Cup> cups = new ArrayList<Cup>();
	
	/**
	 * Erzeuge einen neuen Spieler mit besagter id.
	 * @param id
	 */
	public Player(int id)
	{
		this.id = id;
	}
	
	/**
	 * Gibt die Spieler-id zurueck.
	 * @return id
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Fuege diesem Spieler einen neuen Becher hinzu.
	 * @param cup - neuer Becher
	 */
	public void addCup(Cup cup)
	{
		cups.add(cup);
	}
	
	/**
	 * Gesamtfuellmenge aller Becher dieses Spielers.
	 * @return
	 */
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
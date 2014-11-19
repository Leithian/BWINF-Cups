/** Ein Becher **/
public class Cup implements Cloneable
{
	private int max, cur, def;
	private Player player;
	
	/**
	 * Constructor, eine neue Becherinstanz fuer eine {@link Simulation}
	 * @param cur - Momentane Füllmenge
	 * @param max - Bechergroesse
	 * @param player - Der Spieler dem dieser Becher zugewiesen ist
	 */
	public Cup(int cur, int max, Player player)
	{
		//Fehlermeldung falls der Becher mehr Inhalt besitzt als er halten kann.
		if(cur > max) throw new SimulationException("You can not obey the law of physics!");
		//Fehlermeldung bei einer negativen Fuellmenge.
		if(cur < 0) throw new SimulationException("Yeah, that was actually pretty funny."); 
		this.def = cur;
		this.cur = cur;
		this.max = max;
		this.player = player;
	}
	
	/**
	 * Momentane Fuellmenge
	 * @return {@link #cur}
	 */
	public int getAmount()
	{
		return cur;
	}
	
	/**
	 * Bechergroesse
	 * @return {@link #max}
	 */
	public int getMaxAmount()
	{
		return max;
	}
	
	/**
	 * Der Spieler dem dieser Becher zugewiesen ist
	 * @return {@link #player}
	 */
	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Fuellt den Inhalt eines anderen Bechers in diesen Becher.
	 * @param cup - Becher zum fuellen
	 * @return {@code true} falls die Fuelloperation erforgreich war, 
	 * d. h. wenn sich and der Fuellmenge etwas geaendert hat.
	 */
	public boolean fill(Cup cup)
	{
		//Wenn der uebergebene Becher der selbe ist wie dieser hier aendert sich nichts.
		if(cup == this) return false;
		//Momentane Fuellmenge
		int tcur = this.cur;
		//Theoretische neue Fuellmenge, Fuellmenge plus Fuellmenge des anderen Bechers.
		int ncur = this.cur + cup.cur;
		//Menge die uebrig bleibt wenn nicht alles aus dem anderen Becher umgefuellt 
		//werden kann da die maximale Kapazitaet erreicht wird.
		int over = ncur - this.max;
		//Wenn mehr Inhalt vorhanden ist als von diesem Becher gehalten werden kann, setze
		//die momentane Fuellmenge auf die maximale Kapazitaet.
		this.cur = ncur > this.max ? this.max : ncur;
		//Falls ein positiver Betrag uebrig bleibt, setze die Fuellmenge des anderen Bechers
		//auf diesen betrag.
		cup.cur = over > 0 ? over : 0;
		//Wenn sich die Fuellmenge dieses Bechers veraendert hat, gebe true zurueck.
		return tcur != this.cur;
	}
	
	/**
	 * Setze die Fuellmenge auf den Standartwert zurueck.
	 */
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
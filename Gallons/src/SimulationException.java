/** Falls etwas schief laeuft bei der Erstellung einer {@link Simulation} **/
public class SimulationException extends RuntimeException
{
	public SimulationException(String exc)
	{
		super(exc);
	}
}
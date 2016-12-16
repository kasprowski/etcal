package pl.kasprowski.etcal;

/**
 * Interface may be implemented by a class that invokes 
 * buildAsync, optimizeAsync or mapAsync methods of ETCal
 *   
 * @author pawel@kasprowski.pl
 *
 */
public interface Callback {
	/**
	 * Method that will be called when ETCal finishes work
	 */
	public void ready();
}

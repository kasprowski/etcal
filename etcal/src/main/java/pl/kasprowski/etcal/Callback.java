package pl.kasprowski.etcal;

/**
 * Interface may be implemented by a class that invokes 
 * buildAsync, optimizeAsync or mapAsync methods of Calsoft
 *   
 * @author pawel@kasprowski.pl
 *
 */
public interface Callback {
	/**
	 * Method that will be called when Calsoft finishes work
	 */
	public void ready();
}

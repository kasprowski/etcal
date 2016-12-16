package pl.kasprowski.etcal;

import org.apache.log4j.Logger;

public class ETCalException extends RuntimeException{
	Logger log = Logger.getLogger(ETCalException.class);
	private static final long serialVersionUID = 1L;

	public ETCalException(String name, Exception e) {
		super(name);
		log.error(e.getMessage(),e);
		e.printStackTrace();
	}
}

package pl.kasprowski.etcal.dataunits;

/**
 * Class for storing gaze coordinates.
 * Additional parameter w (weight) not used yet in the current implementation of ETCAL
 * 
 * @author pawel@kasprowski.pl
 *
 */
public class Target {
	private double x;
	private double y;
	private double w;

	private final static double EPSILON = 0.00001;

	
	public Target(double x, double y, double w) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getW() {
		return w;
	}

	public void setW(double w) {
		this.w = w;
	}

	@Override
	public String toString() {
		return "["+x+","+y+","+w+"]";
	}
	
	@Override
	public boolean equals(Object obj) {
		Target t = (Target)obj;
		
		return ((t.x==x)?true:Math.abs(t.x - x) < EPSILON)
				&& ((t.y==y)?true:Math.abs(t.y - y) < EPSILON);
	}
	
}

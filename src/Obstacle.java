import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

public class Obstacle {

	private boolean isCarried = false;
	private Int2D position = null;
	private SparseGrid2D area;

	public Obstacle(Int2D position, SparseGrid2D area) {
		this.area = area;
		setPosition(position);
	}

	public boolean isCarried() {
		return isCarried;
	}

	public void setCarried(boolean isCarried) {
		this.isCarried = isCarried;
	}

	public Int2D getPosition() {
		return position;
	}

	public void setPosition(Int2D position) {
		this.position = position;
		area.setObjectLocation(this, position);
	}

	public Entropy getEntropy() {
		return Entropy.getInstance();
	}
}
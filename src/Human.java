import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

/*
 * Die Klasse Ant implementiert die Schnittstelle Steppable und besitzt eine step() Methode,
 * sodass jede Ant dem Schedule zugewiesen werden kann um steps zu machen.
 */
public class Human implements Steppable {

	private Obstacle obst = null;
	private Int2D oldPos;
	private Boolean rightDirection;
	private int rule;
	public Human(Int2D pos, Boolean rightDirection, int rule) {
		this.oldPos = pos;
		this.rightDirection = rightDirection;
		this.rule = rule;
	}

	@Override
	public void step(SimState state) {
		// SimState state muss in Simulation gecastet werden (was es ja auch ist, da Simulation eine Subklasse von SimState ist).
		Simulation simulation = (Simulation) state;
		SparseGrid2D area = simulation.getArea();
		
		// Der Mensch wei� durch area.getObjectLocation(this), wo er sich befindet.
		Int2D pos = area.getObjectLocation(this);
		boolean shallDrop = false;
		if(rule == 1){
			
			
			
			// In bag befinden sich alle Objekte, der aktuellen Position.
			Bag bag = area.getObjectsAtLocation(pos);
			for (Object object : bag) {
				if (object instanceof Obstacle) {
					// Wenn das Objekt nicht Null ist, nicht dem eigenen Objekt entspricht und nicht 
					// bereits getragen wird, wird es fallen gelassen.
					if (obst != null && !object.equals(obst)
							&& !((Obstacle) object).isCarried()) {
						shallDrop = true;
						break;
					} else {
						//Das Objekt wird aufgenommen.
						obst = (Obstacle) object;
						obst.setCarried(true);
						break;
					}
				}
			}
		}
		// Falls das Objekt abgelegt werden soll, springt die Ameise an die neue Position (old + stepSize),
		// das Objekt wird an der "alten" Position abgelegt und nun nicht mehr getragen.
		if (shallDrop) {
			Int2D newPos = getNewPos(state, pos);
			int x = (pos.x - oldPos.x) * Config.stepSize;
			int y = (pos.y - oldPos.y) * Config.stepSize;
			newPos = new Int2D(pos.x + x, pos.y + y);
			area.setObjectLocation(this, newPos);
			obst.setPosition(oldPos);
			obst.setCarried(false);
			obst = null;
		} else {
			// Falls kein Objekt abgelegt werden soll, "wigglet" die Ameise weiter.
			Int2D newPos = getNewPos(state, pos);
			area.setObjectLocation(this, newPos);
			if (obst != null)
				obst.setPosition(newPos);
		}
		oldPos = pos;
	}
	
	public Int2D rule1 (Int2D pos, SparseGrid2D area){
		int nextFieldDiff;
		if(rightDirection)
			nextFieldDiff = 1;
		else
			nextFieldDiff = -1;
		Int2D checkPos;
		checkPos =new Int2D(pos.x + nextFieldDiff, pos.y);
		// next field with human in same direction --> stay
		if(getHumanAtPos(checkPos, area) == 1){
			return pos;
		}
		// next field with human in different direction
		else if(getHumanAtPos(checkPos, area) == -1){
			checkPos = new Int2D(pos.x, pos.y + nextFieldDiff);
			//empty right field --> step right
			if(getHumanAtPos(checkPos, area) == 0){
				return checkPos;
			}
			//non-empty right field
			else
			{
				checkPos = new Int2D(pos.x, pos.y - nextFieldDiff);
				//empty left field --> step left
				if(getHumanAtPos(checkPos, area) == 0){
					return checkPos;
				}
				//non-empty left field: --> stay
				else
					return pos;
			}
		}
		// empty next Field
		else{
			checkPos =new Int2D(pos.x + 2 * nextFieldDiff, pos.y);
			// human in same direction two fields ahead --> step forward
			if(getHumanAtPos(checkPos, area) == 1)
				return new Int2D(pos.x + nextFieldDiff, pos.y);
			// empty field or human in different direction two fields ahead 
			else{
				checkPos = new Int2D(pos.x, nextFieldDiff + pos.y);
				//right field is empty
				if(getHumanAtPos(checkPos, area) == 0){
					checkPos = new Int2D(pos.x + nextFieldDiff, nextFieldDiff + pos.y);
					// human with same direction right ahead
					if(getHumanAtPos(checkPos, area) == 1)
						return new Int2D(pos.x, pos.y + nextFieldDiff);
					// human different direction right ahead
					else if(getHumanAtPos(checkPos, area) == -1){
						return checkToGoLeft(pos, area, nextFieldDiff);
					}
					//empty field right ahead
					else{
						checkPos = new Int2D(pos.x + nextFieldDiff, 2 * nextFieldDiff + pos.y);
						//human in same direction on field right, two ahead
						if(getHumanAtPos(checkPos, area) == 1)
							return new Int2D(pos.x, pos.y + nextFieldDiff);
						//empty field or human in different direction on field right, two ahead
						else
							return checkToGoLeft(pos, area, nextFieldDiff);
					}
				}
				//right field is non-empty
				else{
					checkPos = new Int2D(pos.x, pos.y - nextFieldDiff);
					//left field is empty
					if(getHumanAtPos(checkPos, area) == 0)
						return checkToGoLeft(pos, area, nextFieldDiff);
					//left field is non-empty
					else 
						return new Int2D(pos.x + nextFieldDiff, pos.y); 
				}
			}
		}
	}
	
	private Int2D checkToGoLeft(Int2D pos, SparseGrid2D area, int nextFieldDiff) {
		Int2D checkPos;
		checkPos = new Int2D(pos.x + nextFieldDiff, pos.y - nextFieldDiff);
		// human with same direction on field left ahead
		if(getHumanAtPos(checkPos, area) == 1){
			return new Int2D(pos.x, pos.y - nextFieldDiff);
		}
		//human with different direction on field left ahead
		else if(getHumanAtPos(checkPos, area) == -1){
			return stepAhead();
		}
		//empty field left ahead
		else{
			checkPos = new Int2D(pos.x + nextFieldDiff, pos.y - 2 * nextFieldDiff);
			// human with same direction on field left, two ahead
			if(getHumanAtPos(checkPos, area) == 1){
				return stepLeft();
			}
			//empty field or human with different direction on field left, two ahead
			else{
				stepAhead();
			}
		}
		
	}

	public int getHumanAtPos(Int2D pos, SparseGrid2D area){
		// return Values:
		// -1: Human in other direction at pos
		// 0: no Human at pos
		// 1: Human in same direction at pos
		Bag bag = area.getObjectsAtLocation(pos);
		if(bag == null)
			return 0;
		else if(bag.numObjs > 1){
			System.out.println("Zwei Objekete an der selben Stelle!");
			return 0;}
		else if(((Human) bag.get(0)).rightDirection == this.rightDirection)
			return -1;
		else
			return 1;
	}
	
	public Boolean getRieghtDirection(){
		return rightDirection;
	}
	
	public int getRule(){
		return rule;
	}
	
	public Obstacle getObst() {
		return obst;
	}

	public Entropy getEntropy() {
		return Entropy.getInstance();
	}

	private Int2D getNewPos(SimState state, Int2D pos) {
		int rnd = (int) (state.random.nextDouble() * 4);
		SparseGrid2D area = ((Simulation) state).getArea();
		int x = pos.x;
		int y = pos.y;
		switch (rnd) {
		case 0:
			if (++x > area.getWidth() - 1)
				x = 0;
			break;
		case 1:
			if (++y > area.getHeight() - 1)
				y = 0;
			break;
		case 2:
			if (--x < 0)
				x = area.getWidth() - 1;
			break;
		default:
			if (--y < 0)
				y = area.getHeight() - 1;
			break;
		}
		return new Int2D(x, y);
	}
}
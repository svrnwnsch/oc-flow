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

	private Int2D position;
	private Boolean rightDirection;
	public Boolean getRightDirection() {
		return rightDirection;
	}

	private int rule;
	public Human(Int2D pos, Boolean rightDirection, int rule) {
		this.position = pos;
		this.rightDirection = rightDirection;
		this.rule = rule;
	}

	@Override
	public void step(SimState state) {
		// SimState state muss in Simulation gecastet werden (was es ja auch ist, da Simulation eine Subklasse von SimState ist).
		Simulation simulation = (Simulation) state;
		SparseGrid2D area = simulation.getArea();

		if(rule == 1){
			position = rule1(area);
		}

		// Neue Position wird auf der area gestzt
		area.setObjectLocation(this, this.position);
	}
	
	public Int2D rule1 (SparseGrid2D area){
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
		else{
			return pos;
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
	
	public Entropy getEntropy() {
		return Entropy.getInstance();
	}


	public Int2D getPosition() {
		//TODO soll Position zurückgeben
		return null;
	}
}
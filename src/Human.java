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
			setPosition(rule1(area));
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
		checkPos =new Int2D(position.x + nextFieldDiff, position.y);
		// next field with human in same direction --> stay
		if(getHumanAtPos(checkPos, area) == 1){
			return position;
		}
		// next field with human in different direction
		else if(getHumanAtPos(checkPos, area) == -1){
			checkPos = new Int2D(position.x, position.y + nextFieldDiff);
			//empty right field --> step right
			if(getHumanAtPos(checkPos, area) == 0){
				return checkPos;
			}
			//non-empty right field
			else
			{
				checkPos = new Int2D(position.x, position.y - nextFieldDiff);
				//empty left field --> step left
				if(getHumanAtPos(checkPos, area) == 0){
					return checkPos;
				}
				//non-empty left field: --> stay
				else
					return position;
			}
		}
		// empty next Field
		else{
			checkPos =new Int2D(position.x + 2 * nextFieldDiff, position.y);
			// human in same direction two fields ahead --> step forward
			if(getHumanAtPos(checkPos, area) == 1)
				return new Int2D(position.x + nextFieldDiff, position.y);
			// empty field or human in different direction two fields ahead 
			else{
				checkPos = new Int2D(position.x, nextFieldDiff + position.y);
				//right field is empty
				if(getHumanAtPos(checkPos, area) == 0){
					checkPos = new Int2D(position.x + nextFieldDiff, nextFieldDiff + position.y);
					// human with same direction right ahead
					if(getHumanAtPos(checkPos, area) == 1)
						return new Int2D(position.x, position.y + nextFieldDiff);
					// human different direction right ahead
					else if(getHumanAtPos(checkPos, area) == -1){
						return checkToGoLeft(area, nextFieldDiff);
					}
					//empty field right ahead
					else{
						checkPos = new Int2D(position.x + nextFieldDiff, 2 * nextFieldDiff + position.y);
						//human in same direction on field right, two ahead
						if(getHumanAtPos(checkPos, area) == 1)
							return new Int2D(position.x, position.y + nextFieldDiff);
						//empty field or human in different direction on field right, two ahead
						else
							return checkToGoLeft(area, nextFieldDiff);
					}
				}
				//right field is non-empty
				else{
					checkPos = new Int2D(position.x, position.y - nextFieldDiff);
					//left field is empty
					if(getHumanAtPos(checkPos, area) == 0)
						return checkToGoLeft(area, nextFieldDiff);
					//left field is non-empty
					else 
						return new Int2D(position.x + nextFieldDiff, position.y); 
				}
			}
		}
	}
	
	private Int2D checkToGoLeft(SparseGrid2D area, int nextFieldDiff) {
		Int2D checkPos;
		checkPos = new Int2D(position.x + nextFieldDiff, position.y - nextFieldDiff);
		// human with same direction on field left ahead
		if(getHumanAtPos(checkPos, area) == 1){
			return new Int2D(position.x, position.y - nextFieldDiff);
		}
		//human with different direction on field left ahead
		else if(getHumanAtPos(checkPos, area) == -1){
			return stepAhead(nextFieldDiff);
		}
		//empty field left ahead
		else{
			checkPos = new Int2D(position.x + nextFieldDiff, position.y - 2 * nextFieldDiff);
			// human with same direction on field left, two ahead
			if(getHumanAtPos(checkPos, area) == 1){
				return stepLeft(nextFieldDiff);
			}
			//empty field or human with different direction on field left, two ahead
			else{
				return stepAhead(nextFieldDiff);
			}
		}
		
	}
	

	public Int2D stepLeft(int nextFieldDiff){
		setPosition(new Int2D(position.x, position.y - nextFieldDiff));
		return position;
	}
	

	public Int2D stepRight(int nextFieldDiff){
		setPosition(new Int2D(position.x, position.y + nextFieldDiff));
		return position;
	}
	
	public Int2D stepAhead(int nextFieldDiff){
		setPosition(new Int2D(position.x + nextFieldDiff, position.y));
		return position;
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
			return 1;
		else
			return -1;
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
		return this.position;
	}
	
	public void setPosition(Int2D position) {
		this.position = position;
	}
}
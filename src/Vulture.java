import bwapi.*;
import java.util.HashSet;

import com.google.common.collect.Range;

public class Vulture {

    private final Mirror bwapi;
    private final HashSet<Unit> enemyUnits;
    final private Unit unit;
    //new 
    private int count = 0;
    private int action = 1;
    
    public Vulture(Unit unit, Mirror bwapi, HashSet<Unit> enemyUnits) {
        this.unit = unit;
        this.bwapi = bwapi;
        this.enemyUnits = enemyUnits;
    }

    public void step() 
    {
        /**
         * TODO: XCS
         */
    	Environment env = new Environment();
    	count++;
    	if (count == 5) 
    	{
    		env = new Environment(unit.getHitPoints(),getClosestEnemy().getHitPoints());
			action = VultureAI.VultXCS.process(env); //run XCS
    	}
    		
    	
    	doAction(action);
    	if (count == 10)	
		{
    		VultureAI.VultXCS.profit(calcReward(env, new Environment(unit.getHitPoints(),getClosestEnemy().getHitPoints()))); //get reward
    		count = 0;
		}	
    }
    
    public void doAction(int action)
    {
    	Unit target;
    	switch (action)
    	{
    		case 0:		//flee
    			System.out.println("Flees");
    			target = getClosestEnemy();
    			flee(target);
    			break; 
    		case 1:		//fight
    			System.out.println("Fights");
    			target = getClosestEnemy();
    			attack(target);
    			break; 
    	
    	}
    }
    
    public int calcReward(Environment oldEnv, Environment currentEnv) //oldEnv = few frames old
    {
    	int reward = 0;
    	
    	if(oldEnv.X == currentEnv.X) reward = 0;
    	if(oldEnv.Y > currentEnv.Y) reward = 1000; 
    	//TODO implementation for Z axis
    	
    	if(oldEnv.X > currentEnv.X && oldEnv.Y == currentEnv.Y) reward = 0;
    	
    	System.out.println("Reward:" + reward);
    	return reward;
    }
    
    public Range<Double> ConvertToRange(double HP,double enemyHP)
    {
    	Range<Double> r = Range.open(1 + HP, 1 + enemyHP);
    	   	
		return r;
    }
    
    public void flee(Unit target)
    {
    	unit.move(new Position(target.getPosition().getX() - 57, target.getPosition().getY() - 57), false);
    }
    
    private void move(Unit target) 
    {
        unit.move(new Position(target.getPosition().getX(), target.getPosition().getY()), false);
        
    }
    
    private void attack(Unit target)
    {
    	unit.attack(new Position(target.getPosition().getX() - 20,target.getPosition().getY() - 20));
    }

    private Unit getClosestEnemy() {
        Unit result = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (Unit enemy : enemyUnits) {
            double distance = getDistance(enemy);
            if (distance < minDistance) {
                minDistance = distance;
                result = enemy;
            }
        }

        return result;
    }

    private double getDistance(Unit enemy) {
    	return this.unit.getPosition().getDistance(enemy.getPosition());
    }
}

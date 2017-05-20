import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ClassifierSet {

	public Classifier[] clSet;
	
	public int GetDA() //distinct actions in action set
	{
		Set<Integer> arr = new HashSet<Integer>(); 
		for (Classifier c : clSet)	arr.add(c.A);
		return arr.size();
	}
	
	public ClassifierSet()
	{
		clSet = new Classifier[Constants.maxPop];
	}
	
	public void add(Classifier cl)
	{
		clSet[clSet.length] = cl; //TODO error handling
	}

	public boolean isEmpty() {
		if (clSet.length == 0)return true; //TODO check if length == current set entries
		return false;
	}

	public int getUnusedAction() {
		
		Set<Integer> usedActions = new HashSet<Integer>();
		ArrayList<Integer> unusedActions = new ArrayList<Integer>();
		
		for (Classifier cl : clSet) usedActions.add(cl.A);
		
		for (int i : Constants.possibleActions) 
			if (!usedActions.contains(i)) unusedActions.add(i); 
			
//		ArrayList<Integer> unusedActions = new ArrayList<Integer>();
//		boolean isInList = false;
//		for (Classifier c : clSet)
//		{
//			for (int i=0;i<Constants.possibleActions.length;i++)
//			{	
//				if(c.A == Constants.possibleActions[i])
//				{
//					isInList = true;
//				}
//			}
//			if (!isInList) unusedActions.add(c.A); //if not used add to list 
//		}
		
		//choose randomly from the list
		int rand = ThreadLocalRandom.current().nextInt(0, unusedActions.size()); //better Math.random?
		return unusedActions.get(rand);
	}
	
	
	
}
 
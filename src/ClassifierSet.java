import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ClassifierSet {

	public ArrayList<Classifier> clSet;
	
	public ClassifierSet()
	{
		//clSet = new Classifier[Constants.maxPop];
		clSet = new ArrayList<Classifier>();
	}
	
	public int GetDA() //distinct actions in action set
	{
		Set<Integer> arr = new HashSet<Integer>(); 
		for (Classifier c : clSet)
			if (c != null) arr.add(c.A);
		return arr.size();
	}
	
	public void add(Classifier cl)//TODO what if clSet is full? 
	{
		clSet.add(cl);
	}

	public boolean isEmpty() 
	{
		for(Classifier cl : clSet)
		{
			if(cl != null) return false; 
		}
		return true;
	}

	public int getUnusedAction() 
	{
		Set<Integer> usedActions = new HashSet<Integer>();
		ArrayList<Integer> unusedActions = new ArrayList<Integer>();
		
		for (Classifier cl : clSet) 
		{
			if (cl != null)	usedActions.add(cl.A);
		}
		
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

	public void removeCl(Classifier cl) {
//		for (int i = 0;i<clSet.size();i++)
//		{
//			
//			if (cl.C.equals(clSet[i].C) && cl.A == clSet[i].A) clSet[i]=null;
//		}	
		clSet.remove(cl);
	}
	
}
 
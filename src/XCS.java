import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.collect.Range;

public class XCS {
	private int action = 0; //action to use for AI
 	public ClassifierSet MS = new ClassifierSet();
	public double[] PA;
	public ClassifierSet AS = new ClassifierSet();
	public ClassifierSet Pop = new ClassifierSet();
	public ClassifierSet ASOld = new ClassifierSet();	
	public int pOld = 0;	
	public Environment envOld = new Environment();
	public Environment env = new Environment();
	
	public Classifier child1 = new Classifier();
	public Classifier child2 = new Classifier();
	
	public int process(Environment environment, int elapsedGameTime)
	{
		System.out.println("Population:" + Pop.clSet.size() + " Action:" + action);
	
		env = environment;
		MS = GenMatchSet(Pop, env, elapsedGameTime);
		PA = GenPredictionArray(MS);
		action = SelectAction();
		AS = GenActionSet(MS,action);
		//now execute action 
		return action;
	}		
	
	public boolean profit(int p, int elapsedGameTime) //get reward rp
	{	
		double P = 0.0f;
		
		if (!ASOld.isEmpty())
		{
			P = pOld + Constants.gamma * GetMax(PA);
			ASOld = UpdateSet(ASOld, P);
			GeneticAlgorithm(ASOld, envOld, elapsedGameTime);
		}
		//if (rp: eop) //on end of program
		//{
		//	UpdateSet(AS, P);
		//	//GeneticAlgorithm(AS,env); not yet
		//	ASOld = new ClassifierSet();
		//}
		//else
		//{
		ASOld = AS;
		pOld = p;
		envOld = env;	
		//}

		return true;
	}
	
	private void GeneticAlgorithm(ClassifierSet A, Environment env, int elapsedGameTime) 
	{
		int sumTStimesN = 0;
		int sumNumerosity = 0;

		Classifier parent1 = new Classifier();
		Classifier parent2 = new Classifier();
		
		for (Classifier cl : A.clSet)	
		{
			sumTStimesN += cl.ts*cl.n;
			sumNumerosity += cl.n;
		}
		
		if ((elapsedGameTime - sumTStimesN/sumNumerosity) > Constants.ThetaGA)
		{
			for (Classifier cl : A.clSet) cl.ts = elapsedGameTime;
			
			parent1 = SelectOffspring(A);
			parent2 = SelectOffspring(A);
			child1 = parent1;
			child2 = parent2;
			child1.n = 1;
			child2.n = 1;
			child1.exp = 0;
			child2.exp = 0;			
			int rand = ThreadLocalRandom.current().nextInt(0, 1);
			if (rand < Constants.chi)
			{
				ApplyCrossover();
				child1.p = (parent1.p + parent2.p)/2;
				child2.p = (parent1.p + parent2.p)/2;
				child1.F = (parent1.F + parent2.F)/2;
				child2.p = child1.p;
				child2.e = child1.e;
				child2.F = child1.F;
			}
			child1.F = child1.F * 0.1;
			child2.F = child2.F * 0.1;
		}
		//once for child 1
		child1 = ApplyMutation(child1, env);
		
		if (Constants.doSubsumption)
		{
			if(doesSubsume(parent1,child1))
			{
				parent1.n++;
			}
			else if (doesSubsume(parent2,child1))
			{
				parent2.n++;
			}else Pop.add(child1);
			
		}else Pop.add(child1);
		
		DeleteFromPop(Pop);
		
		//once for child 2
		child2 = ApplyMutation(child2,env);
		
		if (Constants.doSubsumption)
		{
			if(doesSubsume(parent1,child2))
			{
				parent1.n++;
			}
			else if (doesSubsume(parent2,child2))
			{
				parent2.n++;
			}else Pop.add(child2);
			
		}else Pop.add(child2);
		
		DeleteFromPop(Pop);	
	
	}

	private boolean doesSubsume(Classifier parent, Classifier child) 
	{
		if (parent.A == child.A && couldSubsume(parent) && parent.moreGeneral(child))
		{
			return true;
		}
		
		return false;
	}

	private Classifier ApplyMutation(Classifier child, Environment env) //action can change
	{
		double rand = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
		int Irand = 0;
		if (rand > 0.5)
		{
			Irand = ThreadLocalRandom.current().nextInt(0,20);
			child.C.X = Range.openClosed(env.X + Irand ,env.X + Irand);			
		}
		
		rand = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
		if (rand > 0.5)
		{
			Irand = ThreadLocalRandom.current().nextInt(0,20);
			child.C.Y = Range.openClosed(env.Y + Irand ,env.Y + Irand);			
		}
		
		rand = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
		if (rand > 0.5)
		{
			Irand = ThreadLocalRandom.current().nextInt(0,20);
			child.C.Z = Range.openClosed(env.Z + Irand ,env.Z + Irand);			
		}
		
		//choose random action
		rand = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
		if (rand > Constants.mu)
		{
			Irand = ThreadLocalRandom.current().nextInt(0,Constants.possibleActions.length);
			child.A = Irand;
		}
		return child;
	}

	private void ApplyCrossover() //no change in action
	{
		Condition temp = new Condition();
		double rand = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
		if (rand > 0.6)
		{
			temp.X = Range.openClosed(child1.C.X.lowerEndpoint(), child1.C.X.upperEndpoint());
			child1.C.X = child2.C.X;
			child2.C.X = temp.X;
		}
		
		rand = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
		if (rand > 0.6)
		{		
			temp.Y = Range.openClosed(child1.C.Y.lowerEndpoint(), child1.C.Y.upperEndpoint());
			child1.C.Y = child2.C.Y;
			child2.C.Y = temp.Y;
		}
		
		rand = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
		if (rand > 0.6)
		{
			temp.Z = Range.openClosed(child1.C.Z.lowerEndpoint(), child1.C.Z.upperEndpoint());
			child1.C.Z = child2.C.Z;
			child2.C.Z = temp.Z;
		}
	}

	private Classifier SelectOffspring(ClassifierSet A) 
	{
		double sumFitness = 0;
		for (Classifier cl : A.clSet) sumFitness += cl.F;
		
		double rand = ThreadLocalRandom.current().nextDouble(0, 1);
		Constants.ChoicePoint = rand * sumFitness;
		sumFitness = 0;
		for (Classifier cl : A.clSet)
		{
			sumFitness = sumFitness + cl.F;
			if (sumFitness > Constants.ChoicePoint) return cl;
		}

		return A.clSet.get((int) rand); //shouldn't happen!
	}

	public double GetMax(double[] PA)
	{
		double max = 0.0f;
		int index = 0;
		
		for (int i=0;i<PA.length;i++)
		{
			if (max < PA[i])
			{
				max = PA[i];
				index = i;
			}
		}
		return PA[index];
	}
	
	private ClassifierSet GenActionSet(ClassifierSet M, int act) 
	{
		ClassifierSet A = new ClassifierSet();
		for (Classifier cl : M.clSet)
		{
			if (cl != null && cl.A == act)
			{
				A.add(cl);
			}
		}
		return A;
	}

	private int SelectAction() {
		double Drand = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
		if (Drand < Constants.pExplr) 
		{ 
			return ThreadLocalRandom.current().nextInt(0, PA.length); //just try one
		} 
		else 
		{
			double max = 0;
			int index = 0;
			for (int i = 0;i<PA.length;i++)
			{
				if (PA[i] > max) 
				{
					PA[i] = max;
					index = i;
				}
			}
			
			return index; //return best action	
		}
		
	}

	private double[] GenPredictionArray(ClassifierSet M) {
		
		double[] FArray = new double[Constants.possibleActions.length];
		
		for (Classifier cl : M.clSet)
		{
			PA  = new double[Constants.possibleActions.length];
			PA[cl.A] = cl.p * cl.F;
			
			FArray[cl.A] = FArray[cl.A] + cl.F;
		}
		
		for (int i=0;i<Constants.possibleActions.length;i++)
		{
			if (FArray[i] != 0.0)
			{
				PA[i] = PA[i] / FArray[i];
			}
		}			
		return PA;
	}

	public ClassifierSet UpdateSet(ClassifierSet A,double P)
	{
		for (Classifier cl : A.clSet)
		{
			if (cl == null) continue;
			cl.exp++;
			//update prediction
			if (cl.exp < 1/Constants.beta)
			{
				cl.p = cl.p + (P - cl.p);
			}
			else
			{
				cl.p = cl.p + Constants.beta * (P - cl.p);
			}
			//update prediction error
			if(cl.exp < 1/Constants.beta)
			{
				cl.e = cl.e + (Math.abs(P - cl.p)-cl.e) / cl.exp;
			}
			else
			{
				cl.e = cl.e + Constants.beta * (Math.abs(P-cl.p)-cl.e);
			}
			
			//update action set size estimate
			if (cl.exp < 1/Constants.beta)
			{
				int sumNumerosity = 0;
				for (Classifier c : A.clSet) 
				{
					if(c != null)sumNumerosity += c.n; 
				}
				cl.as = cl.as + (sumNumerosity - cl.as) / cl.exp;
			}
			else
			{
				int sumNumerosity = 0;
				for (Classifier c : A.clSet) 
				{
					if (c == null) continue;
					sumNumerosity += c.n; 
				}
					
				cl.as = cl.as + Constants.beta * (sumNumerosity - cl.as);
			}
		}
		
		A = UpdateFitness(A);
		if (Constants.doSubsumption) //shouldnt be a constant?
		{
			ASSubsumption(A);
		}
		return A;
	}
	
	private void ASSubsumption(ClassifierSet A) //doActionSetSubsumption
	{ 
		Classifier cl = new Classifier();
		
		for (Classifier c : A.clSet)
		{
			if (c == null) continue;
			if (couldSubsume(c))
			{
				if(cl == null || (c.countWC() > cl.countWC())) //count wildcards in c.Condition
				{
					cl = c;
				}	
			}
		}
		if (cl != null)
		{
			//for (Classifier c : A.clSet)
			for (Iterator<Classifier> it = A.clSet.iterator();it.hasNext();)
			{
				Classifier c = it.next();
				if(c == null) continue;
				if (cl.moreGeneral(c))
				{
					cl.n = cl.n + c.n;
					Pop.removeCl(c);
					it.remove();
				}
			}
		}
	}

	private boolean couldSubsume(Classifier c) {
		
		if (c.exp > Constants.ThetaSub && c.e < Constants.epsilon0) return true;
		return false;
	}

	private ClassifierSet UpdateFitness(ClassifierSet A) 
	{
		double accuracySum = 0;
		for (Classifier cl : A.clSet)
		{
			if (cl == null) continue;
			if (cl.e < Constants.epsilon0)
			{
				cl.kapa = 1;
			}
			else
			{
				cl.kapa = Constants.alpha * Math.pow((cl.e / Constants.epsilon0),-Constants.nu);
			}
			accuracySum = accuracySum + cl.kapa * cl.n; //TODO correct? kapa mby zero
		}
		
		for (Classifier cl : A.clSet) 
		{
			if (cl == null) continue;
			cl.F = cl.F + Constants.beta * (cl.kapa * cl.n / accuracySum - cl.F);
		}
		
		return A;
	}

	public ClassifierSet GenMatchSet(ClassifierSet Popu,Environment env, int elapsedGameTime)
	{
		ClassifierSet M = new ClassifierSet();
		while (M.isEmpty())
		{
			for (Classifier cl : Popu.clSet)
			{
				if (cl != null && cl.doesMatch(env)) M.add(cl);
			}
			
			if (M.GetDA() < Constants.ThetaMna) //count distinct actions in M 
			{
				if (Pop.clSet.size() < Constants.maxPop)	Pop.add(Covering(M, env, elapsedGameTime)); //cover and add to Pop
				DeleteFromPop(Popu); //delete some entries with certain probability
				M = new ClassifierSet();
			}
		}
		return M; 
	}
	
	public void DeleteFromPop(ClassifierSet Popu)
	{
		int sumNumerosity = 0;
		double sumFitness = 0;
		for (Classifier c : Popu.clSet) 
		{
			if(c != null)
			{
				sumNumerosity += c.n; 	
				sumFitness += c.F;
			}
		}
	
		if (sumNumerosity < Constants.N) return;
		
		double avgFitInPop = sumFitness / sumNumerosity;
		//double voteSum = 0.0;
		
		ArrayList<Classifier> toDelete = new ArrayList<Classifier>();
		
		for (Classifier c : Pop.clSet)
		{
			if (c.F < avgFitInPop) toDelete.add(c);			
		}
		
//		for (Classifier c : Pop.clSet)
//		{
//			voteSum = voteSum + deletionVote(c,avgFitInPop);
//			if (voteSum > Constants.ChoicePoint)
//			{
//				if (c.n > 1) 
//				{
//					c.n--;
//				}
//				else toDelete.add(c);
//			}
//		}
		for (Classifier c : toDelete) Pop.removeCl(c);
		
		toDelete.clear();
	}
	
	public double deletionVote(Classifier cl, double avg)
	{
		double vote = cl.as * cl.n;
		if(cl.exp > Constants.ThetaDel && cl.F /cl.n < Constants.delta * avg)
		{
			vote = vote * avg / (cl.F / cl.n);
		}
		return vote;
	}
	
	public Classifier Covering(ClassifierSet M, Environment env, int elapsedGameTime)
	{
		Classifier cl = new Classifier();
		
		double rand = ThreadLocalRandom.current().nextDouble(2.0, 100.0); //better Math.random? TODO check interval
		cl.C.X = Range.open(env.X - rand/2, env.X + rand/2);
		cl.C.Y = Range.open(env.Y - rand/2, env.Y + rand/2);
		cl.C.Z = Range.open(env.Z - rand/2, env.Z + rand/2);
		
		cl.A = M.getUnusedAction();
		cl.p = Constants.pI; //initial p
		cl.e = Constants.eI; //inital e
		cl.F = Constants.FI; //inital F
		cl.exp = 0;
		cl.ts = elapsedGameTime;
		cl.as = 1;
		cl.n = 1;
		
		return cl;	
	}

	public int getAction() {
		return action;
	}
	

	
	
}

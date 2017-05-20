import java.util.concurrent.ThreadLocalRandom;

public class XCS {
	private int action = 0; //action to use for AI
 	public ClassifierSet MS = new ClassifierSet();
	public double[] PA;
	public ClassifierSet AS = new ClassifierSet();
	public ClassifierSet Pop = new ClassifierSet();
	public ClassifierSet ASOld = new ClassifierSet();	
	public int pOld = 0;	
	
	public boolean think(String env)
	{
		//env = GetEnv(); //ex. 1010010
		MS = GenMatchSet(Pop, env);
		PA = GenPredictionArray(MS);
		action = SelectAction();
		AS = GenActionSet(MS,action);
		//now execute action 
		return true;
	}		
	
	private ClassifierSet GenActionSet(ClassifierSet M, int act) 
	{
		ClassifierSet A = new ClassifierSet();
		for (Classifier cl : M.clSet)
		{
			if (cl.A == act)
			{
				A.add(cl);
			}
		}
		return A;
	}

	private int SelectAction() {
		int rand = ThreadLocalRandom.current().nextInt(0, 1); //TODO check if interval correct
		
		if (rand < Constants.pExplr) 
		{
			int breaker = 0;
			while (breaker < 10)
			{
				rand = ThreadLocalRandom.current().nextInt(0, PA.length);
				if (PA[rand] != 0.0f)
				{
					return rand;
				}
				breaker++;
			}
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
		return ThreadLocalRandom.current().nextInt(0, PA.length);
	}

	private double[] GenPredictionArray(ClassifierSet M) {
		PA  = new double[Constants.possibleActions.length];
		double[] FArray = new double[PA.length];
		
		for (Classifier cl : M.clSet)
		{
			if (PA[cl.A] == 0.0f) //TODO correct?
			{
				PA[cl.A] = cl.p * cl.F;
			}
			else PA[cl.A] = PA[cl.A] + cl.p * cl.F;
			
			FArray[cl.A] = FArray[cl.A] + cl.F;
		}
		
		for (int i=0;i<Constants.possibleActions.length;i++)
		{
			if (FArray[i] != 0)
			{
				PA[i] = PA[i] / FArray[i];
			}
		}			
		return PA;
	}

	public boolean profit(int rp)
	{	

		p = rp; //get reward
		
		if (!ASOld.isEmpty())
		{
			P = pOld + gamma * max(PA);
			UpdateSet(ASOld, P, Pop);
			GeneticAlgorithm(ASOld, envOld);
		}
		if (rp: eop) //on end of program
		{
			UpdateSet(AS, P, Pop);
			GeneticAlgorithm(AS,env);
			ASOld = new ClassifierSet();
		}
		else
		{
			ASOld = AS;
			pOld = P;
			envOld = env;	
		}

		return true;
	}
	
	public ClassifierSet GenMatchSet(ClassifierSet Pop,String env)
	{
		ClassifierSet M = new ClassifierSet();
		while (M.isEmtpy())
		{
			for (Classifier cl : Pop.clSet)
			{
				if (cl.doesMatch(env)) M.add(cl);
			}
			
			if (M.GetDA() < Constants.ThetaMna) //count distinct actions in M 
			{
				Pop.add(Covering(M, env)); //cover and add to Pop
				Delete(Pop); //delete some entries with certain probability
				M = new ClassifierSet();
			}
			return M;
		}
		return M; 
	}
	
	public Classifier Covering(ClassifierSet M, String env)
	{
		Classifier cl = new Classifier();
		cl.GenCondition(env.length()); //setCondition with lenght of env !no need?
		for (int i = 0;i<cl.C.length();i++) //for each char in Condition
		{
			char[] ch = cl.C.toCharArray();
			int rand = ThreadLocalRandom.current().nextInt(0, 1); //better Math.random? TODO check interval
			if (rand < Constants.Pr) ch[i] = '#'; //P# probability to insert a #
			else 
			{
				ch[i] = env.charAt(i);
			}
			cl.C = ch.toString();
		}
		
		cl.A = M.getUnusedAction();
		cl.p = Constants.pI; //initial p
		cl.e = Constants.eI; //inital e
		cl.F = Constants.FI; //inital F
		cl.exp = 0;
		cl.ts = t; //TODO current frame?
		cl.as = 1;
		cl.n = 1;
		
		return cl;	
	}

	public int getAction() {
		return action;
	}
	
	
}

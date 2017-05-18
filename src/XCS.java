import java.util.concurrent.ThreadLocalRandom;

public class XCS {
	
	public ClassifierSet MS = new ClassifierSet();
	public ClassifierSet PA = new ClassifierSet();
	public ClassifierSet AS = new ClassifierSet();
	public ClassifierSet Pop = new ClassifierSet();
	
	public boolean run()
	{
		int pOld = 0;
		ClassifierSet ASOld = new ClassifierSet(); 
		
		while(termination criteria)
		{
			env = GetEnv(); //ex. 1010010
			MS = GenMatchSet(Pop, env);
			PA = GenPredictionArray();
			action = GetAction();
			AS = GenActionSet();
			//execute action 
			p = rp; //get reward
			
			if (!ASold.isEmpty())
			{
				P = pOld + gamma * max(PA);
				UpdateSet(ASOld, P, Pop);
				GeneticAlgorithm(ASOld, envOld);
			}
			if (rp: eop) //on end of programm
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
			
			if (M.DA < ThetaMna) //count distinct actions in M 
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
		cl.setCondition(); //setCondition with lenght of env
		for (int i = 0;i<cl.C.length();i++) //for each char in Condition
		{
			char[] c = cl.C.toCharArray();
			int rand = ThreadLocalRandom.current().nextInt(0, 1); //better Math.random?
			if (rand < Pr) c[i] = '#'; //P# probability to insert a #
			else 
			{
				c[i] = env.charAt(i);
			}
			cl.C = c.toString();
		}
		
		cl.A = getUnusedAction(M);
		cl.p = pI; //initial p
		cl.e = eI; //inital e
		cl.F = FI; //inital F
		cl.exp = 0;
		cl.ts = t //TODO current frame?
		cl.as = 1;
		cl.n = 1;
		
		return cl;	
	}
	
	
}

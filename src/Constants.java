
public class Constants {
	//TODO get values from paper
	
	public static final boolean doSubsumption = true; //
	
	public static final int[] possibleActions = {0,1,2}; 
	public static final int maxPop = 200; //max population entries
	public static final int ThetaMna = 3; // minimum of distinct actions in M
	//public static final double Pr = 0.33; //P# probabilty to insert a # for covering
	public static final double pExplr = 0.2; //probability to explore 
	public static final double beta = 0.3; //learning rate
	public static final double epsilon0 = 20.0; //minimum error to be ok with
	public static final double alpha = 0.3; 	//
	public static final double nu = 5.0; 		//
	public static final int ThetaSub = 20; 	// minimum exp to subsume
	public static final double gamma = 0.71 ; //discount factor
	public static final int N = maxPop; // = maxPop? 
	public static final double delta = 0.1; //
	public static final double ThetaDel = 20.0; //threshold to consider deletion 
	public static final double ThetaGA = 12.0; //threshold till genetic algorithm
	public static double ChoicePoint = 0.0; //gets updated in selectOffspring()
	public static final double chi = 0.6; //probability to apply crossover
	public static final double mu = 0.05;
	
	public static double pI = 10.0; //initial p
	public static double eI = 0.0; //inital e
	public static double FI = 0.01; //inital F
	
}

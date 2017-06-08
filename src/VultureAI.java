import bwapi.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;

public class VultureAI  extends DefaultBWListener implements Runnable {

    private final Mirror bwapi;
    
    private Game game;
    
    private Player self;

    private Vulture vulture;

    private HashSet<Unit> enemyUnits;

    private int frame;
    
    //new
    public static XCS VultXCS = new XCS(); 
    public static boolean loadOldValues = false;
    public static float RoundCount = 0;
    public float wins = 0;
    public int time = 0;
	public File log = new File(".//eval.txt");
	public File save = new File(".//lastSave.txt");
	ArrayList<String> Alog = new ArrayList<String>();
	public static int evaluateEvery = 20; //rounds
	
    public VultureAI() 
    {
        System.out.println("This is the VultureAI! :)");
        this.bwapi = new Mirror();
    }

    public static void main(String[] args) 
    {
		new VultureAI().run();
    	//new Evaluation.runEvaluation().evaluate();
    }

    @Override
    public void onStart() {
        enemyUnits = new HashSet<Unit>();
        this.game = this.bwapi.getGame();
        this.self = game.self();
        this.frame = 0;
        
        // complete map information
        this.game.enableFlag(0);
        
        // user input
        this.game.enableFlag(1);
        this.game.setLocalSpeed(10);
        
        if (loadOldValues)
        {
        	ArrayList<String> arrSave = readFile(save);
        	VultXCS.Pop = parsePop(arrSave); 
        }
        loadOldValues = false;
        
    }
    
    public ClassifierSet parsePop(ArrayList<String> arrSave)
    {
    	ClassifierSet population = new ClassifierSet();
    	
    	for (String s : arrSave)
    	{
    		
    		String[] a = s.split(";");
    		
    		Classifier c = new Classifier(new Condition(a[0]),Integer.parseInt(a[1]),Double.parseDouble(a[2]),Double.parseDouble(a[3]),Double.parseDouble(a[4]),Integer.parseInt(a[5]),Integer.parseInt(a[6]),Double.parseDouble(a[7]),Integer.parseInt(a[8]));
    		population.clSet.add(c);
    	}
    	return population;
    }

    @Override
    public void onFrame() {
    	if (RoundCount > 0)
    	{
    		game.drawTextScreen(10, 10, "Round: " + RoundCount + " Wins: " + wins + " - " + (wins)/(RoundCount)*100 + " percent");
    		game.drawTextScreen(10, 20, "avg time: " + (time+1)/RoundCount + " s");
    	}
    	
    	vulture.step();
		
        if (frame % 1000 == 0) 
        {
            System.out.println("Frame: " + frame);
        }
        frame++;
    }

    @Override
    public void onUnitCreate(Unit unit) {
        System.out.println("New unit discovered " + unit.getType());
        UnitType type = unit.getType();

        if (type == UnitType.Terran_Vulture) {
            if (unit.getPlayer() == this.self) {
                this.vulture = new Vulture(unit, bwapi, enemyUnits);
            }
        } else if (type == UnitType.Protoss_Zealot) {
            if (unit.getPlayer() != this.self) {
               enemyUnits.add(unit);
            }
        }
    }
    
    @Override
    public void onUnitDestroy(Unit unit) {
    	if(this.enemyUnits.contains(unit)){
            this.enemyUnits.remove(unit);
    	}
    }

    @Override
    public void onEnd(boolean winner) 
    {
    	RoundCount++;
    	time += game.elapsedTime();
    	if (winner) wins++;
    	System.out.println("Round:" + RoundCount + " Wins:" + wins + " - " + (wins)/(RoundCount)*100.0d + "%");
    	
    	//logging
    	Alog.add(RoundCount + ";" + (time+1)/RoundCount + ";" + winner);
    	print2File(Alog);
    	
    	//saving
    	print2File(VultXCS.Pop);
    	
    	if (RoundCount % evaluateEvery == 0) new Evaluation.runEvaluation().evaluate();
    	
    }

	@Override
    public void onSendText(String text) {
    }

    @Override
    public void onReceiveText(Player player, String text) {
    }

    @Override
    public void onPlayerLeft(Player player) {
    }

    @Override
    public void onNukeDetect(Position position) {
    }

    @Override
    public void onUnitEvade(Unit unit) {
    }

    @Override
    public void onUnitShow(Unit unit) {

    }

    @Override
    public void onUnitHide(Unit unit) {
    }

    @Override
    public void onUnitMorph(Unit unit) {

    }

    @Override
    public void onUnitRenegade(Unit unit) {

    }

    @Override
    public void onSaveGame(String gameName) {
    }

    @Override
    public void onUnitComplete(Unit unit) {
    }

    @Override
    public void onPlayerDropped(Player player) {
    }

    @Override
    public void run() {
        this.bwapi.getModule().setEventListener(this);
        this.bwapi.startGame();
    }
    
	public void print2File(ArrayList<String> text)
	{
		try 
		{
			FileWriter pw = new FileWriter(log);
			for(int i=0;i<text.size();i++)
			{
				pw.write(text.get(i) + System.getProperty("line.separator"));
			}
			pw.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	
	}
	
    private void print2File(ClassifierSet clSet) 
    {
		try 
		{
			FileWriter pw = new FileWriter(save);
			for(Classifier cl : clSet.clSet)
			{
				pw.append(cl.toString() + System.getProperty("line.separator"));
			}
			pw.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
    
    private ArrayList<String> readFile(File f)
    {
    	ArrayList<String> text = new ArrayList<String>();
		try 
		{
			Path p = f.toPath();
			text = new ArrayList<String>();
			text.addAll(Files.readAllLines(p));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return text;	
    }
}

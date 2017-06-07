package Evaluation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.swing.JFrame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.IAxisScalePolicy;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;

public class runEvaluation 
{
	public ArrayList<String> text = new ArrayList<String>();
	
	public void evaluate()
	{
		readLog();
		evaluateRoundTimeRatio();
	}

	public void readLog()
	{
		try 
		{
			File log = new File(".//eval.txt");
			Path p = log.toPath();
			text = new ArrayList<String>();
			text.addAll(Files.readAllLines(p));
		}
		catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void evaluateRoundTimeRatio()
	{
		Chart2D chart = new Chart2D();
		IAxis<IAxisScalePolicy> xAxis = (IAxis<IAxisScalePolicy>) chart.getAxisX();
		IAxis<IAxisScalePolicy> yAxis = (IAxis<IAxisScalePolicy>) chart.getAxisY();
		xAxis.setAxisTitle(new AxisTitle("Round"));
		yAxis.setAxisTitle(new AxisTitle("Time in seconds"));
		
		ITrace2D trace = new Trace2DSimple();
		chart.addTrace(trace);
		trace.setName("");

		for(String s : text)
		{
			if (Boolean.parseBoolean(s.split(";")[2])) trace.addPoint(Double.parseDouble(s.split(";")[0]),Double.parseDouble(s.split(";")[1]));
		}
		
		JFrame frame = new JFrame("Round-TimeToWin-Ratio");
	    frame.getContentPane().add(chart);
	    frame.setSize(400,300); 
	    frame.addWindowListener(new WindowAdapter(){
    		public void windowClosing(WindowEvent e)
    		{
              System.exit(0);
    		}
	    });
	    frame.setVisible(true);
	}
}

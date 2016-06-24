package classify;

import java.io.File;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class ClassifyMain {

	public static void main(String[] args) throws Exception {
	
		ArffLoader arffLoader = new ArffLoader();
		arffLoader.setFile(new File("volde2F.arff"));
		Instances instances=arffLoader.getDataSet();
		ClassifyCalculate classifyCalculate=new ClassifyCalculate(instances);
		classifyCalculate.totalCal();
	}

}

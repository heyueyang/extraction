package classify;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instances;

public class Classify {
	Instances instances;

	public Classify(Instances ins,String classAttri) {
		this.instances = ins;
		instances.setClass(new Attribute(classAttri));
	}
	public void classify(){
		Classifier m_classifier = new J48();
		
	}
}

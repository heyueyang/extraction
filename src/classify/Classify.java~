package classify;

import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public abstract class Classify {
	Classifier cla;
	Evaluation eval;
	Instances ins;
	List<Double> res;

	public List<Double> getRes() {
		return res;
	}

	public Classifier getCla() {
		return cla;
	}

	public void setCla(Classifier cla) {
		this.cla = cla;
	}

	public Instances getIns() {
		return ins;
	}

	public void setIns(Instances ins) {
		this.ins = ins;
		ins.setClass(ins.attribute("bug_introducing"));
	}

	public Classify(Classifier classifier, Instances instances) {
		this.cla = classifier;
		this.ins = instances;
	}

	public Classify(Classifier classifier) {
		this.cla = classifier;
	}

	abstract void Evaluation() throws Exception;

}

package classify;

import java.util.ArrayList;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.Bagging;
import weka.core.Instances;

public class BaggingClassify extends Classify {
	Bagging bagging;

	public BaggingClassify(Classifier classifier, Instances instances) throws Exception {
		super(classifier, instances);
		bagging = new Bagging();
		bagging.setClassifier(cla);
		bagging.setBagSizePercent(50);
		bagging.setNumIterations(10);
	}

	@Override
	void Evaluation() throws Exception {
		// TODO Auto-generated method stub
		res=new ArrayList<>();
		eval = new Evaluation(ins);
		eval.crossValidateModel(bagging, ins, 10, new Random(1));
		res.add(eval.recall(0));
		res.add(eval.recall(1));
		res.add(eval.precision(0));
		res.add(eval.precision(1));
		res.add(eval.fMeasure(0));
		res.add(eval.fMeasure(1));
		res.add(eval.areaUnderROC(1));
		res.add(Math.sqrt(res.get(0)* res.get(1)));
		res.add(eval.totalCost());
	}
}

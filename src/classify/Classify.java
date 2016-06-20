package classify;

import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instances;

/**
 * weka分类器，采用十折交叉验证评估分类结果。
 * 
 * @param instances
 *            用于分类的实例。
 * @param classifier
 *            用于分类的分类器。
 * @param evaluation
 *            用于评估分类模型。
 * @param res
 *            存储分类后得到的若干指标。该数组中的值按索引从小到大依次对应于
 *            C-Recall，B-Recall，C-Precision，B-Precision
 *            ，C-Fmesure，B-Fmeasure，AUC，Gmean，time(?totalcost?)
 * @author niu
 *
 */
public class Classify {
	Instances instances;
	Classifier classifier;
	Evaluation evaluation;
	double[] res;

	/**
	 * 构造函数。
	 * 
	 * @param ins
	 *            用于分类的实例。
	 * @param classAttri
	 *            类属性。
	 * @param cla
	 *            使用的分类器。
	 */
	public Classify(Instances ins, String classAttri, Classifier cla) {
		this.instances = ins;
		this.classifier = cla;
		instances.setClass(instances.attribute("bug_introducing"));
	}

	/**
	 * 执行分类，将分类得到的若干结果存储到res数组中。
	 * 
	 * @throws Exception
	 */
	public void exectClassify() throws Exception {
		res = new double[9];
		evaluation = new Evaluation(instances);
		evaluation.crossValidateModel(classifier, instances, 10, new Random());
		res[0] = evaluation.recall(0);
		res[1] = evaluation.recall(1);
		res[2] = evaluation.precision(0);
		res[3] = evaluation.precision(1);
		res[4] = evaluation.fMeasure(0);
		res[5] = evaluation.fMeasure(1);
		res[6] = evaluation.areaUnderROC(1);
		res[7] = Math.sqrt(res[0] * res[1]);
		res[8]=evaluation.totalCost();
	}

	public Classifier getClassifier() {
		return classifier;
	}

	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	public double[] getRes() {
		return res;
	}

	public void setRes(double[] res) {
		this.res = res;
	}
}

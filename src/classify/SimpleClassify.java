package classify;

import java.util.ArrayList;
import java.util.List;
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
public class SimpleClassify extends Classify{

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
	public SimpleClassify( Classifier cla,Instances ins) {
		super(cla, ins);
		ins.setClass(ins.attribute("bug_introducing"));
	}

	public SimpleClassify(Classifier cla) {
		super(cla);
	}

	/**
	 * 执行分类，将分类得到的若干结果存储到res数组中。
	 * 
	 * @throws Exception
	 */
	@Override
public	void Evaluation() throws Exception {
		// TODO Auto-generated method stub
		res = new ArrayList<>();
		eval = new Evaluation(ins);
		eval.crossValidateModel(cla, ins, 10, new Random());
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

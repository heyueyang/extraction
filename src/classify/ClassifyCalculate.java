package classify;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * 分类计算类。用于计算各种分类模型下的结果。
 * 
 * @param classifys
 *            使用的分类器模型。
 * @param methods
 *            使用的采样模型。
 * @author niu
 *
 */
public class ClassifyCalculate {
	String[] classifys = { "weka.classifiers.bayes.NaiveBayes",
			"weka.classifiers.trees.J48", "weka.classifiers.functions.SMO" };
	String[] methods = { "standard", "undersample", "oversample", "bagging",
			"underBagging", "overBagging" };
	Instances ins;
	Map<List<String>, List<Double>> res;

	/**
	 * 构造函数，初始化要使用的用例集。
	 * 
	 * @param instances
	 *            将要用于分类的用力集，可能是不均衡的数据。
	 */
	public ClassifyCalculate(Instances instances) {
		this.ins = instances;
		res = new TreeMap<List<String>, List<Double>>(new Comparator<List<String>>() {

			@Override
			public int compare(List<String> o1, List<String> o2) {
			if (!o1.get(0).equals(o2.get(0))) {
				return o1.get(0).compareTo(o2.get(0));
			}else {
				return o1.get(1).compareTo(o2.get(1));
			}
			}		
		});
	}

	public void totalCal() throws Exception {
		List<Instances> subInstances = new ArrayList<>();
		subInstances.add(ins);
		subInstances.add(Sample.UnderSample(ins));
		subInstances.add(Sample.OverSample(ins));

		for (int i = 0; i < classifys.length; i++) {
			Classify classify = null;
			for (int j = 0; j < 3; j++) {
				List<String> keyList = new ArrayList<>();
				keyList.add(classifys[i]);
		keyList.add(methods[j]);
				
				classify = new SimpleClassify((Classifier) Class.forName(
						classifys[i]).newInstance(), subInstances.get(j));
				classify.Evaluation();
				res.put(keyList, classify.getRes());
			}

			for (int j = 3; j < 6; j++) {
				List<String> keyList = new ArrayList<>();
				keyList.add(classifys[i]);
				keyList.add(methods[j]);
				classify = new BaggingClassify((Classifier) Class.forName(
						classifys[i]).newInstance(), subInstances.get(j - 3));
				classify.Evaluation();
				res.put(keyList, classify.getRes());
			}
		}


		DecimalFormat df=new DecimalFormat("0.00");
		for (List<String> m : res.keySet()) {
			for (String string : m) {
				System.out.print(string+"  ");
			}
			for (Double value : res.get(m)) {
				System.out.print(df.format(value)+"  ");
			}
			System.out.println();
		}
	}
}

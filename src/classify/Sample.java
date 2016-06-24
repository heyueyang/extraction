package classify;

import java.io.IOException;
import java.util.Random;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * 采样类，用于对不均衡数据进行处理。方式主要有过采样和欠采样两种。
 * 
 * @author niu
 *
 */
public class Sample {
	private String file = "";

	public Sample(String str) {
		file = str;
	}

	/**
	 * 过采样方法。
	 * 
	 * @param init
	 * @return
	 * @throws IOException
	 */
	public static Instances OverSample(Instances init) throws IOException {
		FastVector attInfo = new FastVector();
		for (int i = 0; i < init.numAttributes(); i++) {
			weka.core.Attribute temp = init.attribute(i);
			attInfo.addElement(temp);
		}
		Instances YesInstances = new Instances("DefectSample1", attInfo,
				init.numInstances());// 这里的初始容量需要注意，不要小了。
		YesInstances.setClass(YesInstances.attribute("bug_introducing"));
		// YesInstances.setClassIndex(init.numAttributes() - 1);
		// 未能统一的将类标签作为最后一个属性，可能导致计算上的复杂，有待改进。
		Instances Noinstances = new Instances("DefectSample2", attInfo,
				init.numInstances());
		Noinstances.setClass(Noinstances.attribute("bug_introducing"));
		init.setClass(init.attribute("bug_introducing"));
		int classIndex = init.classIndex();
		int numAttr = init.numAttributes();
		int numInstance = init.numInstances();
		int numYes = 0;
		int numNo = 0;
		for (int i = 0; i < numInstance; i++) {
			Instance temp = init.instance(i);
			double Value = temp.value(classIndex);
			if (Value == 1) { // weka的内部值并不与属性的值相对应，参考weka api。
				YesInstances.add(temp);
				numYes++;
			} else // clear change
			{
				Noinstances.add(temp);
				numNo++;
			}
		}
		// 如果数量相等，实际上是没有执行过采样的。
		if (numYes == numNo) {
			return init;
		}
		Instances res;
		if (numYes > numNo) {
			res = excuteSample(YesInstances, Noinstances, 1);
		} else {
			res = excuteSample(Noinstances, YesInstances, 1);
		}
		return res;
	}

	/**
	 * 按照给定的比例进行过抽样。
	 * 
	 * @param instances1
	 *            主实例集，即依据的实例集，也就是全部使用的实例集。
	 * @param instances2
	 *            副实例集，也就是真正实行采样的实例集。
	 * @param i
	 *            抽样后得到的不同的类标签的比例，即抽样后num(yesInstances)/num(noinstances)的比例，注意，
	 *            由于为了 加速程序运行速度，最后实验结果抽样时设置为1。
	 */
	private static Instances excuteSample(Instances instances1,
			Instances instances2, double ratio) {
		int numSample = (int) Math.ceil(instances1.numInstances() * ratio); // 会不会由于实例数过多而崩溃？
		int numNo = instances2.numInstances();
		Random rn = new Random();
		for (int i = 0; i < numSample; i++) {
			instances1.add(instances2.instance(rn.nextInt(numNo)));
		}
		return instances1;
	}

	public static Instances UnderSample(Instances init) throws IOException {
		int numAttr = init.numAttributes();
		int numInstance = init.numInstances();

		FastVector attInfo = new FastVector();
		for (int i = 0; i < numAttr; i++) {
			weka.core.Attribute temp = init.attribute(i);
			attInfo.addElement(temp);
		}

		Instances NoInstances = new Instances("No", attInfo, numInstance);
		NoInstances.setClass(NoInstances.attribute("bug_introducing"));
		Instances YesInstances = new Instances("yes", attInfo, numInstance);
		YesInstances.setClass(YesInstances.attribute("bug_introducing"));
		int numYes = 0;
		int numNo = 0;
		for (int i = 0; i < numInstance; i++) {
			Instance temp = init.instance(i);
			double Value = temp.value(numAttr - 1);
			if (Value == 0) { // yes
				NoInstances.add(temp);
				numNo++;
			} else {
				YesInstances.add(temp);
				numYes++;
			}
		}
		if (numYes == numNo) {
			return init;
		}
		Instances res;
		if (numYes > numNo) {
			res = excuteSample(NoInstances, YesInstances, 1);
		} else {
			res = excuteSample(YesInstances, NoInstances, 1);
		}
		return res;
	}

}
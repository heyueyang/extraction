package classify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

public class PreProcess {
	public static void main(String[] args) throws IOException {
		rmAttribute("/home/niu/voldeFile/123d.arff");
	}

	public static Instances rmAttribute(String string) throws IOException {
		File file = new File(string);
		ArffLoader atf = new ArffLoader();
		atf.setFile(file);
		Instances instances = atf.getDataSet();
		int numAttr = instances.numAttributes();
		int numIns = instances.numInstances();
		System.out.println("total number of the instance is "+numIns);
		System.out.println("total number of the instance is "+numAttr);
		List<Integer> deleAttri = new ArrayList<>();
		for (int i = 0; i < numAttr; i++) {
			if (instances.attribute(i).isNumeric()) {
				double count = 0;
				for (int j = 0; j < numIns; j++) {
					if (instances.instance(j).value(i)== 0) {
						count++;
					}
				}
				double rate = count / numIns;
				if (rate > 0.99) {
					deleAttri.add(i);
				}
			}
		}
//怎么删掉了这么多？
		System.out.println("delete "+deleAttri.size()+" redundancy attributes");
		for (int i = 0; i < deleAttri.size(); i++) {
			int deltIndex = deleAttri.get(i) - i;
			instances.deleteAttributeAt(deltIndex);
		}
		ArffSaver arffSaver=new ArffSaver();
		arffSaver.setInstances(instances);
		arffSaver.setFile(new File("123d2.arff"));
		arffSaver.writeBatch();
		return instances;
	}

}

package classify;

import java.io.IOException;
import java.util.Random;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class Sample{
	 private String file = "";
	 public Sample(String str){
		 file = str;
	 }
	
		public static Instances OverSample(Instances init) throws IOException  //deal the data for under sampling
		{
			FastVector attInfo= new FastVector();
			for(int i = 0;i<init.numAttributes();i++)
			{
				weka.core.Attribute temp = init.attribute(i);
				attInfo.addElement(temp);
			}
			Instances YesInstances = new Instances("DefectSample1",attInfo,init.numInstances());
			YesInstances.setClassIndex(init.numAttributes() - 1);
			Instances instances = new Instances("DefectSample2",attInfo,init.numInstances());
			instances.setClassIndex(init.numAttributes() - 1);

			int numAttr = init.numAttributes();
			int numInstance = init.numInstances();
			int numYes = 0;
			int numNo = 0;
			for(int i = 0;i<numInstance;i++)
			{
				Instance temp = init.instance(i);
				double Value = temp.value(numAttr-1);
				if(Value == 1){   //bug change
					instances.add(temp);
					numNo++;
				}else     //clear change
				{
					YesInstances.add(temp);
					instances.add(temp);
					numYes++;
				}
			}
			YesInstances.randomize(new Random());

			int numCopy = (int) Math.ceil(numNo*1)-numYes;   //����Ϊ40:60
			for(int i=0;i<numCopy;i++)
			{
				Random rn = new Random();
				Instance temp = YesInstances.instance(rn.nextInt(numYes));
				instances.add(temp);
			}

			return instances;

		}

		public static Instances UnderSample(Instances instance) throws IOException  //deal the data for under sampling  ROS
		{
			int numAttr = instance.numAttributes();
			int numInstance = instance.numInstances();

			FastVector attInfo= new FastVector();
			for(int i = 0;i<numAttr;i++)
			{
				weka.core.Attribute temp = instance.attribute(i);
				attInfo.addElement(temp);
			}

			Instances NoInstances = new Instances("No",attInfo,numInstance);
			NoInstances.setClassIndex(numAttr - 1);
			Instances res = new Instances("res",attInfo,numInstance);
			res.setClassIndex(numAttr - 1);
			int numYes = 0;
			int numNo = 0;
			for(int i = 0;i<numInstance;i++)
			{
				Instance temp = instance.instance(i);
				double Value = temp.value(numAttr-1);
				if(Value == 0){  //yes
					res.add(temp);
					numYes++;
				}else
				{
					NoInstances.add(temp);
					numNo++;
				}
			}

			int numSample = numYes*1;
			for(int i=0;i<numSample;i++)
			{
				Random rn = new Random();
				Instance temp = NoInstances.instance(rn.nextInt(numNo));
				res.add(temp);
			}
			return res;
		}
		
}
package classify;
import java.io.File;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Calendar;
import java.util.Random;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.Bagging;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

public class ResultStatis{
		static String[] c = {"weka.classifiers.bayes.NaiveBayes","weka.classifiers.trees.J48","weka.classifiers.functions.SMO"};
		
		public void run(String resultFolder, int sheet,String project) throws Exception{
			
		
			String inputFile = null;
			File input = null;
			ArffLoader loader = null;
			Instances data = null; 
			int numIns = 0;
			int numAttr = 0;
			long start = 0;
			long end = 0;
			double time = 0;
			Calendar calendar = Calendar.getInstance();

			String out = resultFolder+"result0"+".xls";
			File output = new File(out);
	    	String[] head = {"classifier","bag","accuracy","gmean","recall-0","recall-1","precision-0","precision-1","fMeasure-0","fMeasure-1","AUC","time"};	    	
			String[] methods = {"standard","undersample","oversample","bagging","underBagging","OverBagging"};//,"SMOTELog","OverLog","UnderLog"
			int cnt = c.length;
			String[][] result = new String[(cnt)*methods.length][12];
			double[] temp = new double[12];

			Evaluation eval = null;
			Class<?> c1 = null;
			weka.classifiers.Classifier clas = null;
			int m =0;
			int classIndex = 0;
			double run_times = 10.0;
				
			for(int i = 0;i < cnt; i++){	
				inputFile = Config.select_folder + project;
				input = new File(inputFile);
				loader = new ArffLoader();
				loader.setFile(input);
				data = loader.getDataSet(); 
				numIns = data.numInstances();
				numAttr = data.numAttributes();
				classIndex = numAttr-1;
				data.setClassIndex(classIndex);
				data.randomize(new Random());
				
				for(int j = 0;j < methods.length;j++){
					start = System.currentTimeMillis();
					for(int t = 0;t < run_times; t++){
						
						switch(j){
							case 0:{
								//start = System.currentTimeMillis();
								c1 = Class.forName(c[i]);
								clas = (weka.classifiers.Classifier) c1.newInstance();
								eval = new Evaluation(data);
								eval.crossValidateModel(clas, data, 10, new Random());
								//end = System.currentTimeMillis();
								//time = (end-start);
								break;
							
							}
							case 1:{
								//start = System.currentTimeMillis();
								Bagging c2 = new Bagging();
								c2.setClassifier((weka.classifiers.Classifier) Class.forName(c[i]).newInstance());
								c2.setBagSizePercent(50);

								eval = new Evaluation(data);
								eval.crossValidateModel(c2, data, 10, new Random(1));
								//System.out.println(eval.toClassDetailsString());
								//System.out.println(c2.toString());
								//end = System.currentTimeMillis();
								//time = (end-start);
								break;
							}
							case 2:{
								//start = System.currentTimeMillis();
								//inputFile = Config.select_folder + project.substring(0,project.lastIndexOf(".")) + "_" + "undersample.arff";
								//System.out.println(inputFile);
								Instances ins = Sample.UnderSample(data);
								numIns = ins.numInstances();
								numAttr = ins.numAttributes();
								ins.setClassIndex(classIndex);
								ins.randomize(new Random());
								
								c1 = Class.forName(c[i]);
								clas = (weka.classifiers.Classifier) c1.newInstance();
								eval = new Evaluation(ins);
								eval.crossValidateModel(clas, ins, 10, new Random());
								//end = System.currentTimeMillis();
								//time = (end-start);
								break;
							}
							case 3:{
								//start = System.currentTimeMillis();
								//inputFile = Config.select_folder + project.substring(0,project.lastIndexOf(".")) + "_" + "oversample.arff";
								Instances ins = Sample.OverSample(data);
								numIns = ins.numInstances();
								numAttr = ins.numAttributes();
								ins.setClassIndex(classIndex);
								ins.randomize(new Random());
								
								c1 = Class.forName(c[i]);
								clas = (weka.classifiers.Classifier) c1.newInstance();
								eval = new Evaluation(ins);
								eval.crossValidateModel(clas, ins, 10, new Random());
								//end = System.currentTimeMillis();
								//time = (end-start);
								break;
							}
							case 4:{
								//start = System.currentTimeMillis();
								//inputFile = Config.select_folder + project.substring(0,project.lastIndexOf(".")) + "_" + "undersample.arff";
								Instances ins = Sample.UnderSample(data);
								numIns = ins.numInstances();
								numAttr = ins.numAttributes();
								ins.setClassIndex(classIndex);
								ins.randomize(new Random());
								
								Bagging c2 = new weka.classifiers.meta.Bagging();
								c2.setClassifier((weka.classifiers.Classifier) Class.forName(c[i]).newInstance());
								c2.setBagSizePercent(50);
								
								eval = new Evaluation(ins);
								eval.crossValidateModel(c2, ins, 10, new Random());
								//end = System.currentTimeMillis();
								//time = (end-start);
								break;
							}
							case 5:{
								//start = System.currentTimeMillis();
								//inputFile = Config.select_folder + project.substring(0,project.lastIndexOf(".")) + "_" + "oversample.arff";
								Instances ins = Sample.OverSample(data);
								numIns = ins.numInstances();
								numAttr = ins.numAttributes();
								ins.setClassIndex(classIndex);
								ins.randomize(new Random());
								
								Bagging c2 = new weka.classifiers.meta.Bagging();
								c2.setClassifier((weka.classifiers.Classifier) Class.forName(c[i]).newInstance());
								c2.setBagSizePercent(50);
								
								eval = new Evaluation(ins);
								eval.crossValidateModel(c2, ins, 10, new Random());
								//end = System.currentTimeMillis();
								//time = (end-start);
								break;
							}
						
					}

						temp[0] = 0;
						temp[1] = 0;
						temp[2] += 1-eval.errorRate();
				        temp[3] += Math.sqrt(eval.recall(0)*eval.recall(1));
				        temp[4] += eval.recall(0);//sampleRatio//
				        temp[5] += eval.recall(1);
				        temp[6] += eval.precision(0);
				        temp[7] += eval.precision(1);
				        temp[8] += eval.fMeasure(0);
				        temp[9] += eval.fMeasure(1);
				        temp[10] += eval.areaUnderROC(0);	
				        temp[11] += 0;
				       
				    }
					end = System.currentTimeMillis();
					time = (end-start);
					//System.out.println(eval.toClassDetailsString());
					result[m][0] = c[i];
					result[m][1] = methods[j];
					result[m][2] = String.valueOf(temp[2]/run_times);
			        result[m][3] = String.valueOf(temp[3]/run_times);
			        result[m][4] = String.valueOf(temp[4]/run_times);//sampleRatio//
			        result[m][5] = String.valueOf(temp[5]/run_times);
			        result[m][6] = String.valueOf(temp[6]/run_times);
			        result[m][7] = String.valueOf(temp[7]/run_times);
			        result[m][8] = String.valueOf(temp[8]/run_times);
			        result[m][9] = String.valueOf(temp[9]/run_times);
			        result[m][10] = String.valueOf(temp[10]/run_times);	
			        result[m][11] = String.valueOf(time) + "ms";
			        m++;
			        for(int p = 0 ; p < 12; p++) temp[p] = 0;
					
				}
				
	
			}
			
		    exportFile(result, out, project, sheet, head);
		 	System.out.println("======Time Used:"+0+"======");
		}



		public static void exportFile(String[][] res,String file,String project, int sh, String[] head) throws Exception {  
			try { 
				File resultFile = new File(file);
				WritableWorkbook wbook = null;
				WritableSheet wsheet = null;
				if(!resultFile.exists()){
					wbook = Workbook.createWorkbook(resultFile); //����excel�ļ�  
					wsheet = wbook.createSheet("result",0); //���������
				}else{
				
					Workbook wb = Workbook.getWorkbook(resultFile);
					wbook = Workbook.createWorkbook(resultFile,wb); //����excel�ļ�  
					wsheet = wbook.getSheet(0); //���������  
				}
				
				//WritableSheet wsheet = wbook.createSheet("result",0); //���������  

				
				int row = wsheet.getRows();
				System.out.println("======Rows:"+row+"======");
				
				jxl.write.Label content = null;
				if(row<=0){
					for(int j = 0;j<res[0].length; j++){
						if(j<head.length){
						content = new jxl.write.Label(j, 0, head[j]);
						}else{
							content = new jxl.write.Label(j, 0, "");
						}
						
						wsheet.addCell(content);
					}
				}
				
				row = wsheet.getRows();
				
				content = new jxl.write.Label(0, row, project); 
				wsheet.addCell(content);
				
				for(int i = 0; i<res.length; i++)
				{//��
					for(int j = 0;j<res[i].length; j++)
					{//��
						content = new jxl.write.Label(j, row+i+1, String.valueOf(res[i][j])); 
						wsheet.addCell(content);
					}

				} 

				wbook.write(); //д���ļ�  
				wbook.close();  
			} catch (Exception e) {  
				throw new Exception("�����ļ�����");  
			}  
			
		}
	}
	
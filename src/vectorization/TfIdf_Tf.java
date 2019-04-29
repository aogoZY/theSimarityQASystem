package vectorization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import preprocessing.utility.FileOperate;

/**
 * 对预处理后的文本进行向量化操作
 * 本程序包含了两种向量化算法：TF, TFIDF
 * @author Alex
 *
 */
public class TfIdf_Tf {
	List<String> corpus = new ArrayList<String>(); // 预处理后的原始数据
	List<String> featureWords = new ArrayList<String>(); // 特征词
	List<Float> idf = new ArrayList<Float>(); // 逆向文档概率
	List<String> labels = new ArrayList<String>(); // 标签信息
	List<Integer> amounts = new ArrayList<Integer>(); // 每个类别下的样本数量
	
	/**
	 * 准备工作：读入要进行向量化的语料与特征
	 * @param inputFilePath 输入文件存放的路径
	 */
	private void prepare(String inputFilePath) {
		/**
		 * 读入预处理后的文本
		 */
		System.out.println("准备工作：读入语料和特征词，并进行相应的初始化……");
		File infile1 = new File(inputFilePath + "\\preprocessedCorpus.txt");
		FileOperate.ReadFromTxt(corpus, infile1, true);		
		System.out.println("评论数 = " + (corpus.size() - 1));
		
		// 保存类别信息，该信息存存储在语料文本的第一行：
		// @data,neg:2000,pos:2000
		String classInformation = corpus.get(0);
		String fields[] = classInformation.split(",");
		amounts.add(0); // 把每个类别下样本的数量看成区间段，例如：0-2000，2001-4000
		for(int i = 1; i != fields.length; ++i) {
			String field = fields[i];
			String str[] = field.split(":");
			labels.add(str[0]);
			amounts.add(Integer.parseInt(str[1]) + amounts.get(amounts.size() - 1));
		}

		/**
		 * 读入特征词
		 */
		File infile2 = new File(inputFilePath + "\\preprocessedFeatureWords.txt");
		FileOperate.ReadFromTxt(featureWords, infile2, false);
		System.out.println("特征词数 = " + featureWords.size());
		System.out.println("准备工作完成。");
		
	}
	
	/**
	 * 非归一化词频
	 * @param bufferedWriter 写入文件流
	 * @throws IOException
	 */
	private void tfCompute(BufferedWriter bufferedWriter) throws IOException{
		System.out.println("TF向量化……");
		// 词频TF，不进行归一化
		int tf_ij_count = 0;
		for (int i = 1; i < corpus.size(); ++i) {	// 循环从1开始，因为第0行保存的是语料的相关信息
			for (int j = 0; j < featureWords.size(); ++j) {
				tf_ij_count = 0; // 记录第j个特征词在第i条评论中出现的次数，即不进行归一化的词频

				String[] words = corpus.get(i).split(" "); // 按空格对句子进行划分
				for (String word : words) {
					if (word != "") // 去掉一条评论末尾的空串，在切分的时候把末尾空格后面的空串又误当作（词/词性）对存进了word中
					{
						if (featureWords.get(j).equals(word)) {
							++tf_ij_count;
						}
					}
				}

				bufferedWriter.write(tf_ij_count + ",");
			}
			// 输出每一条样本的标签
			for(int z = 0; z != (amounts.size() - 1); ++z) {
				if(i > amounts.get(z) && i <= amounts.get(z + 1)) {
					bufferedWriter.write(labels.get(z));
					break;
				}
			}
			// 换行
			bufferedWriter.newLine();
		}
		System.out.println("TF向量化完成。");
	}
	
	/**
	 * 进行TF-IDF向量化操作
	 * @param bufferedWriter 写入文件流
	 * @throws IOException
	 */
	private void tfidfCompute(BufferedWriter bufferedWriter) throws IOException {
		/**
		 * 特征词权值计算
		 */
		
		File outFile = new File("file\\Corpus\\FAQ\\vectorization\\IDF.txt");
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outFile, false)); // 往txt中追加内容
		BufferedWriter bufferedWriterIDF = new BufferedWriter(writer); // 将使用Tf-Idf向量化后的结果写入到txt文档中
		
		System.out.println("TFIDF向量化…………");
		// 计算逆向文档频率IDF
		for (int j = 0; j < featureWords.size(); ++j) {
			float df_i = 0; // df_i为至少含有一次特征词t_j的评论文本数
			float idf_j = 0; // 特征词t_j在评论集中的逆向文档频率
			for (int i = 0; i < corpus.size(); ++i) {
				String[] words = corpus.get(i).split(" "); // 按空格对句子进行划分
				for (String word : words) {
					if (featureWords.get(j).equals(word)) {
						++df_i;
						break;
					}
				}
			}
			idf_j = (float) (Math.log((corpus.size() / (df_i + 1))) / Math.log(2)); // 根据公式计算IDF，取对数时，取底为2；Math.log默认底数为e
			if (idf_j >= 0) // 当计算出来的IDF<0时，直接取零。因为此时该词在每一个文档中都出现了，它的反向文档概率应该为0
			{
				idf.add(idf_j);
				bufferedWriterIDF.write(featureWords.get(j) + "," + idf_j);
				bufferedWriterIDF.newLine();
			} else {
				idf.add(0f);
				bufferedWriterIDF.write(featureWords.get(j) + "," + 0f);
				bufferedWriterIDF.newLine();
			}
		}
		bufferedWriterIDF.close();
		
		// 计算TF归一化词频，为了提高效率，而没有重用代码
		// 循环计算归一化词频TF
		for (int i = 1; i < corpus.size(); ++i) {	// 循环从1开始，因为第0行保存的是语料的相关信息
			for (int j = 0; j < featureWords.size(); ++j) {
				float tf_ij = 0; // 第j个特征词在第i条评论中的归一化词频
				float tf_ij_count = 0; // 记录第j个特征词在第i条评论中出现的次数
				float tf_i_count = 0; // 记录第i条评论中所有词语出现次数之和 

				String[] words = corpus.get(i).split(" "); // 按空格对句子进行划分
				for (String word : words) {
					if (word != "") // 去掉一条评论末尾的空串，在切分的时候把末尾空格后面的空串又误当作（词/词性）对存进了word中
					{
						if (featureWords.get(j).equals(word)) {
							++tf_ij_count;
						}
						++tf_i_count;
					}
				}
				// 根据计算出来的tf_ij_count和tf_i_count来计算归一化词频tf_ij
				if (tf_ij_count != 0) // 对于tf_ij值为0的词，直接赋0值。这样也避开了分母tf_i为0的情况
				{
					tf_ij = tf_ij_count / tf_i_count; // 计算归一化词频
					
				} else {
					tf_ij = 0;
				}
				bufferedWriter.write((tf_ij * idf.get(j)) + ",");
			}			

			// 换行
			bufferedWriter.newLine();
		}
		System.out.println("TFIDF向量化完成。");
	}
	
	/**
	 * 将向量化后的文本以Arff文件的格式输出
	 * @param outputFilePath 输出文件的路径
	 * @param type 向量化的类型（本程序包含两种：TF, TFIDF）
	 * @throws IOException
	 */
	public void print2Arff(String outputFilePath, String type) throws IOException {
		File outFile = new File(outputFilePath + "\\" + type + ".txt");
		// 如果待输出文件的父路径不存在，则创建之
		File fileParent = outFile.getParentFile();
		if(!fileParent.exists()) {
			fileParent.mkdirs();
		}
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outFile, false)); // 往txt中追加内容
		BufferedWriter bufferedWriter = new BufferedWriter(writer); // 将使用Tf-Idf向量化后的结果写入到txt文档中
		
		
		// 数据信息
		if(type == "Tf") {
			tfCompute(bufferedWriter);
		} else if(type == "TfIdf") {
			tfidfCompute(bufferedWriter);
		}
		bufferedWriter.close();
		System.out.println("向量化的结果已以Arff格式写入文件。");
	}
	
	// 运行TF向量化操作
	public void runTf(String inputFilePath, String outputFilePath) throws IOException {
		prepare(inputFilePath);
		print2Arff(outputFilePath, "Tf");
	}
	
	// 运行TFIDF向量化操作
	public void runTfIdf(String inputFilePath, String outputFilePath) throws IOException {
		prepare(inputFilePath);
		print2Arff(outputFilePath, "TfIdf");
	}

	public static void main(String[] args) throws IOException {
		String rootPath = ".\\file\\Corpus\\"; // 存放语料的根路径
		String inputFilePath = rootPath + "FAQ\\preprocessed";
		String outputFilePath = rootPath + "FAQ";
		
		TfIdf_Tf tfidf_tf = new TfIdf_Tf();
		outputFilePath += "\\vectorization";
		tfidf_tf.runTfIdf(inputFilePath, outputFilePath);
//		tfidf_tf.runTf(inputFilePath, outputFilePath);

	}
}

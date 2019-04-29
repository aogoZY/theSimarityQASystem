package faq.tfidfSimilarity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import preprocessing.chineseWordSegmentation.code.NlpirTest;
import preprocessing.stopWordsFilter.StopWordsFilter;
import preprocessing.utility.FileOperate;

public class CosineSimilarity {

	public static void main(String[] args) {
		Map<Integer, Double> result = new HashMap<Integer, Double>();
		List<Double> questionVector = new ArrayList<Double>();
		List<String> preComputeVector = new ArrayList<String>();
		Map<String, String> idf = new HashMap<String, String>();
		List<String> featureWords = new ArrayList<String>();
		List<String> answers = new ArrayList<String>();
		// 输入问题
		System.out.println("请输入问题：");
		Scanner cin = new Scanner(System.in);
		String questionStr = cin.nextLine();
		System.out.println(questionStr);
		// 分词与词性标注
		String questionStrSegment = NlpirTest.singleSentence(questionStr);
		System.out.println(questionStrSegment);
		// 停用词过滤
		StopWordsFilter swf = new StopWordsFilter();
		String questionStrStopWords = swf.singleRun(questionStrSegment);
		System.out.println(questionStrStopWords);
		// 问句向量化
		// 读入IDF值
		String charset = "GBK";// GBK----0
		File idfFile = new File("file\\Corpus\\FAQ\\vectorization\\IDF.txt");
		if (idfFile.isFile() && idfFile.exists()) {
			try {
				InputStreamReader read;
				read = new InputStreamReader(new FileInputStream(idfFile), charset); // 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				// 循环读入文件中的每一行，并保存到List容器中
				while ((lineTxt = bufferedReader.readLine()) != null) {
					// 该if语句判断读入的字符串是否为空串，空串不进行读入
					if (lineTxt.isEmpty()) {	
						
					} else {
						String[] words = lineTxt.split(",");
						featureWords.add(words[0]);
						questionVector.add(0d);
						idf.put(words[0], words[1]);
					}
				}
				bufferedReader.close();

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String[] questionWords = questionStrStopWords.split(" ");
		
		// 计算问句的TFIDF值
		double y = 0d;
		for(String word : questionWords) {
			double tf = 0;
			for(String _word : questionWords) {
				if(word == _word) {
					++tf;
				}
			}
			if(idf.get(word) != null) {
				double tfidfValue = (tf / questionWords.length) * Double.parseDouble(idf.get(word));
				y += (tfidfValue * tfidfValue);
				for(int i = 0; i != featureWords.size(); ++i) {
					//System.out.println(featureWords.get(i));
					if(featureWords.get(i).equals(word)) {
						questionVector.set(i, tfidfValue);
//						System.out.println("---" + word);
					}
				}
			}

		}
		
		// 计算余弦相似度
		File squareRoot = new File("file\\Corpus\\FAQ\\precomputation\\squareRoot.txt");
		FileOperate.ReadFromTxt(preComputeVector, squareRoot, true);
		
		File tfidfInputFile = new File("file\\Corpus\\FAQ\\vectorization\\TfIdf.txt");
		if (tfidfInputFile.isFile() && tfidfInputFile.exists()) {
			try {
				InputStreamReader read;
				read = new InputStreamReader(new FileInputStream(tfidfInputFile), charset); // 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				double dvalue = 0d;
				// 循环读入文件中的每一行，并计算余弦相似度
				int iter = 0;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					double xy = 0d;
					double cosine = 0d;
					String[] demensions = lineTxt.split(",");
					for(int i = 0; i != demensions.length; ++i) {
						dvalue = Double.parseDouble(demensions[i]);
						if(dvalue > 0){
//							System.out.println(dvalue);
							xy += (dvalue * questionVector.get(i));
						}
					}
					
//					System.out.println(xy);
//					System.out.println(x);
					// 计算余弦相似度
					cosine = xy / (Math.sqrt(y) * Double.parseDouble(preComputeVector.get(iter)));
					if(cosine > 0) {
						result.put((iter + 1), cosine);
						System.out.println((iter + 1) + ":" + cosine);
					}
					++iter;
				}
				bufferedReader.close();

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// 将相似度的结果进行排序
		System.out.println();
		List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(result.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			// 降序排序
			public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		
		// 读入答案语料
		File aInputFile = new File("file\\Corpus\\FAQ\\A_.txt");
		FileOperate.ReadFromTxt(answers, aInputFile, true);
		
		// 显示排序后的结果，采用TopN策略
		int top = 5;
		int current = -1;
		boolean flag1 = false;
		System.out.println("Top" + top);
		for(Map.Entry<Integer, Double> mapping: list) {
			if((++current) == top) {
				break;
			} else if(mapping.getValue() > 0.5) {
				flag1 = true;
				System.out.println(mapping.getKey() + ": " + mapping.getValue());
				System.out.println(answers.get(mapping.getKey() - 1));
			}
			
		}
		
		if(current == -1 || flag1 == false) {
			System.err.println("对不起，本FAQ库中暂时没有与您的提问相关的内容，我们将努力改进！");
		}
		
	}
}

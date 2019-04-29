package preprocessing.resultIntegration;

import java.io.File;
import java.util.ArrayList;

import preprocessing.utility.FileOperate;
import preprocessing.utility.GetFileName;

/**
 * 对预处理后的文件进行整合，便于下一步向量化的处理
 * @author Alex
 *
 */
public class ResultIntegration {
	ArrayList<String> comment = new ArrayList<String>(); 		// 用于保存每个类别下的所有评论
	ArrayList<String> filesPath = new ArrayList<String>();		// 用于保存当前文件夹下所有子文件的路径
	ArrayList<String> filesName = new ArrayList<String>();		// 用于保存当前文件夹下所有子文件的文件名
	ArrayList<String> featureWords = new ArrayList<String>();	// 用于保存特征词
	String headInformation = "@data";							// 用于保存相关说明信息，作为输出文件的第一行
	
	
	/**
	 * 读入当前文件夹下的所有子文件，不考虑当前文件夹下的子文件夹
	 * @param filePath 输入文件夹，即当前文件夹
	 * @return 是否读取成功
	 */
	private boolean obtainFile(String filePath){
		File files = new File(filePath);
		if(!files.isDirectory()) {
			System.out.println("输入的路径是一个文件，不合法，请重新输入！！！");
			return false;
		} else if(files.isDirectory()) {
            String[] filelist = files.list();
            for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(filePath + "\\" + filelist[i]);
                    if (!readfile.isDirectory()) {
                    	filesPath.add(readfile.getPath());
                    	filesName.add(readfile.getName());
                    } else if (readfile.isDirectory()) {
                    	
                    }
            }
		}
		return true;
	}
	
	/**
	 * 将多个类别下的评论样本合并，形成一个整体，并获得与语料有关的信息
	 * @param inputFilePath 输入文件夹路径
	 */
	private void integration(String inputFilePath) {
		System.out.println("语料合并中……");
		obtainFile(inputFilePath);
		// 处理每一个类别下的文件，合并成一个文件
		int oldSize = 0;
		for(int i = 0; i < filesPath.size(); ++i) {
			// 读入一个类别下的评论文本
			File inFile = new File(filesPath.get(i));
			FileOperate.ReadFromTxt(comment, inFile, true);
			// 保存该类别下的相关信息
			String tempClass = filesName.get(i).substring(0, filesName.get(i).indexOf("_"));
			headInformation += ("," + tempClass + ":" + (comment.size() - oldSize));
			oldSize = comment.size();
		}
		System.out.println("语料合并完成。");
	}
	
	/**
	 * 输出整合后的语料
	 * @param outputFilePath 输出路径
	 */
	private void printCorpus(String outputFilePath) {
		System.out.println("将合并语料写入txt中……");
		// 将头部信息添加到首部
		comment.add(0, headInformation);
		// 将该类别下的评论文本输出到最终的文件中
		File outFile = new File(outputFilePath += "Corpus.txt");
		// 如果待输出文件的父路径不存在，则创建之
		File fileParent = outFile.getParentFile();
		if(!fileParent.exists()) {
			fileParent.mkdirs();
		}
		FileOperate.Print2Txt(comment, outFile, false);
		comment.remove(0);
		System.out.println("写入完成。");
	}
	
	/**
	 * 根据语料产生一个特证词典（所有词去空去重）
	 */
	private void FeatureWordsGeneration(){
		ArrayList<String> corpusWords = new ArrayList<String>();
		for(int i = 0; i < comment.size(); ++i) {
			// 该if语句判断读入的字符串是否为空串，空串不进行读入
			if (!comment.get(i).isEmpty()) { // 用于判断是否读入空串
				String line = comment.get(i);
				line = line.trim();
				String[] words = line.split(" ");
				for (String word : words) {
					corpusWords.add(word); // 读入每一个词语
				}
			}
		}
		
		// 提取语料中的词汇
		System.out.println("特征词词典生成中……");
		boolean IsRepeated;	// 用于判断当前词是否是重复词
        featureWords.add(corpusWords.get(0));
        for (int i = 1; i != corpusWords.size(); ++i)
        {
            IsRepeated = false;
            for (int j = 0; j != featureWords.size(); ++j)
            {
                if (corpusWords.get(i).equals(featureWords.get(j)))
                {
                    IsRepeated = true;
                    break;
                }
            }
            if (!IsRepeated)
            {
                featureWords.add(corpusWords.get(i));
            }
        }
        System.out.println("生成完成。");
	}
	
	/**
	 * 得提取的特证词输出到文本文档中
	 * @param outputFilePath 输出路径
	 */
	private void printFeatureWords(String outputFilePath){
		System.out.println("将特征词词典写入txt中……");
		File outFile = new File(outputFilePath + "FeatureWords.txt");
		// 如果待输出文件的父路径不存在，则创建之
		File fileParent = outFile.getParentFile();
		if(!fileParent.exists()) {
			fileParent.mkdirs();
		}
		FileOperate.Print2Txt(featureWords, outFile, false);
		System.out.println("写入完成。");
	}
	
	public void run(String inputFilePath, String outputFilePath){
		integration(inputFilePath);
		printCorpus(outputFilePath);
		FeatureWordsGeneration();
		printFeatureWords(outputFilePath);
	}
	
	public static void main(String[] args) {
		String rootPath = ".\\file\\Corpus\\"; // 存放语料文件的根路径
		String inputFilePath = rootPath + "FAQ\\stopWordsFilter10000";// 输入文件的路径
		String outputFilePath = rootPath + "FAQ";// 保存整合后的结果
		
		ResultIntegration rt = new ResultIntegration();
		outputFilePath += ("\\preprocessed\\preprocessed"); // 确定输出文件的最终路径
		rt.run(inputFilePath, outputFilePath);
	}
	
}

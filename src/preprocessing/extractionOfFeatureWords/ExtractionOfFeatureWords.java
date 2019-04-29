package preprocessing.extractionOfFeatureWords;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import preprocessing.utility.FileOperate;
import preprocessing.utility.GetFileName;

public class ExtractionOfFeatureWords {
	List<String> data = new ArrayList<String>();   // 原数据
    List<String> FeatureWordsList = new ArrayList<String>(); // 特征词列表
    List<String> FinishedFeatureWordList = new ArrayList<String>(); // 特征词提取后的数据
    List<String> FeatureWordList1 = new ArrayList<String>(); // 提取出来的评论文本中的特征词
    List<String> FeatureWordList2 = new ArrayList<String>(); // 提取出来的评论文本中的特征词（去重后）
    
    /**
     * 准备工作：读入需要处理的文件，读入情感词典用于进行特征提取
     * @param inputFilePath	输入文件的路径，即需要处理文件的路径
     */
    private void prepare(String inputFilePath){

        String dictionaryPath = ".\\file\\Dictionary\\情感词词典\\改进后情感词典去空去重版20161219.txt";	// 词典根路径

        /**
         * 读入停用词过滤后的数据
         */
//		File inFile1 = new File(corpusPath + "SemiSuperviesdLearning\\posStopWordsFilter.txt");
		File inFile1 = new File(inputFilePath);
		FileOperate.ReadFromTxt(data, inFile1, true);

        /**
         *  读入情感词典中的情感词
         */
//		File inFile2 = new File(dictionaryPath + "情感词词典\\改进后情感词典去空去重版20161219.txt");
		File inFile2 = new File(dictionaryPath);
		FileOperate.ReadFromTxt(FeatureWordsList, inFile2, false);
    }
    
    /**
     * 进行特征词提取
     * @param outputFilePath 输出文件的路径，即把特证词提取后的数据输出到提定路径的文件中
     */
    private void extraction(String outputFilePath){
    	/**
		 * 特证词提取
		 */
        System.out.println("特征词提取中…………");
        //该循环进行特征词的提取
        for (int i = 0; i < data.size(); i++)
        {
            String FinishedFeatureWordLine = ""; //用于保存已完成特征词提取的一条评论
            if (data.get(i) != "")
            {
                String[] words = data.get(i).split(" ");
                for (String word : words)
                {
                    if (word != "")
                    {
                    	// 与给定的词典进行匹配，判断当前词是否为特征词
                        for (int j = 0; j < FeatureWordsList.size(); j++)
                        {
                            if (word.equals(FeatureWordsList.get(j)))
                            {
                                FeatureWordList1.add(word);
                                FinishedFeatureWordLine += word + " ";
                                break;
                            }
                        }

                    }
                }

                FinishedFeatureWordList.add(FinishedFeatureWordLine);//将特征词提取后的单条评论加入到List中进行保存
            }//if
            else //如果是空串，则直接写入空串
            {
                FinishedFeatureWordList.add("");
            }

        }//for
        System.out.println("特征词提取完成。");

//        //对提取出来的特征词进行去重
//        System.out.println("对从文本中提取出来的特征词进行去重中…………");
//        boolean IsRepeated;	// 用于判断当前词是否是重复词
//        FeatureWordList2.add(FeatureWordList1.get(0));
//        for (int i = 1; i < FeatureWordList1.size(); i++)
//        {
//            IsRepeated = false;
//            for (int j = 0; j < i; j++)
//            {
//                if (FeatureWordList1.get(i).equals(FeatureWordList1.get(j)))
//                {
//                    IsRepeated = true;
//                    break;
//                }
//            }
//            if (!IsRepeated)
//            {
//                FeatureWordList2.add(FeatureWordList1.get(i));
//            }
//        }
//        System.out.println("去重完成。");

        //输出特征提取后的文本文件
        System.out.println("保存特征词提取后的语料……");
        File outFile1 = new File(outputFilePath);
		// 如果待输出文件的父路径不存在，则创建之
		File fileParent = outFile1.getParentFile();
		if(!fileParent.exists()) {
			fileParent.mkdirs();
		}
        FileOperate.Print2Txt(FinishedFeatureWordList, outFile1, false);
        System.out.println("保存完成。");
//
//        //输出从评论文本中提取出来的特征词
//        System.out.println("保存从语料中提取出来的特征词");
//        File outFile2 = new File(outputFilePath + "FeatureWords.txt");
//        FileOperate.Print2Txt(FeatureWordList2, outFile2);
    }
    
    public void run(String inputFilePath, String outputFilePath){
		prepare(inputFilePath);
		extraction(outputFilePath);
    }

	public static void main(String[] args) {
    	String rootPath = ".\\file\\Corpus\\";	// 语料根路径
    	String inputFilePath = rootPath + "HotelTest\\stopWordsFilter\\neg_Segmentation_stopWordsFilter.txt";	 // 输入文件的路径，即需要处理的文件路径
		String outputFilePath = rootPath + "HotelTest"; // 保存特征词提取后的评论文本
		
		/*
		 * 获取输入文件的文件名
		 */
		GetFileName gfp = new GetFileName();
		String fileName = gfp.getFileName(inputFilePath);
		
		outputFilePath += ("\\extractionOfFeatureWords\\" + fileName + "_extOfFeaWor.txt");
    	
    	ExtractionOfFeatureWords eofw = new ExtractionOfFeatureWords();
    	eofw.run(inputFilePath, outputFilePath);
	}
	

}

package preprocessing.stopWordsFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import preprocessing.utility.FileOperate;
import preprocessing.utility.GetFileName;

public class StopWordsFilter {
	List<String> data1 = new ArrayList<String>(); // 原数据
	List<String> data2 = new ArrayList<String>(); // 词语和词性(停用词过滤后的数据)
	List<String> datasolved = new ArrayList<String>(); // 停用词过滤后的数据
	List<String> words = new ArrayList<String>(); // 词语
	List<String> props = new ArrayList<String>(); // 词性
	List<String> StopwordList = new ArrayList<String>(); // 停用词列表
	List<String> StopwordPropsList = new ArrayList<String>(); // 停用词词性列表
	boolean IsStopword = false; // 判断是否是停用词
	boolean IsNull = false; // 判断是否是经过停用词过滤后该条评论为空
	
	/**
	 * 准备工作：读入停用词词典，并设置需要过滤掉的词性。读入需要处理的文本文件
	 * @param inputFilePath
	 */
	private void prepare(String inputFilePath){
		System.out.println("准备工作进行中……");
		/**
		 * 读入停用词表，此处使用的是哈工大停用词表扩展
		 */
		String dictionaryPath = ".\\file\\Dictionary\\停用词词典\\哈工大停用词表扩展.txt";	// 存放停用词词典的路径
		File file1 = new File(dictionaryPath);
		FileOperate.ReadFromTxt(StopwordList, file1, false);

		/**
		 * 构建停用词词性表，即愉有下列的词性的词，将被过滤掉
		 */
		// 标点符号
		StopwordPropsList.add("w");
		StopwordPropsList.add("wkz");
		StopwordPropsList.add("wky");
		StopwordPropsList.add("wyz");
		StopwordPropsList.add("wyy");
		StopwordPropsList.add("wj");
		StopwordPropsList.add("ww");
		StopwordPropsList.add("wt");
		StopwordPropsList.add("wd");
		StopwordPropsList.add("wf");
		StopwordPropsList.add("wn");
		StopwordPropsList.add("wm");
		StopwordPropsList.add("ws");
		StopwordPropsList.add("wp");
		StopwordPropsList.add("wb");
		StopwordPropsList.add("wh");

		// 介词
		StopwordPropsList.add("p");
		StopwordPropsList.add("pba"); // 把
		StopwordPropsList.add("pbei"); // 被

		// 连词
		StopwordPropsList.add("c");
		StopwordPropsList.add("cc"); // 并列连词

		// 助词
		StopwordPropsList.add("u");
		StopwordPropsList.add("uzhe");
		StopwordPropsList.add("ule");
		StopwordPropsList.add("uguo");
		StopwordPropsList.add("ude1");
		StopwordPropsList.add("ude2");
		StopwordPropsList.add("ude3");
		StopwordPropsList.add("usuo");
		StopwordPropsList.add("udeng");
		StopwordPropsList.add("uyy");
		StopwordPropsList.add("udh");
		StopwordPropsList.add("uls");
		StopwordPropsList.add("uzhi");
		StopwordPropsList.add("ulian");

		// 数词
		StopwordPropsList.add("m");
		StopwordPropsList.add("mq");

		// 量词
		StopwordPropsList.add("q");
		StopwordPropsList.add("qv");
		StopwordPropsList.add("qt");

		// 叹词
		StopwordPropsList.add("e");

		// 语气词
		StopwordPropsList.add("y");

		// 拟声词
		StopwordPropsList.add("o");

		// 字符串
		StopwordPropsList.add("x");
		StopwordPropsList.add("xe");
		StopwordPropsList.add("xs");
		StopwordPropsList.add("xm");
		StopwordPropsList.add("xu");

		// 代词
		StopwordPropsList.add("r");
		StopwordPropsList.add("rr");
		StopwordPropsList.add("rz");
		StopwordPropsList.add("rzt");
		StopwordPropsList.add("rzs");
		StopwordPropsList.add("rzv");
		StopwordPropsList.add("ry");
		StopwordPropsList.add("ryt");
		StopwordPropsList.add("rys");
		StopwordPropsList.add("ryv");
		StopwordPropsList.add("rg");

		/**
		 * 将分词后的评论读入data1中
		 */
		File file2 = new File(inputFilePath);
		FileOperate.ReadFromTxt(data1, file2, true);
		System.out.println("准备完成……");
	}
	
	/**
	 * 执行停用词过滤操作，并把过滤后的结果输出到TXT文件中
	 * @param outputFilePath 输出文件的路径
	 */
	private void filter(String outputFilePath){
		/**
		 * 该循环进行停用词的过滤
		 */
		System.out.println("停用词过滤中…………");
		for (int i = 0; i < data1.size(); i++) {
			
			if (data1.get(i) != "") {
				String StrSolved = "";	// 用于保存该行语料中的非停用词
				String[] aWordsandProps = data1.get(i).split(" "); // 按空格对句子进行划分
				IsNull = true; // 为了判断过滤掉了停用词之后，是否是空串
				// foreach循环用于取出一条语料中的所有词，并进行停用词过滤处理
				
				for (String wordandprop : aWordsandProps) {
					IsStopword = false;		// 用于标记当前词是否为停用词
					if (wordandprop != "")  // 判断词语-语性对是否为空串
					{
						if (wordandprop.contains("/")) // 过滤掉中文空格等特殊符号
						{
							String[] Str = wordandprop.split("/"); // 把词语和词性分开
							if (Str.length == 2) // 过滤掉带有“/”的词
							{
								String word = Str[0]; // 词语
								String prop = Str[1]; // 词性

								// 过滤掉特定词性的停用词
								for (int j = 0; j < StopwordPropsList.size(); j++) {
									if (prop.equals(StopwordPropsList.get(j))) {
										IsStopword = true;
										break;
									}
								}

								// 过滤掉停用词表里面的停用词(如果没有通过特定词性过滤掉的话，那么再通过停用词表进行过滤)
								if (!IsStopword) {
									for (int j = 0; j < StopwordList.size(); j++) {
										if (word.equals(StopwordList.get(j))) {
											IsStopword = true;
											break;
										}
									}
								}

								if (!IsStopword) {
									data2.add(wordandprop);
									words.add(word);
									props.add(prop);
									StrSolved += word + " ";

									IsNull = false;
								}

							}
						}
					}
				} // foreach

				if (!IsNull) {
					// 此处的加入的空串是为了将每一条评论用空串隔开
					data2.add("");
					words.add("");
					props.add("");
				}
				datasolved.add(StrSolved);

			} // if
		} // for
		System.out.println("停用词过滤完成，正在将结果写入到txt文件中……");

		/**
		 * 输出停用词过滤后的结果
		 */
		File file3 = new File(outputFilePath);
		// 如果待输出文件的父路径不存在，则创建之
		File fileParent = file3.getParentFile();
		if(!fileParent.exists()) {
			fileParent.mkdirs();
		}
		FileOperate.Print2Txt(datasolved, file3, false);
		System.out.println("写入完成");
	}
	
	public void run(String inputFilePath, String outputFilePath){
		prepare(inputFilePath);
		filter(outputFilePath);
	}
	
	public String singleRun(String line){
		/**
		 * 读入停用词表，此处使用的是哈工大停用词表扩展
		 */
		String dictionaryPath = ".\\file\\Dictionary\\停用词词典\\哈工大停用词表扩展.txt";	// 存放停用词词典的路径
		File file1 = new File(dictionaryPath);
		FileOperate.ReadFromTxt(StopwordList, file1, false);

		/**
		 * 构建停用词词性表，即愉有下列的词性的词，将被过滤掉
		 */
		// 标点符号
		StopwordPropsList.add("w");
		StopwordPropsList.add("wkz");
		StopwordPropsList.add("wky");
		StopwordPropsList.add("wyz");
		StopwordPropsList.add("wyy");
		StopwordPropsList.add("wj");
		StopwordPropsList.add("ww");
		StopwordPropsList.add("wt");
		StopwordPropsList.add("wd");
		StopwordPropsList.add("wf");
		StopwordPropsList.add("wn");
		StopwordPropsList.add("wm");
		StopwordPropsList.add("ws");
		StopwordPropsList.add("wp");
		StopwordPropsList.add("wb");
		StopwordPropsList.add("wh");

		// 介词
		StopwordPropsList.add("p");
		StopwordPropsList.add("pba"); // 把
		StopwordPropsList.add("pbei"); // 被

		// 连词
		StopwordPropsList.add("c");
		StopwordPropsList.add("cc"); // 并列连词

		// 助词
		StopwordPropsList.add("u");
		StopwordPropsList.add("uzhe");
		StopwordPropsList.add("ule");
		StopwordPropsList.add("uguo");
		StopwordPropsList.add("ude1");
		StopwordPropsList.add("ude2");
		StopwordPropsList.add("ude3");
		StopwordPropsList.add("usuo");
		StopwordPropsList.add("udeng");
		StopwordPropsList.add("uyy");
		StopwordPropsList.add("udh");
		StopwordPropsList.add("uls");
		StopwordPropsList.add("uzhi");
		StopwordPropsList.add("ulian");

		// 数词
		StopwordPropsList.add("m");
		StopwordPropsList.add("mq");

		// 量词
		StopwordPropsList.add("q");
		StopwordPropsList.add("qv");
		StopwordPropsList.add("qt");

		// 叹词
		StopwordPropsList.add("e");

		// 语气词
		StopwordPropsList.add("y");

		// 拟声词
		StopwordPropsList.add("o");

		// 字符串
		StopwordPropsList.add("x");
		StopwordPropsList.add("xe");
		StopwordPropsList.add("xs");
		StopwordPropsList.add("xm");
		StopwordPropsList.add("xu");

		// 代词
		StopwordPropsList.add("r");
		StopwordPropsList.add("rr");
		StopwordPropsList.add("rz");
		StopwordPropsList.add("rzt");
		StopwordPropsList.add("rzs");
		StopwordPropsList.add("rzv");
		StopwordPropsList.add("ry");
		StopwordPropsList.add("ryt");
		StopwordPropsList.add("rys");
		StopwordPropsList.add("ryv");
		StopwordPropsList.add("rg");
		
		
		String StrSolved = "";	// 用于保存该行语料中的非停用词
		String[] aWordsandProps = line.split(" "); // 按空格对句子进行划分
		IsNull = true; // 为了判断过滤掉了停用词之后，是否是空串
		// foreach循环用于取出一条语料中的所有词，并进行停用词过滤处理
		
		for (String wordandprop : aWordsandProps) {
			IsStopword = false;		// 用于标记当前词是否为停用词
			if (wordandprop != "")  // 判断词语-语性对是否为空串
			{
				if (wordandprop.contains("/")) // 过滤掉中文空格等特殊符号
				{
					String[] Str = wordandprop.split("/"); // 把词语和词性分开
					if (Str.length == 2) // 过滤掉带有“/”的词
					{
						String word = Str[0]; // 词语
						String prop = Str[1]; // 词性

						// 过滤掉特定词性的停用词
						for (int j = 0; j < StopwordPropsList.size(); j++) {
							if (prop.equals(StopwordPropsList.get(j))) {
								IsStopword = true;
								break;
							}
						}

						// 过滤掉停用词表里面的停用词(如果没有通过特定词性过滤掉的话，那么再通过停用词表进行过滤)
						if (!IsStopword) {
							for (int j = 0; j < StopwordList.size(); j++) {
								if (word.equals(StopwordList.get(j))) {
									IsStopword = true;
									break;
								}
							}
						}

						if (!IsStopword) {
							data2.add(wordandprop);
							words.add(word);
							props.add(prop);
							StrSolved += word + " ";

							IsNull = false;
						}

					}
				}
			}
		} // foreach
		
		return StrSolved;
	}

	public static void main(String[] args) {
		String rootPath = ".\\file\\Corpus\\"; // 存放语料文件的根路径
		String inputFilePath = rootPath + "FAQ\\segmentation\\Q_delEmpNRep_Segmentation.txt";	 // 输入文件的路径，即需要处理的文件路径
		String outputFilePath = rootPath + "FAQ"; // 保存停用词过滤后的评论文本
		
		/*
		 * 获取输入文件的文件名
		 */
		GetFileName gfp = new GetFileName();
		String fileName = gfp.getFileName(inputFilePath);
		
		outputFilePath += ("\\stopWordsFilter\\" + fileName + "_stopWordsFilter.txt");
		
		StopWordsFilter swf = new StopWordsFilter();
		swf.run(inputFilePath, outputFilePath);
	}

}


package preprocessing.dictionaryManage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import preprocessing.utility.FileOperate;
import preprocessing.utility.GetFileName;

/**
 * 用于去空去重操作
 * 
 * @author Alex
 *
 */
public class DeleteEmptyNRepetition {

	List<String> afterDeleteEmpty = new ArrayList<String>(); // 用于保存去空之后的数据
	List<String> afterDeleteRepetition = new ArrayList<String>(); // 用于保存去重之后的数据
	
	List<String> answer = new ArrayList<String>();	//
	List<Integer> lineNum = new ArrayList<Integer>();
	/**
	 * 读入数据并进行去空处理
	 * 
	 * @param inputFilePath
	 *            输入文件路径
	 * @param afterDeleteEmpty
	 *            用于保存去空之后的数据
	 */
	private void deleteEmpty(File inFile) {
		System.out.println("读入文件并进行去空处理中……");
		FileOperate.ReadFromTxt(afterDeleteEmpty, inFile, true); // 该读入函数已经具有了去空的功能
		System.out.println("去空处理完毕。");
	}

	/**
	 * 去重处理
	 */
	private void deleteRepetition() {
		boolean IsRepeated = false; // 用于在去重处理的时候，判断是否出现了重复的评论

		int count = 1;
		
		System.out.println("执行去重处理中…………");
		afterDeleteRepetition.add(afterDeleteEmpty.get(0));
		lineNum.add(0);
		System.out.println(afterDeleteEmpty.size());
		for (int i = 1; i < afterDeleteEmpty.size(); i++) {
			IsRepeated = false;
			for (int j = 0; j < i; j++) {
				if (afterDeleteEmpty.get(i).equals(afterDeleteEmpty.get(j))) {
					IsRepeated = true;
					break;
				}
			}
			if (!IsRepeated) {
				afterDeleteRepetition.add(afterDeleteEmpty.get(i));
				lineNum.add(count);
			}
			++count;
		}
//		System.out.println(afterDeleteRepetition);
		System.out.println("去重处理完毕。");
	}

	public void run(String inputFilePath, String outputFilePath) {
		
		File inFile = new File(inputFilePath);
		
		// 读入答案
		File inFileAnswer = new File("file\\Corpus\\FAQExtra\\rawCorpus\\A.txt");
		FileOperate.ReadFromTxt(answer, inFileAnswer, true);
		System.out.println(answer.size());
		
		// 去空操作
		deleteEmpty(inFile);
		// 去重操作
		deleteRepetition();
		System.out.println(afterDeleteRepetition.size());
		
		// 输出
		System.out.println("将去空去重的结果写入到txt中……");
		File outFile = new File(outputFilePath);
		// 如果待输出文件的父路径不存在，则创建之
		File fileParent = outFile.getParentFile();
		if(!fileParent.exists()) {
			fileParent.mkdirs();
		}
		FileOperate.Print2Txt(afterDeleteRepetition, outFile, false);
		
		// 输出问题对应的答案
		File outFileAnswer = new File("file\\Corpus\\FAQExtra\\deleteEmptyNRepetition\\A_delEmpNRep.txt");
		try {
			OutputStreamWriter writer;
			writer = new OutputStreamWriter(new FileOutputStream(outFileAnswer, false)); // 如果append为true，则进行追加;如果为false，则覆盖原有内容。
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			// 将停用词过滤后的结果写入到txt文档中
			System.out.println("lineNum: " + lineNum.size());
			for (int i = 0; i < lineNum.size(); ++i) {
				bufferedWriter.write(answer.get(lineNum.get(i)) + "\r\n");
			}
			bufferedWriter.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("写入完成");
	}

	public static void main(String[] args) {
		String rootPath = ".\\file\\Corpus\\"; // 存放语料文件的根路径
		String inputFilePath = rootPath + "FAQExtra\\rawCorpus\\Q.txt"; // 输入文件路径
		String outputFilePath = rootPath + "FAQExtra"; // 保存去空去重后的评论文本
		
		/*
		 * 获取输入文件的文件名
		 */
		GetFileName gfp = new GetFileName();
		String fileName = gfp.getFileName(inputFilePath);
		
		outputFilePath += ("\\deleteEmptyNRepetition\\" + fileName + "_delEmpNRep.txt");
		
		DeleteEmptyNRepetition denr = new DeleteEmptyNRepetition(); // 定义一个实现去空去重操作的对象
		denr.run(inputFilePath, outputFilePath);
	}

}

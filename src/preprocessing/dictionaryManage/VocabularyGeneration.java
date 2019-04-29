package preprocessing.dictionaryManage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import preprocessing.utility.FileOperate;

/**
 * 基于语料中的词,形成一个词汇表(词典)
 * 
 * @author Alex
 *
 */
public class VocabularyGeneration {

	public static void main(String[] args) throws IOException, FileNotFoundException {
		List<String> CorpusWords = new ArrayList<String>(); // 用于保存读入的语料中所有的词语
		List<String> Vocabulary = new ArrayList<String>(); // 词汇表（特征词表）
		String dictionaryPath = "E:\\EclipseJ2EEWorkspace\\SentimentAnalysisPreprocess\\file\\Dictionary\\"; // 处理文件的根路径

		// 读入语料
		System.out.println("读入语料中……");
		File file1 = new File(dictionaryPath + "part1.txt");

		// 编码格式
		String charset = "GBK";// GBK----0
		// String charset = "UTF-8";// UTF-8

		if (file1.isFile() && file1.exists()) {
			InputStreamReader read;
			read = new InputStreamReader(new FileInputStream(file1), charset); // 考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			
			// 循环读入文件中的每一行，并保存到List容器中
			while ((lineTxt = bufferedReader.readLine()) != null) {
				// 该if语句判断读入的字符串是否为空串，空串不进行读入
				if (!lineTxt.isEmpty()) { // 用于判断是否读入空串
					lineTxt = lineTxt.trim();
					String[] words = lineTxt.split(" ");
					for (String word : words) {
						CorpusWords.add(word); // 读入每一个词语
					}
				}
			}
			bufferedReader.close();
		}
		System.out.println("语料读入完成");

		// 提取语料中的词汇
		System.out.println("词汇提取中……");
		boolean IsRepeated;	// 用于判断当前词是否是重复词
        Vocabulary.add(CorpusWords.get(0));
        for (int i = 1; i != CorpusWords.size(); ++i)
        {
            IsRepeated = false;
            for (int j = 0; j != Vocabulary.size(); ++j)
            {
                if (CorpusWords.get(i).equals(Vocabulary.get(j)))
                {
                    IsRepeated = true;
                    break;
                }
            }
            if (!IsRepeated)
            {
                Vocabulary.add(CorpusWords.get(i));
            }
        }
        System.out.println("提取完成");

		/**
		 * 输出
		 */
		System.out.println("将词汇表写入txt中……");
		File file2 = new File(dictionaryPath + "FeatureWords.txt");
		FileOperate.Print2Txt(Vocabulary, file2, false);
		System.out.println("写入完成");
	}

}

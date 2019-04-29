package preprocessing.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 该类是一个实用类，用于提供公共的读写文件操作
 * 
 * @author Alex 将文件中的数据按行读入到List容器中，或者将List容器中的数据写到文件中
 */
public class FileOperate {

	/**
	 * 该函数用于进行将数据输出到文件的操作
	 * @param list 保存将要输出内容的列表
	 * @param outFile 输出文件路径
	 * @param append 是否追加
	 */
	public static void Print2Txt(List<String> list, File outFile, boolean append) {

		try {
			OutputStreamWriter writer;
//			writer = new OutputStreamWriter(new FileOutputStream(outFile, true)); // 往txt中追加内容
			writer = new OutputStreamWriter(new FileOutputStream(outFile, append)); // 如果append为true，则进行追加;如果为false，则覆盖原有内容。
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			// 将停用词过滤后的结果写入到txt文档中
			for (int i = 0; i < list.size(); i++) {
				bufferedWriter.write(list.get(i) + "\r\n");
			}
			bufferedWriter.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 该函数用于从txt中读取数据并保存到List中
	 * 
	 * @param list
	 *            用于保存读入的数据
	 * @param inFile
	 *            将要写入的文件
	 */
	/**
	 * 该函数用于从txt中读取数据并保存到List中
	 * @param list 用于保存读入的数据
	 * @param inFile 将要写入的文件
	 * @param flag 用于判断是否读入空串，当为true时表示可以读入空串，当为false时表示不读入空串
	 */
	public static void ReadFromTxt(List<String> list, File inFile, boolean flag) {

		// 编码格式
		String charset = "GBK";// GBK----0
		// String charset = "UTF-8";// UTF-8

		if (inFile.isFile() && inFile.exists()) {
			try {
				InputStreamReader read;
				read = new InputStreamReader(new FileInputStream(inFile), charset); // 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				// 循环读入文件中的每一行，并保存到List容器中
				while ((lineTxt = bufferedReader.readLine()) != null) {
					// 该if语句判断读入的字符串是否为空串，空串不进行读入
					if (!flag && lineTxt.isEmpty()) {	// flag用于指示是否读入空串
						
					} else {
						list.add(lineTxt.trim()); // 读入一行，并保存到list中
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

	}

}

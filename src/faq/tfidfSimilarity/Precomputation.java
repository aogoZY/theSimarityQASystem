package faq.tfidfSimilarity;

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

public class Precomputation {

	public static void main(String[] args) {
		File tfidfVectorFile = new File("file\\Corpus\\FAQ\\vectorization\\TfIdf.txt");
		File squareRoot = new File("file\\Corpus\\FAQ\\precomputation\\squareRoot.txt");
		// 如果待输出文件的父路径不存在，则创建之
		File fileParent = squareRoot.getParentFile();
		if(!fileParent.exists()) {
			fileParent.mkdirs();
		}
				
		// 编码格式
		String charset = "GBK";// GBK----0
		// String charset = "UTF-8";// UTF-8

		if (tfidfVectorFile.isFile() && tfidfVectorFile.exists()) {
			try {
				OutputStreamWriter writer;
				writer = new OutputStreamWriter(new FileOutputStream(squareRoot, false)); // 如果append为true，则进行追加;如果为false，则覆盖原有内容。
				BufferedWriter bufferedWriter = new BufferedWriter(writer);
				
				InputStreamReader read;
				read = new InputStreamReader(new FileInputStream(tfidfVectorFile), charset); // 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				double ival = 0;
				double sum = 0;
				// 循环读入文件中的每一行，并保存到进行预计算
				while ((lineTxt = bufferedReader.readLine()) != null) {
					sum = 0;
					String[] values = lineTxt.split(",");
					for(String value : values) {
						ival = Double.parseDouble(value);
						if(ival > 0) {
							sum += (ival * ival);
						}
					}
					bufferedWriter.write(Double.toString(Math.sqrt(sum)));
					bufferedWriter.newLine();
				}
				bufferedReader.close();
				bufferedWriter.close();

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

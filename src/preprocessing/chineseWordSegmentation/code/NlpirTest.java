package preprocessing.chineseWordSegmentation.code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

//import utils.SystemParas;

import com.sun.jna.Library;
import com.sun.jna.Native;

import preprocessing.utility.GetFileName;

public class NlpirTest {
	
	// 该变量用于保存源文件名，在后续操作中给生成的新文件取合适的名字（基于源文件名）
	String sourceFileName = null;

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	// 定义接口CLibrary，继承自com.sun.jna.Library
	public interface CLibrary extends Library {
		// 定义并初始化接口的静态变量
		
		CLibrary Instance = (CLibrary) Native.loadLibrary(
				new File("file\\win64\\NLPIR").getAbsolutePath(), CLibrary.class);// 设定dll的路径，这里使用了一个临时文件对象，用于在已知相对路径的情况下获取绝对路径

		public int NLPIR_Init(String sDataPath, int encoding, String sLicenceCode);

		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

		public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);

		public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);

		public int NLPIR_AddUserWord(String sWord);

		public int NLPIR_DelUsrWord(String sWord);

		public String NLPIR_GetLastErrorMsg();

		public void NLPIR_Exit();
	}

	public static String transString(String aidString, String ori_encoding, String new_encoding) {
		try {
			return new String(aidString.getBytes(ori_encoding), new_encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void run(String inputFilePath, String outputFilePath) {
		String argu = ".\\file\\ICTCLAS2015";	// 给出Data文件所在的路径，注意根据实际情况修改。
		String system_charset = "GBK";// GBK----0
//		String system_charset = "UTF-8";
		int charset_type = 1;

		int init_flag = CLibrary.Instance.NLPIR_Init(argu, charset_type, "0");
		String nativeBytes = null;

		if (0 == init_flag) {
			nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			System.err.println("初始化失败！fail reason is " + nativeBytes);
			return;
		}
		
		try {
			File comment = new File(inputFilePath);// 读取原始评论
			File wordSegmentComment = new File(outputFilePath);// 保存分词后的评论文本
			// 如果待输出文件的父路径不存在，则创建之
			File fileParent = wordSegmentComment.getParentFile();
			if(!fileParent.exists()) {
				fileParent.mkdirs();
			}
			
			if (comment.isFile() && comment.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(comment), system_charset);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				
				System.out.println("读入语料并进行分词与词性标注操作……");
				OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(wordSegmentComment));// 如果文件已存在，覆盖原有内容
				BufferedWriter bufferedWriter = new BufferedWriter(writer);
				// 循环读入原始评论的每一行，进行分词处理，最后把分词的结果进行保存。
				while ((lineTxt = bufferedReader.readLine()) != null) {
					// 调用NLPIR系统中相应的函数进行分词与词性标注处理
					nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(lineTxt, 1);
					// 将分词的结果写入到txt文档中
					bufferedWriter.write(nativeBytes + "\r\n");
				}
				bufferedWriter.close();
				System.out.println("分词与词性标注处理完成，结果保存到txt文件中。");

				bufferedReader.close();
			}

			CLibrary.Instance.NLPIR_Exit();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	public static String singleSentence(String line) {
		String argu = ".\\file\\ICTCLAS2015";	// 给出Data文件所在的路径，注意根据实际情况修改。
		String system_charset = "GBK";// GBK----0
//		String system_charset = "UTF-8";
		int charset_type = 1;

		int init_flag = CLibrary.Instance.NLPIR_Init(argu, charset_type, "0");
		String nativeBytes = null;

		if (0 == init_flag) {
			nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			System.err.println("初始化失败！fail reason is " + nativeBytes);
			return null;
		}
		
		// 调用NLPIR系统中相应的函数进行分词与词性标注处理
		nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(line, 1);

		CLibrary.Instance.NLPIR_Exit();
		
		return nativeBytes;
	}

	public static void main(String[] args) throws Exception {
		String rootPath = ".\\file\\Corpus\\"; // 存放语料文件的根路径
		String inputFilePath = rootPath + "FAQ\\deleteEmptyNRepetition\\Q_delEmpNRep.txt";// 读取原始评论
		String outputFilePath = rootPath + "FAQ";// 保存分词后的评论文本
		
		/*
		 * 获取输入文件的文件名
		 */
		GetFileName gfp = new GetFileName();
		String fileName = gfp.getFileName(inputFilePath);
		
		outputFilePath += ("\\segmentation\\" + fileName + "_Segmentation.txt");
		
		// 进行分词
		NlpirTest nlpir = new NlpirTest();
		nlpir.run(inputFilePath, outputFilePath);
	}
}

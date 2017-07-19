package tw.com.character;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1CurveGenerator;
import tw.com.encrypt.KeywordIndex;
import tw.com.encrypt.OwnerEncrypt;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.jpbc.PairingParametersGenerator;
public class Owner {
	
	private JButton upload;
	private JButton add;
	private JTextArea path;
	private JTextArea keywordInput;
	private final static JFileChooser fc = new JFileChooser();
	private static JTextArea info;
	private int fileSize = -1;
	private HashMap<String , ArrayList<String>> keywordindex = new HashMap<>();
	private HashMap<String , byte[]> keywordK1 = new HashMap<>();
	private HashMap<String , byte[]> keywordK2 = new HashMap<>();
	private HashMap<String , byte[]> keywordDelta = new HashMap<>();
	private static ArrayList<String> fileName = new ArrayList<String>();
	private static OwnerEncrypt ownerEncrypt ;
	private byte[][] indexMatrix; 
	private static Element g;
	private static Element s;
	private static Element gs;
	private static Pairing pairing;
	private HashMap<String , Element> fileHash = new HashMap<>();
	private HashMap<String , Element> keywordDigest = new HashMap<>();
	private static ArrayList<String> keywordSize = new ArrayList<String>();
	private Element rightNode = null;
	private Element leftNode = null;
	private static Element root;
	private static MessageDigest md;
	
	
	public Owner(boolean b)
	{
		
		JFrame ownerWindows = new JFrame();
		ownerWindows.setSize(600 , 500);
		ownerWindows.setResizable(false);
		JFrame.setDefaultLookAndFeelDecorated(true);
		ownerWindows.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ownerWindows.setTitle("Owner");
		ownerWindows.setBackground(Color.GRAY);
		upload = new JButton("上傳");
		StartButtonListener startListener = new StartButtonListener();
		upload.addActionListener(startListener);
		add = new JButton("加入檔案");
		AddButtonListener addListener = new AddButtonListener();
		add.addActionListener(addListener);
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(300 , 300));
		panel.setLayout(new FlowLayout());

		JLabel label3 = new JLabel("選擇上傳檔案：");
		label3.setFont(new Font(label3.getFont().getName() , Font.BOLD, 16));
		path = new JTextArea(3 , 20);
		path.setLineWrap(true);
		path.setWrapStyleWord(true);
		path.setEditable(false);
		path.setFont(new Font(label3.getFont().getName() , Font.BOLD, 12));
		JButton choosePath = new JButton("選取");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		choosePath.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						fc.showOpenDialog(null);
						File selectedFile = fc.getSelectedFile();
						if(selectedFile != null)
						path.setText(selectedFile.getAbsolutePath());
					}
			
				});
		JLabel keyword = new JLabel("輸入此檔案之keywords：");
		keyword.setFont(new Font(keyword.getFont().getName() , Font.BOLD, 16));
		JLabel format = new JLabel("(格式:keyword,keyword1)");
		format.setFont(new Font(keyword.getFont().getName() , Font.BOLD, 16));
		keywordInput = new JTextArea(3 , 20);
		keywordInput.setLineWrap(true);
		keywordInput.setWrapStyleWord(true);
		keywordInput.setFont(new Font(label3.getFont().getName() , Font.BOLD, 12));
		panel.add(label3);
		panel.add(new JScrollPane(path));
		panel.add(choosePath);
		panel.add(keyword);
		panel.add(format);
		panel.add(keywordInput);
		panel.add(add);
		JPanel infoPanel = new JPanel();
		info = new JTextArea(20 , 20);
		info.setText("加密資訊：\n");
		info.setBackground(Color.white);
		info.setLineWrap(true);
		info.setWrapStyleWord(true);
		infoPanel.add(new JScrollPane(info));
		info.setEditable(false);
		if(b)
			appendInfo("資料夾已成功建立");
		else
			appendInfo("資料夾已存在或建立失敗");
		ownerWindows.getContentPane().add(BorderLayout.SOUTH,upload );
		ownerWindows.getContentPane().add(BorderLayout.WEST, infoPanel);
		ownerWindows.getContentPane().add(BorderLayout.EAST, panel);
		ownerWindows.setVisible(true);
		genPBCKey();
		try {
			ownerEncrypt = new OwnerEncrypt();
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	
	public void encryptKeyword(String keyword)
	{
		try {
			byte[] cipher1 = ownerEncrypt.encryptKeyword(1, keyword);
			keywordK1.put(keyword, cipher1);
			byte[] cipher2 = ownerEncrypt.encryptKeyword(2, keyword);
			keywordK2.put(keyword, cipher2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static ArrayList<byte[]> userEncryptKeyword(ArrayList<String> keywords , int k)
	{
		ArrayList<byte[]> encryptKeyword = new ArrayList<>();
		if(k == 1)
		{
			for(String keyword : keywords)
			{
				if(!keywordSize.contains(keyword))
					return null;
				try {
					byte[] cipher1 = ownerEncrypt.encryptKeyword(1, keyword);
					encryptKeyword.add(cipher1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return encryptKeyword;
		}
		else
		{
			for(String keyword : keywords)
			{
				if(!keywordSize.contains(keyword))
					return null;
				try {
					byte[] cipher2 = ownerEncrypt.encryptKeyword(2, keyword);
					encryptKeyword.add(cipher2);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return encryptKeyword;
		}
	}
	
	public void encryptFile(String path)
	{
		File file = new File(path);
		try {
			ownerEncrypt.encrypt( file, new File(path + "en"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void genPBCKey()
	{
		PairingParametersGenerator parametersGenerator = new TypeACurveGenerator(160 , 512);
		PairingParameters params = parametersGenerator.generate();
		// JPBC Type A pairing generator...
		PairingFactory.getInstance().setUsePBCWhenPossible(true);
		pairing = PairingFactory.getPairing(params);

		System.out.println(params);
		//generate system parameter
		Field G1 = pairing.getG1();
		g = G1.newRandomElement().getImmutable();
		
		//secret key
		s = pairing.getZr().newRandomElement().getImmutable();
		gs = g.powZn(s);
	}
	
	private class StartButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			Owner.appendInfo("建立Index Matrix...");
			buildIndexMatrix();
			Owner.appendInfo("Index Matrixr建立完成");
			Owner.appendInfo("產生keyword delta");
			XOR();
			Owner.appendInfo("產生完成");
			Owner.appendInfo("hash所有加密檔案");
			hashAllFile();
			Owner.appendInfo("hash完成");
			Owner.appendInfo("計算accumulation value");
			keywordAccumulationValue();
			Owner.appendInfo("計算accumulation value完成");
			Owner.appendInfo("建構accumulation tree");
			buildAccTree();
			Owner.appendInfo("建構accumulation tree完成");
			Owner.appendInfo("資料上傳server中");
			sendToServer();
			Owner.appendInfo("資料上傳server完成");
		}
	}
	
	private class AddButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(!keywordInput.getText().equals("") && !path.getText().equals(""))
			{
				fileSize ++;
				String[] keywords = keywordInput.getText().split(",");
				String foriginalpath = path.getText();
				int index = foriginalpath.lastIndexOf("\\");
				String filename = path.getText().substring(index + 1, foriginalpath.length());
				fileName.add(filename);
				Owner.appendInfo("關鍵字加密中...");
				for(int i = 0; i < keywords.length; i++)
				{
					if(keywordindex.containsKey(keywords[i]))
					{
						keywordindex.get(keywords[i]).add(filename);
					}
					else
					{
						ArrayList<String> indexF = new ArrayList<String>();
						indexF.add(filename);
						keywordindex.put(keywords[i],indexF);
						encryptKeyword(keywords[i]);
						keywordSize.add(keywords[i]);
					}
				}
				Owner.appendInfo("關鍵字加密完成!!!");
				String copyPath = "C:\\informationSecurity\\owner\\" + filename;
				File f = new File(copyPath);
				try {
					copyFileUsingFileStreams(new File(foriginalpath) , f);
					Owner.appendInfo(filename + "複製移動中...");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.out.println("Copy File error");
				}
				Owner.appendInfo(filename + "檔案加密中...");
				encryptFile(copyPath);
				Owner.appendInfo(filename + "檔案加密完成!!!");
				path.setText("");
				keywordInput.setText("");
			}
		}
	}
	
	private void copyFileUsingFileStreams(File source, File dest)
	        throws IOException {
	    InputStream input = null;
	    OutputStream output = null;
	    try {
	        input = new FileInputStream(source);
	        output = new FileOutputStream(dest);
	        byte[] buf = new byte[1024];
	        int bytesRead;
	        while ((bytesRead = input.read(buf)) > 0) {
	            output.write(buf, 0, bytesRead);
	        }
	    } finally {
	        input.close();
	        output.close();
	    }
	}
	
	public static void appendInfo(String msg)
	{
		info.append(msg);
		info.append("\n");
	}
	
	public void buildIndexMatrix()
	{
		indexMatrix = new byte[keywordindex.size()][fileSize + 1];
		int ki = 0;
		for(String keyword: keywordindex.keySet())
		{
			for(int i = 0; i <= fileSize; i++)
			{
				if(keywordindex.get(keyword).contains(fileName.get(i)))
				{
					indexMatrix[ki][i] = 1;
				}
				else
				{
					indexMatrix[ki][i] = 0;
				}
			}
			ki++;
		}
	}
	
	public void XOR()
	{
		int i = 0;
		
		for(String keyword: keywordindex.keySet())
		{
			byte[] delta = new byte[fileSize + 1];
			byte[] cipherK = keywordK2.get(keyword);
			for(int j = 0; j <= fileSize; j++)
			{
				if(j < 16 )
				{
					delta[j] = (byte) ((byte) ((cipherK[j] >> 0) & 0x1) ^ indexMatrix[i][j]);
				}
				else
				{
					delta[j] = (byte) (0 ^ indexMatrix[i][j]);
				}
			}
			i++;
			keywordDelta.put(keyword , delta);
		}
	}
	
	//hash file to group G1
	public void hashAllFile()
	{
		for(int i = 0; i <= fileSize; i++)
		{
			try {
				//md = MessageDigest.getInstance("MD5");
				InputStream is = Files.newInputStream(Paths.get("C:\\informationSecurity\\owner\\" + fileName.get(i) + "en"));
				//DigestInputStream dis = new DigestInputStream(is , md);
				byte[] hash = is.toString().getBytes();
				
				fileHash.put(fileName.get(i) , getG1Hash(hash));
				
			} catch ( IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static Element getG1Hash(byte[] hash)
	{
		return pairing.getZr().newElementFromBytes(hash).setFromHash(hash, 0, hash.length);
	}
	
	public static Element getG()
	{
		return g;
	}
	
	public static Element getGS()
	{
		return  gs;
	}
	
	public static MessageDigest getMD()
	{
		return md;
	}
	
	//calculate keyword accumulation value
	public void keywordAccumulationValue()
	{
		Element acc = null;
		for(String keyword: keywordindex.keySet())
		{
			ArrayList<String> fl = keywordindex.get(keyword);
			for(int i = 0; i < fl.size(); i ++)
			{
				if(i == 0)
				{
					
					Element a = fileHash.get(fl.get(i)).add(s);
					acc = g.powZn(a);
				}
				else
				{
					acc = acc.powZn((Element)fileHash.get(fl.get(i)).add(s));
				}
			}
			keywordDigest.put(keyword , acc);
		}
	}
	
	//build accmulation value tree
	public void buildAccTree()
	{
		int ksize = keywordSize.size();
		int left = ksize / 2;
		if(ksize==1)
		{
			left = 1;
		}
		int i = 0;
		Element a;
		for(i = 0; i < left; i++)
		{
			if(i == 0)
			{
				leftNode = keywordDigest.get(keywordSize.get(i)).mul(gs);
			}
			else
			{
				a = keywordDigest.get(keywordSize.get(i)).mul(gs);
				leftNode = leftNode.mul(a);
			}
		}
		for(; i < ksize; i++)
		{
			if(i == left)
			{
				rightNode = keywordDigest.get(keywordSize.get(i)).mul(gs);
			}
			else
			{
				rightNode = rightNode.mul(keywordDigest.get(keywordSize.get(i)).mul(gs));
			}
		}
		//root digest
		if(rightNode == null)
			root = leftNode;
		root = rightNode.mul(leftNode);
	}
	
	//send data to server
	public void sendToServer()
	{
		String name;
		//encrypted file upload
		for(int i = 0;i < fileName.size(); i++)
		{
			name = fileName.get(i);
			try {
				Owner.appendInfo(name + "檔案上傳中!!!");
				copyFileUsingFileStreams(new File("C:\\informationSecurity\\owner\\" + fileName.get(i) + "en") , new File("C:\\informationSecurity\\server\\" + fileName.get(i) + "en"));
				Owner.appendInfo(name + "檔案上傳完成!!!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			}
		}
		//keyword data upload
		String keyword;
		ArrayList<byte[]> k1 = new ArrayList<byte[]>();
		ArrayList<byte[]> k2 = new ArrayList<byte[]>();
		ArrayList<byte[]> delta = new ArrayList<byte[]>();
		Owner.appendInfo("關鍵字資訊上傳中!!!");
		for(int i = 0; i < keywordSize.size(); i++)
		{
			keyword = keywordSize.get(i);
			k1.add(keywordK1.get(keyword));
			k2.add(keywordK2.get(keyword));
			delta.add(keywordDelta.get(keyword));
		}
		Server.setKeywordData(k1, k2, delta);
		Owner.appendInfo("關鍵字資訊上傳完成!!!");
		
		Owner.appendInfo("accumulation value資訊上傳中!!!");
		//accumulation value upload
		ArrayList<Element> LD = new ArrayList<>();
		for(int i = 0; i < keywordSize.size(); i++)
		{
			LD.add(keywordDigest.get(keywordSize.get(i)));
		}
		Server.setDigestTree(LD, rightNode, leftNode, root);
		Owner.appendInfo("accumulation value資訊上傳完成!!!");
	}
	
	//keyword return
	public static ArrayList<String> getKeyword()
	{
		return keywordSize;
	}

	//get filename
	public static ArrayList<String> getFName(ArrayList<Integer> fileIndex)
	{
		ArrayList<String> name = new ArrayList<String>();
		for(int i = 0; i < fileIndex.size(); i++)
		{
			name.add(fileName.get(fileIndex.get(i)) + "en");
		}
		return name;
	}
	
	public static Element getRoot()
	{
		return root;
	}
	
	//decrypt
	public static void fDecrypt(File in , File out)
	{
		try {
			ownerEncrypt.decrypt(in, out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

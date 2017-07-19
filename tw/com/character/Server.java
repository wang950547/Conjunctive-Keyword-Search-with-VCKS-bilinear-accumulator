package tw.com.character;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import it.unisa.dia.gas.jpbc.Element;

public class Server {
	private JFrame frmServer;
	private static JTextArea info;
	private static ArrayList<byte[]> k1 ;
	private static ArrayList<byte[]> k2 ;
	private static ArrayList<byte[]> delta;
	private static ArrayList<Element> leafDigest;
	private static Element rightNode;
	private static Element leftNode;
	private static Element root;
	private static ArrayList<Integer> keywordIndex = new ArrayList<>();
	private static ArrayList<Integer> fIndex = new ArrayList<>();
	private static ArrayList<String > queryFileName;
	private static ArrayList<Element> completeW = new ArrayList<>();
	private static Element subsetW ;;
	private static ArrayList<Element> intersectACC = new ArrayList<>();
	
	public Server(boolean b)
	{
		frmServer = new JFrame();
		frmServer.setTitle("Server");
		frmServer.setBounds(100, 100, 600, 500);
		frmServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmServer.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 564, 441);
		frmServer.getContentPane().add(scrollPane);
		
		info = new JTextArea();
		info.setEditable(false);
		if(b)
			appendInfo("資料夾已成功建立");
		else
			appendInfo("資料夾已存在或建立失敗");
		scrollPane.setViewportView(info);
		frmServer.setResizable(false);
		frmServer.setVisible(true);
	}

	
	public static void setKeywordData(ArrayList<byte[]> key1 , ArrayList<byte[]> key2 , ArrayList<byte[]> xordelta)
	{
		appendInfo("keyword資訊上傳中...");
		k1 = new ArrayList<>();
		k2 = new ArrayList<>();
		delta = new ArrayList<>();
		k1 = key1;
		k2 = key2;
		delta = xordelta;
		appendInfo("keyword資訊上傳完成");
	}
	
	public static void setDigestTree(ArrayList<Element> LD , Element r , Element l , Element droot)
	{
		appendInfo("accumulation資訊上傳中...");
		leafDigest = LD;
		rightNode = r;
		leftNode = l;
		root = droot;
		appendInfo("accumulation資訊上傳完成");
	}
	
	public static void keywordSearch(ArrayList<byte[]>sk1 , ArrayList<byte[]>sk2)
	{
		appendInfo("接收使用者search token");
		for(int i = 0; i < k1.size(); i++)
		{
			byte[] key1 = k1.get(i);
			for(int k = 0; k < sk1.size(); k++)
			for(int j = 0; j < 16;j++)
			{
				if(key1[j] != sk1.get(k)[j])
				{
					break;
				}
				else
				{
					if(j == 15)
					{
						keywordIndex.add(i);
					}
				}
			}
			
		}
		
		for(int i = 0; i < keywordIndex.size(); i++)
		{
			int index = keywordIndex.get(i);
			for(int j = 0; j < delta.get(index).length; j++)
			{
				byte[] d = delta.get(index);
				byte[] key2 = sk2.get(index);
				for(int k = 0; k < d.length;k++)
				{
					if(k <=15)
					{
						if((d[k] ^ (byte) ((key2[k] >> 0) & 0x1))!= 1)
						{
							break;
						}
					}
					else
					{
						if((d[k] ^ 0)!= 1)
						{
							break;
						}
					}
					if(!fIndex.contains(k))
						fIndex.add(k);
				}
			}
		}
		appendInfo("找尋符合之檔案");
		queryFileName = Owner.getFName(fIndex);
		for(int i = 0; i < queryFileName.size(); i++)
		{
			try {
				copyFileUsingFileStreams(new File("C:\\informationSecurity\\server\\" + queryFileName.get(i)) , new File("C:\\informationSecurity\\user\\" + queryFileName.get(i)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		appendInfo("產生驗證碼");
		gnerateProofToUser();
	}
	
	private static void intersectionAcc()
	{
		for(int i = 0; i < queryFileName.size(); i++)
		{
			try {
				InputStream is = Files.newInputStream(Paths.get("C:\\informationSecurity\\server\\" + queryFileName.get(i) ));
				byte[] hash = is.toString().getBytes();
				intersectACC.add(Owner.getG().powZn(Owner.getG1Hash(hash)).mul(Owner.getGS()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//DigestInputStream dis = new DigestInputStream(is , md);

			
		}
	}
	
	private static void gnerateProofToUser()
	{
		//return accumulation value
		appendInfo("傳送accumulation value驗證碼");
		User.setACC(leafDigest, rightNode, leftNode);
		
		//subset,complete witness
		appendInfo("傳送subset/complete witness驗證碼");
		intersectionAcc();
		for(int i= 0 ; i < intersectACC.size(); i++)
		{
			if(i == 0)
				subsetW = intersectACC.get(i);
			else
				subsetW = subsetW.mul(intersectACC.get(i));
		}
		for(int i = 0; i < keywordIndex.size(); i++)
		{
			Element c = null;
			c = leafDigest.get(keywordIndex.get(i)).div(subsetW);
			completeW.add(c);
		}
		
		User.setCSWitness(completeW, subsetW , keywordIndex);
		User.setFName(queryFileName);
	}
	
	private static void copyFileUsingFileStreams(File source, File dest)
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
}

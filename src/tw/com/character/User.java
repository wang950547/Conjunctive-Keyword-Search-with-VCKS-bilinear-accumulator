package tw.com.character;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import it.unisa.dia.gas.jpbc.Element;

public class User {
	
	private JFrame frmUser;
	private JList list ;
	private JScrollPane scrollPane;
	private AbstractListModel model;
	private int size = 0;
	private ArrayList<byte[]> token1;
	private ArrayList<byte[]> token2;
	private static ArrayList<Element> leafDigest;
	private static Element rightNode;
	private static Element leftNode;
	private static ArrayList<Element> completeW = new ArrayList<>();
	private static Element subsetW ;
	private JButton btnNewButton;
	private static JTextArea textArea;
	private static ArrayList<Integer> keywordIndex;
	private static ArrayList<String > queryFileName ;
	private int queryKS = 0;
	
	public User(boolean b)
	{
		frmUser = new JFrame();
		frmUser.setTitle("User");
		frmUser.setBounds(100, 100, 600, 500);
		frmUser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmUser.getContentPane().setLayout(null);
		
		btnNewButton = new JButton("Search");
		btnNewButton.setBounds(55, 375, 150, 50);
		btnNewButton.addActionListener(new SearchListener());
		frmUser.getContentPane().add(btnNewButton);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(260, 39, 314, 412);
		frmUser.getContentPane().add(scrollPane_1);
		
		textArea = new JTextArea();
		scrollPane_1.setViewportView(textArea);
		textArea.setEditable(false);
		if(b)
			appendInfo("資料夾已成功建立");
		else
			appendInfo("資料夾已存在或建立失敗");
		JLabel lblResult = new JLabel("Result");
		lblResult.setBounds(260, 11, 46, 15);
		frmUser.getContentPane().add(lblResult);
		
		JLabel lblHoldCtrlKey = new JLabel("Hold Ctrl key for multi-selecton");
		lblHoldCtrlKey.setBounds(10, 11, 161, 15);
		frmUser.getContentPane().add(lblHoldCtrlKey);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 39, 240, 311);
		frmUser.getContentPane().add(scrollPane);
		
		list = new JList();
		list.setModel(new AbstractListModel() {
			public int getSize() {
				return 0;
			}
			public Object getElementAt(int index) {
				return 0;
			}
		});
		scrollPane.setViewportView(list);
		frmUser.setResizable(false);
		frmUser.setVisible(true);
		refreshKeyWord();
	}
	
	private class task extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ArrayList<String> keywords = Owner.getKeyword();
			if(size != (keywords != null ? keywords.size() : 0))
			{
				model= new AbstractListModel()
				{
					public int getSize() {
						return keywords != null ? size = keywords.size() : 0;
					}
					public Object getElementAt(int index) {
						return keywords != null ? (String)keywords.get(index) : 0;
					}
				};
				list.setModel(model);
				scrollPane.setViewportView(list);
			}
		}
	}
	
	public void refreshKeyWord()
	{
		Timer timer = new Timer();
		timer.schedule(new task(), 2000 , 2000);
	}
	
	
	
	//send keywords and get token
	public void sendKeywordToOwner()
	{
		ArrayList<String> skeywords = (ArrayList<String>)list.getSelectedValuesList();
		queryKS = skeywords.size();
		token1 = Owner.userEncryptKeyword(skeywords, 1);
		token2 = Owner.userEncryptKeyword(skeywords, 2);
		if(token1 == null || token2 == null)
		{
			appendInfo("柴無此搜尋keyword");
			return;
		}
	}
	
	public void sendTokenToServer()
	{
		appendInfo("傳送search token至server");
		Server.keywordSearch(token1, token2);
		
	}
	
	
	
	private class SearchListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			appendInfo("傳送search keyword給予owner");
			sendKeywordToOwner();
			appendInfo("拿取加密search token");
			sendTokenToServer();
			appendInfo("準備進行資料驗證...");
			verification();
			
		}
	}
	
	
	public static void setACC(ArrayList<Element> LD , Element r , Element l)
	{
		leafDigest = LD;
		rightNode = r;
		leftNode = l;
	}
	
	public static void setCSWitness(ArrayList<Element> cW , Element sW , ArrayList<Integer> KI)
	{
		completeW = cW;
		subsetW = sW;
		keywordIndex = KI;
	}
	
	public static void setFName(ArrayList<String> fn)
	{
		queryFileName = fn;
	}
	
	public void verification()
	{
		int size = leafDigest.size();
		
		Element root = Owner.getRoot();
		//verification accumulation value
		Element l = null;
		int left = size / 2;
		int i = 0;
		for(i = 0; i < left; i++)
		{
			if(i == 0)
			l = leafDigest.get(i).mul(Owner.getGS());
			else
			{
				l = l.mul(leafDigest.get(i).mul(Owner.getGS()));
			}
		}
		Element r = null;
		for(;i < size; i++)
		{
			if(i == left)
				r = leafDigest.get(i).mul(Owner.getGS());
			else
			{
				r = r.mul(leafDigest.get(i)).mul(Owner.getGS());
			}
		}
		Element dr = r.mul(l);
		if(!l.equals(leftNode))
		{
			appendInfo("reject!!!");
			return;
		}
		if(!r.equals(rightNode))
		{
			appendInfo("reject!!!");
			return;
		}
		if(!dr.equals(root))
		{
			appendInfo("reject!!!");
			return;
		}
		Element at = null;
		for(i = 0; i < keywordIndex.size(); i++)
		{
			at = completeW.get(i).mul(subsetW);
			if(!at.equals(leafDigest.get(keywordIndex.get(i))))
			{
				appendInfo("reject!!!");
				return;
			}
		}
		appendInfo("驗證結果正確correct!!!");
		appendInfo("資料驗證成功");
		fDecrypt();
	}
	
	public void fDecrypt()
	{
		appendInfo("檔案解密中...");
		String name;
		for(int i = 0; i < queryFileName.size(); i++)
		{
			name = queryFileName.get(i);
			String subname = name.substring(0, name.lastIndexOf("en"));
			Owner.fDecrypt(new File("C:\\informationSecurity\\server\\" + name ), new File("C:\\informationSecurity\\user\\" + name.substring(0, name.lastIndexOf("en"))));
		}
		appendInfo("檔案解密完成!!!");
	}
	
	public static void appendInfo(String msg)
	{
		textArea.append(msg);
		textArea.append("\n");
	}
}

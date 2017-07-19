import java.io.File;

import tw.com.character.Owner;
import tw.com.character.Server;
import tw.com.character.User;

public class Main {

	public static File fs;
	public static File fo;
	public static File fu;
	private static boolean bools;
	private static boolean boolo;
	private static boolean boolu;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		mkdir();
		//server
		Server server = new Server(bools);
		//owner
		Owner owner = new Owner(boolo);
		//user
		User user = new User(boolu);
		
		
	}
	
	public static void mkdir()
	{
		fs = new File("C:\\informationSecurity\\server");
    	fo =  new File("C:\\informationSecurity\\owner");
    	fu =  new File("C:\\informationSecurity\\user");
    // create directories
    	bools = fs.mkdirs();
    	boolo = fo.mkdirs();
    	boolu = fu.mkdirs();
    	
	}

}

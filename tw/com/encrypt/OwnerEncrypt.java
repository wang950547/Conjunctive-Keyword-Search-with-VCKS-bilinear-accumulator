package tw.com.encrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class OwnerEncrypt {
	
	private static SecretKey secretKey1;
	private static SecretKey secretKey2;
	private static SecretKey fileSecretKey;
	
	private static byte[] iv1;
	private static byte[] iv2;
	public byte[] encryptKeyword(int k, String msg) throws Exception{
		if(k == 1)
		{
			 Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING"); 
			  cipher.init(Cipher.ENCRYPT_MODE, secretKey1, new IvParameterSpec(iv1));    
			  System.out.println(msg);
			  System.out.println("AES_CBC_PKCS5PADDING IV:"+iv1);
			  System.out.println("AES_CBC_PKCS5PADDING Algoritm:"+cipher.getAlgorithm());
			  byte[] byteCipherText = cipher.doFinal(msg.getBytes("UTF-8"));
			  System.out.println(byteCipherText[0]);
			  System.out.println("加密結果的Base64編碼：" + Base64.getEncoder().encodeToString(byteCipherText));
			  return byteCipherText;
		}
		else
		{
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING"); 
			  cipher.init(Cipher.ENCRYPT_MODE, secretKey2, new IvParameterSpec(iv2));   
			  System.out.println(msg);
			  System.out.println("AES_CBC_PKCS5PADDING IV:"+iv2);
			  System.out.println("AES_CBC_PKCS5PADDING Algoritm:"+cipher.getAlgorithm());
			  byte[] byteCipherText = cipher.doFinal(msg.getBytes("UTF-8"));
			  System.out.println(byteCipherText[0]);
			  System.out.println("加密結果的Base64編碼：" + Base64.getEncoder().encodeToString(byteCipherText));
			  return byteCipherText;
		}
		  
		}
		 
		 public void decryptKeyword(int k, byte[] cipherText) throws Exception{
			 if(k == 1)
			 {
				 Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING"); 
				  cipher.init(Cipher.DECRYPT_MODE, secretKey1, new IvParameterSpec(iv1));    
				  byte[] decryptedText = cipher.doFinal(cipherText);
				  String strDecryptedText = new String(decryptedText);
				  System.out.println("解密結果：" + strDecryptedText);
			 }
			 else
			 {
				 Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING"); 
				  cipher.init(Cipher.DECRYPT_MODE, secretKey2, new IvParameterSpec(iv2));    
				  byte[] decryptedText = cipher.doFinal(cipherText);
				  String strDecryptedText = new String(decryptedText);
				  System.out.println("解密結果：" + strDecryptedText);
			 }
		  
		 }
	  
	  //file encrypt
	  private final String ALGORITHM = "AES";
	  private final String TRANSFORMATION = "AES";
	 
	    public void encrypt( File inputFile, File outputFile)
	            throws Exception {
	        doCrypto(Cipher.ENCRYPT_MODE, inputFile, outputFile);
	    }
	 
	    public void decrypt( File inputFile, File outputFile)
	            throws Exception {
	        doCrypto(Cipher.DECRYPT_MODE, inputFile, outputFile);
	    }
	 
	    private void doCrypto(int cipherMode, File inputFile,
	            File outputFile) throws Exception {
	        try {
	            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
	            cipher.init(cipherMode, fileSecretKey);
	             
	            FileInputStream inputStream = new FileInputStream(inputFile);
	            byte[] inputBytes = new byte[(int) inputFile.length()];
	            inputStream.read(inputBytes);
	             
	            byte[] outputBytes = cipher.doFinal(inputBytes);
	             
	            FileOutputStream outputStream = new FileOutputStream(outputFile);
	            outputStream.write(outputBytes);
	             
	            inputStream.close();
	            outputStream.close();
	             
	        } catch (NoSuchPaddingException | NoSuchAlgorithmException
	                | InvalidKeyException | BadPaddingException
	                | IllegalBlockSizeException | IOException ex) {
	            throw new Exception("Error encrypting/decrypting file", ex);
	        }
	    }
	    
	    public OwnerEncrypt() throws NoSuchAlgorithmException
	    {
	    	KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128,new SecureRandom( ) );
			secretKey1 = keyGen.generateKey();
			iv1 = new byte[128 / 8]; 
			SecureRandom prng = new SecureRandom();
			prng.nextBytes(iv1);
			keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128,new SecureRandom( ) );
			secretKey2 = keyGen.generateKey();
			iv2 = new byte[128 / 8]; 
			prng = new SecureRandom();
			prng.nextBytes(iv2);
			keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128,new SecureRandom( ) );
			fileSecretKey = keyGen.generateKey();
	    }
	    
	    public static SecretKey getSecretKey1()
	    {
	    	return secretKey1;
	    }
	    
	    public static SecretKey getSecretKey2()
	    {
	    	return secretKey2;
	    }
	    
	    public static SecretKey getFileSecretKey()
	    {
	    	return fileSecretKey;
	    }
	    
	    public static byte[] getIV1()
	    {
	    	return iv1;
	    }
	    
	    public static byte[] getIV2()
	    {
	    	return iv2;
	    }
	    
	    
	
	public static void main(String[] args) throws Exception {

		  //encrypt.decryptKeyword(secretKey, cipher, iv);  
		 
	}
}
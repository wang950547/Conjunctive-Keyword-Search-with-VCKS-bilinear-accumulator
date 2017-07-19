# Conjunctive-Keyword-Search-with-VCKS-bilinear-accumulator
Implement the paper , Catch You If You Lie to Me: Efficient Verifiable Conjunctive Keyword Search over Large Dynamic Encrypted Cloud Data , Wenhai Sun∗†, Xuefeng Liu∗, Wenjing Lou†, Y. Thomas Hou† and Hui Li∗ ∗The State Key Laboratory of Integrated Services Networks, Xidian University, Xi’an, Shaanxi, China †Virginia Polytechnic Institute and State University, Blacksburg, VA, USA


Liberary:
JPBC(Java Pairing Based Cryptography Library) for implementation of Bilinear-accumulator verification.
Crypto(Javax.crypto) for AES encryption and MD5 Hashing.


Step 1:
開啟paperImple.jar檔案，
初始狀態會出現以下三個視窗(Owner , Server , User)，並出現建立資料夾成功訊息(路徑為：C://informationSecurity/owner , C://informationSecurity/server , C://informationSecurity/user)
 ![alt text](https://github.com/wang950547/Conjunctive-Keyword-Search-with-VCKS-bilinear-accumulator/blob/master/img/step1.png)

Step2:
owner可選擇要上傳至雲端的檔案(按下選取鍵)，並且給予每個檔案各自的keyword(輸入形式：keyword,keyword2,…)，並在選擇好一個檔案時按下加入檔案鍵，會將該檔案進行加密並儲存(尚未上傳)，並依序加入要上傳的檔案。
  ![alt text](https://github.com/wang950547/Conjunctive-Keyword-Search-with-VCKS-bilinear-accumulator/blob/master/img/step2.png)
  ![alt text](https://github.com/wang950547/Conjunctive-Keyword-Search-with-VCKS-bilinear-accumulator/blob/master/img/step2_1.png)
  ![alt text](https://github.com/wang950547/Conjunctive-Keyword-Search-with-VCKS-bilinear-accumulator/blob/master/img/step2_2.png)
  
Step3:若所有要上傳的檔案都已經加入完畢，則按下”上傳”鍵，即開始上傳動作，並且或會出現以下訊息(server與owner)。
   ![alt text](https://github.com/wang950547/Conjunctive-Keyword-Search-with-VCKS-bilinear-accumulator/blob/master/img/step3.png)
   
Step4:當檔案及keyword的加密資訊皆上傳完成後，則使用者可進行conjunctive keywords search，如以下：
    ![alt text](https://github.com/wang950547/Conjunctive-Keyword-Search-with-VCKS-bilinear-accumulator/blob/master/img/step4.png)
 
Step5:當選擇完所有要搜尋的keywords後，則進行bilinear accumulation相關驗證，若驗證成功則user會得到"驗證結果:correct"的訊息，並進行檔案解密，若”reject”則表示資料內容有誤。
  ![alt text](https://github.com/wang950547/Conjunctive-Keyword-Search-with-VCKS-bilinear-accumulator/blob/master/img/step5.png)

P.S 在所有過程中真實檔案與加密檔案皆會放置在各角色之資料夾(如圖)
過程中資料夾會有以下內容：
Owner:真實檔案與密文
Server:密文
User: 解密過的真實檔案與密文
  ![alt text](https://github.com/wang950547/Conjunctive-Keyword-Search-with-VCKS-bilinear-accumulator/blob/master/img/info.png)

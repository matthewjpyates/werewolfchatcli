package com.werewolfchat.werewolf_chat_cli;


import com.werewolfchat.werewolf_chat_cli.ntru.encrypt.EncryptionKeyPair;
import com.werewolfchat.werewolf_chat_cli.ntru.encrypt.EncryptionParameters;
import com.werewolfchat.werewolf_chat_cli.ntru.encrypt.EncryptionPublicKey;
import com.werewolfchat.werewolf_chat_cli.ntru.encrypt.NtruEncrypt;

import org.json.JSONObject;

import static com.werewolfchat.werewolf_chat_cli.Utility.ENCRYPTION_PARAMS;
import static com.werewolfchat.werewolf_chat_cli.Utility.dumb_debugging;
import static com.werewolfchat.werewolf_chat_cli.ntru.util.ArrayEncoder.bytesToHex;
import static com.werewolfchat.werewolf_chat_cli.ntru.util.ArrayEncoder.hexStringToByteArray;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//import sun.net.www.http.HttpClient;
/*
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
*/


//import okhttp3.*;

public class CryptoWorker {


    private NtruEncrypt ntruEnc;

    public CryptoWorker() {
        this.ntruEnc = new NtruEncrypt(ENCRYPTION_PARAMS);
    }


    public String convertPlainTextStringToEncryptedHexString(EncryptionPublicKey pubKey, String plainText) {
        return bytesToHex(this.ntruEnc.encrypt(plainText.getBytes(), pubKey));
    }

    public String convertEncryptedHexStringToPlainTextString(EncryptionKeyPair key_pair, String cypherText) {
        return new String(this.ntruEnc.decrypt(hexStringToByteArray(cypherText), key_pair));
    }


    public static void printHelp() {

        System.out.println(
                "Cryptoworker.jar Version " + returnVersionStr() + "\n" +
                        "to print this help message -h or --help\n" +
                        "to encrypt: -e key_in_hex string_to_encrypt\n" +
                        "to encrypt: --encrypt key_in_hex string_to_encrypt\n" +
                        "to print the version: -v\n" +
                        "to start interactive mode: -i\n" +
                        "to start interactive mode with a key file: -i --File=/path/to/your/key/file\n" +
                        "to start interactive mode with a specified user id (will overwrite keyfile): -i  --User_ID=username\n"+
                        "to start interactive mode with an output directory: -i --Output_Dir=/path/to/output/directory/\n\n" +
                        "in interactive mode over https with no proxy to the public server is assumed unless you specify a different type\n" +
                        "the poxy is assumed to be localhost unless otherwise specified by --Proxy_Server=my.proxy.server.url\n" +
                        "TOR:\n" +
                        "to start interactive mode over TOR without a proxy: -i --TOR_No_Proxy\n" +
                        "to start interactive mode over TOR: -i --TOR_Proxy_SOCKS=9050\n" +
                        "to start interactive mode over TOR: -i --TOR_Proxy_HTTP=8118\n" +
                        "I2P:\n" +
                        "to start interactive mode over I2P without a proxy: -i --I2P_No_Proxy\n" +
                        "to start interactive mode over I2P: -i --I2P_Proxy_SOCKS=4444\n" +
                        "to start interactive mode over I2P: -i --I2P_Proxy_HTTP=4444\n" +
                        "HTTPS with a proxy:\n" +
                        "to start interactive mode over HTTPS with a SOCKS proxy -i --HTTPS_Proxy_SOCKS=8118\n" +
                        "to start interactive mode over HTTPS with a HTTP proxy -i --HTTPS_Proxy_HTTP=8118\n" +
                        "A private server:\n" +
                        "to start interactive mode with private server: -i --Private_Server_Url=https://something.com\n" +
                        "to start interactive mode with private server with an SOCKS proxy: -i --Private_Server_Url=https://something.com --Private_Server_Proxy_SOCKS=9050\n" +
                        "to start interactive mode with private server with an HTTP proxy: -i --Private_Server_Url=https://something.com --Private_Server_Proxy_HTTP=9050\n" +
                        "you can't combine the the TOR, I2P, and private_server flags with each other\n" +
                        "all other interactive mode flags can be combined as long as the first flag is an -i\n\n"
                        + "to send a message outside of interactive mode: -s --To_Id=idtosendto  --Message=messagetosend\n"
                        + "all of the -i flags work for -s mode\n\n"
                        + "to pull messages outside of interactive mode: -pm\n"
                        + "all of the -i flags work for -pm mode\n\n"
                        + "to pull the users outside of interactive mode: -u\n" + 
                        "all of the -i flags work for -pk mode\n\n"
                        + "the -i, -e, -s, -pm, and -u flags can not be used together\n"
                        + "the -i, -e, -s, -pm, and -u have to be the first flag");


    }

    public static String returnVersionStr() {
        return "0.2";
    }

    public static void printVersion() {
        System.out.println(returnVersionStr());
    }


    
    public static String urlGetToString(String urlStr)
    {
        return urlGetToString(urlStr, null);

    }

    
    
    public static String urlGetToString(String urlStr, Proxy proxy)
    {
        URL url;
		try {
			url = new URL(urlStr);
	    	HttpURLConnection conn;
	    	if(proxy == null) {
	    	conn= (HttpURLConnection) url.openConnection();
	    	}
	    	else
	    	{
		    	conn= (HttpURLConnection) url.openConnection(proxy);
	    	}
	    	BufferedReader br;
	    	if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
	    	    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    	} else {
	    	    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
	    	}
	    	
	    	StringBuilder sb = new StringBuilder();
	    	String output;
	    	while ((output = br.readLine()) != null) {
	    	  sb.append(output);
	    	}
	    	return sb.toString();

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "error bad URL format";
		} catch (IOException e) {
			e.printStackTrace();
			return "error unable to open connection";
		}
	
    }
    
 

    public String encryptStr(String key, String thingToEncrypt) {

        EncryptionPublicKey epk = new EncryptionPublicKey(hexStringToByteArray(key));
        return convertPlainTextStringToEncryptedHexString(epk, thingToEncrypt);
    }





    //  https://howtodoinjava.com/java/io/java-read-file-to-string-examples/

    public static String fileToString(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    private static final int PROXY_TYPE_NONE = 0;
    private static final int PROXY_TYPE_SOCKS = 1;
    private static final int PROXY_TYPE_HTTP = 2;

    public class InteractiveModeWorker {
        private String serverUrl;
        private String tokenString;
        private long lastPullTime;
        private String chatID;
        private EncryptionKeyPair encKP;
//        private OkHttpClient httpClient;

        private String tokenStr;
        private String outputDir;
        private String proxyURL;
        private int proxyPort;
        private boolean usingProxy;
        private String keyFilePath;
        private ArrayList<WerewolfChatPublicKey> pubKeyArray;
        private String distEndChatID;
        private EncryptionPublicKey distEndPubKey;
        private ArrayList<WerewolfMessage> chatArray;


        private int proxyType; // 0 = none, 1 = SOCKS, 2 = HTTP

        public Proxy makeProxy()
        {
        	switch(this.proxyType)
        	{
        	case 0:
        		return null;
        	case 1:
        		return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(this.getProxyURL(), this.getProxyPort()));
        	case 2:
        		return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.getProxyURL(), this.getProxyPort()));
        	default:
        		return null;
        	}
        }
        
        
        public void printToStdOutAndToFile(String str) {
            System.out.println(str);
            Utility.appendStrToFile(this.getOutputDir() + "/log.txt", str+"\n");
        }


        public String readOneLineFromStdIn() {
            Scanner s = new Scanner(System.in);
            String str = s.nextLine();
            return str;
        }

        public String readOneLineFromStdIn(String prompt) {
            printToStdOutAndToFile(prompt);
            String intput_from_user = readOneLineFromStdIn();
            Utility.appendStrToFile(this.getOutputDir() + "/log.txt", intput_from_user);
            return intput_from_user;
        }


        public int getProxyType() {
            return proxyType;
        }

        public void setProxyType(int intputProxyType) {
            if (intputProxyType == PROXY_TYPE_NONE) { //0
                this.proxyType = PROXY_TYPE_NONE;
                this.setUsingProxy(false);
            } else if (intputProxyType == PROXY_TYPE_SOCKS) { // 1
                this.proxyType = PROXY_TYPE_SOCKS;
                this.setUsingProxy(true);
            } else if (intputProxyType == PROXY_TYPE_HTTP) { // 2
                this.proxyType = PROXY_TYPE_HTTP;
                this.setUsingProxy(true);
            }
        }


        public int getProxyPort() {
            return proxyPort;
        }

        public void setProxyPort(int intputProxyPort) {
            if (intputProxyPort > 0 && intputProxyPort < 65536) {
                this.proxyPort = intputProxyPort;
                this.setUsingProxy(true);
            } else {
                printToStdOutAndToFile(intputProxyPort + " is a bad port for a proxy");
            }
        }

        public boolean isUsingProxy() {
            return usingProxy;
        }

        public void setUsingProxy(boolean usingProxy) {
            this.usingProxy = usingProxy;
        }


        public String getProxyURL() {
            return proxyURL;
        }

        public void setProxyURL(String proxyURL) {
            this.setUsingProxy(true);
            this.proxyURL = proxyURL;
        }


        public String getServerUrl() {
            return serverUrl;
        }

        public void setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
        }


        public long getLastPullTime() {
            return lastPullTime;
        }

        public void setLastPullTime(long lastPullTime) {
            this.lastPullTime = lastPullTime;
        }

        public String getChatID() {
            return chatID;
        }

        public void setChatID(String chatID) {
            this.chatID = chatID;
        }

        public EncryptionKeyPair getEncKP() {
            return encKP;
        }

        public void setEncKP(EncryptionKeyPair encKP) {
            this.encKP = encKP;
        }


        public String getTokenStr() {
            return tokenStr;
        }

        public void setTokenStr(String tokenStr) {
            this.tokenStr = tokenStr;
        }

        public String getOutputDir() {
            return outputDir;
        }

        public void setOutputDir(String outputDir) {
            this.outputDir = outputDir;
            this.keyFilePath = outputDir + "key.txt";
        }


        //empty constructor assumes all defaults
        public InteractiveModeWorker() {
            this.serverUrl = Utility.httpsAddress;
            this.lastPullTime = System.currentTimeMillis();
            this.tokenStr = null;
            this.lastPullTime = System.currentTimeMillis();
            //this.queue = Volley.newRequestQueue();
            //this.httpClient = new OkHttpClient.Builder().build();
            this.outputDir = System.getProperty("user.home");

            this.proxyURL = "localhost";
            this.proxyType = PROXY_TYPE_NONE;
            this.keyFilePath = this.outputDir + "/key.txt";
            if(isThisAGoodKeyFile(this.keyFilePath))
            {
            	this.tryToReadKeyFile(this.keyFilePath);
            }
            else
            {
                this.encKP = ntruEnc.generateKeyPair();
                this.chatID = Utility.makeRandomString();
            }
            this.distEndChatID = null;
            this.distEndPubKey = null;
            this.chatArray = new ArrayList<>();
        }

        public void publishKeys() {
        	String result = urlGetToString(Utility.makeGetStringForPublishingKey(this.serverUrl, this.chatID, bytesToHex(this.encKP.getPublic().getEncoded())), this.makeProxy());
            //   = theResponse.body().string();
			if (result.startsWith("fail:")) {
			    printToStdOutAndToFile("failed to publish keys because of " + result);
			} else if (result.startsWith("good:")) {
			    if (result.split(":").length == 2) {
			        String encryptedToken = result.split(":")[1];
			        this.setTokenStr(convertEncryptedHexStringToPlainTextString(this.encKP, encryptedToken));
			        String verifierResult = urlGetToString(Utility.makeVerifyKeyURL(this.getServerUrl(), this.getChatID(), this.getTokenStr()), this.makeProxy());

			        if (verifierResult.equals("good:keys_verified")) {
			            printToStdOutAndToFile("keys published and verified");
			        } else if (verifierResult.startsWith("fail:")) {
			            printToStdOutAndToFile("failed to verify keys because of " + verifierResult);
			        } else {
			            printToStdOutAndToFile("bad response from server when trying to verify keys " + verifierResult);

			        }


			    } else if (result.split(":").length == 1) {
			        printToStdOutAndToFile("token string was empty");
			    } else {
			        printToStdOutAndToFile("token string was malformed");

			    }
			} else {
			    printToStdOutAndToFile("Received " + result + " while trying to publish keys");
			}


        }


        public void loadTokenStr(String inputEncryptedToken) {
            this.tokenStr = new String(ntruEnc.decrypt(hexStringToByteArray(inputEncryptedToken), this.encKP));
        }

        public void fetchATokenString() {

        	
        	String result = urlGetToString(Utility.makeGetStringForPullingNewToken(this.serverUrl, this.chatID), this.makeProxy());
            //String result = theResponse.body().string();
			if (result.equals("fail:public_key_not_found")) { // if the key can be found, it has not be published yet
			    printToStdOutAndToFile("public keys were not found on the server so publishing the local keys");
			    publishKeys();
			} else if (result.startsWith("good:")) {

			    loadTokenStr(result.split(":")[1]);
			} else {
			    printToStdOutAndToFile("Error fetching token " + result);

			}}

        public void setkeyStringsFile(String filepath) {


            try {
                FileOutputStream output = new FileOutputStream(filepath, false);
                String keysToWrite = this.encKP.getHexStringForOfflineStorage() + "," + this.chatID;
                output.write(keysToWrite.getBytes());
                output.close();
            } catch (IOException e) {
                printToStdOutAndToFile(e.getLocalizedMessage());
            }

            return;
        }


        public void setkeyStringsFile() {
            setkeyStringsFile(this.keyFilePath);
        }




        //            String getNewTokenUrl = Utility.makeGetStringForPullingNewToken(this.serverUrl, this.chatID);



        public String printPromptAndGetValue() {

            printToStdOutAndToFile("Werewolf CLI version: " + returnVersionStr() + "\n" +
                    "Available Actions: q -> quit,  c -> change/show connection config,  k -> change/show key & id config\n" +
                    "Available Actions (cont.): p -> pull messages, d -> display user list, s -> send message\n" +
                    "Available Actions (cont.): o -> change/show output dir\n" +
                    ">");
            Scanner s = new Scanner(System.in);
            String str = s.nextLine();

            return str;
        }

        public void printOutputConfig() {
            printToStdOutAndToFile("Current output directory is " + this.outputDir);
            printToStdOutAndToFile("Current logfile is " + this.outputDir + "/log.txt");
            printToStdOutAndToFile("Current keyfile is " + this.keyFilePath);

        }

        public void printNetworkConfig() {
            printToStdOutAndToFile("Chat Server: " + this.getServerUrl());
            if (this.isUsingProxy()) {
                printToStdOutAndToFile("Proxy server: " + this.getServerUrl());
                printToStdOutAndToFile("Proxy port: " + this.getProxyPort());
                if (this.proxyType == PROXY_TYPE_SOCKS)
                    printToStdOutAndToFile("Proxy type: SOCKS");
                else
                    printToStdOutAndToFile("Proxy type: HTTP");

            } else {
                printToStdOutAndToFile("No proxy");
            }
        }
        
        public boolean isThisAGoodKeyFile(String filePath)
        {
            File f = new File(filePath); 

            if (!f.exists())
            	return false;
            String keyfiledata = fileToString(filePath);
            String[] parts = keyfiledata.split(",");
            if (parts.length == 3)
            	return true;
            else
            	return false;
            
        }
       
        public void tryToReadKeyFile(String filePath) {

            String keyfiledata = fileToString(filePath);
            String[] parts = keyfiledata.split(",");
            if (parts.length == 3) {
                this.setEncKP(new EncryptionKeyPair(parts[0], parts[1]));
                //this.sigKP = new SignatureKeyPair(parts[2],parts[3]);
                this.setChatID(parts[2]);
                this.keyFilePath = filePath;
            } else {
                this.printToStdOutAndToFile(filePath + " unable to be read as a key file");
            }

        }

        public void networkConfigDialog() {
            printNetworkConfig();
            String userInput = readOneLineFromStdIn("Would you like to change the network settings? (y/n)");
            while (!Utility.isStrInStrArray(userInput, new String[]{"y", "n"})) {
                userInput = readOneLineFromStdIn("Don't know what to do with " + userInput + "\n" +
                        "Would you like to change the network settings? (y/n)");
            }

            if (userInput.equals("n")) {
                System.out.println("Leaving network config");
                return;
            }


            userInput = readOneLineFromStdIn(
                    "1) Change chat server\n" +
                            "2) Change proxy server\n" +
                            "3) Change proxy port\n" +
                            "4) Change proxy type\n" +
                            "5) Turn off proxy\n" +
                            "p -> to print network config\n" +
                            "q -> to go back to main prompt\n" +
                            ">");

            while (!Utility.isStrInStrArray(userInput, new String[]{"1", "2", "3", "4", "5", "p", "q"})) {
                userInput = readOneLineFromStdIn("Don't know what to do with " + userInput + "\n" +
                        "1) Change chat server\n" +
                        "2) Change proxy server\n" +
                        "3) Change proxy port\n" +
                        "4) Change proxy type\n" +
                        "5) Turn off proxy\n" +
                        "p -> to print network config\n" +
                        "q -> to go back to main prompt\n" +
                        ">");
            }

            if (userInput.equals("q")) {
                printToStdOutAndToFile("Leaving network config");
                return;
            } else if (userInput.equals("1")) {
                this.setServerUrl(readOneLineFromStdIn("Please enter the URL of the new chat server: "));


            } else if (userInput.equals("2")) {
                this.setProxyURL(readOneLineFromStdIn("Please enter the URL of the new proxy server: "));

            } else if (userInput.equals("3")) {
                this.setProxyPort(Integer.parseInt(readOneLineFromStdIn("Please enter the new port for the proxy server")));

            } else if (userInput.equals("4")) {
                String choiceFromUser = readOneLineFromStdIn("Please enter your choice for type of proxy:\n" +
                        "1) SOCKS proxy\n" +
                        "2) HTTP procy\n" +
                        "3) No proxy\n" +
                        ">");

                while (!Utility.isStrInStrArray(choiceFromUser, new String[]{"1", "2", "3"})) {
                    choiceFromUser = readOneLineFromStdIn("Don't know what to do with " + choiceFromUser + "\n" +
                            "Please enter your choice for type of proxy:\n" +
                            "1) SOCKS proxy\n" +
                            "2) HTTP procy\n" +
                            "3) No proxy\n" +
                            ">");
                }

                this.setProxyType(Integer.parseInt(choiceFromUser));

            } else if (userInput.equals("5")) {
                this.printToStdOutAndToFile("Turning off proxy server");
                this.setUsingProxy(false);
                this.setProxyType(0);
            } else if (userInput.equals("p")) {
                networkConfigDialog();
                return;
            }

            networkConfigDialog();
        }

        public void keyConfigDialog() {


            this.printToStdOutAndToFile("Current Chat id: " + this.chatID);
            String actionFromUser = readOneLineFromStdIn("Current keyfile: " + this.keyFilePath + "\n" +
                    "1) make new keys\n" +
                    "2) make a new random chat id\n" +
                    "3) enter an id\n" +
                    "q -> return to main menu\n" +
                    ">");
            while (!Utility.isStrInStrArray(actionFromUser, new String[]{"1", "2", "3", "q"})) {
                actionFromUser = readOneLineFromStdIn("Don't know what to do with " + actionFromUser + "\n" +
                        "1) make new keys\n" +
                        "2) make a new random chat id\n" +
                        "3) enter an id\n" +
                        "q -> return to main menu\n" +
                        ">");
            }

            if (actionFromUser.equals("q")) {
                this.printToStdOutAndToFile("Returning to main menu from key configuration dialog");
                return;
            } else if (actionFromUser.equals("1")) {
                this.printToStdOutAndToFile("Making new keys");
                this.encKP = ntruEnc.generateKeyPair();
            } else if (actionFromUser.equals("2")) {
                String newChatId = Utility.makeRandomString();
                this.printToStdOutAndToFile("Made new chat id " + newChatId);
                this.setChatID(newChatId);

            } else if (actionFromUser.equals("3")) {
                this.setChatID(this.readOneLineFromStdIn("Please enter a new ID>"));
            }


            String choiceFromUser = readOneLineFromStdIn("Do you want to overwrite the keyfile at " + this.keyFilePath + "? (y/n)\n>");
            while (!Utility.isStrInStrArray(choiceFromUser, new String[]{"y", "n"})) {
                choiceFromUser = readOneLineFromStdIn("Don't know what to do with " + choiceFromUser + "\n" +
                        "Do you want to overwrite the keyfile at " + this.keyFilePath + "? (y/n)\n>");
            }

            if (choiceFromUser.equals("y")) {
                setkeyStringsFile();
            } else {
                choiceFromUser = readOneLineFromStdIn("Please enter a path for the new key file\n>");
                this.keyFilePath = choiceFromUser;
                setkeyStringsFile();
            }
            this.fetchATokenString();

        }


        public void loadPublicKeys() {
        	String result = urlGetToString(Utility.makeGetStringForPullingKeys(this.serverUrl), this.makeProxy());

            //Response theResponse = getResponseFromURL(Utility.makeGetStringForPullingKeys(this.serverUrl));
            this.pubKeyArray = new ArrayList<>();
            ArrayList<JSONObject> jsonResults = Utility.cleanServerResults(result);
			if (jsonResults.size() == 0) {
			    printToStdOutAndToFile("Unable to pull any users from the server, please check your network settings");

			} else {

			    for (JSONObject jobj : jsonResults) {
			        try {
			            pubKeyArray.add(new WerewolfChatPublicKey(jobj.getString("chatid"), jobj.getString("pubkeyhexstr")));
			        } catch (Exception e) {
			            printToStdOutAndToFile("There was bad JSON with " + jobj.toString() + "\n" + e.toString());
			        }

			    }
			}
        }


        public void displayPublicKeys() {

            if (pubKeyArray.isEmpty()) {
                printToStdOutAndToFile("There are no users on this server, try changing the network configs");
                return;
            }
            int userNumber = 0;
            for (WerewolfChatPublicKey pubKey : pubKeyArray) {
                printToStdOutAndToFile(userNumber + ") " + pubKey.chat_id);
                userNumber++;
            }
        }


        public String[] makeStrArrayOfPossibleUserIDIndexes() {
            ArrayList<String> tempArrayList = new ArrayList<>();
            for (int ii = 0; ii < this.pubKeyArray.size(); ii++) {
                tempArrayList.add(Integer.toString(ii));
            }

            return (String[]) tempArrayList.toArray();

        }

        public void displayUserListDialog() {
            displayPublicKeys();

            String actionFromUser = readOneLineFromStdIn("Please enter the number of the other user to talk to\n>");
            //String[] possibleChoices = this.makeStrArrayOfPossibleUserIDIndexes();
           
            
            while (!Utility.isNumeric(actionFromUser) || Integer.parseInt(actionFromUser)<0 ||  Integer.parseInt(actionFromUser)>(this.pubKeyArray.size()-1)) {
                actionFromUser = readOneLineFromStdIn("Don't know what to do with " + actionFromUser + "\n" +
                        "Please enter the number of the other user to talk to\n>");
            }


            printToStdOutAndToFile("Choosing chat id: " + this.pubKeyArray.get(Integer.parseInt(actionFromUser)).chat_id);

            this.distEndPubKey = new EncryptionPublicKey(hexStringToByteArray(this.pubKeyArray.get(Integer.parseInt(actionFromUser)).public_key_hexstring));
            this.distEndChatID = this.pubKeyArray.get(Integer.parseInt(actionFromUser)).chat_id;

            printToStdOutAndToFile("Returning to main menu");

        }

        public void sendMessagesDialog() {
            if (this.distEndChatID == null || this.distEndPubKey == null) {
                printToStdOutAndToFile("Distant End is not set, please set a distant end");
                return;
            }
            if (this.tokenStr == null) {
                printToStdOutAndToFile("Token is not set, please check your key configuration");
                return;
            }
            String messageToSend = readOneLineFromStdIn("Sending a message to: " + this.distEndPubKey + "\n" +
                    "Please enter your message>");
            //makeGetStringForPublishingMessages(String serverUrl, String toid, String fromid, String message, String token)
           
        	String resultStr = urlGetToString(Utility.makeGetStringForPublishingMessages(this.serverUrl, this.distEndChatID, this.chatID,
                    convertPlainTextStringToEncryptedHexString(this.distEndPubKey, messageToSend), this.tokenStr), this.makeProxy());

            //Response theResponse = getResponseFromURL(Utility.makeGetStringForPublishingMessages(this.serverUrl, this.distEndChatID, this.chatID,
            //        convertPlainTextStringToEncryptedHexString(this.distEndPubKey, messageToSend), this.tokenStr));

            // String resultStr = theResponse.body().string();
			if (resultStr.equals("success")) {
			    printToStdOutAndToFile("Message sent");
			} else if (resultStr.startsWith("fail:")) {
			    printToStdOutAndToFile("Error sending message " + resultStr);
			} else {
			    printToStdOutAndToFile("Server sent back unexpected response: " + resultStr);
			}
            return;
        }
        
        public void sendMessage(String message, String usrID)
        {
        	
        	int index = 0;
        	this.distEndPubKey =null;
        	this.distEndChatID = null;
        	
        	if(this.pubKeyArray.size() == 0)
        	{
        		printToStdOutAndToFile("no users found");
        		return;
        	}
        	
        	System.out.println("The user id is "+usrID);
        	
        	for(index =0; index<this.pubKeyArray.size(); index++)
        	{
        		if(this.pubKeyArray.get(index).chat_id.equals(usrID))
        		{
        			this.distEndPubKey = new EncryptionPublicKey(hexStringToByteArray(this.pubKeyArray.get(index).public_key_hexstring));
                    this.distEndChatID = this.pubKeyArray.get(index).chat_id;
                    break;
        		}
        	}
        	
        	if(this.distEndPubKey == null || this.distEndChatID == null)
        	{
        		printToStdOutAndToFile("User Id " + usrID +" could not be found");
        		return;
        	}
        	
        	System.out.println("This is the url string " +Utility.makeGetStringForPublishingMessages(this.serverUrl, this.distEndChatID, this.chatID,
                    convertPlainTextStringToEncryptedHexString(this.distEndPubKey, message), this.tokenStr));
        	
        	String resultStr = urlGetToString(Utility.makeGetStringForPublishingMessages(this.serverUrl, this.distEndChatID, this.chatID,
                    convertPlainTextStringToEncryptedHexString(this.distEndPubKey, message), this.tokenStr), this.makeProxy());

            //Response theResponse = getResponseFromURL(Utility.makeGetStringForPublishingMessages(this.serverUrl, this.distEndChatID, this.chatID,
            //        convertPlainTextStringToEncryptedHexString(this.distEndPubKey, messageToSend), this.tokenStr));

            // String resultStr = theResponse.body().string();
			if (resultStr.equals("success")) {
			    printToStdOutAndToFile("Message sent");
			} else if (resultStr.startsWith("fail:")) {
			    printToStdOutAndToFile("Error sending message " + resultStr);
			} else {
			    printToStdOutAndToFile("Server sent back unexpected response: " + resultStr);
			}
        }


        public void pullMessages() {
            if (this.tokenStr == null) {
                printToStdOutAndToFile("Not pulling messages, token string is null, please check ");
            }

            //Response theResponse = getResponseFromURL(Utility.makeGetStringForPullingMessagesWithToken(this.serverUrl, this.chatID, this.tokenStr));

            
        	String resultStr = urlGetToString(Utility.makeGetStringForPullingMessagesWithToken(this.serverUrl, this.chatID, this.tokenStr), this.makeProxy());

            
            //String result = theResponse.body().string();
			ArrayList<JSONObject> jsonResults = Utility.cleanServerResults(resultStr);
			if (jsonResults.size() == 0) {
			    printToStdOutAndToFile("No Messages found for "+ this.chatID);
			    this.chatArray= new ArrayList<>();
			    return;
			} else {
			    this.chatArray= new ArrayList<>();
			    for (JSONObject jobj : jsonResults) {
			    	if(!jobj.has("toid"))
			    	{
			    		this.printToStdOutAndToFile("Error in JSON from server");
			    	} 
			        WerewolfMessage temp = new WerewolfMessage(jobj.getString("toid"), jobj.getString("fromid"), jobj.getString("encmessagehexstr"));
			        temp.convertEncryptedHexStringToPlainTextString(this.encKP, ntruEnc);
			        chatArray.add(temp);
			    }
			}


        }

        public void printMessages()
        {
            if(this.chatArray.size() ==0)
            {
                printToStdOutAndToFile("No messages received for "+this.chatID);
                return;
            }
            else
            {
                printToStdOutAndToFile("Printing Messages for "+ this.chatID);
            }
            for(WerewolfMessage message : this.chatArray)
            {
                printToStdOutAndToFile("from: "+ message.getSenderId()+ ", to: "+ message.getDestinationId()+ ", messages: "+ message.getText() );
            }
            return;
        }

        //TODO write the interface for pulling new messages
        public void pullMessagesDialog() {
            String actionFromUser = readOneLineFromStdIn(this.chatArray.size()+" messages pulled for " + this.chatID+"\n"+
            "Using this chat server: " + this.serverUrl + "\n" +
                    "1) check for more messages\n" +
                    "2) display messages\n" +
                    "q -> return to main menu\n" +
                    ">");
            while (!Utility.isStrInStrArray(actionFromUser, new String[]{"1", "2", "q"})) {
                 actionFromUser = readOneLineFromStdIn("Don't know what to do with " + actionFromUser + "\n"+
                         this.chatArray.size()+" messages pulled for " + this.chatID+"\n"+
                        "Using this chat server: " + this.serverUrl + "\n" +
                        "1) check for more messages\n" +
                        "2) display messages\n" +
                        "q -> return to main menu\n" +
                        ">");
            }
            switch (actionFromUser)
            {
                case "1":
                    this.pullMessages();
                    break;
                case "2":
                    printMessages();
                    break;
                case "q":
                    printToStdOutAndToFile("Returning to main menu");
                    return;
            }

        }


        public void outputDirDialog() {
            printOutputConfig();
            String actionFromUser = readOneLineFromStdIn("Would you like to change the output Directory? (y/n)");
            while (!Utility.isStrInStrArray(actionFromUser, new String[]{"y", "n"})) {
                actionFromUser = readOneLineFromStdIn("Don't know what to do with " + actionFromUser + "\n" +
                        "Would you like to change the output Directory? (y/n)");
            }

            if (actionFromUser.equals("n")) {
                printToStdOutAndToFile("Returning to main menu");
                return;
            } else if (actionFromUser.equals("y")) {
                this.setOutputDir(readOneLineFromStdIn("Please provide the new output directory"));
                setkeyStringsFile();
            }


        }

        public void runInteractiveMode() {
            boolean keepRunning = true;
            this.loadPublicKeys();
            this.fetchATokenString();
            while (keepRunning) {
                String input = printPromptAndGetValue();
                switch (input) {
                    case "q":
                        printToStdOutAndToFile("Quiting");
                        keepRunning = false;
                        break;
                    case "c":
                        networkConfigDialog();
                        break;
                    case "k":
                        keyConfigDialog();
                        break;
                    case "p":
                        pullMessagesDialog();
                        break;
                    case "d":
                        displayUserListDialog();
                        break;
                    case "s":
                        sendMessagesDialog();
                        break;
                    case "o":
                        outputDirDialog();
                        break;
                    default:
                        printToStdOutAndToFile("Sorry, don't have an action for " + input);
                        break;


                }

            }
        }

    }


    public void loadFlagsForISPMUFlags(String args[]) {
        InteractiveModeWorker worker = new InteractiveModeWorker();
        
        
        if (args.length < 1) {
            System.out.println("invalid arguments");
            return;
        }
        
        if(args.length == 1 && args[0].equals("-i"))
        {
        	worker.runInteractiveMode();
        	return;
        }
        
        if(args.length == 1 && args[0].equals("-pm"))
        {
        	worker.fetchATokenString();
        	worker.pullMessages();
        	worker.printMessages();
        	return;
        }
        
        if(args.length == 1 && args[0].equals("-u"))
        {
        	worker.loadPublicKeys();
        	worker.displayPublicKeys();
        	return;
        }
        
        if(args.length == 1) {
        	System.out.println("Invalid singleton argument with "+ args[0]);
            return;
        }
        
        if (!(args.length > 1 && (args[0].equals("-i") || args[0].equals("-s")  
        		|| args[0].equals("-pm") || args[0].equals("-u")  ))) {
        	System.out.println("Invalid first arguments with "+ args[0]);
            return;
        }
        String firstArg = args[0];
        
        ArrayList<String> argsAFterFirst = new ArrayList<String>();
        for (int ii =1; ii < args.length; ii++) {
        	argsAFterFirst.add(args[ii]);
        }
        

        String userIDToSendTo = "";
        String messageToSend = "";

        for (String value : argsAFterFirst) {
            if (value.startsWith("--File=")) {
                if (value.split("=").length == 2) {
                    worker.tryToReadKeyFile(value.split("=")[1]);
                } else {
                    worker.printToStdOutAndToFile("improper formatting for file with " + value);
                }


            } else if (value.startsWith("--Output_Dir=")) {
                if (value.split("=").length == 2) {
                    worker.setOutputDir(value.split("=")[1]);
                } else {
                    worker.printToStdOutAndToFile("improper formatting for output dir with " + value);
                }

            } else if (value.startsWith("--Proxy_Server=")) {
                if (value.split("=").length == 2) {
                    worker.setProxyURL(value.split("=")[1]);
                } else {
                    worker.printToStdOutAndToFile("improper formatting for proxy server url with " + value);
                }

            } else if (value.startsWith("--TOR_Proxy_SOCKS=")) {
                if (value.split("=").length == 2) {
                    worker.setProxyPort(Integer.parseInt(value.split("=")[1]));
                    worker.setServerUrl(Utility.torAddress);
                    worker.setProxyType(PROXY_TYPE_SOCKS);
                } else {
                    worker.printToStdOutAndToFile("improper formatting for tor socks proxy with " + value);
                }

            } else if (value.startsWith("--TOR_Proxy_HTTP=")) {
                if (value.split("=").length == 2) {
                    worker.setProxyPort(Integer.parseInt(value.split("=")[1]));
                    worker.setServerUrl(Utility.torAddress);
                    worker.setProxyType(PROXY_TYPE_HTTP);
                } else {
                    worker.printToStdOutAndToFile("improper formatting for tor http proxy with " + value);
                }


            } else if (value.startsWith("--I2P_Proxy_SOCKS=")) {
                if (value.split("=").length == 2) {
                    worker.setProxyPort(Integer.parseInt(value.split("=")[1]));
                    worker.setServerUrl(Utility.i2pAddress);
                    worker.setProxyType(PROXY_TYPE_SOCKS);
                } else {
                    worker.printToStdOutAndToFile("improper formatting for i2p socks proxy with " + value);
                }
            } else if (value.startsWith("--I2P_Proxy_HTTP=")) {
                if (value.split("=").length == 2) {
                    worker.setProxyPort(Integer.parseInt(value.split("=")[1]));
                    worker.setServerUrl(Utility.i2pAddress);
                    worker.setProxyType(PROXY_TYPE_HTTP);
                } else {
                    worker.printToStdOutAndToFile("improper formatting for i2p http proxy with " + value);
                }
            } else if (value.startsWith("--HTTPS_Proxy_SOCKS=")) {
                if (value.split("=").length == 2) {
                    worker.setProxyPort(Integer.parseInt(value.split("=")[1]));
                    worker.setServerUrl(Utility.httpsAddress);
                    worker.setProxyType(PROXY_TYPE_SOCKS);
                } else {
                    worker.printToStdOutAndToFile("improper formatting for https server socks proxy port with " + value);
                }
            } else if (value.startsWith("--HTTPS_Proxy_HTTP=")) {
                if (value.split("=").length == 2) {
                    worker.setProxyPort(Integer.parseInt(value.split("=")[1]));
                    worker.setServerUrl(Utility.httpsAddress);
                    worker.setProxyType(PROXY_TYPE_HTTP);
                } else {
                    worker.printToStdOutAndToFile("improper formatting for https server http proxy port with " + value);
                }
            } else if (value.startsWith("--private_server_url=")) {
                if (value.split("=").length == 2) {
                    worker.setServerUrl(value.split("=")[1]);
                } else {
                    dumb_debugging("improper formatting for private server url " + value);
                }
            } else if (value.startsWith("--Private_Server_Proxy_SOCKS=")) {
                if (value.split("=").length == 2) {
                    worker.setProxyPort(Integer.parseInt(value.split("=")[1]));
                    worker.setProxyType(PROXY_TYPE_SOCKS);
                } else {
                    worker.printToStdOutAndToFile("improper formatting for private server socks proxy port with " + value);
                }
            } else if (value.startsWith("--Private_Server_Proxy_HTTP=")) {
                if (value.split("=").length == 2) {
                    worker.setProxyPort(Integer.parseInt(value.split("=")[1]));
                    worker.setProxyType(PROXY_TYPE_HTTP);
                } else {
                    worker.printToStdOutAndToFile("improper formatting for private server http proxy port with " + value);
                }
            } else if (value.equals("--TOR_No_Proxy")) {
                worker.setServerUrl(Utility.torAddress);
                worker.setUsingProxy(false);
            } else if (value.equals("--I2P_No_Proxy")) {
                worker.setServerUrl(Utility.i2pAddress);
                worker.setUsingProxy(false);
            } else if (value.startsWith("--To_Id=")) {
                if (value.split("=").length == 2) {
                	 userIDToSendTo = value.split("=")[1];
                } else {
                    worker.printToStdOutAndToFile("improper formatting for to id with " + value);
                }
            }else if (value.startsWith("--Message=")) {
                if (value.split("=").length == 2) {
                    messageToSend = value.split("=")[1];
                } else {
                    worker.printToStdOutAndToFile("improper formatting for message to send with " + value);
                }
            }else {
                worker.printToStdOutAndToFile("Did not recognise " + value + " as an argument");
            }
        }

        
        // --To_Id=idtosendto  --Message=messagetosend
        // apply whatever switches were set for the connection
       // worker.makeNewHttpClient();
        
        switch(firstArg)
        {
        case "-i": // start interactive mode
        	worker.runInteractiveMode();
        	break;
        case "-s": //send message
        	if(messageToSend.equals(""))
        	{
        		System.out.println("The message to send was not sent.");
        		return;
        	}
        	else if (userIDToSendTo.equals(""))
        	{
        		System.out.println("The user id of whom to send to was not set.");
        		return;
        	}
        	else
        	{
        		worker.fetchATokenString();
            	worker.loadPublicKeys();
            	worker.sendMessage(messageToSend, userIDToSendTo);
        		
        	}
        	break;
        case "-pm": //pull messages and print them
        	worker.fetchATokenString();
        	worker.pullMessages();
        	break;
        case "-u": // pull users and print them
        	worker.loadPublicKeys();
        	worker.displayPublicKeys();
        	break;
        default:
        	
        }


    }


    public static void main(String[] args) {

//System.out.println(args.length);

        if (args.length == 0) {
            System.out.println("no arguments were passed");
            return;
        }

        if (args.length > 0 && args[0].equals("-i")) {
            CryptoWorker worker = new CryptoWorker();
            worker.loadFlagsForISPMUFlags(args);
            return;
        } else if (args.length > 0 && args[0].equals("-s")) {
            CryptoWorker worker = new CryptoWorker();
            worker.loadFlagsForISPMUFlags(args);
            return;
        } else if (args.length > 0 && args[0].equals("-pm")) {
            CryptoWorker worker = new CryptoWorker();
            worker.loadFlagsForISPMUFlags(args);
            return;
        } else if (args.length > 0 && args[0].equals("-u")) {
            CryptoWorker worker = new CryptoWorker();
            worker.loadFlagsForISPMUFlags(args);
            return;
        }

        if (args.length == 1) {
            switch (args[0]) {
                case "-h":
                    printHelp();
                    break;
                case "--help":
                    printHelp();
                    break;

                case "--version":
                    printVersion();
                    break;

                case "-v":
                    printVersion();
                    break;


                default:
                    System.out.println("Invalid Args");
                    break;
            }
            return;
        }

        if (args.length == 3) {
            CryptoWorker worker = new CryptoWorker();
            switch (args[0]) {

                //encryption stuff
                case "-e":
                    System.out.println(worker.encryptStr(args[1], args[2]));
                    break;

                case "--encrypt":
                    System.out.println(worker.encryptStr(args[1], args[2]));
                    break;

                default:
                    System.out.println("Invalid Args");
                    break;
            }
            return;
        }


        System.out.println("Invalid Args");
        return;
    }
}

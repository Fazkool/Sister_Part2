// File Name Server.java



import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Server extends Thread
{
   private ServerSocket serverSocket;
   
   private Database database = new Database();
   
   private int tokenMin,tokenMax,trackerPort;
   String trackerIP;
   
   int myPort;
   
   public Server(int port) throws IOException
   {
      myPort = port;
      serverSocket = new ServerSocket(port);
      trackerIP="localhost";
      trackerPort=12312;
      onCreate();
   }
   
    public Server(ServerSocket serverSocket,Database newDB, int trackerPort, String trackerIP) {
        
        this.database = newDB;
        this.serverSocket = serverSocket;
        this.tokenMin = tokenMin;
        this.tokenMax = tokenMax;
        this.trackerPort = trackerPort;
        this.trackerIP = trackerIP;
    }

    public int getTrackerPort() {
        return trackerPort;
    }

    public String getTrackerIP() {
        return trackerIP;
    }

    public int getTokenMin() {
        return tokenMin;
    }

    public int getTokenMax() {
        return tokenMax;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setTrackerPort(int trackerPort) {
        this.trackerPort = trackerPort;
    }

    public void setTrackerIP(String trackerIP) {
        this.trackerIP = trackerIP;
    }

    public void setTokenMin(int tokenMin) {
        this.tokenMin = tokenMin;
    }

    public void setTokenMax(int tokenMax) {
        this.tokenMax = tokenMax;
    }

    public void createTableOnAllServer(String tableName){
     try{
        System.out.println("Connecting to " + trackerIP
                             + " on port " + trackerPort);
         Socket tracker = new Socket(trackerIP, trackerPort);
         System.out.println("Just connected to "
                      + tracker.getRemoteSocketAddress());     
            //kirim data ke Tracker
            OutputStream outToServer = tracker.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF("create "+tableName);
            
            //baca jawaban server
            InputStream inFromServer = tracker.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            System.out.println("Tracker says " + in.readUTF());    
        
        }
        catch(Exception e){
        
        e.printStackTrace();
       }
    
    
    }
    
   public void run()
   {    
      while(true)
      {
         try
         {
            System.out.println("Waiting for client on port " +
            serverSocket.getLocalPort() + "...");
            Socket server = serverSocket.accept();
            System.out.println("Just connected to " + server.getRemoteSocketAddress());
			
            while(true){
                //baca data dari client	  
                DataInputStream in = new DataInputStream(server.getInputStream());
                String inputClient;
                inputClient = in.readUTF();
                System.out.println(inputClient);
                
                //TOKENING INPUTAN CLIENT
                String[] tokenString = inputClient.split(" ");
                
                String message = "";
                
                if(tokenString[0].equals("exit")){
                    //ini yang dikirim ke client
                    System.out.println("Closing Connection");
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    out.writeUTF("Thank you for connecting");
                    //tutup server nya
                    server.close();
                    break;
                }else if(tokenString[0].equals("create")){
                    if(tokenString.length != 3){
                        System.out.println("Parameter tidak sesuai");
                        message = "parameter tidak sesuai";
                    }else{
                        System.out.println("Command Create Table");
                        //kasih tau server lain buat bikin table dengan nama ini
                        createTableOnAllServer(tokenString[2]);
                        ////////
                        message = database.createTable(tokenString[2]);
                    }
                }else if(tokenString[0].equals("insert")){
                    if(tokenString.length != 4){
                        System.out.println("Parameter tidak sesuai");
                    }else{
                        System.out.println("Command insert");
                        
                        //check apakah key masuk dalam range token server atau tidak
                        if(Integer.parseInt(tokenString[2])>=tokenMin &&Integer.parseInt(tokenString[2])>=tokenMax)
                            message = database.insert(tokenString[1], Integer.parseInt(tokenString[2]), tokenString[3]);
                        else
                            message = insertOnAnotherServer(tokenString[1], Integer.parseInt(tokenString[2]), tokenString[3]);
                    }
                }else if(tokenString[0].equals("display")){
                    if(tokenString.length != 2){
                        System.out.println("Parameter tidak sesuai");
                        message = "parameter tidak sesuai";
                    }else{
                        System.out.println("Command display");
                        //message = database.display(tokenString[1]);
                        message = listOnAllServer(tokenString[1]);
                    }
                 }else if(tokenString[0].equals("displayTracker")){
                    if(tokenString.length != 2){
                        System.out.println("Parameter tidak sesuai");
                        message = "parameter tidak sesuai";
                    }else{
                        System.out.println("Command display");
                        message = database.display(tokenString[1]);
                        //message = listOnAllServer(tokenString[1]);
                    }
                }
                else{
                    System.out.println("Command Tidak Dikenali");
                    message = "Command tidak dikenali";
                }
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF(message);
            }
        }catch(SocketTimeoutException s)
        {
            System.out.println("Socket timed out!");
            break;
        }catch(IOException e)
        {
            e.printStackTrace();
            break;
        }
      }
   }
   
   public boolean onCreate(){
    boolean trackerFound =false;
         try
      {
         System.out.println("Connecting to " + trackerIP
                             + " on port " + trackerPort);
         Socket client = new Socket(trackerIP, trackerPort);
         System.out.println("Just connected to "
                      + client.getRemoteSocketAddress());
         
            //kirim data ke server
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF("addServer " + myPort);
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            System.out.println("Tracker says " + in.readUTF());
        // }
        //matiin client nya

        client.close();
        trackerFound = true;
      }catch(IOException e)
      {
         e.printStackTrace();
      }
    
    
    return trackerFound;
   }
   public boolean searchInTable(){
    boolean found = false;
    
    
    
    return found;
   }
   
   public static void main(String [] args)
   {
      int port = Integer.parseInt(args[0]);
      try
      {
         Thread t = new Server(port);
         t.start();
      }catch(IOException e)
      {
         e.printStackTrace();
      }
   }

    private String insertOnAnotherServer(String tableName, int Key, String Value) {
        String message ="";
        try{
        //hubungin tracker tanya token dengan nilai key , ada di serve mana
        System.out.println("Connecting to " + trackerIP
                             + " on port " + trackerPort);
         Socket client = new Socket(trackerIP, trackerPort);
         System.out.println("Just connected to "
                      + client.getRemoteSocketAddress());
          
          
         
            //kirim data ke tracker
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF("request "+Key);
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            System.out.println("Tracker says " + in.readUTF());
          
        
        //kembalian dari server dengan format IP , port
        String[] trackerAnswer = in.readUTF().split(" ");  
        //hubungin server yang memiliki key , kemudian buat di server tersebut message
         System.out.println("Connecting to " + trackerAnswer[0]
                             + " on port " + trackerAnswer[1]);
         Socket anotherServer = new Socket(trackerAnswer[0],Integer.parseInt(trackerAnswer[1]));
         System.out.println("Just connected to "
                      + anotherServer.getRemoteSocketAddress());
          
            //kirim data ke tracker
            outToServer = anotherServer.getOutputStream();
            out = new DataOutputStream(outToServer);

            out.writeUTF("insert "+tableName+" "+Key+" "+Value);
            inFromServer = anotherServer.getInputStream();
            in = new DataInputStream(inFromServer);
            System.out.println("Tracker says " + in.readUTF());
          
            message = in.readUTF();
        }catch(Exception e){
        
        }
        return message;
    }


    private String listOnAllServer(String namaTable){
        String list="";
        try{
        //hubungin tracker , tracker bakal minta semua serve ngirim list nya , dan di urutin
        //hubungin tracker tanya token dengan nilai key , ada di serve mana
        System.out.println("Connecting to " + trackerIP
                             + " on port " + trackerPort);
         Socket client = new Socket(trackerIP, trackerPort);
         System.out.println("Just connected to "
                      + client.getRemoteSocketAddress());
          
          
         
            //kirim data ke tracker
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF("display "+namaTable);
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            System.out.println("Tracker says " + in.readUTF());
            list = in.readUTF();
            
            String[] lists;
            lists = list.split(" ");
            tokenMin = Integer.parseInt(lists[0]);
            tokenMax = Integer.parseInt(lists[1]);
            
        }catch(Exception e){
        
        e.printStackTrace();
        }
        return list;
    }

}

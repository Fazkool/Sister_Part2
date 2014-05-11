// File Name Server.java



import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Tracker extends Thread
{
   private ServerSocket serverSocket;
   
   //DATA TRACKER
   private ArrayList<Integer> ArrPort;
   private ArrayList<String> ArrIp;
   private ArrayList<Integer> TokenMin;
   private ArrayList<Integer> TokenMax;
   private ArrayList<String> arrTable;
   
   public final static Integer maxInitToken = Integer.MAX_VALUE;
   
   public Tracker(int port) throws IOException
   {
      serverSocket = new ServerSocket(port);
      ArrPort = new ArrayList<Integer>();
      ArrIp = new ArrayList<String>();
      TokenMin = new ArrayList<>();
      TokenMax = new ArrayList<>();
      arrTable = new ArrayList<>();
   }

   public String getIp(int token){
       String st = new String();
       for(int i = 0;i<ArrIp.size();i++){
           if(token >= TokenMin.get(i) && token <= TokenMax.get(i)){
               return ArrIp.get(i) + " " + ArrPort.get(i);
           }
       }
       return st;
   }
   
   public void addServer(String server,int port){
       int biggestServer = getBiggestServer();
       
       //ADD ALAMAT PORT
       try{
            ArrIp.add(biggestServer+1,server);
            ArrPort.add(biggestServer+1,port);
       }catch(Exception e){
            ArrIp.add(server);
            ArrPort.add(port);
       }
       
       
       //ADD TOKEN PADA SERVER YANG INGIN DITAMBAH
       //HANDLE JUMLAH SERVER 0
       if(ArrIp.size() == 1){
           TokenMax.add(maxInitToken);
           TokenMin.add(0);
       }else{
            //HANDLE JUMLAH SERVER NORMAL
            try{
                TokenMax.add(biggestServer+1,TokenMax.get(biggestServer));
                TokenMin.add(biggestServer+1,(int)(((long)TokenMax.get(biggestServer)+(long)TokenMin.get(biggestServer))/(long)2));
                TokenMax.set(biggestServer,TokenMin.get(biggestServer+1)-1);
            }catch(Exception e){
                TokenMax.add(TokenMax.get(biggestServer));
                TokenMin.add((TokenMax.get(biggestServer)+TokenMin.get(biggestServer))/2);
                TokenMax.set(biggestServer,TokenMin.get(TokenMin.size()-1)-1);
            }
            //POTONG UKURAN TOKEN YANG DIBAGI
            
       }
       
       
       
   }
   
   public int getBiggestServer(){
       int maxServer = 0;
       int idx = 0;
       
       for(int i = 0;i < ArrIp.size();i++){
           if(TokenMax.get(i) - TokenMin.get(i) > maxServer){
               maxServer = TokenMax.get(i) - TokenMin.get(i);
               idx = i;
           }
       }
       return idx;
   }
   
   public void migrateDB(int tokenMin,int tokenMax,String fromIP, int fromPort,String toIP, int toPort) throws IOException{
       //BUAT KONEKSI KE SERVER YANG AKAN DIKIRIM DATANYA
       System.out.println("Connecting to " + fromIP + " on port " + fromPort);
       Socket fromServer = new Socket(fromIP,fromPort);
       System.out.println("Connected to " + fromIP + " on port " + fromPort);
       
       //NGAMBIL DATA STREAM SERVER DARI
       OutputStream outToFrom = fromServer.getOutputStream();
       DataOutputStream out = new DataOutputStream(outToFrom);
       
       //KIRIM COMMAND MIGRASI DATA
       out.writeUTF("migrate " + toIP + " " + toPort + " " + tokenMin + " " + tokenMax);
       
       //TUTUP KONEKSI
       fromServer.close();
   }
   
   //FUNGSI UNTUK MEMINTA YANG LAIN CREATE TABLE
   public void createTableAll(String fromIP, int fromPort, String tableName) throws IOException{
       
       //MENAMBAH TABLE KE TABLE TABLE
       arrTable.add(tableName);
       
       //MEMINTA SERVER LAIN MEMBUAT TABLE
       System.out.println("Meminta server lain untuk membuat tabel");
       for(int i = 0;i<ArrIp.size();i++){
           
           if(!ArrIp.get(i).equals(fromIP) && ArrPort.get(i)!= fromPort){//IGNORE IP PENGIRIM
               System.out.println("Meminta ke " + ArrIp.get(i) +":" + ArrPort.get(i));
               
               //MEMBUKA KONEKSI
               Socket serverUpdate = new Socket(ArrIp.get(i),ArrPort.get(i));
               
               //MEMINTA OUTPUT STREAM + KIRIM PERINTAH
               OutputStream outToServer = serverUpdate.getOutputStream();
               DataOutputStream out = new DataOutputStream(outToServer);
               out.writeUTF("create table tracker " + tableName);
               
               //MATIIN KONEKSI
               serverUpdate.close();
           }
       }
   }
           
   
   public void displayStatus(){
       System.out.println("Kondisi Server");
       for(int i = 0;i<ArrIp.size();i++){
           System.out.println(i + ". " + ArrIp.get(i) + ":" + ArrPort.get(i) + " " + TokenMin.get(i) + " " + TokenMax.get(i));    
       }
       System.out.println("\n\nKondisi Table");
       for(int i = 0;i<arrTable.size();i++){
           System.out.println(arrTable.get(i));
       }
   }
   
   public String getDisplay(String fromIp,int fromPort,String tableName) throws IOException{
       
       String message = "";
       
       for(int i = 0;i<ArrIp.size();i++){
           if(!(fromIp.equals(ArrIp.get(i)) && fromPort==ArrPort.get(i))){
               
               System.out.println("Getting display from " + ArrIp.get(i) +":" + ArrPort.get(i));
               
               Socket displayServer = new Socket(ArrIp.get(i),ArrPort.get(i));
               
               OutputStream outToServer = displayServer.getOutputStream();
               DataOutputStream out = new DataOutputStream(outToServer);
               out.writeUTF("displayTracker " + tableName);
               
               DataInputStream inn = new DataInputStream(displayServer.getInputStream());
               String inputClient;
               inputClient = inn.readUTF();
               displayServer.close();
               message += inputClient;
           }
       }
       System.out.println(message);
       return message;
   }
   
   @Override
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
			
            String message = "Command Salah";
            
            while(true){
                //baca data dari client	  
                DataInputStream in = new DataInputStream(server.getInputStream());
                String inputClient;
                inputClient = in.readUTF();
                System.out.println(inputClient);
                
                String[] tokenString = inputClient.split(" ");
                
                if(tokenString[0].equals("addServer")){
                    //CARI SERVER YANG HARUS DIMIGRASI
                    int migrateServerIdx = getBiggestServer();                            
                    
                    //TAMBAHIN KE TABEL SERVER
                    addServer(server.getInetAddress().toString().substring(1),Integer.parseInt(tokenString[1]));
                    
                    //KIRIM JUMLAH TOKEN KE YANG MINTA
                    if(ArrIp.size() == 1){
                        message = TokenMin.get(migrateServerIdx) + " " + TokenMax.get(migrateServerIdx);
                    }else{
                        message = TokenMin.get(migrateServerIdx+1) + " " + TokenMax.get(migrateServerIdx+1);
                    }
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    out.writeUTF(message);
                    server.close();//DUMMY CLOSE
                    server = null;
                    
                    //BIKIN SELURUH TABLE DI SERVER BARU
                    /*for(int i = 0;i<arrTable.size();i++){
                        //NGEBIKIN KONEKSI + AMBIL STREAM
                        Socket serverTable = new Socket(ArrIp.get(ArrIp.size()-1),ArrPort.get(ArrPort.size()-1));//NYAMBUNG KE SERVER BARU
                        OutputStream outToServer = serverTable.getOutputStream();
                        DataOutputStream outt = new DataOutputStream(outToServer);
                        
                        //NGEBIKIN TABLE
                        outt.writeUTF("create table " + arrTable.get(i));
                        InputStream inFromServer = serverTable.getInputStream();
                        DataInputStream inn = new DataInputStream(inFromServer);
                        System.out.println("Server says " + inn.readUTF());
                        serverTable.close();
                        
                    }*/
                    
                    //KIRIM PERINTAH KE SERVER TERBESAR UNTUK MIGRASI DATA
                    if(ArrIp.size()>1){
                        //String portFrom = tokenString[1];//DAPETIN PORT SERVER TUJUAN
                        migrateDB(TokenMin.get(migrateServerIdx+1),TokenMax.get(migrateServerIdx+1),ArrIp.get(migrateServerIdx),ArrPort.get(migrateServerIdx),ArrIp.get(migrateServerIdx+1),ArrPort.get(migrateServerIdx+1));
                    }
                    displayStatus();
                    break;
                }
                
                else if(tokenString[0].equals("request") && tokenString.length == 2){//PESAN REQUEST IP DARI TOKEN
                    try{
                        message = getIp(Integer.parseInt(tokenString[1]));
                    }catch(Exception e){
                        message = "NAN";
                    }
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    out.writeUTF(message);
                    server.close();
                    break;
                }
                
                else if(tokenString[0].equals("create") && tokenString.length == 2){//PESAN BIKIN TABLE
                    createTableAll(server.getLocalAddress().toString(),server.getPort(), tokenString[1]);
                    //TABLE BERHASIL DIBUAT DI SEMUA SERVER
                    System.out.println("TABLE BERHASIL DIBUAT");
                    displayStatus();
                    break;
                }
                

                else if(tokenString[0].equals("display")){//HANDLE DISPLAY
                    System.out.println("Getting display from other server");
                    
                    String messsage = getDisplay(server.getInetAddress().toString().substring(1), Integer.parseInt(tokenString[2]), tokenString[1]);
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    //out.writeUTF("Hello Thank you for connecting");
                    out.writeUTF(messsage);
                    server.close();
                    break;
                }
                
                else if(tokenString[0].equals("exit")){//EXIT KELUAR LOOP
                    server.close();
                    break;
                }
                
                else{
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    //out.writeUTF("Hello Thank you for connecting");
                    out.writeUTF("COMMAND SALAH");
                }
                
                if(server!=null && !server.isClosed()){
                    //server.close();
                }
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
   public static void main(String [] args)
   {
      //int port = Integer.parseInt(args[0]);
      try
      {
         Thread t = new Tracker(12312);
         t.start();
      }catch(IOException e)
      {
         e.printStackTrace();
      }
   }
}
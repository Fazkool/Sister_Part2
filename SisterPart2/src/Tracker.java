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
   
   private int maxInitToken = 65535;
   
   public Tracker(int port) throws IOException
   {
      serverSocket = new ServerSocket(port);
      ArrPort = new ArrayList<Integer>();
      ArrIp = new ArrayList<String>();
      TokenMin = new ArrayList<>();
      TokenMax = new ArrayList<>();
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
       ArrIp.add(server);
       ArrPort.add(port);
       
       //ADD TOKEN PADA SERVER YANG INGIN DITAMBAH
       //HANDLE JUMLAH SERVER 0
       if(ArrIp.size() == 0){
           TokenMax.add(maxInitToken);
           TokenMin.add(0);
       }
       
       //ADD SELURUH TABLE YANG ADA
       
       
       //HANDLE JUMLAH SERVER NORMAL
       TokenMax.add(TokenMax.get(biggestServer));
       TokenMin.add((int)((long)(TokenMax.get(biggestServer)+1)/2));
       
       //POTONG UKURAN TOKEN YANG DIBAGI
       TokenMax.set(biggestServer,TokenMin.get(TokenMin.size()-1)-1);
       
   }
   
   public int getBiggestServer(){
       int maxServer = 0;
       
       for(int i = 0;i < ArrIp.size();i++){
           if(TokenMax.get(i) - TokenMin.get(i) > maxServer){
               maxServer = TokenMax.get(i) - TokenMin.get(i);
           }
       }
       return maxServer;
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
           
           if(ArrIp.get(i) != fromIP && ArrPort.get(i)!= fromPort){//IGNORE IP PENGIRIM
               System.out.println("Meminta ke " + ArrIp.get(i) +":" + ArrPort.get(i));
               
               //MEMBUKA KONEKSI
               Socket serverUpdate = new Socket(ArrIp.get(i),ArrPort.get(i));
               
               //MEMINTA OUTPUT STREAM + KIRIM PERINTAH
               OutputStream outToServer = serverUpdate.getOutputStream();
               DataOutputStream out = new DataOutputStream(outToServer);
               out.writeUTF("create table " + tableName);
               
               //MENERIMA BALASAN SERVER
               InputStream inFromServer = serverUpdate.getInputStream();
               DataInputStream in = new DataInputStream(inFromServer);
               System.out.println(in.readUTF());
               
               //MATIIN KONEKSI
               serverUpdate.close();
           }
       }
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
                    addServer(server.getLocalAddress().toString(),server.getLocalPort());
                    
                    //KIRIM JUMLAH TOKEN KE YANG MINTA
                    message = TokenMax.get(TokenMax.size()-1) + " " + TokenMin.get(TokenMin.size()-1);
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    out.writeUTF(message);
                    server.close();//DUMMY CLOSE
                    server = null;
                    
                    //BIKIN SELURUH TABLE DI SERVER BARU
                    for(int i = 0;i<arrTable.size();i++){
                        Socket serverTable = new Socket(ArrIp.get(ArrIp.size()-1),ArrPort.get(ArrPort.size()));//NYAMBUNG KE SERVER BARU
                        
                    }
                    
                    //KIRIM PERINTAH KE SERVER TERBESAR UNTUK MIGRASI DATA
                    String portFrom = tokenString[1];//DAPETIN PORT SERVER TUJUAN
                    migrateDB(0, 100, ArrIp.get(migrateServerIdx), ArrPort.get(migrateServerIdx), portFrom, MIN_PRIORITY);
                                 
                }
                
                else if(tokenString[0].equals("request") && tokenString.length == 2){//PESAN REQUEST IP DARI TOKEN
                    try{
                        message = getIp(Integer.parseInt(tokenString[1]));
                    }catch(Exception e){
                        message = "NAN";
                    }
                }
                
                else if(tokenString[0].equals("create") && tokenString.length == 2){//PESAN BIKIN TABLE
                    createTableAll(server.getLocalAddress().toString(),server.getPort(), tokenString[1]);
                    //TABLE BERHASIL DIBUAT DI SEMUA SERVER
                }
                
                else{
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    //out.writeUTF("Hello Thank you for connecting");
                    out.writeUTF("COMMAND SALAH");
                }
                
                if(server!=null){
                    server.close();
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
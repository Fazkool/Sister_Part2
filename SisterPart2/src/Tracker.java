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
   
   public void migrateDB(int tokenMin,int tokenMax,String fromIP, int fromPort,String toIP, int toPort){
       //BUAT KONEKSI KE SERVER YANG AKAN DIKIRIM DATANYA
       
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
                    int migrateServer = getBiggestServer();                            
                    
                    //TAMBAHIN KE TABEL SERVER
                    addServer(server.getLocalAddress().toString(),server.getLocalPort());
                    
                    //KIRIM JUMLAH TOKEN KE YANG MINTA
                    message = TokenMax.get(TokenMax.size()-1) + " " + TokenMin.get(TokenMin.size()-1);
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    out.writeUTF(message);
                   
                    
                    
                }
                
                else if(tokenString[0].equals("request") && tokenString.length == 2){
                    try{
                        message = getIp(Integer.parseInt(tokenString[1]));
                    }catch(Exception e){
                        message = "NAN";
                    }
                }else{
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    //out.writeUTF("Hello Thank you for connecting");
                    out.writeUTF("COMMAND SALAH");
                }
                
                server.close();
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
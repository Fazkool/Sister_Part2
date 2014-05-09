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
               return ArrIp.get(i);
           }
       }
       return st;
   }
   
   public void addServer(String server,int port){
       ArrIp.add(server);
       ArrPort.add(port);
       TokenMax.add(TokenMax.get(TokenMin.size()-1)-1);
       TokenMin.add((TokenMax.get(TokenMax.size()-1)+1)/2);
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
                
                String[] tokenString = inputClient.split(" ");
                
                if(tokenString[0].equals("addServer")){
                    
                }
                
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF("Hello Thank you for connecting");
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
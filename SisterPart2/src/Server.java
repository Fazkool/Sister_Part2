// File Name Server.java



import java.net.*;
import java.io.*;

public class Server extends Thread
{
   private ServerSocket serverSocket;
   
   private Database database = new Database();
   
   public Server(int port) throws IOException
   {
      serverSocket = new ServerSocket(port);
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
                        message = database.createTable(tokenString[2]);
                    }
                }else if(tokenString[0].equals("insert")){
                    if(tokenString.length != 4){
                        System.out.println("Parameter tidak sesuai");
                    }else{
                        System.out.println("Command insert");
                        message = database.insert(tokenString[1], tokenString[2], tokenString[3]);
                    }
                }else if(tokenString[0].equals("display")){
                    if(tokenString.length != 2){
                        System.out.println("Parameter tidak sesuai");
                        message = "parameter tidak sesuai";
                    }else{
                        System.out.println("Command display");
                        message = database.display(tokenString[1]);
                    }
                }else{
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
}
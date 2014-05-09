// File NameClient.java

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client
{
   public static void main(String [] args)
   {
       
      String serverName = args[0];
      int port = Integer.parseInt(args[1]);
      try
      {
         System.out.println("Connecting to " + serverName
                             + " on port " + port);
         Socket client = new Socket(serverName, port);
         System.out.println("Just connected to "
                      + client.getRemoteSocketAddress());
          
         String input = "";
         
         while(!input.equals("exit")){
            System.out.print(">");
             
            Scanner sc;
            sc = new Scanner(System.in);
            input = sc.nextLine();
             
            //kirim data ke server
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF(input);
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            System.out.println("Server says " + in.readUTF());
         }
        //matiin client nya
	
        //baca jawaban dari server
        InputStream inFromServer = client.getInputStream();
        DataInputStream in = new DataInputStream(inFromServer);
        System.out.println("Server says " + in.readUTF()); 
        client.close();
      }catch(IOException e)
      {
         e.printStackTrace();
      }
   }
}
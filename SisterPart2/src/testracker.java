// File NameClient.java

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class testracker
{
   public static void main(String [] args)
   {
      while(true){
        try
        {

            System.out.println("MASUK PROGRAM");
            
           String serverName = null;
           int port;

           Scanner sc;
           sc = new Scanner(System.in);
           System.out.print("Masukkan ip yang ingin dihubungi : ");
           //serverName = sc.nextLine();
           System.out.print("Masukkan socket yang ingin dihubungi : ");
           //port = sc.nextInt();

           serverName = "127.0.0.1";
           port = 12312;

           Socket client = new Socket(serverName, port);
           System.out.println("Just connected to "
                        + client.getRemoteSocketAddress());

           String input = "";
           
           do{
              System.out.print(">");
              
              input = sc.nextLine();
              
              //kirim data ke server
              OutputStream outToServer = client.getOutputStream();
              DataOutputStream out = new DataOutputStream(outToServer);

              out.writeUTF(input);
              InputStream inFromServer = client.getInputStream();
              DataInputStream in = new DataInputStream(inFromServer);
              System.out.println("Server says " + in.readUTF()); 
           }while(!input.equals("exit") || !input.equals("addServer"));
          //matiin client nya
          client.close();
           
          //baca jawaban dari server
        }catch(IOException e)
        {
           e.printStackTrace();
        }
      }
   }
}
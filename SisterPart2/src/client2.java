// File NameClient.java

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class client2
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
            serverName = sc.nextLine();
            System.out.print("Masukkan socket yang ingin dihubungi : ");
            port = Integer.parseInt(sc.nextLine());
            System.out.println(">");
            String input = sc.nextLine();

            
            
           
            
            Socket client = new Socket(serverName, port);
            System.out.println("Just connected to "
                        + client.getRemoteSocketAddress());

            
            System.out.print(">");

            
            
            //kirim data ke server
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF(input);
            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            System.out.println("Server says " + in.readUTF()); 
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
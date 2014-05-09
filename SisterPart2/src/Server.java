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
   
   public Server(int port) throws IOException
   {
      serverSocket = new ServerSocket(port);
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
   
   public boolean onCreate(){
    boolean trackerFound =false;
         try
      {
         System.out.println("Connecting to " + trackerIP
                             + " on port " + trackerPort);
         Socket client = new Socket(trackerIP, trackerPort);
         System.out.println("Just connected to "
                      + client.getRemoteSocketAddress());
          
         String input = "";
         
       //  while(!input.equals("exit")){
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
        // }
        //matiin client nya
	
        //baca jawaban dari server
        inFromServer = client.getInputStream();
        in = new DataInputStream(inFromServer);
        System.out.println("Server says " + in.readUTF()); 
        client.close();
      }catch(IOException e)
      {
         e.printStackTrace();
      }
    
    
    return trackerFound;
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
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;


    public ClientHandler (Socket socket){
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("Server: " + clientUsername + "has entered the chat!");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader,bufferedWriter);
        }
    }
    
  

    @Override
    public void run() {
        String messasgeFromClient;

        while(socket.isConnected()){
            try{
                messasgeFromClient = bufferedReader.readLine();
                broadcastMessage(messasgeFromClient);
            } catch(IOException e){
                closeEverything(socket, bufferedReader ,bufferedWriter);
                break;
            }
        }
        
    }


      private void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers){
            try {
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (Exception e) {
                 closeEverything(socket, bufferedReader,bufferedWriter);
            }
        }

    }


    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage( "SERVER: " + clientUsername + "has left the chat!");
    }

    public void closeEverything(Socket socket , BufferedReader bufferedReader ,BufferedWriter bufferedWriter){
        removeClientHandler();

        try {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket!=null){
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

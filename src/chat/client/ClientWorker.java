package chat.client;

import chat.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 * Worker that listens for the server's messages.
 */
public class ClientWorker extends Thread {

    /** The reader which reads the server's messages. */
    private final BufferedReader clientReader;

    /** The client this thread is "working" for. */
    private final ChatClient client;

    ClientWorker(ChatClient client, Socket clientSocket) throws IOException {
        this.client = client;
        this.clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        setName("ClientWorker " + client.getUsername());
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (true) {
                int flag = clientReader.read();

                if(flag == -1) {
                    clientReader.close();
                    client.disconnect();
                    break;
                }

                if(flag == Server.INCOMING_PRIVATE_CLIENT_MESSAGE) {
                    String sender = clientReader.readLine();
                    String receiver = clientReader.readLine();
                    String message = clientReader.readLine();
                    client.receivePrivateClientMessage(sender, receiver, message);

                } else if(flag == Server.INCOMING_PRIVATE_SERVER_MESSAGE) {
                    String message = clientReader.readLine();
                    client.receivePrivateServerMessage(message);

                } else if(flag == Server.INCOMING_GLOBAL_CLIENT_MESSAGE) {
                    String sender = clientReader.readLine();
                    String message = clientReader.readLine();
                    client.receiveGlobalClientMessage(sender, message);

                } else if(flag == Server.INCOMING_GLOBAL_SERVER_MESSAGE) {
                    String message = clientReader.readLine();
                    client.receiveGlobalServerMessage(message);

                } else if(flag == Server.INCOMING_DISCONNECT_MESSAGE) {
                    String message = clientReader.readLine();
                    client.receiveGlobalServerMessage(message);

                } else if(flag == Server.INCOMING_KICK_MESSAGE) {
                    String message = clientReader.readLine();
                    client.receiveGlobalServerMessage(message);

                } else if(flag == Server.INCOMING_BAN_MESSAGE) {
                    String message = clientReader.readLine();
                    client.receiveGlobalServerMessage(message);

                } else if(flag == Server.INCOMING_DELETE_MESSAGE) {
                    String message = clientReader.readLine();
                    client.receiveGlobalServerMessage(message);

                } else if(flag == Server.KICKED) {
                    String kicker = clientReader.readLine();
                    clientReader.close();
                    client.receiveKick(kicker);
                    break;

                } else if(flag == Server.BANNED) {
                    String banner = clientReader.readLine();
                    clientReader.close();
                    client.receiveBan(banner);
                    break;

                } else if(flag == Server.DELETED) {
                    String deleter = clientReader.readLine();
                    clientReader.close();
                    client.receiveDelete(deleter);
                    break;

                } else {
                    System.err.println("Unknown flag with value " + flag + "(" + (char)flag + ") was " + flag);
                }
            }
        } catch (SocketException ex) {
            System.out.println("Socket was closed.");

        } catch (IOException ex) {
            System.out.println("Error while reading line.");
        }
    }
}
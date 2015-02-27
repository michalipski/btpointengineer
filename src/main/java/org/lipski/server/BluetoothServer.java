package org.lipski.server;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;
import java.sql.SQLException;

public class BluetoothServer {

    public void startServer() throws IOException, SQLException, ClassNotFoundException {
        UUID uuid = new UUID("1101", true);

        String connectionString = "btspp://localhost:" + uuid +";name=appServer";
        System.out.println("Server id - " + ServerProperties.getServerId());

        StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);
        while(true)
        {
            System.out.println("\nServer Started. Waiting for clients to connect...");

            StreamConnection connection=streamConnNotifier.acceptAndOpen();

            RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
            System.out.println("Remote device address: "+dev.getBluetoothAddress());
            System.out.println("Remote device name: "+dev.getFriendlyName(true));

            InputStream inStream=connection.openInputStream();
            BufferedReader bReader=new BufferedReader(new InputStreamReader(inStream));
            String lineRead=bReader.readLine();
            System.out.println(lineRead);
            CommandResolver commandResolver = new CommandResolver();
            Object resultObject = commandResolver.forwardCommand(lineRead);

            ObjectOutputStream outStream = new ObjectOutputStream(connection.openOutputStream());
            outStream.writeObject(resultObject);
            outStream.flush();
            outStream.close();
        }
    }
}

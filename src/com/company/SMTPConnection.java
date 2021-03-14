package com.company;

import java.net.*;
import java.io.*;
import java.security.cert.CRL;
import java.util.*;

/**
 * Open an SMTP connection to a mailserver and send one mail.
 *
 */
public class SMTPConnection {
    /* The socket to the server */
    private Socket connection;

    /* Streams for reading and writing the socket */
    private BufferedReader fromServer;
    private DataOutputStream toServer;

    private static final int SMTP_PORT = 25;
    private static final String CRLF = "\r\n";

    /* Are we connected? Used in close() to determine what to do. */
    private boolean isConnected = false;

    /* Create an SMTPConnection object. Create the socket and the 
       associated streams. Initialize SMTP connection. */
    public SMTPConnection(Envelope envelope) throws IOException {
        connection = new Socket(,SMTP_PORT); //create standard socket connection on port 25
        fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        toServer = new DataOutputStream(connection.getOutputStream());

	/* Read a line from server and check that the reply code is 220.
	   If not, throw an IOException. */
        String text = fromServer.readLine();
        System.out.println(parseReply(text));
        if (parseReply(text) != 220)
            throw new IOException("Reply code not 220");
        System.out.println("Reply code not 220");

	/* SMTP handshake. We need the name of the local machine.
	   Send the appropriate SMTP handshake command. */
        String localhost =/* Fill in */;
        sendCommand("HELLO " + localhost + CRLF, 250);
        isConnected = true;
    }

    /* Send the message. Write the correct SMTP-commands in the
       correct order. No checking for errors, just throw them to the
       caller. */
    public void send(Envelope envelope) throws IOException {
	/* Send all the necessary commands to send a message. Call
	   sendCommand() to do the dirty work. Do _not_ catch the
	   exception thrown from sendCommand(). */
        sendCommand("MAIL FROM: " + envelope.Sender + CRLF,250);
        sendCommand("ReCIPIENT TO: " + envelope.Recipient + CRLF ,250);
        sendCommand("DATA"+ CRLF ,354);
    }

    /* Close the connection. First, terminate on SMTP level, then
       close the socket. */
    public void close() {
        isConnected = false;
        try {
            sendCommand("QUIT" + CRLF, 221);
            // connection.close();
        } catch (IOException e) {
            System.out.println("Unable to close connection: " + e);
            isConnected = true;
        }
    }

    /* Send an SMTP command to the server. Check that the reply code is
       what is is supposed to be according to RFC 821. */
    private void sendCommand(String command, int rc) throws IOException {
        /* Write command to server and read reply from server. */
       System.out.println("Command to server" + command + CRLF);
       toServer.writeBytes(command + CRLF);
       System.out.println("Server reply" + fromServer.readLine());

	/* Check that the server's reply code is the same as the parameter
	   rc. If not, throw an IOException. */
       if (parseReply(fromServer.readLine()) !=rc){
           System.out.println("The reply code is no the same as rc");
           throw new IOException("The reply code is not the same as rc");
       }
    }

    /* Parse the reply line from the server. Returns the reply code. */
    private int parseReply(String reply) {
        StringTokenizer tokens = new StringTokenizer(reply,"");
        String rc = tokens.nextToken();
        return Integer.parseInt(rc);
    }

    /* Destructor. Closes the connection if something bad happens. */
    protected void finalize() throws Throwable {
        if(isConnected) {
            close();
        }
        super.finalize();
    }
}
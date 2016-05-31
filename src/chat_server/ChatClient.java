package chat_server;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatClient {

/* Based on Head first Java code - a local same IP chat client using concurrency and classes  
 * Simple really - make a socket (connection) on localhost - mimicks a server 
 * And then write to and read from that socket for a chat app - only need the variables below
 *  */	
	
JTextArea incoming; // displays some text - same as AS level stuff 
JTextField outgoing; // text field to enter text 
BufferedReader reader; 
PrintWriter writer;
Socket sock;
	
public static void main(String[] args) {
	ChatClient client = new ChatClient();
	client.go();
}

/* Mostly GUI in this method */
public void go () {
	JFrame frame = new JFrame("Chat client");
	JPanel mainPanel = new JPanel();
	incoming = new JTextArea(15,50);
	incoming.setLineWrap(true); incoming.setWrapStyleWord(true); incoming.setEditable(false); 
	JScrollPane qScroller = new JScrollPane(incoming); // makes text area scrollable 
	qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	outgoing = new JTextField(20); 
	JButton sendButton = new JButton("Send");
	sendButton.addActionListener(new SendButtonListener()); // sends text to socket using class below - does when action performed (i.e. button clicked)
	mainPanel.add(qScroller); // display messages on J frame
	mainPanel.add(outgoing); // text box to enter messages
	mainPanel.add(sendButton);
	setUpNetworking(); // creates socket as method below
	
	Thread readerThread = new Thread(new IncomingReader());
	readerThread.start(); // loops continuously in run method, read any new messages from socket stream and adds to scrolling text area
	
	frame.getContentPane().add(BorderLayout.CENTER, mainPanel); // centres the jpanel on the frame 
	frame.setSize(400, 500);
	frame.setVisible(true);
}

/* Creates socket, creates reader and writer streams from socket*/
private void setUpNetworking() {
	try {
		sock = new Socket("127.0.0.1", 1337); // localhost IP, random port 
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		System.out.println("Networking Establlished"); 
	} catch(IOException ex) { ex.printStackTrace(); }
}

/* When user clicks send button, method sends text field contents to server */
public class SendButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent ev) {
		try {
			writer.println(outgoing.getText());
			writer.flush();
		} catch (Exception ex) {ex.printStackTrace(); }
		outgoing.setText("");
		outgoing.requestFocus();
	}
} // close inner class

/* Inside thread - inside run() method - loops continually add adds new lines of text */
public class IncomingReader implements Runnable {

	public void run() {
		String message;
		try {
			while ((message = reader.readLine()) != null) {
				System.out.println("read" + message);
				incoming.append(message + "\n");
			}
		} catch(Exception ex) {ex.printStackTrace();}
	}	
} // close inner class

} // close outer class




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.URL;
import java.util.Enumeration;
import java.util.Scanner;

public class B2BCTFilter {

	public static void main(String[] args) throws IOException {
		Socket clientSocket = null;
		// String to hold bib numbers with similar clock times
		String closeTimes = "";
		// Search doesn't use a range, but rather the "prefix" of the runner's clock time
		// for example: 1:19:4 will find 1:19:40, 1:19:41, 1:19:42, ... , 1:19:49
		String timeOne = "clock time:</dt>\n   <dd>1:19:4";
		String timeTwo = "clock time:</dt>\n   <dd>1:19:5";
		String timeThree = "clock time:</dt>\n   <dd>1:20:0";
		for(int bibNumber = 0; bibNumber < 50001; bibNumber++){
			// Print out every 200th try as a visual confirmation that the 
			// program is still running
			if( bibNumber % 10 == 0 ){
			System.out.println("trying: " + bibNumber);
			}
			// Open a socket to the website containing runner's results
			try {
				clientSocket = new Socket("btb17.onlineraceresults.com", 80);
			} catch (IOException e) {
				System.out.println("socket didn't open");
				e.printStackTrace();
			}

			Scanner socketIn = new Scanner(clientSocket.getInputStream());
			PrintWriter socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
			
			// message to send website requesting the html document for
			// the runner's results
			String message = "GET ";
			message = message + "/individual.php?bib=" + bibNumber + "\n";
			message = message + " HTTP/1.1\n";
			message = message + "Host: onlineraceresults.com\n";
			message = message + "Connection: close\n";
			message = message + "User-agent: Mozilla/5.0\n";
			message = message + "Accept-language: en\n";

			// send request and get the html document
			socketOut.println(message);
			String obtained = "";
			while(socketIn.hasNextLine()){
				obtained = obtained + socketIn.nextLine() + "\n";
			}
			
			// search the received html document for the runner times specific by the
			// clockTime "prefixes" 
			// indexOf returns -1 if the substring searched for isn't in the string
			if(obtained.indexOf(timeOne) != -1 || obtained.indexOf(timeTwo) != -1 || obtained.indexOf(timeThree) != -1){
				System.out.println("*******************bibnumber " + bibNumber);
				closeTimes = closeTimes + bibNumber + ", ";
				System.out.println(closeTimes);
			}
			socketIn.close();
		}

		try {
			clientSocket.close();
		} catch (IOException error) {
			error.printStackTrace();
		}
		System.out.println("DONE, found: " + closeTimes);
	}
}

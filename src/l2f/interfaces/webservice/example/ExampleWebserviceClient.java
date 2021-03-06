package l2f.interfaces.webservice.example;

import l2f.interfaces.webservice.WebserviceGenericClient;
import java.io.IOException;

public class ExampleWebserviceClient extends WebserviceGenericClient<ExampleWebservice> {

	public ExampleWebserviceClient(String host, int port) {
		super(host, port, ExampleWebservice.class);
	}

	/**
	 * @param args
	 *          [bindAddr, bindPort]
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String host = "";
		int port = 0;

		if (args.length != 2) {
			System.err.println("usage: <hostname/ip> <port>");
			System.err.println("\twhere: <port> = number from 1-65535");
			System.exit(-1);
		} else {
			host = args[0];
			String portStr = args[1];

			try {
				port = Integer.valueOf(portStr);

				if (port > 65535 || port < 1) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException nfe) {
				System.err.println("usage: <hostname/ip> <port>");
				System.err.println("\twhere: <port> = number from 1-65535");
				System.err.println();
				System.err.println("<port> value invalid, got: " + portStr);
				System.exit(-1);
			}
		}

		System.out.println("Accessing webservice on " + host + ":" + port);
		ExampleWebserviceClient ws = new ExampleWebserviceClient(host, port);

		System.out.println("< isItTrue()");
		boolean isItTrue = ws.accessWebservice().isItTrue();
		System.out.println("> isItTrue() - " + isItTrue);

		System.out.println("< answerQuestion(\"sslc\", \"Who are you?\")");
		String answer = ws.accessWebservice().answerQuestion("sslc", "Who are you?");
		System.out.println("< answerQuestion(\"sslc\", \"Who are you?\") - " + answer);
	}

}

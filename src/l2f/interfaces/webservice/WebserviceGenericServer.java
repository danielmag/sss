package l2f.interfaces.webservice;

import javax.xml.ws.Endpoint;

public class WebserviceGenericServer<T> implements Runnable {
	private final String host;
	private final int port;
	private final String serviceName;
	private final T implementor;

	private Endpoint endpoint;

	public WebserviceGenericServer(String host, int port, Class<T> interfaceClass, T implementor) {
		this.host = host;
		this.port = port;

		String interfaceSimpleName = interfaceClass.getSimpleName();
		this.serviceName = interfaceSimpleName.substring(0, 1).toLowerCase() + interfaceSimpleName.substring(1);

		this.implementor = implementor;
	}

	public void stop() {
		endpoint.stop();
	}

	public T getImplementor() {
		return implementor;
	}

	@Override
	public void run() {
		String address = "http://" + this.host + ":" + this.port + "/" + this.serviceName;
		try {
			this.endpoint = Endpoint.publish(address, implementor);
			System.err.println(getClass().getName()+"Launched " + this.serviceName + " with success on the address: " + address);
		} catch (Exception e) {
			System.err.println(getClass().getName()+ "Failed to launch the webservice!");
			e.printStackTrace(System.err);
		}
	}
}

package l2f.interfaces.webservice;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class WebserviceGenericClient<T> {
	private final Class<T> interfaceClass;
	private final QName serviceQName;
	private final String className;
	private final String serviceName;
	private final String host;
	private final int port;

	public WebserviceGenericClient(String host, int port, Class<T> interfaceClass) {
		this.interfaceClass = interfaceClass;
		this.className = interfaceClass.getSimpleName();

		String wsPath = "";
		String[] packageName = interfaceClass.getPackage().getName().split("\\.");

		for (int i = (packageName.length - 1); i >= 0; i--) {
			wsPath += packageName[i];
			if (i == 0) {
				// last one
			} else {
				// a few more elements remaining
				wsPath += ".";
			}
		}

		this.serviceQName = new QName("http://" + wsPath + "/", className);
		this.serviceName = className.substring(0, 1).toLowerCase() + className.substring(1);
		this.host = host;
		this.port = port;
	}

	public T accessWebservice() throws MalformedURLException {
		String endpointAddress = "http://" + this.host + ":" + this.port + "/" + this.serviceName;

		Service service = null;

		service = Service.create(new URL(endpointAddress + "?wsdl"), this.serviceQName);

		return (T) service.getPort(interfaceClass);
	}
}

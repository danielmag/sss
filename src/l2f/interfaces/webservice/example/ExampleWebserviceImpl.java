package l2f.interfaces.webservice.example;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Map;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Class that implement the interface of the ExampleWebservice used by the
 * server
 * 
 * @author Sergio Curto
 * 
 */
@SuppressWarnings("unused")
@WebService(endpointInterface = "l2f.interfaces.webservice.example.ExampleWebservice", serviceName = "ExampleWebservice")
public class ExampleWebserviceImpl implements ExampleWebservice {

	@Override
	public boolean isItTrue() {
		boolean value = true;
		return value == true;
	}

	@Override
	public String answerQuestion(@WebParam(name = "username") String username, @WebParam(name = "question") String question) {
		String result = "I can't answer the question: " + question + "\tSorry Dave... " + username;

		return result;
	}

}

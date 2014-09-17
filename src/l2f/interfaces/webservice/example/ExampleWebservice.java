package l2f.interfaces.webservice.example;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface ExampleWebservice {

	/**
	 * Generic boolean returning webservice method without parameters
	 * 
	 * @return boolean
	 */
	public boolean isItTrue();

	/**
	 * Generic String returning webservice method with two string parameters
	 * 
	 * @param username
	 *          identifier of the user asking the question
	 * @param question
	 *          question placed by the user
	 * @return xml or json reply with the answer information
	 */
	public String answerQuestion(@WebParam(name = "username") String username, @WebParam(name = "question") String question);

}

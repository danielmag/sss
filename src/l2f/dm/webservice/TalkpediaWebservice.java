package l2f.dm.webservice;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface TalkpediaWebservice {
	
	/**
	 * Asks Talkpedia to reply to the given utterance 
	 * @param utterance
	 * @return string containing the reply
	 */
	public String interact(@WebParam(name = "utterance") String utterance);
}

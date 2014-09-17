package l2f.dm.webservice;

import org.apache.lucene.queryparser.classic.ParseException;
import org.xml.sax.SAXException;
import ptstemmer.exceptions.PTStemmerException;
import sss.lucene.LuceneManager;

import javax.jws.WebService;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


/**
 * Class that implement the interface of the TalkpediaWebservice used by the
 * server
 * 
 * @author Sergio Curto
 * 
 */
@SuppressWarnings("unused")
@WebService(endpointInterface = "l2f.dm.webservice.TalkpediaWebservice", serviceName = "TalkpediaWebservice")
public class TalkpediaWebserviceImpl implements TalkpediaWebservice {
	
	// Never greater then 10000 ( 10 secs)
	private static final long MAXIMUM_TIME_MILLIS = 10000;
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();	
	
	private LuceneManager luceneManager = null;
	
	public TalkpediaWebserviceImpl(){

        try {
            this.luceneManager = new LuceneManager();
        } catch (Throwable e) {
            System.err.println("An error occurred while loading the lucene manager" + e.getLocalizedMessage());
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }

	@Override
	public String interact(String utterance) {
		String reply = luceneManager.getNoReplyMessage();
		final String utt = utterance;

		List<Callable<String>> taskList = new ArrayList<Callable<String>>(1);
		taskList.add(new Callable<String>() {
			public String call() {
                try {
                    return luceneManager.getAnswer(utt);
                } catch (Throwable e) {
                    System.err.println("An error occurred while getting an answer from lucene manager" + e.getLocalizedMessage());
                    e.printStackTrace(System.err);
                }

                return luceneManager.getNoReplyMessage();
            }
		});
		
		try {
			reply = executor.invokeAny(taskList, MAXIMUM_TIME_MILLIS, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) { 
			// time has run out
			System.out.println("Time has run out for the utterance "+utterance);
			e.printStackTrace();
		} catch (InterruptedException e) {
			// webservice got a CTRL+C 
			System.out.println("Interrupted while processing the utterance "+utterance);
			e.printStackTrace();
		} catch (ExecutionException e) {
			// exception during the call() method
			System.out.println("Exception thrown by call() the utterance "+utterance);
			e.printStackTrace();
		}
		
		return reply;
	}

	

}

package sss.resources;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigParser {

    private String language;
    private String corpusPath;
    //private List<Map<String, String>> strategiesDefinitions;
    private String luceneIndexPath;
    private int hitsPerQuery;
    private String logPath;
    private String noAnswerFoundMsg;
    private boolean usePreviouslyCreatedIndex;

    public ConfigParser(String configfile) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(configfile);
            doc.getDocumentElement().normalize();

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            XPathExpression expr;
            Node node;
            NodeList nodeList;
            NamedNodeMap attrs;
            ArrayList<Map<String, String>> listMap;
            Map<String, String> doubleStringMap;


            //strategies
            /*
            expr = xpath.compile("//dialog/evaluator/strategies/strategy");
            nodeList = (NodeList) expr.score(doc, XPathConstants.NODESET);
            listMap = new ArrayList<Map<String, String>>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                node = nodeList.item(i);
                attrs = node.getAttributes();
                doubleStringMap = new HashMap<String, String>();
                for (int j = 0; j < attrs.getLength(); j++) {
                    node = attrs.item(j);
                    doubleStringMap.put(node.getNodeName(), node.getNodeValue());
                }
                listMap.add(doubleStringMap);
                System.out.println("Config: Evaluator strategy configuration detected: " + doubleStringMap);
            }

            strategiesDefinitions = listMap;
            */

            expr = xpath.compile("//config/corpusPath");
            node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            corpusPath = node.getTextContent();

            expr = xpath.compile("//config/logPath");
            node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            logPath = node.getTextContent();

            expr = xpath.compile("//config/language");
            node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            language = node.getTextContent();

            expr = xpath.compile("//config/lucene/indexPath");
            node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            luceneIndexPath = node.getTextContent() + "/" + language;

            expr = xpath.compile("//config/lucene/usePreviouslyCreatedIndex");
            node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            usePreviouslyCreatedIndex = Boolean.parseBoolean(node.getTextContent());

            expr = xpath.compile("//config/lucene/hitsPerQuery");
            node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            hitsPerQuery = Integer.parseInt(node.getTextContent());

            expr = xpath.compile("//config/noAnswerFoundMsg");
            node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            noAnswerFoundMsg = node.getTextContent();


        } catch (Exception e) {
            System.err.println("Config: Can't load the given configuration! File: " + configfile + " Exception Message: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    public String getLanguage() {
        return language;
    }

    public String getCorpusPath() {
        return corpusPath;
    }

    public String getLuceneIndexPath() {
        return luceneIndexPath;
    }

    public int getHitsPerQuery() {
        return hitsPerQuery;
    }

    public String getLogPath() {
        return logPath;
    }

    public String getNoAnswerFoundMsg() {
        return noAnswerFoundMsg;
    }

    public boolean isUsePreviouslyCreatedIndex() {
        return usePreviouslyCreatedIndex;
    }
}

package sss.resources;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sss.exceptions.dialog.evaluator.WeightException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigParser {

    private List<String> qaScorers;
    private List<String> normalizations;
    private String distanceAlgorithm;
    private String language;
    private String stopWordsLocation;
    private String corpusPath;
    private String luceneIndexPath;
    private int hitsPerQuery;
    private String logPath;
    private String noAnswerFoundMsg;
    private boolean usePreviouslyCreatedIndex;

    public ConfigParser(String configfile) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(configfile);
        doc.getDocumentElement().normalize();

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        XPathExpression expr;
        Node node;

        int total = 0;
        NodeList nodeList = doc.getElementsByTagName("qaScorers");
        nodeList = nodeList.item(0).getChildNodes();
        this.qaScorers = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) item;
                String name = eElement.getAttribute("name");
                String weight = eElement.getAttribute("weight");
                if (name.equals("SimpleConversationContext")) {
                    String nPreviousQAs = eElement.getAttribute("nPreviousQAs");
                    this.qaScorers.add(name + "," + weight + "," + nPreviousQAs);
                } else {
                    this.qaScorers.add(name + "," + weight);
                }
                int weightInt = Integer.parseInt(weight);
                if (weightInt < 0 || weightInt > 100) {
                    throw new WeightException();
                }
                total += weightInt;
            }
        }
        if (total != 100) {
            throw new WeightException();
        }

        expr = xpath.compile("//config/normalizations");
        node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        normalizations = tokenize(((Element)node).getAttribute("names"));

        expr = xpath.compile("//config/distanceAlgorithm");
        node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        String distanceName = ((Element)node).getAttribute("name");
        if (distanceName.equals("JaccardOverlap")) {
            distanceAlgorithm = distanceName + "," + ((Element)node).getAttribute("jaccardWeight");
        } else {
            distanceAlgorithm = distanceName;
        }

        expr = xpath.compile("//config/corpusPath");
        node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        corpusPath = node.getTextContent();

        expr = xpath.compile("//config/logPath");
        node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        logPath = node.getTextContent();

        expr = xpath.compile("//config/language");
        node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        language = node.getTextContent();

        expr = xpath.compile("//config/stopWordsLocation");
        node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        stopWordsLocation = node.getTextContent();

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

    }

    private List<String> tokenize(String s) {
        return Arrays.asList(s.split("(,)(\\s)*"));
    }

    public List<String> getQaScorers() {
        return qaScorers;
    }

    public List<String> getNormalizations() {
        return normalizations;
    }

    public String getLanguage() {
        return language;
    }

    public String getStopWordsLocation() {
        return stopWordsLocation;
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

    public boolean usePreviouslyCreatedIndex() {
        return usePreviouslyCreatedIndex;
    }

    public String getDistanceAlgorithm() {
        return distanceAlgorithm;
    }
}

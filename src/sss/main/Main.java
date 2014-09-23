package sss.main;

import org.apache.lucene.queryparser.classic.ParseException;
import org.xml.sax.SAXException;
import ptstemmer.exceptions.PTStemmerException;
import sss.lucene.LuceneManager;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static final boolean DEBUG = false;
    private static final boolean TEST = false;
    public static final boolean SORT = false;
    public static final boolean LEARN_TO_RANK = true;
    public static int qid = 1;
    public static final int N_ANSWERS = 20;
    public static Map<String, Integer> answerHeadMap = new HashMap<>();

    public static void main(String[] args) throws IOException, ParserConfigurationException, XPathExpressionException, SAXException, ParseException, ClassNotFoundException, PTStemmerException {

        String[] loebner = {"Hello I'm Ronan. what is your name?", "What is your mother's name?", "What is your birth sign?", "How many children do you have?", "Do you prefer red or white wine?", "I like bananas. Which is your favorite fruit?", "What music do you like to listen to?", "what is your favorite song?", "I like Waiting for Godot. What is your favorite play?", "What color do you dye your hair?", "Do you remember my name?", "Where do you live.", "Where do you like to go on holidays?", "I have a Mazda. What type of car do you have?", "I like Linux. Which computer operating system do you like?", "I am an atheist. Which religion are you?", "Who invented the telephone?", "I am a Type B personality. Which type are you?", "What emotions are you now feeling?", "What time do you usually go to bed?"};
        String[] sss = {"I like your hair?", "what time is it", "I like your hair", "What is your name?", "what's your name", "how are you?", "fine thanks", "your mom's", "you are stupid", "fuck you!", "where are you from?", "how old are you?", "thats right my son", "homosexual", "how is the weather", "do my homework", "you have beautiful blue eyes", "do you cook", "do you have brothers", "are you married", "do you have a brother", "who is your father", "are you an older", "where are you", "where are going", "why are you going there", "what do you do", "what do you write", "who is the president of usa", "who is the president of america", "who is the leader of america", "what is your name", "where are you living", "what about your wife", "why are your wife divorcing you", "why do your wife want to divorce you", "who is the president of facebook", "who is the leader of facebook", "who is the ceo of facebook", "who is ceo of microsoft", "who is the president of microsoft", "why are going to die", "who is threating you", "where is your mother", "how old is your son", "do you have children", "do you have a son", "where are from", "where are you from", "what country do you live", "do you have sex", "when do you have sex", "who do you have sex with", "what is the color of hair", "do you have any bothers", "do you have any brothers", "is you a loser", "are you a loser", "do you think you are a loser", "do you think you are a definite loser", "what is two plus two", "what is the result of one plus one", "are you an idiot", "what is the capital of spain", "what is the capital of japan", "where is the capital of japan", "is tokyo the capital of japan", "Fine. How are you?", "How is the weather today?", "I wasn't looking for a ride.  Do you have a car?", "I wonder if you could have a smirk on your face when you give an off answer.", "Are you a mutant?", "where are you?", "what is the building behind you?", "what is reed?", "is Reed a building name?", "what do you know?", "Where is Paris?", "what do you do?", "what do you write?", "what is it about?", "who is the professor?", "are you smart?", "are you a robot?", "can you help me?", "do you like cooking?", "good evening", "What do you like?", "are you joking?", "Did you kill Eric?", "hello", "hi, I am Luke.", "You can't do much.", "hello, how are you?", "i'm fine thanks. what's your name?", "where have you studied?", "what is your job?", "are you married?", "would you like to get married?", "would you like to have children?", "what's your mother's name?", "what's your father's name?", "do you have any brothers or sisters"};
        String[] loebnerShuffled = {"What time do you usually go to bed?", "Where do you live.", "I like Waiting for Godot. What is your favorite play?", "Do you remember my name?", "I have a Mazda. What type of car do you have?", "Where do you like to go on holidays?", "How many children do you have?", "I am an atheist. Which religion are you?", "I am a Type B personality. Which type are you?", "Do you prefer red or white wine?", "what is your favorite song?", "Who invented the telephone?", "What music do you like to listen to?", "Hello I'm Ronan. what is your name?", "What emotions are you now feeling?", "What is your mother's name?", "I like bananas. Which is your favorite fruit?", "I like Linux. Which computer operating system do you like?", "What color do you dye your hair?", "What is your birth sign?"};
        String[] sssShuffled = {"where is the capital of japan",
                "where is your mother",
                "where are you living",
                "do you have brothers",
                "what country do you live",
                "what's your mother's name?",
                "are you married?",
                "What do you like?",
                "are you joking?",
                "are you a loser",
                "where are you from",
                "what about your wife",
                "what is your job?",
                "what is the building behind you?",
                "what's your father's name?",
                "I wasn't looking for a ride.  Do you have a car?",
                "what is the result of one plus one",
                "how are you?",
                "what is two plus two",
                "do you have sex",
                "You can't do much.",
                "how old are you?",
                "where are you going",
                "do you have any brothers or sisters",
                "what time is it",
                "are you an idiot",
                "are you a robot?",
                "Fine. How are you?",
                "why are you going there",
                "hello",
                "who is threating you",
                "would you like to have children?",
                "what is the color of hair",
                "hi, I am Luke.",
                "is tokyo the capital of japan",
                "where are you?",
                "fuck you!",
                "do you like cooking?",
                "who is the president of usa",
                "what is reed?",
                "what do you know?",
                "Are you a mutant?",
                "what is it about?",
                "who is the leader of america",
                "I like your hair",
                "do you cook",
                "who do you have sex with",
                "why are you going to die",
                "where have you studied?",
                "who is the ceo of facebook",
                "are you smart?",
                "what do you do",
                "who is the professor?",
                "you are stupid",
                "do you think you are a definite loser",
                "do you have a son",
                "thats right my son",
                "how old is your son",
                "do you have any brothers",
                "do my homework",
                "i'm fine thanks. what's your name?",
                "what do you write?",
                "do you have any bothers",
                "would you like to get married?",
                "Did you kill Eric?",
                "good evening",
                "do you have children",
                "you have beautiful blue eyes",
                "What is your name?",
                "is Reed a building name?",
                "hello, how are you?",
                "I wonder if you could have a smirk on your face when you give an off answer.",
                "why are your wife divorcing you",
                "who is ceo of microsoft",
                "fine thanks",
                "when do you have sex",
                "Where is Paris?",
                "can you help me?",
                "how is the weather",
                "what is the capital of spain"};

        LuceneManager luceneManager = new LuceneManager();

        if (Main.LEARN_TO_RANK) {
            System.out.print("{");
            for (int i = 0; i < sssShuffled.length; i++) {
                String query = sssShuffled[i];
//                System.out.println("T - " + query);
                luceneManager.getAnswer(query);
                qid++;
            }
            System.out.print("}");
        } else {
            if (Main.TEST) {
                for (int i = 28; i < sssShuffled.length; i++) {
                    String query = sssShuffled[i];
                    System.out.println("T - " + query);
                    luceneManager.getAnswer(query);
                }
            } else {
                while (true) {
                    System.out.println("Say something: ");
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    String query = br.readLine();
                    if (query.isEmpty()) {
                        continue;
                    }
                    String answer = luceneManager.getAnswer(query);
                    printDebug("Question: " + query);
                    System.out.println("Answer: " + answer);
                    System.out.println("");
                }
            }
        }
    }

    public static void printDebug(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }
}


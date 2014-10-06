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

public class Main {

    private static final boolean DEBUG = false;
    private static final boolean TEST = true;
    public static final boolean SORT = false;
    public static final boolean LEARN_TO_RANK = false;
    public static int qid = 1;
    public static final int N_ANSWERS = 20;

    public static void main(String[] args) throws IOException, ParserConfigurationException, XPathExpressionException, SAXException, ParseException, ClassNotFoundException, PTStemmerException {

        String[] loebner = {"Hello I'm Ronan. what is your name?", "What is your mother's name?", "What is your birth sign?", "How many children do you have?", "Do you prefer red or white wine?", "I like bananas. Which is your favorite fruit?", "What music do you like to listen to?", "what is your favorite song?", "I like Waiting for Godot. What is your favorite play?", "What color do you dye your hair?", "Do you remember my name?", "Where do you live.", "Where do you like to go on holidays?", "I have a Mazda. What type of car do you have?", "I like Linux. Which computer operating system do you like?", "I am an atheist. Which religion are you?", "Who invented the telephone?", "I am a Type B personality. Which type are you?", "What emotions are you now feeling?", "What time do you usually go to bed?"};
        String[] sss = {"I like your hair?", "what time is it", "I like your hair", "What is your name?", "what's your name", "how are you?", "fine thanks", "your mom's", "you are stupid", "fuck you!", "where are you from?", "how old are you?", "thats right my son", "homosexual", "how is the weather", "do my homework", "you have beautiful blue eyes", "do you cook", "do you have brothers", "are you married", "do you have a brother", "who is your father", "are you an older", "where are you", "where are going", "why are you going there", "what do you do", "what do you write", "who is the president of usa", "who is the president of america", "who is the leader of america", "what is your name", "where are you living", "what about your wife", "why are your wife divorcing you", "why do your wife want to divorce you", "who is the president of facebook", "who is the leader of facebook", "who is the ceo of facebook", "who is ceo of microsoft", "who is the president of microsoft", "why are going to die", "who is threating you", "where is your mother", "how old is your son", "do you have children", "do you have a son", "where are from", "where are you from", "what country do you live", "do you have sex", "when do you have sex", "who do you have sex with", "what is the color of hair", "do you have any bothers", "do you have any brothers", "is you a loser", "are you a loser", "do you think you are a loser", "do you think you are a definite loser", "what is two plus two", "what is the result of one plus one", "are you an idiot", "what is the capital of spain", "what is the capital of japan", "where is the capital of japan", "is tokyo the capital of japan", "Fine. How are you?", "How is the weather today?", "I wasn't looking for a ride.  Do you have a car?", "I wonder if you could have a smirk on your face when you give an off answer.", "Are you a mutant?", "where are you?", "what is the building behind you?", "what is reed?", "is Reed a building name?", "what do you know?", "Where is Paris?", "what do you do?", "what do you write?", "what is it about?", "who is the professor?", "are you smart?", "are you a robot?", "can you help me?", "do you like cooking?", "good evening", "What do you like?", "are you joking?", "Did you kill Eric?", "hello", "hi, I am Luke.", "You can't do much.", "hello, how are you?", "i'm fine thanks. what's your name?", "where have you studied?", "what is your job?", "are you married?", "would you like to get married?", "would you like to have children?", "what's your mother's name?", "what's your father's name?", "do you have any brothers or sisters"};
        String[] loebnerShuffled = {"What time do you usually go to bed?", "Where do you live.", "I like Waiting for Godot. What is your favorite play?", "Do you remember my name?", "I have a Mazda. What type of car do you have?", "Where do you like to go on holidays?", "How many children do you have?", "I am an atheist. Which religion are you?", "I am a Type B personality. Which type are you?", "Do you prefer red or white wine?", "what is your favorite song?", "Who invented the telephone?", "What music do you like to listen to?", "Hello I'm Ronan. what is your name?", "What emotions are you now feeling?", "What is your mother's name?", "I like bananas. Which is your favorite fruit?", "I like Linux. Which computer operating system do you like?", "What color do you dye your hair?", "What is your birth sign?"};
        String[] sssShuffled = {"where is the capital of japan", "where is your mother", "where are you living", "do you have brothers", "what country do you live", "what's your mother's name?", "are you married?", "What do you like?", "are you joking?", "are you a loser", "where are you from", "what about your wife", "what is your job?", "what is the building behind you?", "what's your father's name?", "I wasn't looking for a ride.  Do you have a car?", "what is the result of one plus one", "how are you?", "what is two plus two", "do you have sex", "You can't do much.", "how old are you?", "where are you going", "do you have any brothers or sisters", "what time is it", "are you an idiot", "are you a robot?", "Fine. How are you?", "why are you going there", "hello", "who is threating you", "would you like to have children?", "what is the color of hair", "hi, I am Luke.", "is tokyo the capital of japan", "where are you?", "fuck you!", "do you like cooking?", "who is the president of usa", "what is reed?", "what do you know?", "Are you a mutant?", "what is it about?", "who is the leader of america", "I like your hair", "do you cook", "who do you have sex with", "why are you going to die", "where have you studied?", "who is the ceo of facebook", "are you smart?", "what do you do", "who is the professor?", "you are stupid", "do you think you are a definite loser", "do you have a son", "thats right my son", "how old is your son", "do you have any brothers", "do my homework", "i'm fine thanks. what's your name?", "what do you write?", "do you have any bothers", "would you like to get married?", "Did you kill Eric?", "good evening", "do you have children", "you have beautiful blue eyes", "What is your name?", "is Reed a building name?", "hello, how are you?", "I wonder if you could have a smirk on your face when you give an off answer.", "why are your wife divorcing you", "who is ceo of microsoft", "fine thanks", "when do you have sex", "Where is Paris?", "can you help me?", "how is the weather", "what is the capital of spain"};

        LuceneManager luceneManager = new LuceneManager();

        if (Main.LEARN_TO_RANK) {
            for (int i = 0; i < sssShuffled.length; i++) {
                String query = sssShuffled[i];
//                System.out.println("T - " + query);
                luceneManager.getAnswer(query);
                qid++;
            }
        } else {
            if (Main.TEST) {
                for (int i = 28; i < 29; i++) {
                    String query = sssShuffled[i];
                    System.out.println("T - " + query);
                    System.out.println("A - " + luceneManager.getAnswer(query));
                }
                for (int i = 78; i < 79; i++) {
                    String query = sssShuffled[i];
                    System.out.println("T - " + query);
                    System.out.println("A - " + luceneManager.getAnswer(query));
                }
            } else {
//                String text = "Where um dois tres um";
//
//                AnalisedSegment s = new AnalisedSegment(text);
//
//                s = TokenizerFactory.getTokenizer(TokenizerFactory.TokenizerType.SIMPLE).tokenize(s);
//
//                EnumSet<Feature> features = EnumSet.of(Feature.BINARY_UNIGRAM, Feature.BINARY_BIGRAM, Feature.BINARY_TRIGRAM, Feature.LENGTH, Feature.FIRST_TOKEN, Feature.QUESTION_HEADWORD);
//
//                SimpleFeatureExtractor sfe = new SimpleFeatureExtractor(features);
//
//                MapCounter counter = sfe.extract(s);
//
//                System.out.println(counter.count("#BU#um"));
//                System.out.println(counter.count("#BB#um dois"));
//                System.out.println(counter.count("#BT#um dois tres"));
//                System.out.println(counter.count("#LENGHT##S#"));
//                System.out.println(counter.count("#LENGHT##L#"));
//                System.out.println(counter.count("#FIRST_TOKEN#um"));
//                System.out.println(counter.count("#FIRST_TOKEN#dois"));
//                System.out.println(counter.count("#HW#um"));
//                System.out.println(counter.count("#HW#Where"));

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

    public static String[] questionHeadWords = {"where", "do", "what", "be", "i", "how", "you", "fine", "why", "hello", "who", "would", "hi", "fuck", "thats", "good", "when", "can"};
    public static String[] answerHeadWords = {"ooh", "do", "i", "no", "oh", "captain", "it", "excellent", "yeah", "now", "but", "curry", "cut", "corte", "japan", "oy", "get", "she", "in", "my", "what", "back", "son", "why", "we", "norman", "brussels", "yes", "new", "here", "straight", "right", "over", "euclid", "nowhere", "staten", "foothill", "about", "senju", "well", "you", "when", "a", "how", "so", "take", "nobody", "and", "big", "really", "the", "l", "pulse", "on", "think", "ask", "boy", "vickie", "eva", "mumtaz", "elizabeth", "last", "have", "maybe", "billy", "congratulation", "hey", "that", "widow", "go", "this", "hangovers", "where", "marlboro", "everything", "yoga", "linda", "see", "okay", "tony", "watch", "shut", "lord", "he", "sorry", "thank", "one", "loser", "come", "who", "shit", "man", "arkansas", "hawaii", "boston", "hollola", "lsb", "every", "n", "up", "live", "any", "hm", "sometimes", "goodlooking", "mitch", "fuck", "beware", "easy", "lydia", "construction", "waitress", "patrick", "which", "celebrity", "couple", "observe", "there", "nothing", "colorados", "um", "look", "radio", "thomas", "subramanyam", "venna", "eladio", "good", "pony", "jave", "woodruff", "george", "retire", "all", "kathryn", "be", "statistically", "bumper", "e", "plus", "tomorrow", "three", "sir", "coffee", "follow", "pc", "luckiest", "hus", "fair", "furious", "fine", "everyone", "because", "morning", "gentleman", "four", "two", "highly", "vincent", "hello", "not", "needless", "excedrin", "pshaw", "stop", "mel", "just", "to", "rarely", "can", "ok", "of", "twentyseven", "fifty", "old", "mother", "twenty", "seventyeight", "twentyfive", "jai", "twentyone", "twentysix", "seven", "homework", "sit", "if", "give", "six", "by", "dr", "p", "five", "relax", "lt", "am", "they", "only", "pray", "charade", "talk", "robot", "absolutely", "line", "shell", "or", "proceed", "open", "anita", "corinne", "whar", "quick", "forget", "alone", "somebody", "martin", "allison", "freddy", "bonsoir", "wake", "tada", "pretty", "let", "sure", "then", "for", "as", "luke", "blond", "red", "b", "black", "make", "seem", "colortress", "auburn", "tripod", "ha", "blast", "doc", "remember", "finally", "gambare", "soon", "tokyo", "may", "hour", "north", "copper", "sonia", "pippa", "asshole", "whatever", "best", "peg", "mps", "say", "pittsburgh", "earth", "lrb", "ronald", "even", "taft", "nancy", "eisenhower", "reed", "evening", "teach", "wait", "tricky", "abby", "please", "second", "most", "fire", "genetically", "damn", "noah", "start", "better", "during", "rajputs", "rajput", "sebastian", "technically", "sooraj", "ban", "thanks", "uh", "smell", "idiot", "christian", "matt", "cause", "at", "pardon", "economics", "guwahati", "university", "under", "dinseyarthurmitland", "subtitle", "listen", "cool", "farmers", "harrison", "suicidal", "christ", "bought", "protect", "lester", "tea", "catch", "melina", "slave", "clarence", "mommy", "kind", "bitch", "until", "marry", "turn", "doctor", "nice", "plan", "faster", "reporter", "thats", "chill", "god", "eleven", "should", "bobby", "very", "frame", "glad", "bullshit", "yucky", "them", "too", "dad", "could", "especially", "buffy", "anna", "julio", "umm", "mostly", "ique", "lf", "mr", "ca", "geeta", "would", "aaahhhhhhh", "actually", "nowadays", "alas", "wayne", "dude", "eric", "wow", "inspector", "devlin", "operator", "daughter", "emanuele", "fourth", "johnny", "ana", "sam", "james", "meghna", "paro", "aidan", "wendy", "lllarry", "charles", "agnes", "leena", "jenny", "anybody", "next", "deploy", "young", "agent", "none", "excuse", "chin", "love", "hard", "sibel", "weii", "nasa", "mummy", "ganesh", "setting", "micro", "thirty", "sex", "with", "paris", "hotel", "oui", "help", "heidi", "huh", "miserable", "jake", "funny", "cavu", "pingo", "lexington", "lusaka", "shawn", "lincoln", "quito", "theodore"};
}




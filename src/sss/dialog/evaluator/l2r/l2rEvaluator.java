package sss.dialog.evaluator.l2r;

import ciir.umass.edu.learning.DataPoint;
import ciir.umass.edu.learning.RankList;
import ciir.umass.edu.learning.Ranker;
import ciir.umass.edu.learning.RankerFactory;
import org.apache.commons.lang3.StringUtils;
import sss.dialog.QA;
import sss.dialog.evaluator.Evaluator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class L2REvaluator implements Evaluator {
    private final List<Evaluator> evaluators;
    private final String modelPath;

    public L2REvaluator(String modelPath, List<Evaluator> evaluators) {
        this.modelPath = modelPath;
        this.evaluators = evaluators;
    }

    @Override
    public void score(String userQuestion, List<QA> qas) {
        RankerFactory rFact = new RankerFactory();
        Ranker ranker = rFact.loadRanker(modelPath);
//        int[] features = ranker.getFeatures();
        for (Evaluator qaScorer : evaluators) {
            qaScorer.score(userQuestion, qas);
        }
        String stringSamples = StringUtils.join(qas, "\n");
        List<RankList> test = getSamples(stringSamples);

        StringBuilder results = new StringBuilder();
        for (int i = 0; i < test.size(); i++) {
            RankList l = test.get(i);
            double[] scores = new double[l.size()];
            for (int j = 0; j < l.size(); j++) {
                scores[j] = ranker.eval(l.get(j));
            }
//            int[] idx = Sorter.sort(scores, false);
//            List<Integer> ll = new ArrayList();
//            for (int j = 0; j < idx.length; j++) {
//                ll.add(Integer.valueOf(idx[j]));
//            }
            for (int j = 0; j < l.size(); j++) {
                results.append(scores[j] + (j == l.size() - 1 ? "" : " ") + "\n");
            }
            System.out.println(results.toString());
        }
    }

    private List<RankList> getSamples(String test) {
        List<RankList> samples = new ArrayList();
        Hashtable<String, Integer> ht = new Hashtable();
        int countRL = 0;
        int countEntries = 0;
        String[] lines = test.split("\n");
        for (String content : lines) {
            content = content.trim();
            if (content.length() != 0) {
                if (content.indexOf("#") != 0) {
                    if ((Ranker.verbose) && (countEntries % 10000 == 0)) {
                        System.out.print("\rReading feature file [" + test + "]: " + countRL + "... ");
                    }
                    DataPoint qp = new DataPoint(content);
                    RankList rl;
                    if (ht.get(qp.getID()) == null) {
                        rl = new RankList();

                        ht.put(qp.getID(), Integer.valueOf(samples.size()));
                        samples.add(rl);
                        countRL++;
                    } else {
                        rl = samples.get((ht.get(qp.getID())).intValue());
                    }
                    rl.add(qp);

                    countEntries++;
                }
            }
        }
        if (Ranker.verbose) {
            System.out.println("\rReading feature file [" + test + "]... [Done.]            ");
            System.out.println("(" + samples.size() + " ranked lists, " + countEntries + " entries read)");
        }
        return samples;
    }
}

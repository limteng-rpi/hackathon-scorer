package work;

import model.Mention;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Evaluation {
    private static boolean textMatchMode = false;

    private static List<String> brokenLines   = new ArrayList<String>();
    private static List<String> repeatedLines = new ArrayList<String>();

    /** Scorer
     * @param args args[0] annotation file 1: gold
     *             args[1] annotation file 2: result
     */
    public static void evaluate(String[] args)  {
        String line;
        NumberFormat formatter;
        BufferedReader br1, br2;
        List<Mention> mentionList1, mentionList2;
        Set<String> lineSet1, lineSet2;

        try {
            int lineNum;
            formatter = new DecimalFormat("#0.00");
            mentionList1 = new ArrayList<Mention>();
            mentionList2 = new ArrayList<Mention>();
            lineSet1     = new HashSet<String>();
            lineSet2     = new HashSet<String>();
            br1 = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "utf-8"));
            br2 = new BufferedReader(new InputStreamReader(new FileInputStream(args[1]), "utf-8"));

            /* read gold */
            lineNum = 0;
            while ((line = br1.readLine()) != null) {
                lineNum++;

                if (lineSet1.contains(line)) {
                    repeatedLines.add("file 1, line " + lineNum + ": " + line);
                    continue;
                } else {
                    lineSet1.add(line);
                }

                String[] segs = line.split("\t");

                if (segs.length != 7) {
                    brokenLines.add("file 1, line " + lineNum + ": " + line);
                    continue;
                }

                try {
                    String annotator = segs[0];
                    String id        = segs[1];
                    String text      = segs[2];
                    String doc       = segs[3].substring(0, segs[3].indexOf(':'));
                    int startOffset  = Integer.parseInt(segs[3].substring(segs[3].indexOf(':') + 1, segs[3].indexOf('-')));
                    int endOffset    = Integer.parseInt(segs[3].substring(segs[3].indexOf('-') + 1));
                    String trans     = segs[4];
                    String wiki      = segs[5];
                    String type      = segs[6];

                    Mention mention = new Mention(text, startOffset, endOffset);
                    mention.setAnnotator(annotator);
                    mention.setId(id);
                    mention.setDoc(doc);
                    mention.setTranslation(trans);
                    mention.setWiki(wiki);
                    mention.setType(type);
                    mention.setTextMatchMode(textMatchMode);
                    if (!mentionList1.contains(mention)) {
                        mentionList1.add(mention);
                    }
                } catch (Exception ex) {
                    brokenLines.add("file 1, line " + lineNum + ": " + line);
                }
            }
            br1.close();
            System.out.println("#1: " + mentionList1.size());

            /* read result */
            lineNum = 0;
            while ((line = br2.readLine()) != null) {
                lineNum++;

                if (lineSet2.contains(line)) {
                    repeatedLines.add("file 2, line " + lineNum + ": " + line);
                    continue;
                } else {
                    lineSet2.add(line);
                }

                String[] segs = line.split("\t");

                if (segs.length != 7) {
                    brokenLines.add("file 2, line " + lineNum + ": " + line);
                    continue;
                }

                try {
                    String annotator = segs[0];
                    String id        = segs[1];
                    String text      = segs[2];
                    String doc       = segs[3].substring(0, segs[3].indexOf(':'));
                    int startOffset  = Integer.parseInt(segs[3].substring(segs[3].indexOf(':') + 1, segs[3].indexOf('-')));
                    int endOffset    = Integer.parseInt(segs[3].substring(segs[3].indexOf('-') + 1));
                    String trans     = segs[4];
                    String wiki      = segs[5];
                    String type      = segs[6];

                    Mention mention = new Mention(text, startOffset, endOffset);
                    mention.setAnnotator(annotator);
                    mention.setId(id);
                    mention.setDoc(doc);
                    mention.setTranslation(trans);
                    mention.setWiki(wiki);
                    mention.setType(type);
                    mention.setTextMatchMode(textMatchMode);
                    if (!mentionList2.contains(mention)) {
                        mentionList2.add(mention);
                    }
                } catch (Exception ex) {
                    brokenLines.add("file 2, line " + lineNum + ": " + line);
                }
            }
            br2.close();
            System.out.println("#2: " + mentionList2.size());


            /* identification f-score */
            int menNum1 = mentionList1.size();
            int menNum2 = mentionList2.size();
            int correctPositiveNum = 0;
            for (Mention men2 : mentionList2) {
                if (mentionList1.contains(men2)) {
                    correctPositiveNum++;
                }
            }
            double idPrecision;
            double idRecall;
            double idFScore;
            if (menNum2 == 0) {
                idPrecision = 0.0;
            } else {
                idPrecision = (double) correctPositiveNum / menNum2;
            }
            if (menNum1 == 0) {
                idRecall = 0.0;
            } else {
                idRecall = (double) correctPositiveNum / menNum1;
            }
            if (idPrecision + idRecall < 10e-6) {
                idFScore = 0.0;
            } else {
                idFScore = 2.0 * (idPrecision * idRecall) / (idPrecision + idRecall);
            }
            System.out.println("ID Precision:      " + formatter.format(idPrecision * 100.0) + "%");
            System.out.println("ID Recall:         "  + formatter.format(idRecall * 100.0) + "%");
            System.out.println("ID F-score:        " + formatter.format(idFScore * 100.0) + "%");


            /* type accuracy */
            int correctTypeNum = 0;
            for (Mention men2 : mentionList2) {
                if (mentionList1.contains(men2)) {
                    if (men2.getType().equals(mentionList1.get(mentionList1.indexOf(men2)).getType())) {
                        correctTypeNum++;
                    }
                }
            }
            double typeAccuracy;
            if (correctPositiveNum == 0) {
                typeAccuracy = 0.0;
            } else {
                typeAccuracy = (double) correctTypeNum / correctPositiveNum;
            }
            System.out.println("Type Accuracy:     " + formatter.format(typeAccuracy * 100.0) + "%");


            /* translation accuracy */
            int correctTransNum = 0;
            for (Mention men2 : mentionList2) {
                if (mentionList1.contains(men2)) {
                    if (men2.getTranslation().equals(mentionList1.get(mentionList1.indexOf(men2)).getTranslation())) {
                        correctTransNum++;
                    }
                }
            }
            double transAccuracy;
            if (correctPositiveNum == 0) {
                transAccuracy = 0.0;
            } else {
                transAccuracy = (double) correctTransNum / correctPositiveNum;
            }
            System.out.println("Trans Accuracy:    " + formatter.format(transAccuracy * 100.0) + "%");

            /* wikipedia accuracy */
            int correctWikiNum = 0;
            for (Mention men2 : mentionList2) {
                if (mentionList1.contains(men2)) {
                    if (men2.getWiki().equals(mentionList1.get(mentionList1.indexOf(men2)).getWiki())) {
                        correctWikiNum++;
                    }
                }
            }
            double wikiAccuracy;
            if (correctPositiveNum == 0) {
                wikiAccuracy = 0.0;
            } else {
                wikiAccuracy = (double) correctWikiNum / correctPositiveNum;
            }
            System.out.println("Wiki Accuracy:     " + formatter.format(wikiAccuracy * 100.0) + "%");

            /* overall */
            int exactlyMatchNum = 0;
            for (Mention men2 : mentionList2) {
                if (mentionList1.contains(men2)) {
                    if (men2.match(mentionList1.get(mentionList1.indexOf(men2)))) {
                        exactlyMatchNum++;
                    }
                }
            }
            double overallPrecision;
            double overallRecall;
            double overallFScore;
            if (menNum2 == 0) {
                overallPrecision = 0.0;
            } else {
                overallPrecision = (double) exactlyMatchNum / menNum2;
            }
            if (menNum1 == 0) {
                overallRecall = 0.0;
            } else {
                overallRecall = (double) exactlyMatchNum / menNum1;
            }
            if (overallPrecision + overallRecall < 10e-6) {
                overallFScore = 0.0;
            } else {
                overallFScore = 2.0 * (overallPrecision * overallRecall) / (overallPrecision + overallRecall);
            }
            System.out.println("Overall Precision: " + formatter.format(overallPrecision * 100.0) + "%");
            System.out.println("Overall Recall:    " + formatter.format(overallRecall * 100.0) + "%");
            System.out.println("Overall F-score:   " + formatter.format(overallFScore * 100.0) + "%");
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }

    public static void evaluateTextMatchMode(String[] args) {

    }

    public static void main(String[] args) {
        /* for testing */
        // String[] testArgs = {"data/anno_1_gold_t.txt", "data/anno_2_t.txt", "-t"};
        // String[] testArgs = {"data/anno_1_gold_t.txt", "data/anno_2_t.txt"};
        // args = testArgs;

        /* check arguments */
        if (args.length < 2) {
            System.out.println("ERROR: Invalid arguments\n" +
                    "USAGE: java -jar eval.jar [annotation_file_1] [annotation_fle_2]\n" +
                    "annotation_file_1: gold\nannotation_file_2: result");
            return;
        } else if (args.length == 2) {
            /* run scorer */
            evaluate(args);

        } else {
            /* parameters */
            for (int i = 2; i < args.length; i ++) {
                if (args[i].equals("-t") || args[i].equals("--textMatch")) {
                    textMatchMode = true;
                }
            }

            /* run scorer */
            String[] files = {args[0], args[1]};
            evaluate(files);
        }

        /* print broken lines */
        if (brokenLines.size() != 0) {
            System.out.println("\nBroken lines:");
            for (String line : brokenLines) {
                System.out.println(line);
            }
        }
    }
}

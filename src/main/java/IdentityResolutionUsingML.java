import blocker.PlayerBlockingKeyByNationalityGenerator;
import comparator.*;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import model.Player;
import model.PlayerXMLReader;
import org.slf4j.Logger;

import java.io.File;

public class IdentityResolutionUsingML {
    private static final Logger logger = WinterLogManager.activateLogger("default");

    public static void main( String[] args ) throws Exception
    {
        // loading data
        System.out.println("*\n*\tLoading datasets\n*");

        //loading the data
        HashedDataSet<Player, Attribute> dataPredictionPlayers = new HashedDataSet<>();
        HashedDataSet<Player, Attribute> dataRealPlayers = new HashedDataSet<>();
        HashedDataSet<Player, Attribute> dataFifaPlayers = new HashedDataSet<>();
        new PlayerXMLReader().loadFromXML(new File("data/input/real_market_players.xml"),"/players/player", dataRealPlayers);
        new PlayerXMLReader().loadFromXML(new File("data/input/prediction_players.xml"),"/players/player", dataPredictionPlayers);
        new PlayerXMLReader().loadFromXML(new File("data/input/fifa_players.xml"),"/players/player", dataFifaPlayers);

        // load the training set
        MatchingGoldStandard gsTraining = new MatchingGoldStandard();
        gsTraining.loadFromCSVFile(new File("data/goldstandard/real_market_2_prediction_train.csv"));

        // create a matching rule
        String options[] = new String[] { "-S" };
        String modelType = "SimpleLogistic"; // use a logistic regression
        WekaMatchingRule<Player, Attribute> matchingRuleForReal2Pred = new WekaMatchingRule<>(0.7, modelType, options);
        matchingRuleForReal2Pred.activateDebugReport("data/output/debugResultsMatchingRule.csv", 1000, gsTraining);

        // add comparators
        matchingRuleForReal2Pred.addComparator(new PlayerNameComparatorJaccard());
        matchingRuleForReal2Pred.addComparator(new PlayerNameComparatorJaccardTokenizer());
        matchingRuleForReal2Pred.addComparator(new PlayerClubComparatorJaccard());
        matchingRuleForReal2Pred.addComparator(new PlayerNationalityComparatorJaccard());
        matchingRuleForReal2Pred.addComparator(new PlayerBirthDateComparatorEqual());


        // train the matching rule's model
        System.out.println("*\n*\tLearning matching rule\n*");
        RuleLearner<Player, Attribute> learner = new RuleLearner<>();
        learner.learnMatchingRule(dataRealPlayers, dataPredictionPlayers, null, matchingRuleForReal2Pred, gsTraining);
        System.out.println(String.format("Matching rule is:\n%s", matchingRuleForReal2Pred.getModelDescription()));

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Player, Attribute> blocker = new StandardRecordBlocker<Player, Attribute>(new PlayerBlockingKeyByNationalityGenerator());
//		SortedNeighbourhoodBlocker<Movie, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new MovieBlockingKeyByDecadeGenerator(), 1);
        blocker.collectBlockSizeData("data/output/debugResultsBlocking.csv", 100);

        // Initialize Matching Engine
        MatchingEngine<Player, Attribute> engine = new MatchingEngine<>();

        // Execute the matching
        System.out.println("*\n*\tRunning identity resolution\n*");
        Processable<Correspondence<Player, Attribute>> correspondences = engine.runIdentityResolution(
                dataRealPlayers, dataPredictionPlayers, null, matchingRuleForReal2Pred,
                blocker);

        // write the correspondences to the output file
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/real_2_prediction_correspondences.csv"), correspondences);

        // load the gold standard (test set)
        System.out.println("*\n*\tLoading gold standard\n*");
        MatchingGoldStandard gsTest = new MatchingGoldStandard();
        gsTest.loadFromCSVFile(new File(
                "data/goldstandard/real_market_2_prediction_test.csv"));

        // evaluate your result
        System.out.println("*\n*\tEvaluating result\n*");
        MatchingEvaluator<Player, Attribute> evaluator = new MatchingEvaluator<Player, Attribute>();
        Performance perfTest = evaluator.evaluateMatching(correspondences,
                gsTest);

        // print the evaluation result
        System.out.println("Academy Awards <-> Actors");
        System.out.println(String.format(
                "Precision: %.4f",perfTest.getPrecision()));
        System.out.println(String.format(
                "Recall: %.4f",	perfTest.getRecall()));
        System.out.println(String.format(
                "F1: %.4f",perfTest.getF1()));
    }
}

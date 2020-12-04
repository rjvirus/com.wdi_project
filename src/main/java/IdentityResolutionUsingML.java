import blocker.PlayerBlockingKeyByNationalityGenerator;
import comparator.*;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;
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
    private static final Logger logger = WinterLogManager.activateLogger("trace");

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

        // load the training sets
        MatchingGoldStandard gsTrainingRealPred = new MatchingGoldStandard();
        MatchingGoldStandard gsTrainingPredFifa = new MatchingGoldStandard();
        MatchingGoldStandard gsTrainingRealFifa = new MatchingGoldStandard();

        gsTrainingRealPred.loadFromCSVFile(new File("data/goldstandard/real_market_2_prediction_train.csv"));
        gsTrainingPredFifa.loadFromCSVFile(new File("data/goldstandard/prediction_2_fifa_train.csv"));
        gsTrainingRealFifa.loadFromCSVFile(new File("data/goldstandard/real_market_2_fifa_train.csv"));

        // create matching rules
        String options[] = new String[] { "-S" };
        String modelType = "SimpleLogistic"; // use a logistic regression
        WekaMatchingRule<Player, Attribute> matchingRuleRealPred = new WekaMatchingRule<>(0.8, modelType, options);
        matchingRuleRealPred.activateDebugReport("data/output/debugResultsMatchingRule.csv", 1000, gsTrainingRealPred);

        WekaMatchingRule<Player, Attribute> matchingRulePredFifa = new WekaMatchingRule<>(0.75, modelType, options);
        matchingRulePredFifa.activateDebugReport("data/output/debugResultsMatchingRulePredFifa.csv", 1000, gsTrainingPredFifa);

        WekaMatchingRule<Player, Attribute> matchingRuleRealFifa = new WekaMatchingRule<>(0.75, modelType, options);
        matchingRuleRealFifa.activateDebugReport("data/output/debugResultsMatchingRule.csv", 1000, gsTrainingRealFifa);


        // add comparators
        matchingRuleRealPred.addComparator(new PlayerNameComparatorJaccard());
        matchingRuleRealPred.addComparator(new PlayerNameComparatorJaccardTokenizer());
        matchingRuleRealPred.addComparator(new PlayerNameComparatorLevenshtein());
        matchingRuleRealPred.addComparator(new PlayerClubComparatorNGramJaccard());
        matchingRuleRealPred.addComparator(new PlayerNationalityComparatorJaccard());
        matchingRuleRealPred.addComparator(new PlayerBirthDateComparatorEqual());

        // add comparators
        matchingRulePredFifa.addComparator(new PlayerNameComparatorJaccard());
        matchingRulePredFifa.addComparator(new PlayerNameShortComparatorJaccard());
        matchingRulePredFifa.addComparator(new PlayerClubComparatorNGramJaccard());
        matchingRulePredFifa.addComparator(new PlayerNationalityComparatorJaccard());
        matchingRulePredFifa.addComparator(new PlayerContractExpComparatorEqual());
        matchingRulePredFifa.addComparator(new PlayerPositionComparatorJaccard());

        // add comparators
        matchingRuleRealFifa.addComparator(new PlayerNameComparatorJaccard());
        matchingRuleRealFifa.addComparator(new PlayerNameShortComparatorJaccard());
        matchingRuleRealFifa.addComparator(new PlayerClubComparatorNGramJaccard());
        matchingRuleRealFifa.addComparator(new PlayerNationalityComparatorJaccard());
        matchingRuleRealFifa.addComparator(new PlayerContractExpComparatorEqual());
        matchingRuleRealFifa.addComparator(new PlayerPositionComparatorJaccard());

        // train the matching rule's model
        System.out.println("*\n*\tLearning matching rule\n*");
        RuleLearner<Player, Attribute> learnerRealPred = new RuleLearner<>();
        RuleLearner<Player, Attribute> learnerPredFifa = new RuleLearner<>();
        RuleLearner<Player, Attribute> learnerRealFifa = new RuleLearner<>();

        learnerRealPred.learnMatchingRule(dataRealPlayers, dataPredictionPlayers, null, matchingRuleRealPred, gsTrainingRealPred);
        learnerPredFifa.learnMatchingRule(dataPredictionPlayers, dataFifaPlayers, null, matchingRulePredFifa, gsTrainingPredFifa);
        learnerRealFifa.learnMatchingRule(dataRealPlayers, dataFifaPlayers, null, matchingRuleRealFifa, gsTrainingRealFifa);
        System.out.println(String.format("Matching rule for Real <-> Pred  is:\n%s", matchingRuleRealPred.getModelDescription()));
        System.out.println(String.format("Matching rule for Pred <-> Fifa is:\n%s", matchingRulePredFifa.getModelDescription()));
        System.out.println(String.format("Matching rule for Real <-> Fifa is:\n%s", matchingRuleRealFifa.getModelDescription()));

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Player, Attribute> blocker = new StandardRecordBlocker<Player, Attribute>(new PlayerBlockingKeyByNationalityGenerator());
        //SortedNeighbourhoodBlocker<Player, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new PlayerBlockingKeyByNationalityGenerator(), 1);
        blocker.collectBlockSizeData("data/output/debugResultsBlocking.csv", 100);

        // Initialize Matching Engine
        MatchingEngine<Player, Attribute> engine = new MatchingEngine<>();

        // Execute the matching
        System.out.println("*\n*\tRunning identity resolution\n*");
        Processable<Correspondence<Player, Attribute>> correspondencesRealPred = engine.runIdentityResolution(
                dataRealPlayers, dataPredictionPlayers, null, matchingRuleRealPred,
                blocker);
        Processable<Correspondence<Player, Attribute>> correspondencesPredFifa = engine.runIdentityResolution(
                dataPredictionPlayers, dataFifaPlayers, null, matchingRulePredFifa,
                blocker);
        Processable<Correspondence<Player, Attribute>> correspondencesRealFifa = engine.runIdentityResolution(
                dataRealPlayers, dataFifaPlayers, null, matchingRuleRealFifa,
                blocker);

        // write the correspondences to the output file
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/correspondences/real_2_prediction_correspondences.csv"), correspondencesRealPred);
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/correspondences/prediction_2_fifa_correspondences.csv"), correspondencesPredFifa);
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/correspondences/real_2_fifa_correspondences.csv"), correspondencesRealFifa);


        // load the gold standard (test set)
        System.out.println("*\n*\tLoading gold standard\n*");
        MatchingGoldStandard gsTestRealPred = new MatchingGoldStandard();
        MatchingGoldStandard gsTestPredFifa = new MatchingGoldStandard();
        MatchingGoldStandard gsTestRealFifa = new MatchingGoldStandard();

        gsTestRealPred.loadFromCSVFile(new File("data/goldstandard/real_market_2_prediction_test.csv"));
        gsTestRealFifa.loadFromCSVFile(new File("data/goldstandard/real_market_2_fifa_test.csv"));
        gsTestPredFifa.loadFromCSVFile(new File("data/goldstandard/prediction_2_fifa_test.csv"));
        System.out.println(matchingRuleRealPred.getModelDescription());
        System.out.println(matchingRulePredFifa.getModelDescription());
        System.out.println(matchingRuleRealFifa.getModelDescription());


        // evaluate your result
        System.out.println("*\n*\tEvaluating result\n*");
        MatchingEvaluator<Player, Attribute> evaluatorRealPred = new MatchingEvaluator<Player, Attribute>();
        MatchingEvaluator<Player, Attribute> evaluatorPredFifa = new MatchingEvaluator<Player, Attribute>();
        MatchingEvaluator<Player, Attribute> evaluatorRealFifa = new MatchingEvaluator<Player, Attribute>();
        Performance perfTestRealPred = evaluatorRealPred.evaluateMatching(correspondencesRealPred,
                gsTestRealPred);
        Performance perfTestPredFifa = evaluatorPredFifa.evaluateMatching(correspondencesPredFifa,
                gsTestPredFifa);
        Performance perfTestRealFifa = evaluatorRealFifa.evaluateMatching(correspondencesRealFifa,
                gsTestRealFifa);

        // print the evaluation result
        System.out.println("Real <-> Pred");
        System.out.println(String.format(
                "Precision: %.4f",perfTestRealPred.getPrecision()));
        System.out.println(String.format(
                "Recall: %.4f",	perfTestRealPred.getRecall()));
        System.out.println(String.format(
                "F1: %.4f",perfTestRealPred.getF1()));

        System.out.println("Prediction <-> Fifa");
        System.out.println(String.format(
                "Precision: %.4f",perfTestPredFifa.getPrecision()));
        System.out.println(String.format(
                "Recall: %.4f",	perfTestPredFifa.getRecall()));
        System.out.println(String.format(
                "F1: %.4f",perfTestPredFifa.getF1()));

        System.out.println("Real <-> Fifa");
        System.out.println(String.format(
                "Precision: %.4f",perfTestRealFifa.getPrecision()));
        System.out.println(String.format(
                "Recall: %.4f",	perfTestRealFifa.getRecall()));
        System.out.println(String.format(
                "F1: %.4f",perfTestRealFifa.getF1()));
    }
}

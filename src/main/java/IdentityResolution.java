import blocker.PlayerBlockingKeyByNationalityGenerator;
import comparator.*;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import model.Player;
import model.PlayerXMLReader;
import org.slf4j.Logger;
import java.io.File;

public class IdentityResolution {
    private static final Logger logger = WinterLogManager.activateLogger("trace");

    public static void main(String args[]) throws Exception {

        //create 3 matching rules
        LinearCombinationMatchingRule<Player, Attribute> matchingRuleRealPred = new LinearCombinationMatchingRule<>(0.7);
        LinearCombinationMatchingRule<Player, Attribute> matchingRuleRealFifa = new LinearCombinationMatchingRule<>(0.53);
        LinearCombinationMatchingRule<Player, Attribute> matchingRulePredFifa = new LinearCombinationMatchingRule<>(0.55);

        //loading the data
        HashedDataSet<Player, Attribute> dataRealPlayers = new HashedDataSet<>();
        HashedDataSet<Player, Attribute> dataPredictionPlayers = new HashedDataSet<>();
        HashedDataSet<Player, Attribute> dataFifaPlayers = new HashedDataSet<>();
        new PlayerXMLReader().loadFromXML(new File("data/input/real_market_players.xml"),"/players/player", dataRealPlayers);
        new PlayerXMLReader().loadFromXML(new File("data/input/prediction_players.xml"),"/players/player", dataPredictionPlayers);
        new PlayerXMLReader().loadFromXML(new File("data/input/fifa_players.xml"),"/players/player", dataFifaPlayers);

        // load the gold standards (test set)
        System.out.println("*\n*\tLoading gold standard for Real Market Players to Prediction \n*");
        MatchingGoldStandard gsTestRealPred = new MatchingGoldStandard();
        gsTestRealPred.loadFromCSVFile(new File("data/goldstandard/real_market_2_prediction_test.csv"));

        System.out.println("*\n*\tLoading gold standard for Real Market Players to FIFA \n*");
        MatchingGoldStandard gsTestRealFifa = new MatchingGoldStandard();
        gsTestRealFifa.loadFromCSVFile(new File("data/goldstandard/real_market_2_fifa_test.csv"));

        System.out.println("*\n*\tLoading gold standard for Prediction to FIFA\n*");
        MatchingGoldStandard gsTestPredFifa = new MatchingGoldStandard();
        gsTestPredFifa.loadFromCSVFile(new File("data/goldstandard/prediction_2_fifa_test.csv"));

        //added comparators for RealPred
        matchingRuleRealPred.addComparator(new PlayerNameComparatorJaccard(), 0.50);
        //matchingRuleRealPred.addComparator(new PlayerClubComparatorJaccard(), 0.10);
        matchingRuleRealPred.addComparator(new PlayerNationalityComparatorJaccard(), 0.25);
        matchingRuleRealPred.addComparator(new PlayerBirthDateComparatorEqual(), 0.25);

        //added comparators for RealFifa
        matchingRuleRealFifa.addComparator(new PlayerNameShortComparatorJaccard(), 0.6);
        matchingRuleRealFifa.addComparator(new PlayerClubComparatorNGramJaccard(), 0.12);
        matchingRuleRealFifa.addComparator(new PlayerNationalityComparatorJaccard(), 0.23);
        matchingRuleRealFifa.addComparator(new PlayerKitNumberComparatorEqual(), 0.05);

        //added comparators for PredFifa
        matchingRulePredFifa.addComparator(new PlayerNameShortComparatorJaccard(), 0.6);
        matchingRulePredFifa.addComparator(new PlayerClubComparatorNGramJaccard(), 0.20);
        matchingRulePredFifa.addComparator(new PlayerNationalityComparatorJaccard(), 0.15);
        //matchingRulePredFifa.addComparator(new PlayerContractExpComparatorEqual(), 0.05);
        matchingRulePredFifa.addComparator(new PlayerPositionComparatorJaccard(), 0.05);

        // Initialize Matching Engines
        MatchingEngine<Player, Attribute> engineRealPred = new MatchingEngine<>();
        MatchingEngine<Player, Attribute> engineRealFifa = new MatchingEngine<>();
        MatchingEngine<Player, Attribute> enginePredFifa = new MatchingEngine<>();

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Player, Attribute> blocker = new StandardRecordBlocker<Player, Attribute>(new PlayerBlockingKeyByNationalityGenerator());

        // Execute the matchings
        Processable<Correspondence<Player, Attribute>> correspondencesRealPred = engineRealPred.runIdentityResolution(
                dataRealPlayers, dataPredictionPlayers, null, matchingRuleRealPred, blocker);
        Processable<Correspondence<Player, Attribute>> correspondencesRealFifa = engineRealFifa.runIdentityResolution(
                dataRealPlayers, dataFifaPlayers, null, matchingRuleRealFifa, blocker);
        Processable<Correspondence<Player, Attribute>> correspondencesPredFifa = enginePredFifa.runIdentityResolution(
                dataPredictionPlayers, dataFifaPlayers , null, matchingRulePredFifa, blocker);


        // write the correspondences to the output file
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/correspondences/real_2_prediction_correspondences.csv"), correspondencesRealPred);
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/correspondences/real_2_fifa_correspondences.csv"), correspondencesRealFifa);
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/correspondences/prediction_2_fifa_correspondences.csv"), correspondencesPredFifa);


        System.out.println("*\n*\tEvaluating result for Real to Prediction\n*");
        // evaluate your results
        MatchingEvaluator<Player, Attribute> evaluatorRealPred = new MatchingEvaluator<Player, Attribute>();
        Performance perfTestRealPred = evaluatorRealPred.evaluateMatching(correspondencesRealPred,
                gsTestRealPred);

        System.out.println("*\n*\tEvaluating result for Real to FIFA\n*");
        MatchingEvaluator<Player, Attribute> evaluatorRealFifa = new MatchingEvaluator<Player, Attribute>();
        Performance perfTestRealFifa = evaluatorRealFifa.evaluateMatching(correspondencesRealFifa,
                gsTestRealFifa);

        System.out.println("*\n*\tEvaluating result for Prediction to FIFA\n*");
        MatchingEvaluator<Player, Attribute> evaluatorPredFifa = new MatchingEvaluator<Player, Attribute>();
        Performance perfTestPredFifa = evaluatorPredFifa.evaluateMatching(correspondencesPredFifa,
                gsTestPredFifa);

        // print the evaluation result
        System.out.println("Players - Real Market <-> Players - Predicted Price");
        System.out.println(String.format(
                "Precision: %.4f",perfTestRealPred.getPrecision()));
        System.out.println(String.format(
                "Recall: %.4f",	perfTestRealPred.getRecall()));
        System.out.println(String.format(
                "F1: %.4f",perfTestRealPred.getF1()));

        System.out.println("Players - Real Market <-> Players - Fifa");
        System.out.println(String.format(
                "Precision: %.4f",perfTestRealFifa.getPrecision()));
        System.out.println(String.format(
                "Recall: %.4f",	perfTestRealFifa.getRecall()));
        System.out.println(String.format(
                "F1: %.4f",perfTestRealFifa.getF1()));

        System.out.println("Players - Predicted Prict <-> Players - Fifa");
        System.out.println(String.format(
                "Precision: %.4f",perfTestPredFifa.getPrecision()));
        System.out.println(String.format(
                "Recall: %.4f",	perfTestPredFifa.getRecall()));
        System.out.println(String.format(
                "F1: %.4f",perfTestPredFifa.getF1()));
    }
}

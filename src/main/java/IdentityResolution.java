import blocker.PlayerBlockingKeyByNationalityGenerator;
import comparator.*;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
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
    private static final Logger logger = WinterLogManager.activateLogger("default");

    public static void main(String args[]) throws Exception {

        //create 3 matching rules
        LinearCombinationMatchingRule<Player, Attribute> matchingRuleRealPred = new LinearCombinationMatchingRule<>(0.7);
        LinearCombinationMatchingRule<Player, Attribute> matchingRuleFifaReal = new LinearCombinationMatchingRule<>(0.5);
        LinearCombinationMatchingRule<Player, Attribute> matchingRuleFifaPred = new LinearCombinationMatchingRule<>(0.5);

        //loading the data
        HashedDataSet<Player, Attribute> dataRealPlayers = new HashedDataSet<>();
        HashedDataSet<Player, Attribute> dataPredictionPlayers = new HashedDataSet<>();
        HashedDataSet<Player, Attribute> dataFifaPlayers = new HashedDataSet<>();
        new PlayerXMLReader().loadFromXML(new File("data/input/real_market_players.xml"),"/players/player", dataRealPlayers);
        new PlayerXMLReader().loadFromXML(new File("data/input/prediction_players.xml"),"/players/player", dataPredictionPlayers);
        new PlayerXMLReader().loadFromXML(new File("data/input/fifa_players.xml"),"/players/player", dataFifaPlayers);

        //added comparators for RealPred
        matchingRuleRealPred.addComparator(new PlayerNameComparatorJaccardTokenizer(), 0.40);
        matchingRuleRealPred.addComparator(new PlayerClubComparatorJaccard(), 0.10);
        matchingRuleRealPred.addComparator(new PlayerNationalityComparatorJaccard(), 0.25);
        matchingRuleRealPred.addComparator(new PlayerBirthDateComparatorEqual(), 0.25);

        //added comparators for FifaReal
        matchingRuleFifaReal.addComparator(new PlayerNameComparatorJaccard(), 0.6);
        matchingRuleFifaReal.addComparator(new PlayerClubComparatorJaccard(), 0.3);
        // matchingRuleFifaReal.addComparator(new PlayerMarketValueComparatorPercentageSim(), 0.1);
        matchingRuleFifaReal.addComparator(new PlayerKitNumberComparatorEqual(), 0.1);

        //added comparators for FifaPred
        matchingRuleFifaPred.addComparator(new PlayerNameComparatorJaccard(), 0.35);
        matchingRuleFifaPred.addComparator(new PlayerClubComparatorJaccard(), 0.25);
        matchingRuleFifaPred.addComparator(new PlayerNationalityComparatorJaccard(), 0.15);
        matchingRuleFifaPred.addComparator(new PlayerContractExpComparatorEqual(), 0.15);
        matchingRuleFifaPred.addComparator(new PlayerPositionComparatorJaccard(), 0.1);

        // Initialize Matching Engines
        MatchingEngine<Player, Attribute> engineRealPred = new MatchingEngine<>();
        MatchingEngine<Player, Attribute> engineFifaReal = new MatchingEngine<>();
        MatchingEngine<Player, Attribute> engineFifaPred = new MatchingEngine<>();

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Player, Attribute> blocker = new StandardRecordBlocker<Player, Attribute>(new PlayerBlockingKeyByNationalityGenerator());

        // Execute the matchings
        Processable<Correspondence<Player, Attribute>> correspondencesRealPred = engineRealPred.runIdentityResolution(
                dataRealPlayers, dataPredictionPlayers, null, matchingRuleRealPred, blocker);
        Processable<Correspondence<Player, Attribute>> correspondencesFifaReal = engineFifaReal.runIdentityResolution(
                dataFifaPlayers, dataRealPlayers, null, matchingRuleFifaReal, blocker);
        Processable<Correspondence<Player, Attribute>> correspondencesFifaPred = engineFifaPred.runIdentityResolution(
                dataFifaPlayers, dataPredictionPlayers, null, matchingRuleFifaPred, blocker);


        // write the correspondences to the output file
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/real_2_prediction_correspondences.csv"), correspondencesRealPred);
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/fifa_2_real_correspondences.csv"), correspondencesFifaReal);
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/fifa_2_prediction_correspondences.csv"), correspondencesFifaPred);
    }
}

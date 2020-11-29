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
        LinearCombinationMatchingRule<Player, Attribute> matchingRulePredReal = new LinearCombinationMatchingRule<>(0.7);
        LinearCombinationMatchingRule<Player, Attribute> matchingRuleFifaReal = new LinearCombinationMatchingRule<>(0.5);

        //loading the data
        HashedDataSet<Player, Attribute> dataPredictionPlayers = new HashedDataSet<>();
        HashedDataSet<Player, Attribute> dataRealPlayers = new HashedDataSet<>();
        HashedDataSet<Player, Attribute> dataFifaPlayers = new HashedDataSet<>();
        new PlayerXMLReader().loadFromXML(new File("data/input/prediction_players.xml"),"/players/player", dataPredictionPlayers);
        new PlayerXMLReader().loadFromXML(new File("data/input/real_market_players.xml"),"/players/player", dataRealPlayers);
        new PlayerXMLReader().loadFromXML(new File("data/input/fifa_players.xml"),"/players/player", dataFifaPlayers);

        //added comparators for PredReal
        matchingRulePredReal.addComparator(new PlayerNameComparatorJaccard(), 0.40);
        matchingRulePredReal.addComparator(new PlayerClubComparatorJaccard(), 0.10);
        matchingRulePredReal.addComparator(new PlayerNationalityComparatorJaccard(), 0.25);
        matchingRulePredReal.addComparator(new PlayerBirthDateComparatorEqual(), 0.25);

        //added comparators for FifaReal
        matchingRuleFifaReal.addComparator(new PlayerNameComparatorJaccard(), 0.6);
        matchingRuleFifaReal.addComparator(new PlayerClubComparatorJaccard(), 0.3);
        // matchingRuleFifaReal.addComparator(new PlayerMarketValueComparatorPercentageSim(), 0.1);
        matchingRuleFifaReal.addComparator(new PlayerKitNumberComparatorEqual(), 0.1);

        // Initialize Matching Engines
        MatchingEngine<Player, Attribute> enginePredReal = new MatchingEngine<>();
        MatchingEngine<Player, Attribute> engineFifaReal = new MatchingEngine<>();

        // create a blocker (blocking strategy)
        StandardRecordBlocker<Player, Attribute> blocker = new StandardRecordBlocker<Player, Attribute>(new PlayerBlockingKeyByNationalityGenerator());

        // Execute the matchings
        Processable<Correspondence<Player, Attribute>> correspondencesPredReal = enginePredReal.runIdentityResolution(
                dataPredictionPlayers, dataRealPlayers, null, matchingRulePredReal, blocker);
        Processable<Correspondence<Player, Attribute>> correspondencesFifaReal = engineFifaReal.runIdentityResolution(
                dataFifaPlayers, dataRealPlayers, null, matchingRuleFifaReal, blocker);


        // write the correspondences to the output file
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/prediction_2_real_correspondences.csv"), correspondencesPredReal);
        new CSVCorrespondenceFormatter().writeCSV(new File("data/output/fifa_2_real_correspondences.csv"), correspondencesFifaReal);

        System.out.println("Hello world");
    }
}

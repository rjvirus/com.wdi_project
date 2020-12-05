import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEngine;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEvaluator;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionStrategy;
import de.uni_mannheim.informatik.dws.winter.model.*;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import evaluation.*;
import fusers.*;
import model.Player;
import model.PlayerXMLFormatter;
import model.PlayerXMLReader;
import org.slf4j.Logger;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

public class DataFusion {

    private static final Logger logger = WinterLogManager.activateLogger("trace");

    public static void main( String[] args ) throws Exception
    {
        // Load the Data into FusibleDataSet
        System.out.println("*\n*\tLoading datasets\n*");
        FusibleDataSet<Player, Attribute> dsReal = new FusibleHashedDataSet<>();
        new PlayerXMLReader().loadFromXML(new File("data/input/real_market_players.xml"), "/players/player", dsReal);
        dsReal.printDataSetDensityReport();

        FusibleDataSet<Player, Attribute> dsPrediction = new FusibleHashedDataSet<>();
        new PlayerXMLReader().loadFromXML(new File("data/input/prediction_players.xml"), "/players/player", dsPrediction);
        dsPrediction.printDataSetDensityReport();

        FusibleDataSet<Player, Attribute> dsFifa = new FusibleHashedDataSet<>();
        new PlayerXMLReader().loadFromXML(new File("data/input/fifa_players.xml"), "/players/player", dsFifa);
        dsFifa.printDataSetDensityReport();

        // Scores (e.g. from rating)
        dsReal.setScore(3.0);
        dsPrediction.setScore(1.0);
        dsFifa.setScore(2.0);

        // Date (e.g. last update)
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd")
                .parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter(Locale.ENGLISH);

        dsReal.setDate(LocalDateTime.parse("2019-03-15", formatter));
        dsPrediction.setDate(LocalDateTime.parse("2018-12-31", formatter));
        dsFifa.setDate(LocalDateTime.parse("2018-12-21", formatter));

        // load correspondences
        System.out.println("*\n*\tLoading correspondences\n*");
        CorrespondenceSet<Player, Attribute> correspondences = new CorrespondenceSet<>();
        correspondences.loadCorrespondences(new File("data/output/correspondences/real_2_prediction_correspondences.csv"),dsReal, dsPrediction);
        correspondences.loadCorrespondences(new File("data/output/correspondences/real_2_fifa_correspondences.csv"),dsReal, dsFifa);
        correspondences.loadCorrespondences(new File("data/output/correspondences/prediction_2_fifa_correspondences.csv"),dsPrediction, dsFifa);

        correspondences.printGroupSizeDistribution();

        // load the gold standard
        System.out.println("*\n*\tEvaluating results\n*");
        DataSet<Player, Attribute> gs = new FusibleHashedDataSet<>();
        new PlayerXMLReader().loadFromXML(new File("data/goldstandard/fused.xml"), "/players/player", gs);

        for(Player p : gs.get()) {
            System.out.println(String.format("gs: %s", p.getIdentifier()));
        }

        // define the fusion strategy
        DataFusionStrategy<Player, Attribute> strategy = new DataFusionStrategy<>(new PlayerXMLReader());
        // write debug results to file
        strategy.activateDebugReport("data/output/debugResultsDatafusion.csv", 100000000, gs);

        // add attribute fusers
        strategy.addAttributeFuser(Player.NAME, new NameFuserLongestString(),new NameEvaluationRule());
        strategy.addAttributeFuser(Player.BIRTHDATE, new BirthDateFuserFavourSource(), new BirthDateEvaluationRule());
        strategy.addAttributeFuser(Player.BIRTHPLACE, new BirthPlaceFuserFavourSource(),new BirthPlaceEvaluationRule());
        strategy.addAttributeFuser(Player.CLUB, new ClubFuserMostRecent(),new ClubEvaluationRule());
        strategy.addAttributeFuser(Player.CONTRACTEXP, new ContractExpFuserFavourSource(),new ContractExpEvaluationRule());
        strategy.addAttributeFuser(Player.COMPETITIONS, new PlayerCompetitionsFuser(), new PlayerCompetitionsEvaluationRule());

        strategy.addAttributeFuser(Player.POSITIONS,new PositionsFuserUnion(), new PositionsEvaluationRule());
        strategy.addAttributeFuser(Player.POTENTIAL,new PotentialFuserFavourSources(), new PotentialEvaluationRule());
        strategy.addAttributeFuser(Player.RELEASECLAUSE, new ReleaseCluaseFuserFavourSources(), new ReleaseCluaseEvaluationRule());
        strategy.addAttributeFuser(Player.WAGE, new WageFuserFavourSources(), new WageEvaluationRule());
        strategy.addAttributeFuser(Player.STRONGFOOT, new StrongFootFuserVoting(), new StrongFootEvaluationRule());

        strategy.addAttributeFuser(Player.ESTMARKETVALUE18,new EstimatedVal18FuserLongestString(),new EstimatedValEvaluationRule());
        strategy.addAttributeFuser(Player.KITNUMBER,new KitNumberFuserFavourSource(),new KitNumberEvaluationRule());
        strategy.addAttributeFuser(Player.MARKETVALUE19,new MarketValueFuserLongestString(),new MarketValueEvaluationRule());
        strategy.addAttributeFuser(Player.NATIONALITY,new NationalityFuserFavourSource(),new NationalityEvaluationRule());
        strategy.addAttributeFuser(Player.OVERALL,new OverallFuserLongestString(),new OverallEvaluationRule());

        // create the fusion engine
        DataFusionEngine<Player, Attribute> engine = new DataFusionEngine<>(strategy);

        // print consistency report
        engine.printClusterConsistencyReport(correspondences, null);

        // print record groups sorted by consistency
        engine.writeRecordGroupsByConsistency(new File("data/output/recordGroupConsistencies.csv"), correspondences, null);

        // run the fusion
        System.out.println("*\n*\tRunning data fusion\n*");
        FusibleDataSet<Player, Attribute> fusedDataSet = engine.run(correspondences, null);

        // write the result
        new PlayerXMLFormatter().writeXML(new File("data/output/fused.xml"), fusedDataSet);

        // evaluate
        DataFusionEvaluator<Player, Attribute> evaluator = new DataFusionEvaluator<>(strategy, new RecordGroupFactory<Player, Attribute>());

        double accuracy = evaluator.evaluate(fusedDataSet, gs, null);

        System.out.println(String.format("Accuracy: %.2f", accuracy));

        FusibleDataSet<Player, Attribute> dsfusi = new FusibleHashedDataSet<>();
        new PlayerXMLReader().loadFromXML(new File("data/output/fused.xml"), "/players/player", dsfusi);
        dsfusi.printDataSetDensityReport();
    }
}


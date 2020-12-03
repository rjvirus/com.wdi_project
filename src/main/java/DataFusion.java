import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEngine;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEvaluator;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionStrategy;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleDataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleHashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroupFactory;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import evaluation.NameEvaluationRule;
import fusers.NameFuserLongestString;
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
        dsReal.setScore(2.0);
        dsPrediction.setScore(1.0);
        dsFifa.setScore(3.0);

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
        correspondences.getRecordGroups();
    }
}

// add attribute fusers //TODO: fix and add evaluators and fusers
        //strategy.addAttributeFuser(Player.NAME, new NameFuserLongestString(),new NameEvaluationRule());
        /*strategy.addAttributeFuser(Player.BIRTHDATE,new DirectorFuserLongestString(), new DirectorEvaluationRule());
        strategy.addAttributeFuser(Player.BIRTHPLACE, new DateFuserFavourSource(),new DateEvaluationRule());
        strategy.addAttributeFuser(Player.CLUB,new ActorsFuserUnion(),new ActorsEvaluationRule());
        strategy.addAttributeFuser(Player.COMPETITIONS,new ActorsFuserUnion(),new ActorsEvaluationRule());
        strategy.addAttributeFuser(Player.CONTRACTEXP,new ActorsFuserUnion(),new ActorsEvaluationRule());

        mert
        strategy.addAttributeFuser(Player.ESTMARKETVALUE18,new ActorsFuserUnion(),new ActorsEvaluationRule());
        strategy.addAttributeFuser(Player.KITNUMBER,new ActorsFuserUnion(),new ActorsEvaluationRule());
        strategy.addAttributeFuser(Player.LASTINJURY,new ActorsFuserUnion(),new ActorsEvaluationRule());
        strategy.addAttributeFuser(Player.MARKETVALUE19,new ActorsFuserUnion(),new ActorsEvaluationRule());
        strategy.addAttributeFuser(Player.NATIONALITY,new ActorsFuserUnion(),new ActorsEvaluationRule());
        strategy.addAttributeFuser(Player.OVERALL,new ActorsFuserUnion(),new ActorsEvaluationRule());

        kai
        strategy.addAttributeFuser(Player.POSITIONS,new ActorsFuserUnion(),new ActorsEvaluationRule());
        strategy.addAttributeFuser(Player.POTENTIAL,new ActorsFuserUnion(),new ActorsEvaluationRule());
        strategy.addAttributeFuser(Player.RELEASECLAUSE,new ActorsFuserUnion(),new ActorsEvaluationRule());
        strategy.addAttributeFuser(Player.STRONGFOOT,new ActorsFuserUnion(),new ActorsEvaluationRule());
        strategy.addAttributeFuser(Player.WAGE,new ActorsFuserUnion(),new ActorsEvaluationRule());*/

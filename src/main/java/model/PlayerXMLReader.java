package model;

import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleFactory;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

public class PlayerXMLReader extends XMLMatchableReader<Player, Attribute> implements
        FusibleFactory<Player, Attribute> {

    protected void initialiseDataset(DataSet<Player, Attribute> dataset) {
        super.initialiseDataset(dataset);
        // the schema is defined in the Movie class and not interpreted from the file, so we have to set the attributes manually

        dataset.addAttribute(Player.NAME);
        dataset.addAttribute(Player.BIRTHDATE);
        dataset.addAttribute(Player.BIRTHPLACE);
        dataset.addAttribute(Player.CLUB);
        dataset.addAttribute(Player.COMPETITIONS);
        dataset.addAttribute(Player.CONTRACTEXP);
        dataset.addAttribute(Player.ESTMARKETVALUE18);
        dataset.addAttribute(Player.KITNUMBER);
        dataset.addAttribute(Player.MARKETVALUE19);
        dataset.addAttribute(Player.NATIONALITY);
        dataset.addAttribute(Player.POSITIONS);
        dataset.addAttribute(Player.RELEASECLAUSE);
        dataset.addAttribute(Player.POTENTIAL);
        dataset.addAttribute(Player.STRONGFOOT);
        dataset.addAttribute(Player.WAGE);
    }

    @Override
    public Player createModelFromElement(Node node, String provenanceInfo) {
        String id = getValueFromChildElement(node, "id");

        Player player = new Player(id, provenanceInfo);

        // fill the attributes
        player.setName(StringUtils.stripAccents(getValueFromChildElement(node, "name")));
        player.setNationality(getValueFromChildElement(node, "nationality"));
        player.setClub(getValueFromChildElement(node, "club"));

        String strong_foot = getValueFromChildElement(node, "strong_foot");
        if (strong_foot != null && !strong_foot.isEmpty()) {
            player.setStrong_foot(strong_foot);
        }
        String birth_place = getValueFromChildElement(node, "birth_place");
        if (birth_place != null && !birth_place.isEmpty()) {
            player.setBirth_place(birth_place);
        }
        String kit_number = getValueFromChildElement(node, "kit_number");
        if (kit_number != null && !kit_number.isEmpty()) {
            player.setKit_number(Integer.parseInt(kit_number));
        }
        String wage = getValueFromChildElement(node, "wage");
        if (wage != null && !wage.isEmpty()) {
            player.setWage(Integer.parseInt(wage));
        }
        String market_value_19 = getValueFromChildElement(node, "market_value_19");
        if (market_value_19 != null && !market_value_19.isEmpty()) {
            player.setMarket_value_19(Integer.parseInt(market_value_19));
        }
        String est_market_value_18 = getValueFromChildElement(node, "est_market_value_18");
        if (est_market_value_18 != null && !est_market_value_18.isEmpty()) {
            player.setEst_market_value_18(Integer.parseInt(est_market_value_18));
        }
        String release_clause = getValueFromChildElement(node, "release_clause");
        if (release_clause != null && !release_clause.isEmpty()) {
            player.setRelease_clause(Integer.parseInt(release_clause));
        }
        String overall = getValueFromChildElement(node, "overall");
        if (overall != null && !overall.isEmpty()) {
            player.setOverall(Integer.parseInt(overall));
        }
        String potential = getValueFromChildElement(node, "potential");
        if (potential != null && !potential.isEmpty()) {
            player.setPotential(Integer.parseInt(potential));
        }
        String birth_date = getValueFromChildElement(node, "birth_date");
        if (birth_date != null && !birth_date.isEmpty()) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd").toFormatter(Locale.ENGLISH);
            LocalDate dt = LocalDate.parse(birth_date, formatter);
            player.setBirth_date(dt);
        }
        String contract_exp = getValueFromChildElement(node, "contract_exp");
        if (contract_exp != null && !contract_exp.isEmpty()) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd").toFormatter(Locale.ENGLISH);
            LocalDate dt = LocalDate.parse(contract_exp, formatter);
            player.setContract_exp(dt);
        }

        String competitions = getValueFromChildElement(node, "competitions");
        if(competitions != null) {
            String competitions1 = competitions.replaceAll("\\s+"," ");
            List<String> competitionsFinal = new ArrayList<String>(Arrays.asList(competitions1.split(" ")));
            player.setCompetitions(competitionsFinal);
        }

        String positions1 = getValueFromChildElement(node, "positions");
        if(competitions != null) {
            String positions2 = positions1.replaceAll("\\s+"," ");
            List<String> positions1Final = new ArrayList<String>(Arrays.asList(positions2.split(" ")));
            player.setPositions(positions1Final);
        }
        return player;
    }

    @Override
    public Player createInstanceForFusion(RecordGroup<Player, Attribute> cluster) {
        List<String> ids = new LinkedList<>();

        for (Player p : cluster.getRecords()) {
            ids.add(p.getIdentifier());
        }

        Collections.sort(ids);

        String mergedId = StringUtils.join(ids, '+');

        return new Player(mergedId, "fused");
    }
}

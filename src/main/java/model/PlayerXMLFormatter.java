package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.uni_mannheim.informatik.dws.winter.model.io.XMLFormatter;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PlayerXMLFormatter extends XMLFormatter<Player> {

    @Override
    public Element createRootElement(Document doc) {
        return doc.createElement("players");
    }

    @Override
    public Element createElementFromRecord(Player record, Document doc) {
        Element player = doc.createElement("player");

        player.appendChild(createTextElement("id", record.getIdentifier(), doc));

        player.appendChild(createTextElementWithProvenance("name", record.getName(),
                record.getMergedAttributeProvenance(Player.NAME), doc));

        player.appendChild(createTextElementWithProvenance("birth_place", record.getBirth_place(),
                record.getMergedAttributeProvenance(Player.BIRTHPLACE), doc));

        if (record.getBirth_date() != null) {
            player.appendChild(createTextElementWithProvenance("birth_date", record.getBirth_date().toString(),
                    record.getMergedAttributeProvenance(Player.BIRTHDATE), doc));
        }

        player.appendChild(createTextElementWithProvenance("nationality", record.getNationality(),
                record.getMergedAttributeProvenance(Player.NATIONALITY), doc));

        player.appendChild(createTextElementWithProvenance("club", record.getClub(),
                record.getMergedAttributeProvenance(Player.CLUB), doc));

        if (record.getContract_exp() != null) {
            player.appendChild(createTextElementWithProvenance("contract_exp", record.getContract_exp().toString(),
                    record.getMergedAttributeProvenance(Player.CONTRACTEXP), doc));
        }
        // if(!record.getPositions().isEmpty()) {
        // player.appendChild(createPositionsElement(record, doc));
        // }
        player.appendChild(createTextElementWithProvenance("potential", Integer.toString(record.getPotential()),
                record.getMergedAttributeProvenance(Player.POTENTIAL), doc));
        if (!Integer.toString(record.getRelease_clause()).isEmpty()) {
            player.appendChild(
                    createTextElementWithProvenance("release_clause", Integer.toString(record.getRelease_clause()),
                            record.getMergedAttributeProvenance(Player.RELEASECLAUSE), doc));
        }
        if (!Integer.toString(record.getWage()).isEmpty()) {
            player.appendChild(createTextElementWithProvenance("wage", Integer.toString(record.getWage()),
                    record.getMergedAttributeProvenance(Player.WAGE), doc));
        }
        if (!Integer.toString(record.getMarket_value_19()).isEmpty()) {
            player.appendChild(createTextElementWithProvenance("market_value_19", Integer.toString(record.getMarket_value_19()),
                    record.getMergedAttributeProvenance(Player.MARKETVALUE19), doc));
        }
        if (!Integer.toString(record.getEst_market_value_18()).isEmpty()) {
            player.appendChild(createTextElementWithProvenance("est_market_value_18", Integer.toString(record.getEst_market_value_18()),
                    record.getMergedAttributeProvenance(Player.ESTMARKETVALUE18), doc));
        }
        if (!Integer.toString(record.getKit_number()).isEmpty()) {
            player.appendChild(createTextElementWithProvenance("kit_number", Integer.toString(record.getKit_number()),
                    record.getMergedAttributeProvenance(Player.KITNUMBER), doc));
        }
        if (!Integer.toString(record.getOverall()).isEmpty()) {
            player.appendChild(createTextElementWithProvenance("overall", Integer.toString(record.getOverall()),
                    record.getMergedAttributeProvenance(Player.OVERALL), doc));
        }
        if (record.getStrong_foot() != null) {
            player.appendChild(createTextElementWithProvenance("strong_foot", record.getStrong_foot(),
                    record.getMergedAttributeProvenance(Player.STRONGFOOT), doc));
        }

        List<String> competitions = record.getCompetitions();
        if (competitions != null && !competitions.isEmpty()) {
            Element competitions1 = doc.createElement("competitions");
            for (int i = 0; i < record.getCompetitions().size(); i++) {
                competitions1
                        .appendChild(createTextElementWithProvenance("competition", record.getCompetitions().get(i),
                                record.getMergedAttributeProvenance(Player.COMPETITIONS), doc));
            }
            player.appendChild(competitions1);
        }

        if (!record.getPositions().isEmpty()) {
            List<String> positions = record.getPositions();
            if (positions != null && !positions.isEmpty()) {
                Element positions1 = doc.createElement("positions");
                for (int i = 0; i < record.getPositions().size(); i++) {
                    positions1.appendChild(createTextElementWithProvenance("positions", record.getPositions().get(i),
                            record.getMergedAttributeProvenance(Player.COMPETITIONS), doc));
                }
                player.appendChild(positions1);
            }
        }

        return player;
    }

    protected Element createTextElementWithProvenance(String name, String value, String provenance, Document doc) {
        Element elem = createTextElement(name, value, doc);
        elem.setAttribute("provenance", provenance);
        return elem;

    }

}

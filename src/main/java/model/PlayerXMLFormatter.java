package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.uni_mannheim.informatik.dws.winter.model.io.XMLFormatter;

import java.time.format.DateTimeFormatter;

public class PlayerXMLFormatter extends XMLFormatter<Player> {

    @Override
    public Element createRootElement(Document doc) {
        return doc.createElement("players");
    }

    @Override
    public Element createElementFromRecord(Player record, Document doc) {
        Element player = doc.createElement("player");

        player.appendChild(createTextElement("id", record.getIdentifier(), doc));

        player.appendChild(createTextElementWithProvenance("name",
                record.getName(),
                record.getMergedAttributeProvenance(Player.NAME), doc));
        player.appendChild(createTextElementWithProvenance("birth_place",
                record.getBirth_place(),
                record.getMergedAttributeProvenance(Player.BIRTHPLACE), doc));
        if(record.getBirth_date() != null) {
            player.appendChild(createTextElementWithProvenance("birth_date",
                    record.getBirth_date().toString(),
                    record.getMergedAttributeProvenance(Player.BIRTHDATE), doc));
        }

        player.appendChild(createTextElementWithProvenance("club",
                record.getClub(),
                record.getMergedAttributeProvenance(Player.CLUB), doc));

        if(record.getContract_exp() != null) {
            player.appendChild(createTextElementWithProvenance("contract_exp",
                    record.getContract_exp().toString(),
                    record.getMergedAttributeProvenance(Player.CONTRACTEXP), doc));
        }
        if(!record.getPositions().isEmpty()) {
            player.appendChild(createPositionsElement(record, doc));
        }
        player.appendChild(createTextElementWithProvenance("potential",Integer.toString(record.getPotential()), record.getMergedAttributeProvenance(Player.POTENTIAL), doc));
        if(!Integer.toString(record.getRelease_clause()).isEmpty()) {
            player.appendChild(createTextElementWithProvenance("release_clause", Integer.toString(record.getRelease_clause()), record.getMergedAttributeProvenance(Player.RELEASECLAUSE), doc));
        }
        if(!Integer.toString(record.getWage()).isEmpty()) {
            player.appendChild(createTextElementWithProvenance("wage", Integer.toString(record.getWage()), record.getMergedAttributeProvenance(Player.WAGE), doc));
        }
        if(record.getStrong_foot() != null) {
            player.appendChild(createTextElementWithProvenance("strong_foot", record.getStrong_foot(), record.getMergedAttributeProvenance(Player.STRONGFOOT), doc));
        }

        if(!record.getPositions().isEmpty()) {
            player.appendChild(createPositionsElement(record, doc));
        }

        //TODO: add more attributes

        return player;
    }

    protected Element createTextElementWithProvenance(String name,
                                                      String value, String provenance, Document doc) {
        Element elem = createTextElement(name, value, doc);
        elem.setAttribute("provenance", provenance);
        return elem;

    }

    protected Element createPositionsElement(Player record, Document doc) {
        PositionXMLFormatter positionFormatter = new PositionXMLFormatter();
        Element actorRoot = positionFormatter.createRootElement(doc);
        actorRoot.setAttribute("provenance",
                record.getMergedAttributeProvenance(Player.POSITIONS));
        if (record.getPositions() !=  null) {
            for (String p : record.getPositions()) {
                actorRoot.appendChild(positionFormatter
                        .createElementFromRecord(p, doc));
            }
        }
        return actorRoot;
    }

}


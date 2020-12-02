package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.uni_mannheim.informatik.dws.winter.model.io.XMLFormatter;

public class PlayerXMLFormatter extends XMLFormatter<Player> {

    @Override
    public Element createRootElement(Document doc) {
        return doc.createElement("movies");
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
        player.appendChild(createTextElementWithProvenance("birth_date", record
                .getBirth_date().toString(), record
                .getMergedAttributeProvenance(Player.BIRTHDATE), doc));

        //TODO: add more attributes

        return player;
    }

    protected Element createTextElementWithProvenance(String name,
                                                      String value, String provenance, Document doc) {
        Element elem = createTextElement(name, value, doc);
        elem.setAttribute("provenance", provenance);
        return elem;
    }

}


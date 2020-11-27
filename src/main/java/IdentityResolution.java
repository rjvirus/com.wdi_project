import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import model.Player;
import model.PlayerXMLReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

public class IdentityResolution {
    public static void main(String args[]) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {

        HashedDataSet<Player, Attribute> dataPredictionPlayers = new HashedDataSet<>();
        HashedDataSet<Player, Attribute> dataRealPlayers = new HashedDataSet<>();
        HashedDataSet<Player, Attribute> dataFifaPlayers = new HashedDataSet<>();
        new PlayerXMLReader().loadFromXML(new File("data/input/prediction_players.xml"),"/players/player", dataPredictionPlayers);
        new PlayerXMLReader().loadFromXML(new File("data/input/real_market_players.xml"),"/players/player", dataRealPlayers);
        new PlayerXMLReader().loadFromXML(new File("data/input/fifa_players.xml"),"/players/player", dataFifaPlayers);
        
        System.out.println("Hello world");
    }
}

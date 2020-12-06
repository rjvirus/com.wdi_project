package evaluation;

import java.util.HashSet;
import java.util.Set;


import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import model.Player;

public class CompetitionsEvaluationRule extends EvaluationRule<Player, Attribute>{

    @Override
    public boolean isEqual(Player record1, Player record2, Attribute schemaElement) {
        Set<String> actors1 = new HashSet<>();
        if (record1.getCompetitions() != null) {
            for (String p : record1.getCompetitions()) {

                actors1.add(p);
            }
        }
        Set<String> actors2 = new HashSet<>();
        if (record2.getCompetitions() != null) {
            for (String p : record2.getCompetitions()) {
                actors2.add(p);
            }
        }

        return actors1.containsAll(actors2) && actors2.containsAll(actors1);
    }

    @Override
    public boolean isEqual(Player record1, Player record2,
                           Correspondence<Attribute, Matchable> schemaCorrespondence) {
        return isEqual(record1, record2, (Attribute)null);
    }
}


package evaluation;
import java.util.HashSet;
import java.util.Set;


import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import model.Player;

public class PositionsEvaluationRule extends EvaluationRule<Player, Attribute>{

    @Override
    public boolean isEqual(Player record1, Player record2, Attribute schemaElement) {
        Set<String> actors1 = new HashSet<>();
        if (record1.getPositions() != null) {
            for (String p : record1.getPositions()) {

                actors1.add(p);
            }
        }
        Set<String> actors2 = new HashSet<>();
        if (record2.getPositions() != null) {
            for (String p : record2.getPositions()) {
                actors2.add(p);
            }
        }

        return actors1.containsAll(actors2) && actors2.containsAll(actors1);
    }

    /* (non-Javadoc)
     * @see de.uni_mannheim.informatik.wdi.datafusion.EvaluationRule#isEqual(java.lang.Object, java.lang.Object, de.uni_mannheim.informatik.wdi.model.Correspondence)
     */
    @Override
    public boolean isEqual(Player record1, Player record2,
                           Correspondence<Attribute, Matchable> schemaCorrespondence) {
        return isEqual(record1, record2, (Attribute)null);
    }
}

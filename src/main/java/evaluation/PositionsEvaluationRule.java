package evaluation;
import java.util.*;


import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import model.Player;

public class PositionsEvaluationRule extends EvaluationRule<Player, Attribute>{

    @Override
    public boolean isEqual(Player record1, Player record2, Attribute schemaElement) {
        // Set<String> positions1 = new HashSet<>();
        Collection positions1 = new ArrayList();
        if (record1.getPositions() != null) {
            for (String p : record1.getPositions()) {

                positions1.add(p);
            }
        }
        //Set<String> positions2 = new HashSet<>();
        Collection positions2 = new ArrayList();
        if (record2.getPositions() != null) {
            for (String p : record2.getPositions()) {
                positions2.add(p);
            }
        }


        positions1.retainAll( positions2 );
        //System.out.println( positions1.size() );
        return positions1.size()>0;
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

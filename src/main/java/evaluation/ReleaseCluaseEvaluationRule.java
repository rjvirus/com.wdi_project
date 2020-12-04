package evaluation;

import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import model.Player;

public class ReleaseCluaseEvaluationRule extends EvaluationRule<Player, Attribute> {
    @Override
    public boolean isEqual(Player record1, Player record2, Attribute schemaElement) {
        if(record1.getRelease_clause()> ((record2.getRelease_clause()/100)*90)&& record1.getRelease_clause()<(record2.getRelease_clause()/100)*110)
            return true;
        else
            return false;
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

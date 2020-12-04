package evaluation;

import model.Player;

import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class PotentialEvaluationRule extends EvaluationRule<Player, Attribute>{
    @Override
    public boolean isEqual(Player record1, Player record2, Attribute schemaElement) {
        if(record1.getPotential()> (record2.getPotential()-2)&& record1.getPotential()<record2.getPotential()+2)
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

package fusers;

import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.meta.MostRecent;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import model.Player;

import java.util.List;

public class LastInjuryFuserMostRecent extends AttributeValueFuser<String, Player, Attribute> {

    public LastInjuryFuserMostRecent () {
        super(new MostRecent<String, Player, Attribute>());
    }

    @Override
    public boolean hasValue(Player record, Correspondence<Attribute, Matchable> correspondence) {
        return record.hasValue(Player.LASTINJURY);
    }

    @Override
    public String getValue(Player record, Correspondence<Attribute, Matchable> correspondence) {
        return record.getLast_injury();
    }

    @Override
    public void fuse(RecordGroup<Player, Attribute> group, Player fusedRecord, Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
        FusedValue<String, Player, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
        fusedRecord.setLast_injury(fused.getValue());
        fusedRecord.setAttributeProvenance(Player.LASTINJURY, fused.getOriginalIds());
    }

}

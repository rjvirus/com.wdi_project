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

public class ClubFuserMostRecent extends AttributeValueFuser<String, Player, Attribute> {

    public ClubFuserMostRecent() {
        super(new MostRecent<String, Player, Attribute>());
    }

    @Override
    public boolean hasValue(Player record, Correspondence<Attribute, Matchable> correspondence) {
        return record.hasValue(Player.CLUB);
    }

    @Override
    public String getValue(Player record, Correspondence<Attribute, Matchable> correspondence) {
        return record.getClub();
    }

    @Override
    public void fuse(RecordGroup<Player, Attribute> group, Player fusedRecord, Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
        FusedValue<String, Player, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
        fusedRecord.setClub(fused.getValue());
        fusedRecord.setAttributeProvenance(Player.CLUB, fused.getOriginalIds());
    }

}

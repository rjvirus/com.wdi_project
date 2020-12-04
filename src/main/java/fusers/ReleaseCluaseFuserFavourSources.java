package fusers;


import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.meta.FavourSources;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import model.Player;

public class ReleaseCluaseFuserFavourSources extends  AttributeValueFuser<Double, Player, Attribute> {
    public ReleaseCluaseFuserFavourSources() {
        super(new FavourSources<Double, Player, Attribute>());
    }

    @Override
    public boolean hasValue(Player record, Correspondence<Attribute, Matchable> correspondence) {
        return record.hasValue(Player.RELEASECLAUSE);
    }

    @Override
    public Double getValue(Player record, Correspondence<Attribute, Matchable> correspondence) {
        return new Double(record.getRelease_clause());
    }

    @Override
    public void fuse(RecordGroup<Player, Attribute> group, Player fusedRecord, Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
        FusedValue<Double, Player, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
        if (fused.getValue() != null) {
            fusedRecord.setRelease_clause((int) Math.round(fused.getValue()));
        }
        fusedRecord.setAttributeProvenance(Player.RELEASECLAUSE, fused.getOriginalIds());
    }
}

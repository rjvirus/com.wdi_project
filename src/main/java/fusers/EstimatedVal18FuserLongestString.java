package fusers;

import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.string.LongestString;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import model.Player;

public class EstimatedVal18FuserLongestString extends AttributeValueFuser<String, Player, Attribute> {

    public EstimatedVal18FuserLongestString() {
        super(new LongestString<>());
    }

    @Override
    public boolean hasValue(Player record, Correspondence<Attribute, Matchable> correspondence) {
        return record.hasValue(Player.ESTMARKETVALUE18);
    }

    @Override
    public String getValue(Player record, Correspondence<Attribute, Matchable> correspondence) {
        if (hasValue(record, correspondence)){
            return Integer.toString(record.getEst_market_value_18());
        }else {
            return "0";
        }
    }

    @Override
    public void fuse(RecordGroup<Player, Attribute> group, Player fusedRecord, Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
        FusedValue<String, Player, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
        fusedRecord.setEst_market_value_18(Integer.parseInt(fused.getValue()));
        fusedRecord.setAttributeProvenance(Player.ESTMARKETVALUE18,
                fused.getOriginalIds());
    }

}

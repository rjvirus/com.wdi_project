
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

import java.time.LocalDate;

public class DateFuserFavourSource extends AttributeValueFuser<LocalDate, Player, Attribute> {

	public DateFuserFavourSource() {
		super(new FavourSources<LocalDate, Player, Attribute>());
	}

	@Override
	public boolean hasValue(Player record, Correspondence<Attribute, Matchable> correspondence) {
		return record.hasValue(Player.BIRTHDATE);
	}

	@Override
	public LocalDate getValue(Player record, Correspondence<Attribute, Matchable> correspondence) {
		return record.getBirth_date();
	}

	@Override
	public void fuse(RecordGroup<Player, Attribute> group, Player fusedRecord, Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
		FusedValue<LocalDate, Player, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
		fusedRecord.setBirth_date(fused.getValue());
		fusedRecord.setAttributeProvenance(Player.BIRTHDATE, fused.getOriginalIds());
	}

}

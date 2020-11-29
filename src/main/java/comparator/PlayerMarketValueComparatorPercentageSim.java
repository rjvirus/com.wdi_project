package comparator;

import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.numeric.PercentageSimilarity;
import model.Player;


public class PlayerMarketValueComparatorPercentageSim implements Comparator<Player, Attribute> {

    private static final long serialVersionUID = 1L;
    PercentageSimilarity sim = new PercentageSimilarity(5.0);

    private ComparatorLogger comparisonLog;

    @Override
    public double compare(Player record1, Player record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
        double d1 = record1.getMarket_value_19();
        double d2 = record2.getMarket_value_19();

        double similarity = sim.calculate(d1, d2);

        if(this.comparisonLog != null){
            this.comparisonLog.setComparatorName(getClass().getName());
            this.comparisonLog.setRecord1Value(String.valueOf(d1));
            this.comparisonLog.setRecord2Value(String.valueOf(d2));
            this.comparisonLog.setSimilarity(Double.toString(similarity));
        }

        return similarity;
    }

    @Override
    public ComparatorLogger getComparisonLog() {
        return this.comparisonLog;
    }

    @Override
    public void setComparisonLog(ComparatorLogger comparatorLog) {
        this.comparisonLog = comparatorLog;
    }

}
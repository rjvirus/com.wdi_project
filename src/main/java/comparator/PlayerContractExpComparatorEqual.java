package comparator;

import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.EqualsSimilarity;
import model.Player;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PlayerContractExpComparatorEqual implements Comparator<Player, Attribute> {
    private static final long serialVersionUID = 1L;
    private EqualsSimilarity<String> sim = new EqualsSimilarity<String>();

    private ComparatorLogger comparisonLog;

    @Override
    public double compare(Player record1, Player record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
        LocalDate record1ContractExp = record1.getContract_exp();
        LocalDate record2ContractExp = record2.getContract_exp();

        if(record1ContractExp != null && record2ContractExp != null) {
            String s1 = record1ContractExp.format(DateTimeFormatter.ofPattern("yyyy"));
            String s2 = record2ContractExp.format(DateTimeFormatter.ofPattern("yyyy"));;
        if (s1.equals("1582") || s2.equals("1582")){
                return 0;
        }
            double similarity = sim.calculate(s1, s2);

            if(this.comparisonLog != null){
                this.comparisonLog.setComparatorName(getClass().getName());

                this.comparisonLog.setRecord1Value(s1);
                this.comparisonLog.setRecord2Value(s2);

                this.comparisonLog.setSimilarity(Double.toString(similarity));
            }
            return similarity;
        } else {
            return 0;
        }
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

package comparator;

import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.TokenizingJaccardSimilarity;
import model.Player;

import java.util.List;

public class PlayerPositionComparatorJaccard implements Comparator<Player, Attribute> {
    private static final long serialVersionUID = 1L;
    TokenizingJaccardSimilarity sim = new TokenizingJaccardSimilarity();

    private ComparatorLogger comparisonLog;

    @Override
    public double compare(Player record1, Player record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
        List<String> s1L = record1.getPositions();
        List<String> s2 = record2.getPositions();
        if (s1L != null && s2 != null) {
            String s1 = s1L.get(0);
            for (int posNumb = 0; posNumb < s2.size(); posNumb++) {
                String chars = "";
                for (int i = 0; i < s2.get(posNumb).length(); i++) {
                    if (Character.isUpperCase(s2.get(posNumb).charAt(i))) {
                        chars = chars + s2.get(posNumb).charAt(i);
                    }
                }
                s2.set(posNumb, chars);
            }
            double maxVal = 0;
            String s2S = "";
            for (int i = 0; i < s2.size(); i++) {
                double similarity = sim.calculate(s1, s2.get(i));
                if (similarity > maxVal) {
                    maxVal = similarity;
                    s2S = s2.get(i);
                }
            }

            if (this.comparisonLog != null) {
                this.comparisonLog.setComparatorName(getClass().getName());

                this.comparisonLog.setRecord1Value(s1);
                this.comparisonLog.setRecord2Value(s2S);

                this.comparisonLog.setSimilarity(Double.toString(maxVal));
            }

            return maxVal;
        } else{
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

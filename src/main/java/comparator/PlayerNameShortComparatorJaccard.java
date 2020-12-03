package comparator;

import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.JaccardOnNGramsSimilarity;
import model.Player;

public class PlayerNameShortComparatorJaccard implements Comparator<Player, Attribute> {

    private static final long serialVersionUID = 1L;
    JaccardOnNGramsSimilarity sim = new JaccardOnNGramsSimilarity(3);

    private ComparatorLogger comparisonLog;

    @Override
    public double compare(Player record1, Player record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
        String s1 = record1.getName();
        String s2 = record2.getName();

        String[] s1Parts = s1.split("(?=\\p{Upper})");
        String[] s2Parts = s2.split("(?=\\p{Upper})");

        for (int i = 0; i < s1Parts.length; i++) {
            if(i == 0) {
                if(s2Parts[0].length() > 3) {
                    s1 = s1Parts[0];
                } else {
                    s1 = s1Parts[0].charAt(0) + ".";
                }
            } else {
                s1 += " " + s1Parts[i].trim();
            }
        }

        double similarity = sim.calculate(s1, s2);

        if(this.comparisonLog != null){
            this.comparisonLog.setComparatorName(getClass().getName());

            this.comparisonLog.setRecord1Value(s1);
            this.comparisonLog.setRecord2Value(s2);

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

package kz.ilotterytea.javaextra.comparators;

import java.util.Comparator;
import java.util.Map;


// Cv pasted from https://stackoverflow.com/a/53081966
public class MapValueKeyComparator<K extends Comparable<? super K>, V extends Comparable<? super V>>
        implements Comparator<Map.Entry<K, V>> {

    public int compare(Map.Entry<K, V> a, Map.Entry<K, V> b) {
        int cmp1 = b.getValue().compareTo(a.getValue()); //can reverse a and b position for ascending/descending ordering
        if (cmp1 != 0) {
            return cmp1;
        } else {
            return a.getKey().compareTo(b.getKey()); //can reverse a and b position for ascending/descending ordering
        }
    }

}

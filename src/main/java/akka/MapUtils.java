package akka;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {
    public static <T> Map<T, Integer> aggregate(Map<T, Integer> data1,
                                                Map<T, Integer> data2) {
        Map<T, Integer> aggregatedData = new HashMap<T, Integer>(data1);
        for (T key : data2.keySet()) {
            int existingFrequency = 0;
            if (aggregatedData.containsKey(key)) {
                existingFrequency = aggregatedData.get(key);
            }
            aggregatedData.put(key, existingFrequency + data2.get(key));
        }
        return aggregatedData;
    }
}
package thesis.antlersolver.statistics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Statistics {

    private static Statistics stat;
    public Map<String, Long> info;
    private final int pad = 25;

    private Statistics() {
        info = new HashMap<>();
    }

    public static Statistics getStat()
    {
        if (stat == null)
            stat = new Statistics();
        return stat;
    }

    public void set(String key, long value) {
        info.put(key, value);
    }

    public void count(String key, long add) {
        Long value = info.get(key);
        if(value == null) {
            info.put(key, add);
        } else {
            info.put(key, value+add);
        }
    }

    public void count(String key) {
        count(key, 1);
    }

    public void print() {
        String[] keys = info.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        for(String key : keys) {
            String padding = "";
            for(int i = key.length(); i < pad; i++) {
                padding += " ";
            }
            System.out.println(key+":"+padding+info.get(key));
        }
    }

    public static void reset() {
        getStat().info.clear();
    }
}

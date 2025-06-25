package thesis.antlersolver.statistics;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Statistics {

    private static Statistics stat;
    public Map<String, Long> info;
    private final int pad = 50;

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

    public void print(String path) {
        String[] keys = info.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        StringBuilder sb = new StringBuilder();
        for(String key : keys) {
            String padding = "";
            for(int i = key.length(); i < pad; i++) {
                padding += " ";
            }
            sb.append(key).append(":").append(padding).append(info.get(key)).append("\n");
        }
        try {
            FileWriter fw = new FileWriter(new File(path));
            fw.write(sb.toString());
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reset() {
        getStat().info.clear();
    }
}

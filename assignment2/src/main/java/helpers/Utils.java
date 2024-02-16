package helpers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Utils {
    public static final <T> Set<T> newHashSet(T... objs) {
        Set<T> set = new HashSet<T>();
        Collections.addAll(set, objs);
        return set;
    }

    public static String uniqueDirName(){
        LocalDateTime ldt = LocalDateTime.now();
        return String.format("%02d%02d%02d%02d", ldt.getDayOfMonth(),ldt.getHour(),ldt.getMinute(),ldt.getMinute());
    }
}

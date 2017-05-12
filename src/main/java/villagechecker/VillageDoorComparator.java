package villagechecker;

import java.util.Comparator;

/**
 * Created by yu on 2015/05/23.
 */
public class VillageDoorComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        VillageData v1 = (VillageData) o1;
        VillageData v2 = (VillageData) o2;

        return v2.numDoors - v1.numDoors;
    }
}
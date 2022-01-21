import java.util.HashMap;
import java.util.Map;

public class MapTest {
    public static void main(String[] args) {
//        Map<String, Integer> map = new HashMap<String, Integer>();
//        map.put("Ibrahim", 19);
//        System.out.println(map.values());

        String path = "./src/demoFiles/";
        String[] arr = {"p5", "flags", "large0", "power", "sfull", "noop"};
        Map<String, PCB> map = new HashMap<String, PCB>();
        for (int i = 0; i < arr.length; i++) {
            map.put(arr[i], new PCB(path + arr[i]));
        }
        System.out.println(map.get("power"));
    }
}

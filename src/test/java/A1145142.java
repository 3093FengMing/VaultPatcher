import java.util.HashMap;

public interface A1145142 {
    public static HashMap<String, String> map = new HashMap<String, String>(2) {
        {
            put("a", "v");
            put("s", "b");
        }
    };
    public static void __vp_init() {
        map.get("sss");
    }
}

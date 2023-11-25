import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

import java.util.HashMap;
import java.util.stream.IntStream;

public class Main {
    String abcc = "sssh";
    String abcdc = "ss2sh";

    private static final HashMap<String, String> __vp_map = new HashMap<String, String>(2222) {
        {
            put("aa", "bb");
        }
    };
    private void aaa(String abc) {
        System.out.println(abc);
        System.out.println(this.abcc);
        __vp_replace(this.abcc);
        __vp_replace("nams");
    }

//    public void main(String sss) {
//        String newSss = anotherMethod("text1", "text2");
//        __replaceMethod(newSss);
////        String c = "222a %s";
////        String f = StringUtils.capitalize(__replaceMethod(sss.name(), "1", "2a").toLowerCase());
////        int g = 999;
////        for (int i = 0; i < g; i++) {
////            String fc = i + "1";
////            System.out.println(fc);
////        }
////        String f = c.formatted(new Object[]{testMethod()});
////        System.out.println(__replaceMethod("1source", "1key", "1value"));
//        //System.out.println("f = " + f);
//        //String fc = testMethod(aa1().testMethod(enumGetName_1()));
//
//    }
//    private static String enumGetName_1() {return "target";}
//    private void testMethod(String a) {
//        this.abcc = anotherMethod(__replaceMethod(a), "");
//    }
//
//    private String anotherMethod(String s, String b) {
//        return s;
//    }
//
//    public static String __replaceMethod(String key) {
//        return "";
//    }
//
//    public static AbstractInsnNode __a(String[] a, String[] b) {
//        return new LdcInsnNode(1);
//    }
//    public Main aa1(){
//        return this;
//    }
//
//    //private static String __vp_replace(String source) {
//        return __vp_map.getOrDefault(source, source);
//    }

    private static String __vp_replace(Object source) {
        if (source == null) return null;
        return __vp_map.getOrDefault(source.toString(), source.toString());
    }
}

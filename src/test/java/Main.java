import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class Main {
    String abcc = "sssh";

    public void main(String sss) {
        String newSss = anotherMethod("text1", "text2");
        __replaceMethod(newSss);
//        String c = "222a %s";
//        String f = StringUtils.capitalize(__replaceMethod(sss.name(), "1", "2a").toLowerCase());
//        int g = 999;
//        for (int i = 0; i < g; i++) {
//            String fc = i + "1";
//            System.out.println(fc);
//        }
//        String f = c.formatted(new Object[]{testMethod()});
//        System.out.println(__replaceMethod("1source", "1key", "1value"));
        //System.out.println("f = " + f);
        //String fc = testMethod(aa1().testMethod(enumGetName_1()));

    }
    private static String enumGetName_1() {return "target";}
    private void testMethod(String a) {
        this.abcc = anotherMethod(__replaceMethod(a), "");
    }

    private String anotherMethod(String s, String b) {
        return s;
    }

    public static String __replaceMethod(String key) {
        return "";
    }

    public static AbstractInsnNode __a(String[] a, String[] b) {
        return new LdcInsnNode(1);
    }
    public Main aa1(){
        return this;
    }

    private static String __vp_replace(String source) {
        String[] keys = new String[]{"bbb"};
        String[] values = new String[]{"sss"};
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(source)) return values[i];
        }
        return source;
    }
}

package me.fengming.vpdummy.dummy;

import me.fengming.vpdummy.annotations.VpClassAnno;
import me.fengming.vpdummy.annotations.VpFieldAnno;

@VpClassAnno(
        value = "VP_TEST_CLASS_FIELD_01",
        name = "VP_TEST_CLASS_FIELD_NAME_01",
        desc = "VP_TEST_CLASS_FIELD_DESC_01"
)
public class AnnotationFieldDummy {

    @VpFieldAnno(value = "VP_TEST_FIELD_VALUE_01", name = "VP_TEST_FIELD_NAME_01", desc = "VP_TEST_FIELD_DESC_01")
    public static final String FIELD_CONST_01 = "FIELD_CONST_RAW_01";

    @VpFieldAnno(value = "VP_TEST_FIELD_VALUE_02", name = "VP_TEST_FIELD_NAME_02", desc = "VP_TEST_FIELD_DESC_02")
    public static final String FIELD_CONST_02 = "FIELD_CONST_RAW_02";

    @VpFieldAnno(value = "VP_TEST_FIELD_VALUE_03", name = "VP_TEST_FIELD_NAME_03", desc = "VP_TEST_FIELD_DESC_03")
    private String text03 = "FIELD_VAR_RAW_03";

    @VpFieldAnno(value = "VP_TEST_FIELD_VALUE_04", name = "VP_TEST_FIELD_NAME_04", desc = "VP_TEST_FIELD_DESC_04")
    private String text04 = "FIELD_VAR_RAW_04";

    @VpFieldAnno(value = "VP_TEST_FIELD_VALUE_05", name = "VP_TEST_FIELD_NAME_05", desc = "VP_TEST_FIELD_DESC_05")
    private String text05 = "FIELD_VAR_RAW_05";

    public String getJoined() {
        return FIELD_CONST_01 + "|" + FIELD_CONST_02 + "|" + text03 + "|" + text04 + "|" + text05;
    }
}

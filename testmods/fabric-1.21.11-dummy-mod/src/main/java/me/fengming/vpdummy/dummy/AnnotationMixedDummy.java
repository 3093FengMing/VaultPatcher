package me.fengming.vpdummy.dummy;

import me.fengming.vpdummy.annotations.VpClassAnno;
import me.fengming.vpdummy.annotations.VpFieldAnno;
import me.fengming.vpdummy.annotations.VpMethodAnno;
import me.fengming.vpdummy.annotations.VpParamAnno;
import me.fengming.vpdummy.annotations.VpTypeUseAnno;

@VpClassAnno(
        value = "VP_TEST_CLASS_MIXED_01",
        name = "VP_TEST_CLASS_MIXED_NAME_01",
        desc = "VP_TEST_CLASS_MIXED_DESC_01"
)
public class AnnotationMixedDummy {

    @VpFieldAnno(value = "VP_TEST_MIX_FIELD_VALUE_01", name = "VP_TEST_MIX_FIELD_NAME_01", desc = "VP_TEST_MIX_FIELD_DESC_01")
    private String label = "MIX_FIELD_RAW_01";

    @VpFieldAnno(value = "VP_TEST_MIX_FIELD_VALUE_02", name = "VP_TEST_MIX_FIELD_NAME_02", desc = "VP_TEST_MIX_FIELD_DESC_02")
    private String detail = "MIX_FIELD_RAW_02";

    @VpMethodAnno(value = "VP_TEST_MIX_METHOD_VALUE_01", name = "VP_TEST_MIX_METHOD_NAME_01", desc = "VP_TEST_MIX_METHOD_DESC_01")
    public String combine(
            @VpParamAnno(value = "VP_TEST_MIX_PARAM_VALUE_01", name = "VP_TEST_MIX_PARAM_NAME_01", desc = "VP_TEST_MIX_PARAM_DESC_01") String prefix,
            @VpParamAnno(value = "VP_TEST_MIX_PARAM_VALUE_02", name = "VP_TEST_MIX_PARAM_NAME_02", desc = "VP_TEST_MIX_PARAM_DESC_02") String suffix
    ) {
        @VpTypeUseAnno(value = "VP_TEST_MIX_LOCAL_VALUE_01", name = "VP_TEST_MIX_LOCAL_NAME_01", desc = "VP_TEST_MIX_LOCAL_DESC_01")
        String local = "MIX_LOCAL_RAW_01";
        return prefix + ":" + label + ":" + detail + ":" + local + ":" + suffix;
    }

    @VpMethodAnno(value = "VP_TEST_MIX_METHOD_VALUE_02", name = "VP_TEST_MIX_METHOD_NAME_02", desc = "VP_TEST_MIX_METHOD_DESC_02")
    public String ordinalProbe() {
        @VpTypeUseAnno(value = "VP_TEST_MIX_LOCAL_VALUE_02", name = "VP_TEST_MIX_LOCAL_NAME_02", desc = "VP_TEST_MIX_LOCAL_DESC_02")
        String s1 = "ORDINAL_RAW_01";
        @VpTypeUseAnno(value = "VP_TEST_MIX_LOCAL_VALUE_03", name = "VP_TEST_MIX_LOCAL_NAME_03", desc = "VP_TEST_MIX_LOCAL_DESC_03")
        String s2 = "ORDINAL_RAW_02";
        @VpTypeUseAnno(value = "VP_TEST_MIX_LOCAL_VALUE_04", name = "VP_TEST_MIX_LOCAL_NAME_04", desc = "VP_TEST_MIX_LOCAL_DESC_04")
        String s3 = "ORDINAL_RAW_03";
        return s1 + "|" + s2 + "|" + s3;
    }
}

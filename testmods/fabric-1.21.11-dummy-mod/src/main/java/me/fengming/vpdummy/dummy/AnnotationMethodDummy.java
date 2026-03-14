package me.fengming.vpdummy.dummy;

import me.fengming.vpdummy.annotations.VpClassAnno;
import me.fengming.vpdummy.annotations.VpMethodAnno;
import me.fengming.vpdummy.annotations.VpParamAnno;
import me.fengming.vpdummy.annotations.VpTypeUseAnno;

@VpClassAnno(
        value = "VP_TEST_CLASS_METHOD_01",
        name = "VP_TEST_CLASS_METHOD_NAME_01",
        desc = "VP_TEST_CLASS_METHOD_DESC_01"
)
public class AnnotationMethodDummy {

    @VpMethodAnno(value = "VP_TEST_METHOD_VALUE_01", name = "VP_TEST_METHOD_NAME_01", desc = "VP_TEST_METHOD_DESC_01")
    public String method01(
            @VpParamAnno(value = "VP_TEST_PARAM_VALUE_01", name = "VP_TEST_PARAM_NAME_01", desc = "VP_TEST_PARAM_DESC_01") String a,
            @VpParamAnno(value = "VP_TEST_PARAM_VALUE_02", name = "VP_TEST_PARAM_NAME_02", desc = "VP_TEST_PARAM_DESC_02") String b
    ) {
        @VpTypeUseAnno(value = "VP_TEST_LOCAL_VALUE_01", name = "VP_TEST_LOCAL_NAME_01", desc = "VP_TEST_LOCAL_DESC_01")
        String local01 = "LOCAL_RAW_01";
        return a + ":" + b + ":" + local01;
    }

    @VpMethodAnno(value = "VP_TEST_METHOD_VALUE_02", name = "VP_TEST_METHOD_NAME_02", desc = "VP_TEST_METHOD_DESC_02")
    public String method02(
            @VpParamAnno(value = "VP_TEST_PARAM_VALUE_03", name = "VP_TEST_PARAM_NAME_03", desc = "VP_TEST_PARAM_DESC_03") String c
    ) {
        @VpTypeUseAnno(value = "VP_TEST_LOCAL_VALUE_02", name = "VP_TEST_LOCAL_NAME_02", desc = "VP_TEST_LOCAL_DESC_02")
        String local02 = "LOCAL_RAW_02";
        @VpTypeUseAnno(value = "VP_TEST_LOCAL_VALUE_03", name = "VP_TEST_LOCAL_NAME_03", desc = "VP_TEST_LOCAL_DESC_03")
        String local03 = "LOCAL_RAW_03";
        return c + ":" + local02 + ":" + local03;
    }

    @VpMethodAnno(value = "VP_TEST_METHOD_VALUE_03", name = "VP_TEST_METHOD_NAME_03", desc = "VP_TEST_METHOD_DESC_03")
    public String method03() {
        @VpTypeUseAnno(value = "VP_TEST_LOCAL_VALUE_04", name = "VP_TEST_LOCAL_NAME_04", desc = "VP_TEST_LOCAL_DESC_04")
        String local04 = "LOCAL_RAW_04";
        return local04;
    }
}

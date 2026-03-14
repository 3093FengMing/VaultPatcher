package me.fengming.vpdummy.dummy;

import me.fengming.vpdummy.annotations.VpComplexAnno;
import me.fengming.vpdummy.annotations.VpLeafAnno;
import me.fengming.vpdummy.annotations.VpNestedAnno;

@VpComplexAnno(
        value = "VP_TEST_COMPLEX_CLASS_VALUE_01",
        labels = {"VP_TEST_COMPLEX_LABEL_01", "VP_TEST_COMPLEX_LABEL_02"},
        nested = @VpNestedAnno(
                value = "VP_TEST_NESTED_CLASS_VALUE_01",
                names = {"VP_TEST_NESTED_NAME_01", "VP_TEST_NESTED_NAME_02"},
                child = @VpLeafAnno(
                        value = "VP_TEST_LEAF_CLASS_VALUE_01",
                        tags = {"VP_TEST_LEAF_TAG_01", "VP_TEST_LEAF_TAG_02"}
                )
        ),
        leafArray = {
                @VpLeafAnno(
                        value = "VP_TEST_LEAF_ARRAY_VALUE_01",
                        tags = {"VP_TEST_LEAF_ARRAY_TAG_01", "VP_TEST_LEAF_ARRAY_TAG_02"}
                ),
                @VpLeafAnno(
                        value = "VP_TEST_LEAF_ARRAY_VALUE_02",
                        tags = {"VP_TEST_LEAF_ARRAY_TAG_03", "VP_TEST_LEAF_ARRAY_TAG_04"}
                )
        }
)
public class AnnotationNestedDummy {

    @VpComplexAnno(
            value = "VP_TEST_COMPLEX_METHOD_VALUE_01",
            labels = {"VP_TEST_COMPLEX_METHOD_LABEL_01", "VP_TEST_COMPLEX_METHOD_LABEL_02"},
            nested = @VpNestedAnno(
                    value = "VP_TEST_NESTED_METHOD_VALUE_01",
                    names = {"VP_TEST_NESTED_METHOD_NAME_01"},
                    child = @VpLeafAnno(
                            value = "VP_TEST_LEAF_METHOD_VALUE_01",
                            tags = {"VP_TEST_LEAF_METHOD_TAG_01", "VP_TEST_LEAF_METHOD_TAG_02"}
                    )
            ),
            leafArray = {
                    @VpLeafAnno(
                            value = "VP_TEST_LEAF_METHOD_ARRAY_VALUE_01",
                            tags = {"VP_TEST_LEAF_METHOD_ARRAY_TAG_01", "VP_TEST_LEAF_METHOD_ARRAY_TAG_02"}
                    )
            }
    )
    public String nestedMethod() {
        return "NESTED_DUMMY_METHOD";
    }
}

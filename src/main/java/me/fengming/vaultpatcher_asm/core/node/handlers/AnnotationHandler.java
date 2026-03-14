package me.fengming.vaultpatcher_asm.core.node.handlers;
import me.fengming.vaultpatcher_asm.config.TranslationInfo;
import me.fengming.vaultpatcher_asm.core.utils.MatchUtils;
import me.fengming.vaultpatcher_asm.core.utils.Utils;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.List;

public class AnnotationHandler {
    public static void classAnnotationHandler(ClassNode input, String annotationName, TranslationInfo info, int[] ordinal) {
        if (info.getTargetClassInfo().getAnnoType().equals("CLASS") || info.getTargetClassInfo().getAnnoType().equals("ALL")) {
            List<List<? extends AnnotationNode>> classAnnotationType = Arrays.asList(input.visibleAnnotations, input.visibleTypeAnnotations);
            for (List<? extends AnnotationNode> list : classAnnotationType) {
                if (list == null) continue;
                for (AnnotationNode annotation : list) {
                    if (annotation == null) continue;
                    stringHandler(input, annotation, annotationName, info, ordinal);
                }
            }
        }
    }

    public static void fieldAnnotationHandler(ClassNode input, FieldNode field, String annotationName, TranslationInfo info, int[] ordinal) {
        if (info.getTargetClassInfo().getAnnoType().equals("FIELD") || info.getTargetClassInfo().getAnnoType().equals("ALL")) {
            List<List<? extends AnnotationNode>> fieldAnnotationType = Arrays.asList(field.visibleAnnotations, field.visibleTypeAnnotations);
            for (List<? extends AnnotationNode> list : fieldAnnotationType) {
                if (list == null) continue;
                for (AnnotationNode annotation : list) {
                    if (annotation == null) continue;
                    stringHandler(input, annotation, annotationName, info, ordinal);
                }
            }
        }
    }

    public static void methodAnnotationHandler(ClassNode input, MethodNode method, String annotationName, TranslationInfo info, int[] ordinal) {
        if (info.getTargetClassInfo().getAnnoType().equals("METHOD") || info.getTargetClassInfo().getAnnoType().equals("ALL")) {
            List<List<? extends AnnotationNode>> methodAnnotationType = Arrays.asList(method.visibleAnnotations, method.visibleTypeAnnotations, method.visibleLocalVariableAnnotations);
            for (List<? extends AnnotationNode> list : methodAnnotationType) {
                if (list == null) continue;
                for (AnnotationNode annotation : list) {
                    if (annotation == null) continue;
                    stringHandler(input, annotation, annotationName, info, ordinal);
                }
            }
            // visibleParameterAnnotation is a list of list of AnnotationNode
            if (method.visibleParameterAnnotations != null) {
                for (List<? extends AnnotationNode> annotationNodeList : method.visibleParameterAnnotations) {
                    if (annotationNodeList == null) continue;
                    for (AnnotationNode annotation : annotationNodeList) {
                        if (annotation == null) continue;
                        stringHandler(input, annotation, annotationName, info, ordinal);
                    }
                }
            }
            defaultAnnotationHandler(input, method, info, annotationName, ordinal);
        }
    }

    @SuppressWarnings("unchecked")
    private static void stringHandler(ClassNode input, AnnotationNode annotation, String annotationName, TranslationInfo info, int[] ordinal) {
            if (annotation.values != null) {
                boolean currentIsTarget = annotation.desc.equals(annotationName);
                for (int i = 0; i < annotation.values.size() - 1; i += 2) {
                    String key = (String) annotation.values.get(i);
                    Object value = annotation.values.get(i + 1);
                    ordinal[0]++;
                    // Replace String value
                    if (value instanceof String && currentIsTarget && MatchUtils.matchOrdinal(info, ordinal[0]) && (key.equals(info.getTargetClassInfo().getAnnoKey()) || info.getTargetClassInfo().getAnnoKey().isEmpty())) {
                        String strValue = (String) value;
                        String newValue = MatchUtils.matchPairs(info.getPairs(), strValue, false);
                        annotation.values.set(i + 1, newValue);
                        Utils.printDebugInfo(ordinal[0], strValue, "ASMTransformMethod-Annotation", newValue, input.name, info, null);
                      // If value is another annotation, recursively handle it.
                    } else if (value instanceof AnnotationNode && !currentIsTarget) {
                        stringHandler(input, (AnnotationNode) value, annotationName, info, ordinal);

                    } else if (value instanceof List) {
                        if (currentIsTarget) {
                            // The current node is target: only handle current-level values.
                            listHandler(input, (List<Object>) value, info, ordinal);
                        } else {
                            // The current node is not target: keep searching nested annotations.
                            listSearchHandler(input, (List<Object>) value, annotationName, info, ordinal);
                        }
                    }
                }
            }
    }
    //Handle the default value of annotation
    @SuppressWarnings("unchecked")
    private static void defaultAnnotationHandler(ClassNode input, MethodNode method, TranslationInfo info, String annotationName, int[] ordinal) {
        if (method.annotationDefault != null) {
            if (method.annotationDefault instanceof String) {
                ordinal[0]++;
                if (MatchUtils.matchOrdinal(info, ordinal[0])) {
                    String strValue = (String) method.annotationDefault;
                    String newValue = MatchUtils.matchPairs(info.getPairs(), strValue, false);
                    method.annotationDefault = newValue;
                    Utils.printDebugInfo(ordinal[0], strValue, "ASMTransformMethod-Annotation", newValue, input.name, info, null);
                }
            } else if (method.annotationDefault instanceof List) {
                List<Object> list = (List<Object>) method.annotationDefault;
                listHandler(input, list, info, ordinal);

            } else if (method.annotationDefault instanceof AnnotationNode) {
                AnnotationNode defaultNode = (AnnotationNode) method.annotationDefault;
                if (!defaultNode.desc.equals(annotationName)) {
                    stringHandler(input, defaultNode, annotationName, info, ordinal);
                }
            }
        }
    }

    //Handle List elements
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void listHandler(ClassNode input, List<Object> list, TranslationInfo info, int[] ordinal) {
        for (int j=0; j<list.size(); j++) {
            if (list.get(j) instanceof String) {
                ordinal[0]++;
                if (MatchUtils.matchOrdinal(info, ordinal[0])) {
                    String strValue = (String) list.get(j);
                    String newValue = MatchUtils.matchPairs(info.getPairs(), strValue, false);
                    list.set(j, newValue);
                    Utils.printDebugInfo(ordinal[0], strValue, "ASMTransformMethod-Annotation", newValue, input.name, info, null);

                }
            } else if (list.get(j) instanceof List) {
                listHandler(input, (List) list.get(j), info, ordinal);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void listSearchHandler(ClassNode input, List<Object> list, String annotationName, TranslationInfo info, int[] ordinal) {
        for (Object value : list) {
            if (value instanceof AnnotationNode) {
                stringHandler(input, (AnnotationNode) value, annotationName, info, ordinal);
            } else if (value instanceof List) {
                listSearchHandler(input, (List<Object>) value, annotationName, info, ordinal);
            }
        }
    }
}

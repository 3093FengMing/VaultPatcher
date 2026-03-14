# Fabric 1.21.11 Dummy Annotation Stress Mod

This project is a standalone Fabric dummy mod used to test VaultPatcher annotation replacement behavior.

## Build

From the repository root:

```powershell
.\gradlew.bat -p "testmods/fabric-1.21.11-dummy-mod" build
```

Output jar:

- `testmods/fabric-1.21.11-dummy-mod/build/libs/vpdummy-annotation-stress-0.0.1.jar`

## Deploy for VaultPatcher test

1. Put the dummy jar into your Fabric `mods/` folder.
2. Copy `vp-module/annotation-dummy-module.json` to `<minecraft>/vaultpatcher/modules/`.
3. Ensure VaultPatcher debug output is enabled in `config/vaultpatcher_asm/config.json`.
4. Start the game and inspect logs for `ASMTransformMethod-Annotation`.

## What is covered

- Class annotation replacement (`type=CLASS`)
- Field annotation replacement (`type=FIELD`)
- Method/parameter/type-use annotation replacement (`type=METHOD`)
- `annokey` exact match and empty key behavior
- `ordinal` cases: omitted, single index, range, range-to-end

## Target classes in module

- `me/fengming/vpdummy/dummy/AnnotationFieldDummy`
- `me/fengming/vpdummy/dummy/AnnotationMethodDummy`
- `me/fengming/vpdummy/dummy/AnnotationMixedDummy`

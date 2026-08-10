# Runic Rebirth — Migration Plan: 1.21.1 → 26.1.2

## Overview

**Version chain:** 1.21.1 → 1.21.2 → 1.21.4 → 1.21.5 → 1.21.9 → 1.21.11 → 26.1 → (26.1.2 patch)

**Java:** 21 → 25 (required at 26.1)

**Key library versions:**
- NeoForge: target version for 26.1.2
- GeckoLib: 4.8.4 → **5.5.1** (supports 26.1.2) — major rewrite
- Modonomicon: 1.110.0 → 26.1.x build
- Curios: find 26.1.x build from `https://maven.theillusivec4.top/`
- SmartBrainLib: find 26.1.x from cloudsmith maven
- Athena CTM: verify 26.1.x availability; if not, plan to remove or replace

**Biggest migrations by impact:**
1. **GeckoLib 4→5**: two-stage render pipeline, affects ~50 files
2. **ResourceLocation→Identifier rename**: every Java file
3. **Rendering pipeline**: `MultiBufferSource`→`SubmitNodeCollector`, `render()`→`submit()`
4. **Item system overhaul**: `setId()` required everywhere, armor component system
5. **SavedData→SavedDataType**: affects `DungeonInstanceManager`
6. **Recipe serializer as records**: affects `InfusionRecipeSerializer`, `RunicAnvilRecipeSerializer`
7. **Particle system rewrite**: `TextureSheetParticle`→`SingleQuadParticle`
8. **Modonomicon codec/registry system**: affects `SpellPage`, `ModonomiconCompat`

---

## PART 0 — Branch Setup + Build System

**New branch:** `1.26.1.2-Neoforge` from `1.21.1-Neoforge`

### `build.gradle`
- `java.sourceCompatibility` / `toolchain.languageVersion` → `JavaVersion.VERSION_25`
- `neoforge` dep version → appropriate 26.1.2 NeoForge build
- `minecraft` version → target MC version for 26.1.2
- GeckoLib dep → `software.bernie.geckolib:geckolib-neoforge-<26.1.2-target>:5.5.1`
- Modonomicon dep → find `com.klikli_dev:modonomicon-neoforge-<version>` for 26.1.x
- Curios dep → find 26.1.x build from `https://maven.theillusivec4.top/`
- SmartBrainLib dep → find 26.1.x from cloudsmith maven
- Athena CTM → verify 26.1.x availability; if not, plan to remove CTM or replace
- Parchment mappings → update to 26.1.x mappings or use official Mojang names (vanilla now uses official names per 26.1 primer)

### `gradle.properties`
- `neo_version` → 26.1.x NeoForge version
- `minecraft_version` → MC version for 26.1.2
- `java_version=25`

### `neoforge.mods.toml`
- `[[dependencies.runicrebirth]]` version bounds for all deps → update to 26.1.x ranges
- `loaderVersion` → 26.x range

---

## PART 1 — Mass Rename: ResourceLocation → Identifier

**Scope:** Every `.java` file in the project.

**All changes are mechanical find-and-replace within imports + usages:**

| Old | New |
|-----|-----|
| `net.minecraft.resources.ResourceLocation` | `net.minecraft.resources.Identifier` |
| `ResourceLocation.fromNamespaceAndPath(...)` | `Identifier.fromNamespaceAndPath(...)` |
| `ResourceLocation.withDefaultNamespace(...)` | `Identifier.withDefaultNamespace(...)` |
| `new ResourceLocation(...)` | `Identifier.of(...)` or `Identifier.parse(...)` |
| `ResourceLocationException` | `IdentifierException` |
| `FriendlyByteBuf#readResourceLocation()` | `FriendlyByteBuf#readIdentifier()` |
| `FriendlyByteBuf#writeResourceLocation(rl)` | `FriendlyByteBuf#writeIdentifier(id)` |
| `ResourceLocation.CODEC` | `Identifier.CODEC` |

**Files with highest density of ResourceLocation usage (do these first to verify pattern):**
- `RunicRebirth.java` — registry wiring
- `ModPackets.java`, all `*Packet.java` files — network codec
- `SpellDamageSource.java` — `spellTypeId` field
- `SpellAdvancementHelper.java`, `SpellUnlockEvents.java`
- `ModShapes.java`, `ShapeRegistry.java`
- `MagicData.java`, `WandStacksData.java`
- All `GeoModel` subclasses — `getModelResource()`, `getTextureResource()`, `getAnimationResource()`
- `ModCriteriaTriggers.java`, `MagicKillTrigger.java`
- `DungeonData.java`, `ModDimensions.java`

---

## PART 2 — NeoForge Core API (1.21.1 → 1.21.2)

### `ModBlocks.java`
- Every `BlockBehaviour.Properties.of()` call → add `.setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("runicrebirth", "<block_id>")))`
- 11 blocks: `runic_stone`, `runic_stone_slab`, `runic_stone_stairs`, `runic_stone_pillar`, `oculus_portal`, `oculus_controller`, `runesteel_pylon`, `return_portal`, `trial_spawner`, `infusion_altar`, `runic_anvil`

### `ModItems.java`
- Every `Item.Properties()` → add `.setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("runicrebirth", "<item_id>")))`
- All items: 4 apprentice set, `basic_wand`, `inscribed_wand`, `ring_of_expansion`, `runic_codex`, `arcane_spirit`, `runic_key_ring`, all circuit items, all block items, `runic_anvil`

### `ModBlockEntities.java`
- `BlockEntityType` construction: `BlockEntityType.Builder.of(...)` constructor was privatized in 1.21.2 — NeoForge provides workaround via `BlockEntityType.Builder`; verify registration pattern still works

### `SpellWriter.java`, `RunicCodexItem.java`, `RunicCircuitItem.java`, `RunicKeyRingItem.java`, `InscribedTool.java`, `BasicWand.java`, `InscribedWand.java`
- `Item#use()` return type: `InteractionResultHolder<ItemStack>` → `InteractionResult`
  - `InteractionResultHolder.success(stack)` → `InteractionResult.SUCCESS`
  - `InteractionResultHolder.consume(stack)` → `InteractionResult.CONSUME`
  - `InteractionResultHolder.pass(stack)` → `InteractionResult.PASS`
  - Item transformation: `InteractionResult.SUCCESS.heldItemTransformedTo(newStack)`
- `ItemInteractionResult` (from block use) → `InteractionResult`

### `ReturnPortalBlock.java`, `OculusPortalBlock.java`, `OculusControllerBlock.java`, `InfusionAltarBlock.java`, `RunicAnvilBlock.java`, `DungeonTemporaryPlatformBlock.java`, `DungeonPressurePlateBlock.java`, `DungeonSpikeBlock.java`
- Same `InteractionResult` changes on `useWithoutItem()` / `use()` / `useOn()` return types

### Block property classes
- Check if `DirectionProperty` used anywhere → replace with `EnumProperty.create("facing", Direction.class, ...)`
- All property classes now final — no subclassing

---

## PART 3 — Recipe System Migration (1.21.2 + 26.1)

### `DataGenHandler.java`
- `RecipeProvider` no longer extends `DataProvider` — wrap in `RecipeProvider.Runner`:

```java
// Old pattern: class MyRecipeProvider extends RecipeProvider
// New pattern:
public class MyRecipeProvider extends RecipeProvider {
    public MyRecipeProvider(HolderLookup.Provider reg, RecipeOutput out) { super(reg, out); }
    @Override protected void buildRecipes() { /* recipes here */ }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput out, CompletableFuture<HolderLookup.Provider> reg) { super(out, reg); }
        @Override protected RecipeProvider createRecipeProvider(HolderLookup.Provider r, RecipeOutput o) {
            return new MyRecipeProvider(r, o);
        }
        @Override public String getName() { return "RunicRebirth Recipes"; }
    }
}
// Register Runner instance in GatherDataEvent, not MyRecipeProvider
```

### `InfusionRecipeSerializer.java` + `RunicAnvilRecipeSerializer.java` → DELETE (26.1)
- `RecipeSerializer` is now a record taking `MapCodec` + `StreamCodec`
- Move `MapCodec` + `StreamCodec` constants into their recipe classes as `MAP_CODEC` / `STREAM_CODEC`
- Delete both serializer files

### `ModRecipeSerializers.java`
```java
// New pattern — RecipeSerializer is a record:
public static final RecipeSerializer<InfusionRecipe> INFUSION = new RecipeSerializer<>(
    InfusionRecipe.MAP_CODEC,
    InfusionRecipe.STREAM_CODEC
);
public static final RecipeSerializer<RunicAnvilRecipe> RUNIC_ANVIL = new RecipeSerializer<>(
    RunicAnvilRecipe.MAP_CODEC,
    RunicAnvilRecipe.STREAM_CODEC
);
```

### `InfusionRecipe.java`, `RunicAnvilRecipe.java`
- Add `MAP_CODEC` and `STREAM_CODEC` static fields (moved from serializer class)
- Add `placementInfo()` method:
```java
private PlacementInfo placementInfo;
@Override
public PlacementInfo placementInfo() {
    if (this.placementInfo == null)
        this.placementInfo = PlacementInfo.createFromIngredients(this.ingredients);
    return this.placementInfo;
}
```
- `getResultItem()` removed — result accessed via `RecipeDisplay`
- Add `display()` returning list of `RecipeDisplay` (or empty list if no recipe book integration needed)
- `Recipe#recipeBookCategory()` → return registered `RecipeBookCategory`
- `Ingredient.of(tag)` → `Ingredient.of(registry.getOrThrow(tag))` — needs `HolderLookup.Provider`

---

## PART 4 — Item/Armor System Overhaul (1.21.2-1.21.5)

### `MagicArmorItem.java`, `ApprenticeSetItem.java`
- `ArmorItem` class removed in 1.21.5 — both must extend `Item` (or `MagicItem`) directly
- Replace `ArmorItem(material, EquipmentSlot, properties)` → `Item.Properties` with components:
```java
new Item.Properties()
    .setId(...)
    .component(DataComponents.MAX_DAMAGE, durability)
    .component(DataComponents.EQUIPPABLE, Equippable.builder(slot)
        .setEquipSound(equipSound)
        .setAsset(equipmentAssetKey) // ResourceKey<EquipmentAsset>
        .build())
    .component(DataComponents.ATTRIBUTE_MODIFIERS, attributeModifiers)
    .component(DataComponents.REPAIRABLE, repairIngredient)
    .component(DataComponents.ENCHANTABLE, new Enchantable(enchantmentValue))
```
- `ArmorMaterial` → becomes a plain record (not registry object): define inline or as static constant
- Equipment model → `EquipmentAsset` system:
  - Create `assets/runicrebirth/models/equipment/apprentice.json`
  - Register `ResourceKey<EquipmentAsset>` (NeoForge provides registration hook)
- GeckoLib armor rendering: see Part 5G

### Dyeable armor (`ApprenticeSetItem`)
- `DyedItemColor` registration via `ItemColor` removed in 1.21.4
- Replace with `DataComponents.DYED_COLOR` on the item + `ItemTintSource.Dye` tint source
- Add `component(DataComponents.DYED_COLOR, new DyedItemColor(ApprenticeSetItem.DEFAULT_DYE_COLOR, true))` to item properties
- Register `ItemTintSource` (see Part 12 `RunicRebirthClient` changes)

---

## PART 5 — GeckoLib 4 → 5 Migration

> **Agent note:** GeckoLib 5 docs at https://wiki.geckolib.com/docs/geckolib5/. The two-stage pipeline means all rendering uses a `GeoRenderState` extracted before rendering — the entity/block is NOT accessible during rendering. Consult GeckoLib 5 source/examples before implementing. GeckoLib 5.5.1 targets MC 26.1.2 and 26.2.

### Core architectural shift:
- `GeoEntityRenderer<T>` → `GeoEntityRenderer<T, S extends GeoRenderState>`
- Must implement `createRenderState() → S`, `extractRenderState(T entity, S state, float partial)`
- `render(T entity, ...)` → `submit(S state, PoseStack, SubmitNodeCollector, CameraRenderState)`
- `AnimationController` API changed — update `registerControllers()` / equivalent
- `GeoModel<T>` path methods take render state not entity

---

### 5A — GeoEntity implementations (game-thread side, ~25 files)

#### `DrawingCanvasEntity.java`
- Update `GeoEntity` interface to GeckoLib 5
- `registerControllers()` — update `AnimationController` creation syntax
- `getAnimatableInstanceCache()` — cache type may change

#### `AbstractCircleEntity.java`, `BasicCircleEntity.java`, `IntermediateCircleEntity.java`, `AdvancedCircleEntity.java`
- Same `GeoEntity` interface update
- "form" `AnimationController` — update creation syntax
- `SynchedEntityData` types — verify `EntityDataSerializer` for `Identifier` type (was `ResourceLocation`)

#### `TargetCircleEntity.java`
- Same GeoEntity update + `AnimationController`

#### All spell entities implementing `GeoEntity`:
`AbstractSpellEntity.java`, `AbstractProjectileSpellEntity.java`, `AbstractSpellCircleEntity.java`,
`MagicProjectileEntity.java`, `MagicArrowEntity.java`, `MagicSlashEntity.java`, `MagicMeteorEntity.java`,
`MagicShieldEntity.java`, `MagicHammerEntity.java`, `MagicBindingEntity.java`, `MagicBallistaEntity.java`,
`MagicBlastEntity.java`, `MagicExplosionEntity.java`, `MagicBeamEntity.java`, `InfusionCircleEntity.java`,
`MagicBallistaCircleEntity.java`, `MagicSlashCircleEntity.java`, `MagicMeteorCircleEntity.java`
- Update `GeoEntity` interface for GeckoLib 5
- Update `AnimationController` registration

#### All mob entities implementing `GeoEntity`:
`RunesteelGolemEntity.java`, `ZombifiedRunebladeAcolyteEntity.java`, `SkeletalMageAcolyteEntity.java`,
`SkeletalWizardAcolyteEntity.java`, `ZombifiedArtificerAcolyteEntity.java`
- Same GeoEntity update

---

### 5B — GeoBlockEntity implementations (5 files)

`InfusionAltarBlockEntity.java`, `OculusPortalBlockEntity.java`, `OculusControllerBlockEntity.java`,
`RunesteelPylonBlockEntity.java`, `RunicAnvilBlockEntity.java`
- Update `GeoBlockEntity` interface to GeckoLib 5
- `AnimationController` registration — update syntax
- `getAnimatableInstanceCache()` cache type

---

### 5C — GeoItem implementations (3 files)

`MagicItem.java`, `BasicWand.java`, `RunicCodexItem.java`
- Update `GeoItem` interface to GeckoLib 5
- `getAnimatableInstanceCache()` update

---

### 5D — GeoModel subclasses (~25 files)

All files in `client/renderers/models/`:
`BasicCircleGeoModel`, `IntermediateCircleGeoModel`, `AdvancedCircleGeoModel`,
`MagicProjectileGeoModel`, `MagicArrowGeoModel`, `MagicSlashGeoModel`, `MagicMeteorGeoModel`,
`MagicShieldGeoModel`, `MagicHammerGeoModel`, `MagicBindingGeoModel`, `MagicBallistaGeoModel`,
`MagicBallistaCircleGeoModel`, `MagicSlashCircleGeoModel`, `MagicMeteorCircleGeoModel`,
`MagicBeamGeoModel`, `MagicBlastGeoModel`, `MagicExplosionGeoModel`, `InfusionCircleGeoModel`,
`TargetCircleGeoModel`, `DrawingCanvasGeoModel`,
`RunesteelGolemGeoModel`, `ZombifiedRunebladeAcolyteGeoModel`, `SkeletalMageAcolyteGeoModel`,
`SkeletalWizardAcolyteGeoModel`, `ZombifiedArtificerAcolyteGeoModel`

All files in `client/renderers/blocks/`:
`InfusionAltarModel`, `RunesteelPylonModel`, `OculusPortalModel`, `OculusControllerModel`, `RunicAnvilModel`

Item models: `BasicWandModel`, `RunicCodexModel`

**For each:**
- `getModelResource(T animatable)` → `getModelResource(S renderState)` (GeoRenderState-based)
- `getTextureResource(T animatable)` → `getTextureResource(S renderState)`
- `getAnimationResource(T animatable)` → `getAnimationResource(S renderState)`
- Return type: `Identifier` (after Part 1 rename)

---

### 5E — GeoEntityRenderer subclasses (~25 files)

All files in `client/renderers/entities/`:
`AbstractSpellRenderer`, `AbstractCircleRenderer`, `BasicCircleRenderer`, `IntermediateCircleRenderer`,
`AdvancedCircleRenderer`, `DrawingCanvasRenderer`, `MagicProjectileRenderer`, `MagicArrowRenderer`,
`MagicSlashRenderer`, `MagicMeteorRenderer`, `MagicShieldRenderer`, `MagicHammerRenderer`,
`MagicBindingRenderer`, `MagicBallistaRenderer`, `MagicBallistaCircleRenderer`,
`MagicSlashCircleRenderer`, `MagicMeteorCircleRenderer`, `MagicBeamRenderer`, `MagicBlastRenderer`,
`MagicExplosionRenderer`, `InfusionCircleRenderer`, `TargetCircleRenderer`,
`RunesteelGolemRenderer`, `ZombifiedRunebladeAcolyteRenderer`, `SkeletalMageAcolyteRenderer`,
`SkeletalWizardAcolyteRenderer`, `ZombifiedArtificerAcolyteRenderer`

**For each:**
- Signature: `GeoEntityRenderer<T, S extends GeoRenderState>` with custom `S` per entity
- Create inner/separate `S extends GeoRenderState` capturing needed render data (spell type, element, phase, size, modifier IDs, etc.)
- Implement `createRenderState() → new S()`
- Implement `extractRenderState(T entity, S state, float partial)` → populate `S` from entity fields
- `render(...)` / `preRender(...)` / `postRender(...)` → `submit(S, PoseStack, SubmitNodeCollector, CameraRenderState)` equivalents
- `getRenderType(T entity, ...)` → `getRenderType(S state, ...)`
- Remove all direct entity field reads inside render methods — read from `state` only

**Special cases:**
- `DrawingCanvasRenderer.java` — caches bone world-positions for screen projection by `DrawingCanvasScreen`. GeckoLib 5 bone access API may differ; consult wiki.
- `AbstractCircleRenderer.java` — full-brightness rune bone + rotation logic, verify bone-access API in GeckoLib 5.

---

### 5F — GeoBlockRenderer subclasses (5 files)

`InfusionAltarRenderer`, `RunesteelPylonRenderer`, `OculusPortalRenderer`, `OculusControllerRenderer`, `RunicAnvilRenderer`

**For each:**
- Create `S extends GeoRenderState` (or GeckoLib's block entity render state subclass)
- `extractRenderState(BlockEntity, S, float partial, Vec3 camera, ...)`
- `submit(S, PoseStack, SubmitNodeCollector, CameraRenderState)`
- `InfusionAltarRenderer` also renders floating orbiting items — update to `SubmitNodeCollector.submitModel()` calls

---

### 5G — Armor Renderers (2 files)

`MagicArmorRenderer.java`, `DyeableMagicArmorRenderer.java`
- `GeoArmorRenderer` → GeckoLib 5 armor renderer (API likely changed significantly; consult wiki)
- Equipment layer rendering uses `EquipmentClientInfo.LayerType` system
- `DyeableMagicArmorRenderer` dyeable bone tinting → verify works with `DataComponents.DYED_COLOR` approach
- May need to extend GeckoLib 5's equivalent of `DyeableGeoArmorRenderer`

---

## PART 6 — Rendering Pipeline Migration

### 6A — Particle System (4 files)

#### `FireElementParticle.java`, `ArcaneTinyParticle.java`, `IceElementParticle.java`
- `TextureSheetParticle` → `SingleQuadParticle`
- `getRenderType()` → `getGroup()` returning `ParticleRenderType`
- Define `SingleQuadParticle.Layer` — use `TRANSLUCENT` for these particles
- `Provider.create(SpriteSet)` → `Provider.createParticle(..., RandomSource random)` — add `RandomSource` param
- Remove explicit buffer builder setup

#### `TremorBlockParticle.java`
- Complex custom rendering via `RenderLevelStageEvent` + `BlockRenderDispatcher` — outside GeckoLib
- `TextureSheetParticle` base → needs `ParticleGroup` implementation:
  - Extend `ParticleGroup<TremorBlockParticle>`
  - Create `ParticleGroupRenderState` that calls `submitBlock()` on `SubmitNodeCollector`
- `TremorBlockParticle.renderAll()` method → replace with group extraction pattern
- `Particle#render()` → absorbed into `ParticleGroupRenderState#submit()`

### 6B — Custom Render Types (`ModRenderTypes.java` if exists)
- `RenderType` construction: `RenderStateShard`-based `CompositeState` → `RenderSetup.Builder` pattern (1.21.11)
- `entityTranslucentNoDepth`, `entityTranslucentNoCullNoShade` etc. → rebuild using `RenderSetup.Builder` with appropriate `RenderPipeline`
- `MultiBufferSource` parameter in any render type callback → remove (26.2)

### 6C — `TargetCircleManager.java`
- `EntityRenderDispatcher.getRenderer()` + manual render call → update to `submit()` pattern
- `LightTexture.FULL_BRIGHT` → verify still exists
- `RenderLevelStageEvent` → verify event still fired
- `buf.endBatch(ModRenderTypes.entityTranslucentNoCullNoShade(...))` → `SubmitNodeCollector` pattern
- Reflection on `Entity.setSharedFlag` for glow flag — verify internal field name unchanged

### 6D — `CrackManager.java`, `CameraShakeHandler.java`
- `RenderLevelStageEvent` → verify still exists in 26.x
- `CrackManager` uses `BlockRenderDispatcher.renderSingleBlock()` for crack overlays — verify API available

---

## PART 7 — HUD/GUI Rendering

### `SpellStackOverlay.java`, `InfusionAltarOverlay.java`, `RunicAnvilOverlay.java`
- `GuiGraphics.blit(ResourceLocation, x, y, u, v, w, h)` → new signature requiring `RenderType` function + texture dimensions:
  ```java
  // Old: graphics.blit(texture, x, y, u, v, w, h);
  // New: graphics.blit(RenderType::guiTextured, texture, x, y, u, v, w, h, 256, 256);
  ```
- `GuiGraphics.blitSprite(...)` for sprite-based slots — verify sprite API unchanged
- `LayeredDraw.Layer` HUD registration — verify API

### `DrawingCanvasScreen.java`
- World-to-screen bone projection: depends on GeckoLib 5 bone API (coordinate system may differ)
- `Screen` widget API drift — check any `AbstractWidget` subclass usage
- Font rendering via `GuiGraphics.drawString()` — likely stable

### `DungeonSelectionScreen.java`, `RunicKeyRingScreen.java`
- `AbstractSelectionList` → `AbstractScrollArea` composition (1.21.4):
  - `getMaxScroll()`, `getScrollAmount()`, `scrollbarVisible` → moved to `AbstractScrollArea`
  - `setRenderHeader()` → constructor parameter
  - `setClampedScrollAmount()`, `setScrollAmount()` → `AbstractScrollArea#setScrollAmount()`
  - `clampScrollAmount()` → `refreshScrollAmount()`
  - `updateScrollingState()` → `AbstractScrollArea#updateScrolling()`
  - `getScrollbarPosition()` → `scrollBarY`
- `AbstractScrollWidget` removed → use `AbstractContainerWidget`

---

## PART 8 — SavedData → SavedDataType (1.21.5)

### `DungeonInstanceManager.java`

```java
// Before:
public class DungeonInstanceManager extends SavedData {
    public static final SavedData.Factory<DungeonInstanceManager> FACTORY =
        new SavedData.Factory<>(DungeonInstanceManager::new, DungeonInstanceManager::load, null);
    // load/save via CompoundTag parameters
}
// Access: level.getDataStorage().computeIfAbsent(FACTORY, "runicrebirth_dungeon_instances")

// After:
public class DungeonInstanceManager extends SavedData {
    public static final SavedDataType<DungeonInstanceManager> TYPE = new SavedDataType<>(
        Identifier.fromNamespaceAndPath("runicrebirth", "dungeon/instances"),
        DungeonInstanceManager::new,         // constructor (takes SavedData.Context)
        MapCodec.unit(DungeonInstanceManager::new), // or full codec-based factory
        null
    );
}
// Access: level.getDataStorage().computeIfAbsent(DungeonInstanceManager.TYPE)
// File stored at: data/runicrebirth/dungeon/instances.dat
```

- `DimensionDataStorage` → `SavedDataStorage`
- `save(CompoundTag)` / `load(CompoundTag)` → verify new persistence pattern (codec or override)
- Constructor parameter changes: may receive `SavedData.Context` providing level access

---

## PART 9 — NBT / Tag API Migration (1.21.5)

### Files using `CompoundTag` directly:
`MagicData.java`, `WandStacksData.java`, `DungeonData.java`, `DungeonInstance.java`,
`OculusPortalBlockEntity.java`, `OculusControllerBlockEntity.java`, `TrialSpawnerBlockEntity.java`,
`RunicAnvilBlockEntity.java`, `InfusionAltarBlockEntity.java`, `DungeonPressurePlateBlockEntity.java`

**For each:**
| Old | New |
|-----|-----|
| `tag.getString("key")` | `tag.getStringOr("key", "")` |
| `tag.getInt("key")` | `tag.getIntOr("key", 0)` |
| `tag.getBoolean("key")` | `tag.getBooleanOr("key", false)` |
| `tag.getAsString()` on any tag | Use typed Optional getter |
| `ListTag`, `CompoundTag` are now final | No subclassing of these |

- `CompoundTag#store(key, codec, value)` / `#read(key, codec)` available for codec-based field persistence
- `ListTag` access: verify `getCompound(i)` returns Optional or throws; update accordingly

---

## PART 10 — Modonomicon Compat Migration

### `SpellPage.java`

```java
// Before (1.21.1):
public class SpellPage implements BookPage {
    public static final BookPageType<SpellPage> TYPE = ...;
    public BookPageType<?> getType() { return TYPE; }
    public static SpellPage fromJson(JsonObject json) { ... }
    public static SpellPage fromNetwork(FriendlyByteBuf buf) { ... }
    public void toNetwork(FriendlyByteBuf buf) { ... }
}

// After (26.1.2):
public class SpellPage implements BookPage {
    public static final Identifier ID = Identifier.fromNamespaceAndPath("runicrebirth", "spell");
    public static final MapCodec<SpellPage> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Identifier.CODEC.fieldOf("spell_id").forGetter(SpellPage::getSpellId)
    ).apply(i, SpellPage::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, SpellPage> STREAM_CODEC =
        Identifier.STREAM_CODEC.map(SpellPage::new, SpellPage::getSpellId);

    @Override
    public BookPageType<?> type() { return /* registered type lookup */; }

    // Remove fromJson(), fromNetwork()
    // toNetwork() delegates to STREAM_CODEC.encode(this, buf)
}
```

### `ModonomiconCompat.java`

```java
// Before:
LoaderRegistry.registerPageLoader(SpellPage.TYPE);

// After:
BookPageTypeRegistry.register(SpellPage.ID, SpellPage.CODEC, SpellPage.STREAM_CODEC);
```

### `SpellPageModel.java` (datagen)
- `BookRecipePageModel` requires subclasses implement `createPage(BookRecipePage.JsonDataHolder)` — verify `SpellPageModel` is not extending this
- Ensure serialized JSON includes explicit `type` field

### `RunicCodexBookProvider.java`, `FoundationCategory.java`, `SpellsCategory.java`
- Entries need explicit `type` field in serialized JSON (Modonomicon datagen builders may add automatically — verify)
- All IDs must be fully qualified: e.g., `"features"` → `"modonomicon:features"` in cross-references
- Multiblock matcher: `.getType()` → `.stateMatcher().type()`
- State matcher constants: `StateMatcherTypeRegistry.ANY` instead of class constants

### `SpellEntryProvider.java`, `ModifierEntryProvider.java`, `MilestoneEntryProvider.java`
- `BookAdvancementConditionModel` API — verify unchanged; tooltip parameter handling

---

## PART 11 — Permission System (1.21.11)

### `RunicRebirthCommand.java`

```java
// Before:
.requires(source -> source.hasPermission(2))

// After:
.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
// If Commands.LEVEL_GAMEMASTERS is now a PermissionCheck instance:
// .requires(source -> Commands.LEVEL_GAMEMASTERS.check(source.getPermissions()))
```

- `source.getPlayer()` — verify `CommandSourceStack` API unchanged
- `Player#getPermissionLevel()` if called → `Player#permissions()` then `PermissionSet#hasPermission(...)`

---

## PART 12 — Advanced Rendering Cleanup (26.1–26.2)

### `ChatFormatting` deprecation
- Any `ChatFormatting.RED` etc. in component building → `Style.EMPTY.withColor(TextColor.fromRgb(0xFF5555))` or `Style.EMPTY.withColor(ChatFormatting.RED)` (some overloads remain)
- Files: `DungeonEventHandler.java` (announcements), `DungeonSelectionScreen.java` (status text)

### `MultiBufferSource` removal (26.2)
- Any remaining `MultiBufferSource` parameter usages → `SubmitNodeCollector`
- `OutlineBufferSource` removed — outline rendering via `OrderedSubmitNodeCollector#submitShapeOutline()`

### `Tesselator` removal (26.2)
- `Tesselator.getInstance()` if used in particle/overlay code → GPU buffer APIs

### `Font#drawInBatch()` removal (26.2)
- Any `minecraft.font.drawInBatch(...)` → `font.prepareText()` + `Font.GlyphVisitor#acceptRenderable()` pattern
- Check `DrawingCanvasScreen`, `SpellStackOverlay` for direct font draw calls

### GUI field access changes (26.2)
- `Minecraft.getInstance().screen` → `Minecraft.getInstance().gui.screen()`
- `Minecraft.getInstance().gui.setOverlayMessage(...)` — check if used anywhere

### ItemColor → ItemTintSource (`RunicRebirthClient.java`) (1.21.4)
- `RegisterColorHandlersEvent.Item` registration of dyeable armor color → removed
- Instead: ensure `DataComponents.DYED_COLOR` on `ApprenticeSetItem` (Part 4), register `ItemTintSource.Dye` via `ItemTintSources` registry
- `ItemColors` class removed — no replacement registration site; tints specified in Client Items JSON via `tints` array

### Client Items JSON (1.21.4)
- GeckoLib items (`BasicWand`, `RunicCodexItem`, armor pieces) had `isCustomRenderer()→true`
- `BakedModel#isCustomRenderer` removed — replaced by `Client Items JSON` `minecraft:special` model type
- For each GeckoLib item: create `assets/runicrebirth/items/<item_id>.json` with `minecraft:special` type pointing to `SpecialModelRenderer`
- GeckoLib 5 likely provides its own `SpecialModelRenderer` adapter — consult GeckoLib 5 item rendering docs

### `DataGenHandler.java` — model package move (1.21.4)
- `net.minecraft.data.models.*` → `net.minecraft.client.data.models.*`
- Update imports in any datagen providers using model classes

---

## PART 13 — Advancement API Migration (1.21.11)

### `ModCriteriaTriggers.java`, `HeldSpellWriterTrigger.java`, `MagicKillTrigger.java`
- Package: `net.minecraft.advancements.critereon` → `net.minecraft.advancements.criterion` (1.21.11)
- `SimpleCriterionTrigger` subclass pattern — verify `TriggerInstance` inner class unchanged
- `EntityPredicate.ADVANCEMENT_CODEC` — verify still exists in `MagicKillTrigger`

### `SpellAdvancementHelper.java`
- `AdvancementProgress.isDone()` — verify API unchanged
- `ServerPlayer` advancement management API — verify

### `SpellUnlockEvents.java`
- `LivingDeathEvent` — verify event class name and package

---

## PART 14 — Miscellaneous API Drift

### `ModParticles.java`
- `ParticleType` constructor may change
- `ScaledParticleOption.DESERIALIZER` field removed in newer versions → codec-only approach for particle options

### `ModAttachments.java`
- NeoForge `AttachmentType.Builder` API — verify stable between 1.21.1 and 26.1.x

### `ModDataComponents.java`
- `DataComponentType` registration — stable
- `CustomModelData` type changed to list-based (not used in this mod — no action needed)

### `ModDimensions.java` + custom dimension JSON
- `ResourceKey<Level>` / `ResourceKey<DimensionType>` — stable
- Verify `data/runicrebirth/dimension/dungeon.json` format unchanged

### `DungeonEventHandler.java`
- `LivingDeathEvent`, `PlayerLoggedInEvent`, `PlayerLoggedOutEvent` — verify package/name
- `player.setGameMode(GameType.ADVENTURE)` — verify API unchanged

### `MobDamageModifiers.java`
- `LivingIncomingDamageEvent` — verify package/name

### `DamageSources.java`, `SpellDamageSource.java`, `IMDamageTypes.java`
- Custom damage types and `DamageSource` creation — generally stable; verify

### `RunicRebirthClient.java`
- `RegisterParticleProvidersEvent` — verify name unchanged
- Block entity renderer registration API — verify unchanged
- `EntityRenderDispatcher` — verify `getRenderer()` still public (used by `TargetCircleManager`)
- Remove `RegisterColorHandlersEvent.Item` armor color registration (see Part 12)
- Add any new GeckoLib 5 client setup requirements (consult GeckoLib 5 setup docs)

### `ScaledParticleOption.java`
- `ParticleOptions.DESERIALIZER` pattern removed — use codec-only:
  - Remove `DESERIALIZER` field
  - Codec and StreamCodec fields remain; registration uses codec

### `AbstractMultiblockValidator.java`, `DimensionalOculusValidator.java`, `InfusionAltarValidator.java`, `RunicAnvilValidator.java`
- Pure game-logic classes — minimal API drift; mostly affected by Identifier rename (Part 1)

---

## Execution Order (Recommended)

Split into these agent tasks in order:

| # | Task | Key Files | Risk |
|---|------|-----------|------|
| 0 | Build system | `build.gradle`, `gradle.properties`, `neoforge.mods.toml` | Medium |
| 1 | ResourceLocation → Identifier | ALL Java files | Low (mechanical) |
| 2 | Item `setId()` + Properties | `ModBlocks`, `ModItems`, all item classes | Low |
| 3 | InteractionResult unification | All items + blocks with `use()` | Low |
| 4 | Recipe serializers → records | 2 serializer classes + 2 recipe classes + `ModRecipeSerializers` | Medium |
| 5 | RecipeProvider.Runner wrap | `DataGenHandler` | Low |
| 6 | SavedData → SavedDataType | `DungeonInstanceManager` | Medium |
| 7 | NBT Optional API | ~10 block entity + data files | Medium |
| 8 | GeckoLib: GeoEntity interfaces | ~25 entity files (game thread) | High |
| 9 | GeckoLib: GeoModel subclasses | ~25 model files | Medium |
| 10 | GeckoLib: Entity renderers | ~25 renderer files | High |
| 11 | GeckoLib: Block entity renderers | ~5 renderer files | High |
| 12 | GeckoLib: Item + armor renderers | ~5 files | High |
| 13 | Particle system rewrite | 4 particle files | High |
| 14 | Armor system → EQUIPPABLE component | `MagicArmorItem`, `ApprenticeSetItem` | High |
| 15 | GUI blit signatures | 3 overlay + 3 screen files | Medium |
| 16 | MultiBufferSource removal cleanup | Any remaining usages | Medium |
| 17 | Modonomicon compat | 3 compat + 3-4 datagen files | Medium |
| 18 | Permission system | `RunicRebirthCommand` | Low |
| 19 | Advancement package rename | 3 trigger files | Low |
| 20 | ChatFormatting → Style | Event handlers, screens | Low |
| 21 | ItemColor → ItemTintSource | `RunicRebirthClient`, item JSON files | Low |
| 22 | Client Items JSON for GeckoLib items | 1 file per GeckoLib item | Medium |
| 23 | Remaining API drift (misc) | Various | Low |
| 24 | Compile + fix errors iteratively | — | — |

> **Tasks 8–12 (GeckoLib) are the riskiest.** GeckoLib 5's exact API must be read from https://wiki.geckolib.com/docs/geckolib5/ and its source before implementing. Complete Tasks 0–7 first to establish a compiling baseline, then tackle GeckoLib. Tasks 13–23 can be done in parallel with or after GeckoLib once the rendering paradigm is understood.

---

## Reference Links

- NeoForge primers: `https://docs.neoforged.net/primer/docs/<version>/`
  - 1.21.2, 1.21.4, 1.21.5, 1.21.9, 1.21.11, 26.1, 26.2
- GeckoLib 5 wiki: `https://wiki.geckolib.com/docs/geckolib5/`
- GeckoLib 5 update guide: `https://wiki.geckolib.com/docs/geckolib5/updating/important/conceptual-changes`
- Modonomicon migration: `https://klikli-dev.github.io/modonomicon/docs/getting-started/updating-from-1.21.1-to-26.1.2/`
- NeoForge docs 26.x: `https://docs.neoforged.net/docs/26.x/` (verify URL)

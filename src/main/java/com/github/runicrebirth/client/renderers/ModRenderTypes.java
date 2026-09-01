package com.github.runicrebirth.client.renderers;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public final class ModRenderTypes {

    private static final RenderPipeline UNLIT_NO_CULL = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
        .withLocation("pipeline/runicrebirth_unlit_no_cull")
        .withVertexShader("core/entity")
        .withFragmentShader("core/entity")
        .withShaderDefine("EMISSIVE")
        .withShaderDefine("NO_OVERLAY")
        .withShaderDefine("NO_CARDINAL_LIGHTING")
        .withSampler("Sampler0")
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
        .withCull(false)
        .build();

    public static RenderType entityTranslucentNoDepth(Identifier texture) {
        RenderSetup state = RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT)
            .withTexture("Sampler0", texture)
            .useLightmap()
            .useOverlay()
            .affectsCrumbling()
            .sortOnUpload()
            .createRenderSetup();
        return RenderType.create("magic_translucent_no_depth", state);
    }

    public static RenderType entityTranslucentNoCullNoShade(Identifier texture) {
        RenderSetup state = RenderSetup.builder(UNLIT_NO_CULL)
            .withTexture("Sampler0", texture)
            .useOverlay()
            .sortOnUpload()
            .createRenderSetup();
        return RenderType.create("magic_translucent_no_shade", state);
    }

    public static RenderType entityUnlit(Identifier texture) {
        RenderSetup state = RenderSetup.builder(UNLIT_NO_CULL)
            .withTexture("Sampler0", texture)
            .useOverlay()
            .sortOnUpload()
            .createRenderSetup();
        return RenderType.create("magic_unlit", state);
    }

    private ModRenderTypes() {}
}

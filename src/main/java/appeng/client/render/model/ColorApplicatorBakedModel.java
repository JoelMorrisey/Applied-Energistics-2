package appeng.client.render.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import appeng.mixins.BakedQuadAccessor;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;

/**
 * This baked model will take the generated item model for the colored color applicator,
 * and associate tint indices with the added layers that correspond to the light/medium/dark
 * variants of the {@link appeng.api.util.AEColor}.
 * <p>
 * Using the color provider registered in {@link appeng.items.tools.powered.ColorApplicatorItemRendering},
 * this results in the right color being multiplied with the corresponding layer.
 */
class ColorApplicatorBakedModel extends ForwardingBakedModel {

    private final EnumMap<Direction, List<BakedQuad>> quadsBySide;

    private final List<BakedQuad> generalQuads;

    ColorApplicatorBakedModel(BakedModel baseModel, Sprite texDark,
                              Sprite texMedium, Sprite texBright) {
        this.wrapped = baseModel;

        // Put the tint indices in... Since this is an item model, we are ignoring rand
        this.generalQuads = this.fixQuadTint(null, texDark, texMedium, texBright);
        this.quadsBySide = new EnumMap<>(Direction.class);
        for (Direction facing : Direction.values()) {
            this.quadsBySide.put(facing, this.fixQuadTint(facing, texDark, texMedium, texBright));
        }
    }

    private Sprite getSprite(BakedQuad quad) {
        return ((BakedQuadAccessor) quad).getSprite();
    }

    private List<BakedQuad> fixQuadTint(Direction facing, Sprite texDark, Sprite texMedium,
                                        Sprite texBright) {
        List<BakedQuad> quads = this.wrapped.getQuads(null, facing, new Random(0));
        List<BakedQuad> result = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads) {
            int tint;

            if (getSprite(quad) == texDark) {
                tint = 1;
            } else if (getSprite(quad) == texMedium) {
                tint = 2;
            } else if (getSprite(quad) == texBright) {
                tint = 3;
            } else {
                result.add(quad);
                continue;
            }

            BakedQuad newQuad = new BakedQuad(quad.getVertexData(), tint, quad.getFace(), getSprite(quad),
                    quad.hasShade());
            result.add(newQuad);
        }

        return result;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        if (side == null) {
            return this.generalQuads;
        }
        return this.quadsBySide.get(side);
    }

}
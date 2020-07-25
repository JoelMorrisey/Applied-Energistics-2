
package appeng.client.render.cablebus;

import appeng.api.parts.IDynamicPartBakedModel;
import appeng.api.util.AEColor;
import appeng.util.Platform;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class P2PTunnelFrequencyBakedModel implements IDynamicPartBakedModel {

    private final Renderer renderer = RendererAccess.INSTANCE.getRenderer();

    private final Sprite texture;

    private final static Cache<Long, Mesh> modelCache = CacheBuilder.newBuilder().maximumSize(100).build();

    private static final int[][] QUAD_OFFSETS = new int[][]{{4, 10, 2}, {10, 10, 2}, {4, 4, 2}, {10, 4, 2}};

    public P2PTunnelFrequencyBakedModel(final Sprite texture) {
        this.texture = texture;
    }

    @Override
    public void emitQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context,
                          Direction partSide, @Nullable Object modelData) {
        if (!(modelData instanceof Long)) {
            return;
        }
        long frequency = (long) modelData;

        Mesh frequencyMesh = getFrequencyModel(frequency);
        if (frequencyMesh != null) {
            context.meshConsumer().accept(frequencyMesh);
        }
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelTransformation.NONE;
    }

    private Mesh createFrequencyMesh(final short frequency, final boolean active) {

        MeshBuilder meshBuilder = renderer.meshBuilder();

        final AEColor[] colors = Platform.p2p().toColors(frequency);
        final CubeBuilder cb = new CubeBuilder(meshBuilder.getEmitter());

        cb.setTexture(this.texture);
        cb.useStandardUV();
        cb.setRenderFullBright(active);

        for (int i = 0; i < 4; ++i) {
            final int[] offs = QUAD_OFFSETS[i];
            for (int j = 0; j < 4; ++j) {
                final float[] cv = colors[j].dye.getColorComponents();
                if (active) {
                    cb.setColorRGB(cv[0], cv[1], cv[2]);
                } else {
                    cb.setColorRGB(cv[0] * 0.5f, cv[1] * 0.5f, cv[2] * 0.5f);
                }

                final int startx = j % 2;
                final int starty = 1 - j / 2;

                cb.addCube(offs[0] + startx, offs[1] + starty, offs[2], offs[0] + startx + 1, offs[1] + starty + 1,
                        offs[2] + 1);
            }

        }

        return meshBuilder.build();
    }

    private Mesh getFrequencyModel(long partFlags) {
        try {
            return modelCache.get(partFlags, () -> {
                short frequency = (short) (partFlags & 0xffffL);
                boolean active = (partFlags & 0x10000L) != 0;
                return this.createFrequencyMesh(frequency, active);
            });
        } catch (ExecutionException e) {
            return null;
        }
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public Sprite getSprite() {
        return this.texture;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }
}
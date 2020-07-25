package appeng.client.render.model;

import appeng.client.render.BasicUnbakedModel;
import appeng.core.AppEng;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Model wrapper for the biometric card item model, which combines a base card
 * layer with a "visual hash" of the player name
 */
public class BiometricCardModel implements BasicUnbakedModel {

    public static final Identifier MODEL_BASE = new Identifier(AppEng.MOD_ID, "item/biometric_card_base");
    private static final SpriteIdentifier TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX,
            new Identifier(AppEng.MOD_ID, "item/biometric_card_hash"));

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.singleton(MODEL_BASE);
    }

    @Override
    public Stream<SpriteIdentifier> getAdditionalTextures() {
        return Stream.of(TEXTURE);
    }

    @Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        Sprite texture = textureGetter.apply(TEXTURE);

        BakedModel baseModel = loader.bake(MODEL_BASE, rotationContainer);

        return new BiometricCardBakedModel(baseModel, texture);
    }

}
package xfacthd.framedblocks.common.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;

@JeiPlugin
public final class FramedJeiPlugin implements IModPlugin
{
    private static final ResourceLocation ID = Utils.rl("jei_plugin");
    static final RecipeType<FramingSawRecipe> FRAMING_SAW_RECIPE_TYPE = new RecipeType<>(
            Utils.rl("framing_saw"), FramingSawRecipe.class
    );

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        registration.addRecipeCategories(new FramingSawRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        registration.addRecipes(FRAMING_SAW_RECIPE_TYPE, FramingSawRecipeCache.get(true).getRecipes());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration)
    {
        registration.addRecipeTransferHandler(
                new FramingSawTransferHandler(registration.getTransferHelper()),
                FRAMING_SAW_RECIPE_TYPE
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(
                new ItemStack(FBContent.blockFramingSaw.get()),
                FRAMING_SAW_RECIPE_TYPE
        );
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
    {
        JeiCompat.Guarded.acceptRuntime(jeiRuntime);
    }

    @Override
    public void onRuntimeUnavailable()
    {
        JeiCompat.Guarded.acceptRuntime(null);
    }

    @Override
    public ResourceLocation getPluginUid() { return ID; }
}

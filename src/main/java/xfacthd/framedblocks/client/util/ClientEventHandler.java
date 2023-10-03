package xfacthd.framedblocks.client.util;

import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeCache;

public final class ClientEventHandler
{
    public static void onRecipesUpdated(final RecipesUpdatedEvent event)
    {
        FramingSawRecipeCache.get(true).update(event.getRecipeManager());
    }

    public static void onClientDisconnect(final ClientPlayerNetworkEvent.LoggingOut event)
    {
        FramingSawRecipeCache.get(true).clear();
    }



    private ClientEventHandler() { }
}

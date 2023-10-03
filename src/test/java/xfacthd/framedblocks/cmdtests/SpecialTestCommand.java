package xfacthd.framedblocks.cmdtests;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FileUtils;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.cmdtests.tests.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpecialTestCommand
{
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu_MM_dd-kk_mm_ss");
    private static final Path EXPORT_DIR = Path.of("./logs/test");

    @SubscribeEvent
    public static void registerCommands(final RegisterCommandsEvent event)
    {
        event.getDispatcher().register(Commands.literal("fbtest")
                .then(Commands.literal("skippredicates")
                        .executes(async(SkipPredicates.NAME, SkipPredicates::testSkipPredicates))
                )
                .then(Commands.literal("recipecollision")
                        .executes(async(RecipeCollisions.NAME, RecipeCollisions::checkForRecipeCollisions))
                )
                .then(Commands.literal("recipepresent")
                        .executes(RecipePresent::checkForRecipePresence)
                )
        );
    }

    private static Command<CommandSourceStack> async(String testName, AsyncCommand cmd)
    {
        return ctx ->
        {
            ctx.getSource().sendSuccess(Component.literal("Starting " + testName + " test"), false);

            Consumer<Component> appender = msg -> ctx.getSource().getServer().submit(() ->
                    ctx.getSource().source.sendSystemMessage(msg)
            );
            runGuardedOffThread(testName, appender, () -> cmd.run(ctx, appender));

            return Command.SINGLE_SUCCESS;
        };
    }

    public static void runGuardedOffThread(String testName, Consumer<Component> msgQueueAppender, Runnable test)
    {
        Util.backgroundExecutor().submit(() ->
        {
            try
            {
                test.run();
            }
            catch (Throwable t)
            {
                msgQueueAppender.accept(Component.literal(
                        "Encountered an uncaught error while testing " + testName + ". See log for details"
                ));
                FramedBlocks.LOGGER.error("Encountered an error while testing {}", testName, t);
            }
        });
    }

    public static Component writeResultToFile(String filePrefix, String data)
    {
        FileUtils.getOrCreateDirectory(EXPORT_DIR, "Test results");
        String dateTime = FORMATTER.format(LocalDateTime.now());
        Path path = EXPORT_DIR.resolve("%s_%s.txt".formatted(filePrefix, dateTime));

        try
        {
            Files.writeString(path, data, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            String pathText = path.toAbsolutePath().toString();
            Component pathComponent = Component.literal(pathText).withStyle(style ->
                    style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, pathText))
                            .applyFormat(ChatFormatting.UNDERLINE)
            );
            return Component.literal("Tests results exported to ").append(pathComponent);
        }
        catch (IOException e)
        {
            FramedBlocks.LOGGER.error("Encountered an error while exporting test results", e);
            return Component.literal("Export of test results failed with error: %s: %s".formatted(
                    e.getClass().getSimpleName(), e.getMessage()
            ));
        }
    }

    public interface AsyncCommand
    {
        void run(CommandContext<CommandSourceStack> ctx, Consumer<Component> msgQueueAppender);
    }



    private SpecialTestCommand() { }
}

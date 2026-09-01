package com.github.runicrebirth.command;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.advancement.SpellAdvancementHelper;
import com.github.runicrebirth.api.registry.SpellTypeRegistry;
import com.github.runicrebirth.api.spells.SpellType;
import com.github.runicrebirth.capabilities.dungeon.DungeonData;
import com.github.runicrebirth.network.DungeonDataSyncS2CPacket;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

@EventBusSubscriber(modid = RunicRebirth.MODID)
public final class RunicRebirthCommand {

    private static final List<Identifier> ELEMENT_ADVANCEMENTS = List.of(
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "elements/root"),
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "elements/fire"),
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "elements/ice"),
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "elements/wind"),
        Identifier.fromNamespaceAndPath(RunicRebirth.MODID, "elements/earth")
    );

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_SPELLS = (context, builder) -> {
        Iterable<SpellType> types = SpellTypeRegistry.REGISTRY;
        return SharedSuggestionProvider.suggestResource(
            SpellTypeRegistry.REGISTRY.keySet().stream()
                .filter(id -> context.getSource().getServer().getAdvancements()
                    .get(SpellAdvancementHelper.advancementIdFor(id)) != null),
            builder
        );
    };

    private RunicRebirthCommand() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("runicrebirth")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("unlock")
                    .then(Commands.literal("all")
                        .executes(RunicRebirthCommand::unlockAll))
                    .then(Commands.argument("spell", IdentifierArgument.id())
                        .suggests(SUGGEST_SPELLS)
                        .executes(RunicRebirthCommand::unlockSpell)))
                .then(Commands.literal("lock")
                    .then(Commands.literal("all")
                        .executes(RunicRebirthCommand::lockAll))
                    .then(Commands.argument("spell", IdentifierArgument.id())
                        .suggests(SUGGEST_SPELLS)
                        .executes(RunicRebirthCommand::lockSpell)))
                .then(Commands.literal("kp")
                    .then(Commands.literal("add")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                            .then(Commands.argument("player", EntityArgument.player())
                                .executes(RunicRebirthCommand::kpAdd))))
                    .then(Commands.literal("remove")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                            .then(Commands.argument("player", EntityArgument.player())
                                .executes(RunicRebirthCommand::kpRemove)))))
                .then(Commands.literal("unlocks")
                    .then(Commands.literal("reset")
                        .executes(RunicRebirthCommand::unlocksReset)))
        );
    }

    private static int unlockSpell(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Identifier spellId = IdentifierArgument.getId(context, "spell");
        SpellType type = SpellTypeRegistry.get(spellId);
        if (type == null) {
            context.getSource().sendFailure(Component.literal("Unknown spell: " + spellId));
            return 0;
        }
        SpellAdvancementHelper.grantSpellAdvancement(player, type);
        context.getSource().sendSuccess(() -> Component.literal("Unlocked " + spellId.getPath()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int lockSpell(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Identifier spellId = IdentifierArgument.getId(context, "spell");
        SpellType type = SpellTypeRegistry.get(spellId);
        if (type == null) {
            context.getSource().sendFailure(Component.literal("Unknown spell: " + spellId));
            return 0;
        }
        SpellAdvancementHelper.revokeSpellAdvancement(player, type);
        context.getSource().sendSuccess(() -> Component.literal("Locked " + spellId.getPath()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int unlockAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        for (SpellType type : SpellTypeRegistry.REGISTRY) {
            SpellAdvancementHelper.grantSpellAdvancement(player, type);
        }
        for (Identifier id : ELEMENT_ADVANCEMENTS) {
            SpellAdvancementHelper.grantAdvancement(player, id);
        }
        context.getSource().sendSuccess(() -> Component.literal("Unlocked all spells and elements"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int lockAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        for (SpellType type : SpellTypeRegistry.REGISTRY) {
            SpellAdvancementHelper.revokeSpellAdvancement(player, type);
        }
        for (Identifier id : ELEMENT_ADVANCEMENTS) {
            SpellAdvancementHelper.revokeAdvancement(player, id);
        }
        context.getSource().sendSuccess(() -> Component.literal("Locked all spells and elements"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int kpAdd(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        DungeonData data = DungeonData.of(target);
        data.addKnowledgePoints(amount);
        DungeonDataSyncS2CPacket.sendTo(target);
        int newKp = data.getKnowledgePoints();
        context.getSource().sendSuccess(() -> Component.literal(
                "Added " + amount + " KP to " + target.getName().getString() + " (now " + newKp + ")"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int kpRemove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "player");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        DungeonData data = DungeonData.of(target);
        int actual = Math.min(amount, data.getKnowledgePoints());
        data.addKnowledgePoints(-actual);
        DungeonDataSyncS2CPacket.sendTo(target);
        int newKp = data.getKnowledgePoints();
        context.getSource().sendSuccess(() -> Component.literal(
                "Removed " + actual + " KP from " + target.getName().getString() + " (now " + newKp + ")"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int unlocksReset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        DungeonData data = DungeonData.of(player);
        data.clearUnlockedEntries();
        DungeonDataSyncS2CPacket.sendTo(player);
        context.getSource().sendSuccess(() -> Component.literal("Reset all unlock tree entries"), true);
        return Command.SINGLE_SUCCESS;
    }
}

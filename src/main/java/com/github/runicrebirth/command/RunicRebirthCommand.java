package com.github.runicrebirth.command;

import com.github.runicrebirth.RunicRebirth;
import com.github.runicrebirth.advancement.SpellAdvancementHelper;
import com.github.runicrebirth.api.registry.SpellTypeRegistry;
import com.github.runicrebirth.api.spells.SpellType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

@EventBusSubscriber(modid = RunicRebirth.MODID)
public final class RunicRebirthCommand {

    private static final List<ResourceLocation> ELEMENT_ADVANCEMENTS = List.of(
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "elements/root"),
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "elements/fire"),
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "elements/ice"),
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "elements/wind"),
        ResourceLocation.fromNamespaceAndPath(RunicRebirth.MODID, "elements/earth")
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
                .requires(src -> src.hasPermission(2))
                .then(Commands.literal("unlock")
                    .then(Commands.literal("all")
                        .executes(RunicRebirthCommand::unlockAll))
                    .then(Commands.argument("spell", ResourceLocationArgument.id())
                        .suggests(SUGGEST_SPELLS)
                        .executes(RunicRebirthCommand::unlockSpell)))
                .then(Commands.literal("lock")
                    .then(Commands.literal("all")
                        .executes(RunicRebirthCommand::lockAll))
                    .then(Commands.argument("spell", ResourceLocationArgument.id())
                        .suggests(SUGGEST_SPELLS)
                        .executes(RunicRebirthCommand::lockSpell)))
        );
    }

    private static int unlockSpell(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ResourceLocation spellId = ResourceLocationArgument.getId(context, "spell");
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
        ResourceLocation spellId = ResourceLocationArgument.getId(context, "spell");
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
        for (ResourceLocation id : ELEMENT_ADVANCEMENTS) {
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
        for (ResourceLocation id : ELEMENT_ADVANCEMENTS) {
            SpellAdvancementHelper.revokeAdvancement(player, id);
        }
        context.getSource().sendSuccess(() -> Component.literal("Locked all spells and elements"), true);
        return Command.SINGLE_SUCCESS;
    }
}

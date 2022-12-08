package me.melontini.tweaks.util;

import me.melontini.crackerutil.CrackerLog;
import me.melontini.crackerutil.util.MakeSure;
import me.melontini.tweaks.Tweaks;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementPositioner;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static me.melontini.tweaks.Tweaks.RANDOM;

public class MiscUtil {
    public static final Map<RecipeType<?>, Consumer<Map<Identifier, Advancement.Builder>, Recipe<?>>> TYPE_CONSUMER_MAP = new ConcurrentHashMap<>();

    public static <T> T pickRandomEntryFromList(@NotNull List<T> list) {
        MakeSure.notEmpty(list);
        int index = RANDOM.nextInt(list.size());
        return list.get(index);
    }


    public static void hackAdvancements(MinecraftServer server) {
        Map<Identifier, Advancement.Builder> map = new ConcurrentHashMap<>();
        int i = 0;
        Collection<Recipe<?>> collection = server.getRecipeManager().values();
        for (Recipe<?> recipe : collection) {
            if (Tweaks.CONFIG.autogenRecipeAdvancements.blacklistedRecipeNamespaces.contains(recipe.getId().getNamespace()))
                continue;
            if (Tweaks.CONFIG.autogenRecipeAdvancements.blacklistedRecipeIds.contains(recipe.getId().toString()))
                continue;

            if (TYPE_CONSUMER_MAP.get(recipe.getType()) != null) {
                i++;
                TYPE_CONSUMER_MAP.get(recipe.getType()).accept(map, recipe);
            } else {
                if (!recipe.getIngredients().isEmpty()) {
                    if (recipe.isIgnoredInRecipeBook() && Tweaks.CONFIG.autogenRecipeAdvancements.ignoreRecipesHiddenInTheRecipeBook)
                        continue;
                    i++;
                    map.put(new Identifier(recipe.getId().getNamespace(), "recipes/gen/generic/" + recipe.getId().toString().replace(":", "_")), MiscUtil.createAdvBuilder(recipe.getId(), recipe.getIngredients().toArray(Ingredient[]::new)));
                }
            }
        }

        AdvancementManager advManager = server.getAdvancementLoader().manager;
        advManager.load(map);

        for (Advancement advancement : advManager.getRoots()) {
            if (advancement.getDisplay() != null) {
                AdvancementPositioner.arrangeForTree(advancement);
            }
        }

        server.getPlayerManager().getPlayerList().forEach(entity -> server.getPlayerManager().getAdvancementTracker(entity).reload(server.getAdvancementLoader()));
        CrackerLog.info("finished hacking-in {} recipe advancements", i);
        map.clear();
    }

    public static Advancement.@NotNull Builder createAdvBuilder(Identifier id, Ingredient... ingredients) {
        MakeSure.notEmpty(ingredients);// shouldn't really happen
        //TODO maybe filter identical stacks
        var builder = Advancement.Builder.create();
        builder.parent(Identifier.tryParse("minecraft:recipes/root"));

        List<String> names = new ArrayList<>();
        for (int i = 0; i < ingredients.length; i++) {
            Ingredient ingredient = ingredients[i];
            List<ItemPredicate> predicates = new ArrayList<>();
            for (int j = 0; j < ingredient.entries.length; j++) {
                Ingredient.Entry entry = ingredient.entries[j];
                if (entry instanceof Ingredient.StackEntry stackEntry) {
                    if (!stackEntry.stack.isEmpty()) {
                        names.add(String.valueOf(i));
                        predicates.add(new ItemPredicate(null, Set.of(stackEntry.stack.getItem()), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, new EnchantmentPredicate[0], new EnchantmentPredicate[0], null, stackEntry.stack.getNbt() != null ? new NbtPredicate(stackEntry.stack.getNbt()) : NbtPredicate.ANY));
                    }
                } else if (entry instanceof Ingredient.TagEntry tagEntry) {
                    names.add(String.valueOf(i));
                    predicates.add(new ItemPredicate(tagEntry.tag, null, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, new EnchantmentPredicate[0], new EnchantmentPredicate[0], null, NbtPredicate.ANY));
                } else {
                    CrackerLog.error("unknown ingredient found in {}", id);
                }
            }
            builder.criterion(String.valueOf(i), new InventoryChangedCriterion.Conditions(EntityPredicate.Extended.EMPTY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, predicates.toArray(ItemPredicate[]::new)));
        }
        builder.criterion("has_recipe", new RecipeUnlockedCriterion.Conditions(EntityPredicate.Extended.EMPTY, id));

        String[][] reqs;
        if (Tweaks.CONFIG.autogenRecipeAdvancements.requireAllItems) {
            reqs = new String[names.size()][2];
            for (int i = 0; i < names.size(); i++) {
                String s = names.get(i);
                reqs[i][0] = s;
                reqs[i][1] = "has_recipe";
            }
        } else {
            reqs = new String[1][names.size() + 1];
            for (int i = 0; i < names.size(); i++) {
                String s = names.get(i);
                reqs[0][i] = s;
            }
            reqs[0][names.size()] = "has_recipe";
        }
        builder.requirements(reqs);

        builder.rewards(new AdvancementRewards(0, new Identifier[0], new Identifier[]{id}, CommandFunction.LazyContainer.EMPTY));
        return builder;
    }

    @FunctionalInterface
    public interface Consumer<T, U> {
        void accept(T t, U u);
    }
}

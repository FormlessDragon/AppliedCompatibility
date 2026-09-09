package github.formlessdragon.appcompat.bridge.jecharacters;

import me.towdium.jecharacters.JechConfig;

import java.util.Arrays;

public final class JechSearchCompatibility {

    private static final String[] AE2_SEARCH_METHODS = {
        "ae2.client.gui.me.search.NameSearchPredicate:test",
        "ae2.client.gui.me.search.ModSearchPredicate:test",
        "ae2.client.gui.me.search.ItemIdSearchPredicate:test",
        "ae2.client.gui.me.search.TooltipsSearchPredicate:test",
        "ae2.client.gui.me.search.OreDictSearchPredicate:matchesTerm",
        "ae2.client.gui.widgets.CPUSelectionList$ViewState:lambda$compute$0",
        "ae2.client.gui.me.patternaccess.PatternAccessDisplaySupport:matchesSearch",
        "ae2.client.gui.me.patternaccess.PatternAccessDisplaySupport:stackMatchesInputFilter",
        "ae2.client.gui.me.patternaccess.PatternAccessDisplaySupport:stackMatchesOutputFilter",
        "ae2.items.tools.advancedmemorycard.AdvancedMemoryCardFilterLogic:matchesSearch",
        "ae2.container.implementations.ContainerProviderSelect:rebuildFilteredEntries"
    };

    private JechSearchCompatibility() {
    }

    public static void addAE2SearchMethods() {
        int originalLength = JechConfig.listAdditionalString.length;
        String[] methods = Arrays.copyOf(
            JechConfig.listAdditionalString,
            originalLength + AE2_SEARCH_METHODS.length
        );
        int size = originalLength;
        for (String searchMethod : AE2_SEARCH_METHODS) {
            boolean present = false;
            for (int i = 0; i < originalLength; i++) {
                if (searchMethod.equals(methods[i])) {
                    present = true;
                    break;
                }
            }
            if (!present) {
                methods[size++] = searchMethod;
            }
        }
        JechConfig.listAdditionalString = size == methods.length ? methods : Arrays.copyOf(methods, size);
    }
}

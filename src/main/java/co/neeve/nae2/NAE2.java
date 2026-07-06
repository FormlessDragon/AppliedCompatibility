package co.neeve.nae2;

import co.neeve.nae2.common.registration.Registration;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NAE2 {

    public static final NAE2 instance = new NAE2();
    private static final Logger LOGGER = LogManager.getLogger(Tags.MODID);
    private static final Registration REGISTRATION = new Registration();

    private NAE2() {
    }

    public static Registration definitions() {
        return REGISTRATION;
    }

    public static Logger logger() {
        return LOGGER;
    }

    public static ItemStack icon() {
        return REGISTRATION.items().patternMultiTool().maybeStack(1).orElse(ItemStack.EMPTY);
    }
}

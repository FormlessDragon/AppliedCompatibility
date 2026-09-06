package github.formlessdragon.appcompat.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class JechCallHookTransformer implements IClassTransformer {

    private static final String TARGET_CLASS = "me.towdium.jecharacters.core.JechCallHook";
    private static final String HOOK_OWNER =
        "github/formlessdragon/appcompat/bridge/jecharacters/JechSearchCompatibility";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!TARGET_CLASS.equals(name) && !TARGET_CLASS.equals(transformedName)) {
            return basicClass;
        }
        if (basicClass == null) {
            throw new IllegalStateException("JEC call hook class bytes are unavailable");
        }

        ClassReader reader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode(Opcodes.ASM9);
        reader.accept(classNode, 0);

        MethodNode targetMethod = null;
        for (MethodNode method : classNode.methods) {
            if ("call".equals(method.name) && "()Ljava/lang/Void;".equals(method.desc)) {
                targetMethod = method;
                break;
            }
        }
        if (targetMethod == null) {
            throw new IllegalStateException("JEC call hook method call()Ljava/lang/Void; was not found");
        }

        targetMethod.instructions.insert(new MethodInsnNode(
            Opcodes.INVOKESTATIC,
            HOOK_OWNER,
            "addAE2SearchMethods",
            "()V",
            false
        ));

        ClassWriter writer = new ClassWriter(reader, 0);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}

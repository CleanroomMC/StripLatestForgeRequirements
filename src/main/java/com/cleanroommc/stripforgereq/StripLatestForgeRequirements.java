package com.cleanroommc.stripforgereq;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.annotation.Nullable;
import java.util.Map;

public class StripLatestForgeRequirements implements IFMLLoadingPlugin {
    public static final class Transformer implements IClassTransformer {
        @Override
        public byte[] transform(String name, String transformedName, byte[] basicClass) {
            if ("net.minecraftforge.fml.common.FMLModContainer".equals(transformedName)) {
                ClassNode classNode = new ClassNode();
                new ClassReader(basicClass).accept(classNode, 0);

                // append Hooks::voidForgeRequirements to the end of FMLModContainer::bindMetadata
                for (MethodNode method : classNode.methods) {
                    if ("bindMetadata".equals(method.name)) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/fml/common/FMLModContainer", "modMetadata", "Lnet/minecraftforge/fml/common/ModMetadata;"));
                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/cleanroommc/stripforgereq/StripLatestForgeRequirements$Hooks", "voidForgeRequirements", "(Lnet/minecraftforge/fml/common/ModMetadata;)V", false));

                        method.instructions.insertBefore(method.instructions.getLast().getPrevious(), list);
                        break;
                    }
                }

                // write changes
                ClassWriter writer = new ClassWriter(0);
                classNode.accept(writer);
                return writer.toByteArray();
            }

            return basicClass;
        }
    }

    public static final class Hooks {
        public static void voidForgeRequirements(ModMetadata modMetadata) {
            if (modMetadata.requiredMods != null) modMetadata.requiredMods.removeIf(av -> av.getLabel().equals("forge"));
            if (modMetadata.dependencies != null) modMetadata.dependencies.removeIf(av -> av.getLabel().equals("forge"));
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { StripLatestForgeRequirements.class.getName() + "$Transformer" };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}

/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2015, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2. If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.items.materials;

import github.formlessdragon.appcompat.bridge.ae.LegacyAeItemMappings;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public enum MaterialType {
    INVALID_TYPE(-1, "material_invalid_type"),

    CERTUS_QUARTZ_CRYSTAL(0, "material_certus_quartz_crystal", "crystalCertusQuartz"),
    CERTUS_QUARTZ_CRYSTAL_CHARGED(1, "material_certus_quartz_crystal_charged"),

    CERTUS_QUARTZ_DUST(2, "material_certus_quartz_dust", "dustCertusQuartz"),
    NETHER_QUARTZ_DUST(3, "material_nether_quartz_dust", "dustNetherQuartz,dustQuartz"),
    FLOUR(4, "material_flour", "dustWheat"),
    GOLD_DUST(51, "material_gold_dust", "dustGold"),
    IRON_DUST(49, "material_iron_dust", "dustIron"),

    SILICON(5, "material_silicon", "itemSilicon"),
    MATTER_BALL(6, "material_matter_ball"),

    FLUIX_CRYSTAL(7, "material_fluix_crystal", "crystalFluix"),
    FLUIX_DUST(8, "material_fluix_dust", "dustFluix"),
    FLUIX_PEARL(9, "material_fluix_pearl", "pearlFluix"),

    PURIFIED_CERTUS_QUARTZ_CRYSTAL(10, "material_purified_certus_quartz_crystal", "crystalPureCertusQuartz"),
    PURIFIED_NETHER_QUARTZ_CRYSTAL(11, "material_purified_nether_quartz_crystal", "crystalPureNetherQuartz"),
    PURIFIED_FLUIX_CRYSTAL(12, "material_purified_fluix_crystal", "crystalPureFluix"),

    CALCULATION_PROCESSOR_PRESS(13, "material_calculation_processor_press"),
    ENGINEERING_PROCESSOR_PRESS(14, "material_engineering_processor_press"),
    LOGIC_PROCESSOR_PRESS(15, "material_logic_processor_press"),

    CALCULATION_PROCESSOR_PRINT(16, "material_calculation_processor_print"),
    ENGINEERING_PROCESSOR_PRINT(17, "material_engineering_processor_print"),
    LOGIC_PROCESSOR_PRINT(18, "material_logic_processor_print"),

    SILICON_PRESS(19, "material_silicon_press"),
    SILICON_PRINT(20, "material_silicon_print"),

    NAME_PRESS(21, "material_name_press"),

    LOGIC_PROCESSOR(22, "material_logic_processor"),
    CALCULATION_PROCESSOR(23, "material_calculation_processor"),
    ENGINEERING_PROCESSOR(24, "material_engineering_processor"),

    BASIC_CARD(25, "material_basic_card"),
    CARD_REDSTONE(26, "material_card_redstone"),
    CARD_CAPACITY(27, "material_card_capacity"),

    ADVANCED_CARD(28, "material_advanced_card"),
    CARD_FUZZY(29, "material_card_fuzzy"),
    CARD_SPEED(30, "material_card_speed"),
    CARD_INVERTER(31, "material_card_inverter"),

    CELL2_SPATIAL_PART(32, "material_cell2_spatial_part"),
    CELL16_SPATIAL_PART(33, "material_cell16_spatial_part"),
    CELL128_SPATIAL_PART(34, "material_cell128_spatial_part"),

    CELL1K_PART(35, "material_cell1k_part"),
    CELL4K_PART(36, "material_cell4k_part"),
    CELL16K_PART(37, "material_cell16k_part"),
    CELL64K_PART(38, "material_cell64k_part"),
    EMPTY_STORAGE_CELL(39, "material_empty_storage_cell"),

    WOODEN_GEAR(40, "material_wooden_gear", "gearWood"),

    WIRELESS(41, "material_wireless"),
    WIRELESS_BOOSTER(42, "material_wireless_booster"),

    FORMATION_CORE(43, "material_formation_core"),
    ANNIHILATION_CORE(44, "material_annihilation_core"),

    SKY_DUST(45, "material_sky_dust"),

    ENDER_DUST(46, "material_ender_dust", "dustEnder,dustEnderPearl"),
    SINGULARITY(47, "material_singularity"),
    QUANTUM_ENTANGLED_SINGULARITY(48, "material_quantum_entangled_singularity"),

    BLANK_PATTERN(52, "material_blank_pattern"),
    CARD_CRAFTING(53, "material_card_crafting"),

    FLUID_CELL1K_PART(54, "material_fluid_cell1k_part"),
    FLUID_CELL4K_PART(55, "material_fluid_cell4k_part"),
    FLUID_CELL16K_PART(56, "material_fluid_cell16k_part"),
    FLUID_CELL64K_PART(57, "material_fluid_cell64k_part"),

    CARD_PATTERN_EXPANSION(58, "material_card_pattern_expansion"),
    CARD_QUANTUM_LINK(59, "material_card_quantum_link"),
    CARD_MAGNET(60, "material_card_magnet"),
    CARD_STICKY(61, "material_card_sticky"),
    ;

    private static final String LEGACY_MATERIAL_ITEM = "appliedenergistics2:material";

    private final ModelResourceLocation model;
    private final int damageValue;
    private final String oreName;

    MaterialType(final int metaValue, final String modelName) {
        this(metaValue, modelName, null);
    }

    MaterialType(final int metaValue, final String modelName, final String oreName) {
        this.damageValue = metaValue;
        this.oreName = oreName;
        this.model = new ModelResourceLocation(
            new ResourceLocation("appliedenergistics2", modelName), "inventory");
    }

    public ItemStack stack(final int size) {
        return LegacyAeItemMappings.stack(LEGACY_MATERIAL_ITEM, this.damageValue, size);
    }

    public String getOreName() {
        return this.oreName;
    }

    public boolean isRegistered() {
        final LegacyAeItemMappings.SupplierItemStack mapping =
            LegacyAeItemMappings.get(LEGACY_MATERIAL_ITEM, this.damageValue);
        return mapping != null && mapping != LegacyAeItemMappings.ERROR;
    }

    public int getDamageValue() {
        return this.damageValue;
    }

    public Item getItemInstance() {
        return this.stack(1).getItem();
    }

    public ModelResourceLocation getModel() {
        return this.model;
    }
}

/*
 * Copyright 2018 Theatrical Team (James Conway (615283) & Stuart (Rushmead)) and it's contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.georlegacy.general.theatrical.items.attr.fixture.gel;

public enum GelType {

    // Roscolux
    CLEAR("Clear", 0, 0xFFFFFF),
    LIGHT_BASTARD_AMBER("Light Bastard Amber", 1, 0xFBB39A),
    BASTARD_AMBER("Bastard Amber", 2, 0xFFD1AC),
    DARK_BASTARD_AMBER("Dark Bastard Amber", 3, 0xFFD1AC),
    MEDIUM_BASTARD_AMBER("Medium Bastard Amber", 4, 0xF9B09A),
    ROSE_TINT("Rose Tint", 5, 0xFFD7D3),
    NO_COLOR_STRAW("No Color Straw", 6, 0xFCFADB),
    PALE_YELLOW("Pale Yellow", 7, 0xFDFAD1),
    PALE_GOLD("Pale Gold", 8, 0xFFE0B8),
    PALE_AMBER_GOLD("Pale Amber Gold", 9, 0xFFCB86),
    MEDIUM_YELLOW("Medium Yellow", 10, 0xFFD21A),
    LIGHT_STRAW("Light Straw", 11, 0xFFF64D),
    STRAW("Straw", 12, 0xFFD88F),
    STRAW_TINT("Straw Tint", 13, 0xFFF64D),
    MEDIUM_STRAW("Medium Straw", 14, 0xFFD88F),
    DEEP_STRAW("Deep Straw", 15, 0xFECB00),
    LIGHT_AMBER("Light Amber", 16, 0xFFC47D),
    LIGHT_FLAME("Light Flame", 17, 0xFF934B),
    FLAME("Flame", 18, 0xFF9343),
    FIRE("Fire", 19, 0xFF390B),
    MEDIUM_AMBER("Medium Amber", 20, 0xFF871C),
    GOLDEN_AMBER("Golden Amber", 21, 0xFF6613),
    DEEP_AMBER("Deep Amber", 22, 0xFF430A),
    ORANGE("Orange", 23, 0xFF5A00),
    SCARLET("Scarlet", 24, 0xF50014),
    ORANGE_RED("Orange Red", 25, 0xE51F00),
    LIGHT_RED("Light Red", 26, 0xD70229),
    MEDIUM_RED("Medium Red", 27, 0xB00202),

    LIGHT_SALMON_PINK("Light Salmon Pink", 30, 0xFF7A59),
    SALMON_PINK("Salmon Pink", 31, 0xFF847F),
    MEDIUM_SALMON_PINK("Medium Salmon Pink", 32, 0xFF413C),
    NO_COLOR_PINK("No Color Pink", 33, 0xFFC2D0),
    FLESH_PINK("Flesh Pink", 34, 0xFF8D8D),
    LIGHT_PINK("Light Pink", 35, 0xFFA7BB),
    MEDIUM_PINK("Medium Pink", 36, 0xFF6D96),
    PALE_ROSE_PINK("Pale Rose Pink", 37, 0xF7B1CB),
    LIGHT_ROSE("Light Rose", 38, 0xFFBBE2),
    SKELTON_EXOTIC_SANGRIA("Skelton Exotic Sangria", 39, 0xE800BC),
    LIGHT_SALMON("Light Salmon", 40, 0xFF4F1F),
    SALMON("Salmon", 41, 0xFF5D30),
    DEEP_SALMON("Deep Salmon", 42, 0xFF1C26),
    DEEP_PINK("Deep Pink", 43, 0xFF3E93),
    MIDDLE_ROSE("Middle Rose", 44, 0xFF33B5),
    ROSE("Rose", 45, 0xEB016D),
    MAGENTA("Magenta", 46, 0xBD045D),
    LIGHT_ROSE_PURPLE("Light Rose Purple", 47, 0xCC4EB9),
    ROSE_PURPLE("Rose Purple", 48, 0xC800CF),
    MEDIUM_PURPLE("Medium Purple", 49, 0xC900E6),
    MAUVE("Mauve", 50, 0xBB002C),
    SURPRISE_PINK("Surprise Pink", 51, 0xE6CBFF),
    LIGHT_LAVENDER("Light Lavender", 52, 0xDDBFFF),
    PALE_LAVENDER("Pale Lavender", 53, 0xE4DCFF),
    SPECIAL_LAVENDER("Special Lavender", 54, 0xE6C7FF),
    LILAC("Lilac", 55, 0xC0AAFD),
    GYPSY_LAVENDER("Gypsy Lavender", 56, 0x8C2FFF),
    LAVENDER("Lavender", 57, 0xB482FF),
    DEEP_LAVENDER("Deep Lavender", 58, 0x933FFD),
    INDIGO("Indigo", 59, 0x7200FF),
    NO_COLOR_BLUE("No Color Blue", 60, 0xCCD8FF),
    MIST_BLUE("Mist Blue", 61, 0xD3EAFF),
    BOOSTER_BLUE("Booster Blue", 62, 0xA1CEFF),
    PALE_BLUE("Pale Blue", 63, 0xA4D3FF),
    LIGHT_STEEL_BLUE("Light Steel Blue", 64, 0x50AEFD),
    DAYLIGHT_BLUE("Daylight Blue", 65, 0x00A9FF),
    COOL_BLUE("Cool Blue", 66, 0x94AEFF),
    LIGHT_SKY_BLUE("Light Sky Blue", 67, 0x14A9FF),
    PARRY_SKY_BLUE("Parry Sky Blue", 68, 0x447DFF),
    BRILLIANT_BLUE("Brilliant Blue", 69, 0x00A3F7),
    NILE_BLUE("Nile Blue", 70, 0x6CE5FF),
    SEA_BLUE("Sea Blue", 71, 0x0096C7),
    AZURE_BLUE("Azure Blue", 72, 0x55CCFF),
    PEACOCK_BLUE("Peacock Blue", 73, 0x00A4B8),
    NIGHT_BLUE("Night Blue", 74, 0x4200FF),
    TWILIGHT_BLUE("Twilight Blue", 75, 0x007AAC),
    LIGHT_GREEN_BLUE("Light Green Blue", 76, 0x005773),
    GREEN_BLUE("Green Blue", 77, 0x014E95),
    TRUDY_BLUE("Trudy Blue", 78, 0x6F6FFF),
    BRIGHT_BLUE("Bright Blue", 79, 0x1626FF),
    PRIMARY_BLUE("Primary Blue", 80, 0x0048FF),
    URBAN_BLUE("Urban Blue", 81, 0x486FFF),
    SURPRISE_BLUE("Surprise Blue", 82, 0x4F34F8),
    MEDIUM_BLUE("Medium Blue", 83, 0x0228EC),
    ZEPHYR_BLUE("Zephyr Blue", 84, 0x5767FF),
    DEEP_BLUE("Deep Blue", 85, 0x0049CE),
    PEA_GREEN("Pea Green", 86, 0x89FA19),
    PALE_YELLOW_GREEN("Pale Yellow Green", 87, 0xF0FFBA),
    LIGHT_GREEN("Light Green", 88, 0xDCFE92),
    MOSS_GREEN("Moss Green", 89, 0x51F655),
    DARK_YELLOW_GREEN("Dark Yellow Green", 90, 0x007F06),
    PRIMARY_GREEN("Primary Green", 91, 0x005E2C),
    TURQUOISE("Turquoise", 92, 0x19FFC1),
    BLUE_GREEN("Blue Green", 93, 0x01A3A0),
    KELLY_GREEN("Kelly Green", 94, 0x00985D),
    MEDIUM_GREEN("Medium Green", 95, 0x009C91),
    LIME("Lime", 96, 0xF3FF6B),
    LIGHT_GREY("Light Grey", 97, 0xC0BFC0),
    MEDIUM_GREY("Medium Grey", 98, 0x8F8989),
    CHOCOLATE("Chocolate", 99, 0xD08864),
    FROST("Frost", 100, 0xFFFFFF),
    LIGHT_FROST("Light Frost", 101, 0xFFFFFF),
    LIGHT_TOUGH_FROST("Light Tough Frost", 102, 0xFFFFFF),
    TOUGH_FROST("Tough Frost", 103, 0xFFFFFF),
    TOUGH_SILK("Tough Silk", 104, 0xFFFFFF),
    TOUGH_SPUN("Tough Spun", 105, 0xFFFFFF),
    LIGHT_TOUGH_SPUN("Light Tough Spun", 106, 0xFFFFFF),

    TOUGH_ROLUX("Tough Rolux", 111, 0xFFFFFF),
    OPAL_TOUGH_FROST("Opal Tough Frost", 112, 0xFFFFFF),
    MATTE_SILK("Matte Silk", 113, 0xFFFFFF),
    HAMBURG_FROST("Hamburg Frost", 114, 0xFFFFFF),
    LIGHT_TOUGH_ROLUX("Light Tough Rolux", 115, 0xFFFFFF),
    TOUGH_WHITE_DIFFUSION("Tough White Diffusion", 116, 0xFFFFFF),
    TOUGH_HALF_WHITE_DIFFUSION("Tough 1/2 White Diffusion", 117, 0xFFFFFF),
    TOUGH_QUARTER_WHITE_DIFFUSION("Tough 1/4 White Diffusion", 118, 0xFFFFFF),
    LIGHT_HAMBURG_FROST("Light Hamburg Frost", 119, 0xFFFFFF),
    RED_DIFFUSION("Red Diffusion", 120, 0xCF000E),
    BLUE_DIFFUSION("Blue Diffusion", 121, 0x0F27FF),
    GREEN("Green Diffusion", 122, 0x1E9600),
    RED_CYC_SILK("Red Cyc Silk", 124, 0xC90011),
    BLUE_CYC_SILK("Blue Cyc Silk", 125, 0x001AFF),
    GREEN_CYC_SILK("Green Cyc Silk", 126, 0x1A8F00),
    AMBER_CYC_SILK("Amber Cyc Silk", 127, 0xFF430A),

    QUARTER_HAMBURG_FROST("Quarter Hamburg Frost", 132, 0xFFFFFF),

    SUBTLE_HAMBURG_FROST("Subtle Hamburg Frost", 140, 0xFFFFFF),

    LIGHT_TOUCH_SILK("Light Touch Silk", 160, 0xFFFFFF),

    LIGHT_OPAL("Light Opal", 162, 0xFFFFFF),
    POWDER_FROST("Powder Frost", 163, 0xFFFFFF),

    PALE_BASTARD_AMBER("Pale Bastard Amber", 302, 0xFEE5CF),
    WARM_PEACH("Warm Peach", 303, 0xFF8A4A),
    PALE_APRICOT("Pale Apricot", 304, 0xFABCA9),
    ROSE_GOLD("Rose Gold", 305, 0xF5BAB8),

    DAFFODIL("Daffodil", 310, 0xFFEF4F),

    CANARY("Canary", 312, 0xFFEA00),
    LIGHT_RELIEF_YELLOW("Light Relief Yellow", 313, 0xFFE462),

    GALLO_GOLD("Gallo Gold", 316, 0xF7AA4F),
    APRICOT("Apricot", 317, 0xFF7418),
    MAYAN_SUN("Mayan Sun", 318, 0xFF6F29),

    SOFT_GOLDEN_AMBER("Soft Golden Amber", 321, 0xFF943C),

    GYPSY_RED("Gypsy Red", 324, 0xF50F39),
    HENNA_SKY("Henna Sky", 325, 0xE32B0C),

    SHELL_PINK("Shell Pink", 331, 0xFF9D8D),
    CHERRY_ROSE("Cherry Rose", 332, 0xFF2957),

    BILLINTON_PINK("Billinton Pink", 336, 0xF9C5D8),
    TRUE_PINK("True Pink", 337, 0xFFAFC2),

    BROADWAY_PINK("Broadway Pink", 339, 0xFF1283),

    ROSE_PINK("Rose Pink", 342, 0xFF1562),
    NEON_PINK("Neon Pink", 343, 0xFF397F),
    FOLLIES_PINK("Follies Pink", 344, 0xFF05D3),

    TROPICAL_MAGENTA("Tropical Magenta", 346, 0xFF2DD5),
    BELLADONNA_ROSE("Belladonna Rose", 347, 0xD101DD),
    PURPLE_JAZZ("Purple Jazz", 348, 0xDA2DFF),
    FISCHER_FUCHSIA("Fischer Fuchsia", 349, 0xF000FF),

    LAVENDER_MIST("Lavender Mist", 351, 0xEFDCFF),

    LILLY_LAVENDER("Lilly Lavender", 353, 0xC4ADFF),

    PALE_VIOLET("Pale Violet", 355, 0xA590FF),
    MIDDLE_LAVENDER("Middle Lavender", 356, 0xC38DFF),
    ROYAL_LAVENDER("Royal Lavender", 357, 0x8A2BFF),
    ROSE_INDIGO("Rose Indigo", 358, 0x8E0AEA),
    MEDIUM_VIOLET("Medium Violet", 359, 0x8E0AEA),

    HEMSLEY_BLUE("Hemsley Blue", 361, 0x669EFC),
    TIPTON_BLUE("Tipton Blue", 362, 0xA7BCFF),
    AQUAMARINE("Aquamarine", 363, 0xABE9FF),
    BLUE_BELL("Blue Bell", 364, 0x8CA9FF),

    JORDAN_BLUE("Jordan Blue", 366, 0x29C0F9),
    SLATE_BLUE("Slate Blue", 367, 0x44A5FF),
    WINKLER_BLUE("Winkler Blue", 368, 0x448AFF),
    TAHITIAN_BLUE("Tahitian Blue", 369, 0x00C6FF),
    ITALIAN_BLUE("Italian Blue", 370, 0x01CDDF),
    THEATRE_BOOSTER_ONE("Theatre Booster 1", 371, 0xA3A8FF),
    THEATRE_BOOSTER_TWO("Theatre Booster 2", 372, 0xD9DCFF),
    THEATRE_BOOSTER_THREE("Theatre Booster 3", 373, 0xE0E9FD),
    SEA_GREEN("Sea Green", 374, 0x01A4A6),
    CERULEAN_BLUE("Cerulean Blue", 375, 0x00B5DE),
    BERMUDA_BLUE("Bermuda Blue", 376, 0x0087B1),
    IRIS_PURPLE("Iris Purple", 377, 0x7124FF),
    ALICE_BLUE("Alice Blue", 378, 0x8474FD),

    CONGO_BLUE("Congo Blue", 382, 0x250070),
    SAPPHIRE_BLUE("Sapphire Blue", 383, 0x0022D1),
    MIDNIGHT_BLUE("Midnight Blue", 384, 0x0500D0),
    ROYAL_BLUE("Royal Blue", 385, 0x4F02CF),
    LEAF_GREEN("Leaf Green", 386, 0x7BD300),

    GASLIGHT_GREEN("Gaslight Green", 388, 0xD0F54E),
    CHROMA_GREEN("Chroma Green", 389, 0x29F433),

    PACIFIC_GREEN("Pacific Green", 392, 0x009493),
    EMERALD_GREEN("Emerald Green", 393, 0x007150),

    TEAL_GREEN("Teal Green", 395, 0x00726A),

    PALE_GREY("Pale Grey", 397, 0xD4D3D3),
    NEUTRAL_GREY("Neutral Grey", 398, 0xB0B4B9);

    private String name;
    private int id;
    private int hex;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getHex() {
        return hex;
    }

    GelType(String name, int id, int hex) {
        this.name = name;
        this.id = id;
        this.hex = hex;
    }

    public static GelType getGelType(int id){
        for(GelType gelType : values()){
            if(gelType.getId() == id){
                return gelType;
            }
        }
        return null;
    }

}

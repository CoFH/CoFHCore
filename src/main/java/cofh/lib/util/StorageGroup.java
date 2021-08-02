package cofh.lib.util;

public enum StorageGroup {

    ALL,            // Added ONLY to Inventory, no sub-category
    INPUT,          // Input, can insert + extract
    OUTPUT,         // Output Only, cannot insert
    ACCESSIBLE,     // Input + Output
    CATALYST,       // Used in Process; accessible on Input Sides. Non-recipe.
    INTERNAL        // Inaccessible externally
}

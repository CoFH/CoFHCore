package cofh.lib.api;

public enum StorageGroup {

    ALL,            // Added ONLY to Inventory, no sub-category
    INPUT,          // Input, can insert + extract
    OUTPUT,         // Output Only, cannot insert
    INPUT_OUTPUT,   // Input + Output, with input restriction.
    ACCESSIBLE,     // Input + Output
    CATALYST,       // Used in Process; accessible on Input Sides. Non-recipe.
    INTERNAL        // Inaccessible externally
}

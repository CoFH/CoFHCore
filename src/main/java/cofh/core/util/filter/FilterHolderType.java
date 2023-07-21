package cofh.core.util.filter;

public enum FilterHolderType {
    SELF, TILE, ITEM, ENTITY;

    public static final FilterHolderType[] VALUES = values();

    public static FilterHolderType from(int type) {

        if (type >= VALUES.length) {
            return SELF;
        }
        return VALUES[type];
    }
}

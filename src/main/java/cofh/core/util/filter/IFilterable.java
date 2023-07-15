package cofh.core.util.filter;

public interface IFilterable {

    IFilter getFilter();

    default boolean hasFilter() {

        return getFilter() != null && getFilter() != EmptyFilter.INSTANCE;
    }

    default void onFilterChanged() {

    }

}

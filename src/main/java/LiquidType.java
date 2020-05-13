public enum LiquidType {
    WATER("Water"),
    MILK("Milk"),
    SOUP("Soup"),
    SODA("Soda");

    private String name;

    LiquidType (String name) {
        this.name = name;
    }

    @Override
    public String toString () {
        return name;
    }
}

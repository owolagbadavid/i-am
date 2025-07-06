package me.oreos.iam.entities.enums;

public enum EffectiveScopeEnum {
    OWN("own"),
    GROUP("group"),
    ALL("all"),
    ITEM("item");

    private String value;

    EffectiveScopeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

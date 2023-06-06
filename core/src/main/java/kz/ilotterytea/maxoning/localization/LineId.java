package kz.ilotterytea.maxoning.localization;

public enum LineId {
    MENU_PRESS_START("menu.press_start");

    private final String nameId;

    LineId(String nameId) {
        this.nameId = nameId;
    }

    public String getName() {
        return nameId;
    }
}

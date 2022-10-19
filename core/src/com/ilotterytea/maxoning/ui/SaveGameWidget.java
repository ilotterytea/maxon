package com.ilotterytea.maxoning.ui;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;
import com.ilotterytea.maxoning.player.MaxonSavegame;
import com.ilotterytea.maxoning.utils.formatters.NumberFormatter;

public class SaveGameWidget extends Button {
    public SaveGameWidget(
            Skin skin,
            Skin widgetSkin,
            @Null MaxonSavegame sav
    ) {
        // Setting the stack:
        super(widgetSkin, "slot");

        // // // Save slot data:
        // // Info row:
        Table infoTable = new Table();

        // Top left label (name):
        Label topleftLabel = new Label((sav != null) ? sav.name : "[EMPTY]", skin);
        topleftLabel.setAlignment(Align.left);
        infoTable.add(topleftLabel).width(256f);

        // Top right label (elapsed time):
        String time = "--:--";

        if (sav != null) {
            long h = sav.elapsedTime / 1000 / 60 / 60;
            long m = sav.elapsedTime / 1000 / 60;

            time = String.format(
                    "%s:%s",
                    ((h < 10) ? "0" : "") + h,
                    ((m < 10) ? "0" : "") + m
            );
        }

        Label toprightLabel = new Label(
                time,
                skin
        );
        toprightLabel.setAlignment(Align.right);
        infoTable.add(toprightLabel).width(256f);

        // // Description row:
        Table descTable = new Table();

        // Bottom left label (purchased items):
        Label bottomleftLabel = new Label(
                String.format(
                        "%s purchased items",
                        (sav != null) ? sav.inv.size() : "?"
                ),
                skin
        );
        bottomleftLabel.setAlignment(Align.left);
        descTable.add(bottomleftLabel).width(256f);

        // Bottom right label (points/multiplier):
        Label pointsLabel = new Label(
                String.format(
                        "%s$/x%s",
                        (sav != null) ? NumberFormatter.format(sav.points) : "---",
                        (sav != null) ? NumberFormatter.format(sav.multiplier) : "0"
                ),
                skin
        );
        pointsLabel.setAlignment(Align.right);
        descTable.add(pointsLabel).width(256f);

        // Adding the tables to main table:
        Table summaryTable = new Table();
        summaryTable.add(infoTable).pad(5f).row();
        summaryTable.add(descTable).pad(5f).row();

        if (sav == null) {
            topleftLabel.setColor(Color.DARK_GRAY);
            toprightLabel.setColor(Color.DARK_GRAY);
            bottomleftLabel.setColor(Color.DARK_GRAY);
            pointsLabel.setColor(Color.DARK_GRAY);
        }

        super.add(summaryTable);
    }
}

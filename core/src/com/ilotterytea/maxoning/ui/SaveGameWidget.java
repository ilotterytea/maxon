package com.ilotterytea.maxoning.ui;


import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.ilotterytea.maxoning.player.MaxonPlayer;
import com.ilotterytea.maxoning.utils.formatters.NumberFormatter;


public class SaveGameWidget extends Button {
    public SaveGameWidget(
            Skin skin,
            Skin widgetSkin,
            MaxonPlayer sav
    ) {
        // Setting the stack:
        super(widgetSkin, "slot");

        // // // Save slot data:
        // // Info row:
        Table infoTable = new Table();

        // Top left label:
        Label topleftLabel = new Label(String.format("%s Squish Points", (sav != null) ? NumberFormatter.format((long) sav.points) : "---"), skin);
        topleftLabel.setAlignment(Align.left);
        infoTable.add(topleftLabel).width(256f);

        // Top right label:
        Label toprightLabel = new Label(
                String.format("%s purchased items", (sav != null) ? sav.purchasedItems.size() : "---"),
                skin
        );
        toprightLabel.setAlignment(Align.right);
        infoTable.add(toprightLabel).width(256f);

        // // Description row:
        Table descTable = new Table();

        // Bottom left label:
        Label bottomleftLabel = new Label(
                String.format(
                        "x%s",
                        (sav != null) ? NumberFormatter.format((long) sav.multiplier) : "?"
                ),
                skin
        );
        bottomleftLabel.setAlignment(Align.left);
        descTable.add(bottomleftLabel).width(256f);

        /* NOT IN USE. Bottom right label:
        Label pointsLabel = new Label(
                String.format(
                        "%s$/x%s",
                        NumberFormatter.format(points),
                        NumberFormatter.format(multiplier)
                ),
                skin
        );
        pointsLabel.setAlignment(Align.right);
        descTable.add(pointsLabel).width(256f);*/

        // Adding the tables to main table:
        Table summaryTable = new Table();
        summaryTable.add(infoTable).pad(5f).row();
        summaryTable.add(descTable).pad(5f).row();

        super.add(summaryTable);
    }
}

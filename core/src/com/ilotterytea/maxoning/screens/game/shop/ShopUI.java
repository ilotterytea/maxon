package com.ilotterytea.maxoning.screens.game.shop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.ilotterytea.maxoning.player.MaxonSavegame;
import com.ilotterytea.maxoning.utils.math.Math;

public class ShopUI {
    private final Stage stage;
    private final Skin skin;
    private final TextureAtlas atlas;

    public ShopUI(Stage stage, Skin skin, TextureAtlas atlas) {
        this.stage = stage;
        this.skin = skin;
        this.atlas = atlas;
    }

    public void createSavegameUI(final MaxonSavegame player) {
        Table table = new Table(this.skin);
        table.setBackground("board");

        table.setWidth(Math.percentFromValue(25f, Gdx.graphics.getWidth()));
        table.setHeight(Math.percentFromValue(15f, Gdx.graphics.getHeight()));
        table.setX(Gdx.graphics.getWidth() - table.getWidth());
        table.align(Align.center | Align.left);
        table.pad(10f);

        // Setting up the points
        Table pointsTable = new Table();

        Image pointsImage = new Image(this.atlas.findRegion("points"));
        Label pointsLabel = new Label(String.valueOf(player.points), this.skin);

        pointsTable.add(pointsImage);
        pointsTable.add(pointsLabel).padLeft(15f);

        table.add(pointsTable).padBottom(10f).row();

        // Setting up the multiplier
        Table multiplierTable = new Table();

        Image multiplierImage = new Image(this.atlas.findRegion("multiplier"));
        Label multiplierLabel = new Label(String.format("%s/s", player.multiplier), this.skin);

        multiplierTable.add(multiplierImage);
        multiplierTable.add(multiplierLabel).padLeft(15f);

        table.add(multiplierTable);

        this.stage.addActor(table);
    }

    public void createShopTitleUI() {
        Table table = new Table(this.skin);
        table.setBackground("board");

        table.setWidth(Math.percentFromValue(25f, Gdx.graphics.getWidth()));
        table.setHeight(Math.percentFromValue(5f, Gdx.graphics.getHeight()));
        table.setX(Gdx.graphics.getWidth() - table.getWidth());
        table.setY(Gdx.graphics.getHeight() - table.getHeight());
        table.align(Align.center);
        table.pad(10f);

        Label label = new Label("Store", skin);
        table.add(label);

        this.stage.addActor(table);
    }
}

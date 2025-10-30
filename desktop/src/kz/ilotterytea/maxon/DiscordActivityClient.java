package kz.ilotterytea.maxon;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Timer;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.LogLevel;
import de.jcm.discordgamesdk.activity.Activity;
import kz.ilotterytea.maxon.player.Savegame;
import kz.ilotterytea.maxon.screens.SlotsMinigameScreen;
import kz.ilotterytea.maxon.screens.game.GameScreen;
import kz.ilotterytea.maxon.utils.DiscordActivityInterface;
import kz.ilotterytea.maxon.utils.OsUtils;
import kz.ilotterytea.maxon.utils.formatters.NumberFormatter;

import java.time.Instant;

public class DiscordActivityClient implements DiscordActivityInterface {
    private final Logger logger = new Logger(DiscordActivityClient.class.getName());

    private Core core;
    private Instant startTime;
    private Timer.Task task;

    public DiscordActivityClient() {
        if (!OsUtils.isPC) {
            logger.info("Discord Game SDK is only supported for PC devices");
            return;
        }

        startTime = Instant.now();
    }

    @Override
    public void init() {
        task = new Timer.Task() {
            @Override
            public void run() {
                if (core == null) {
                    super.cancel();
                    return;
                }
                updateActivity();
                core.runCallbacks();
            }
        };

        try (CreateParams params = new CreateParams()) {
            params.setClientID(MaxonConstants.DISCORD_APPLICATION_ID);
            params.setFlags(CreateParams.Flags.DEFAULT);

            this.core = new Core(params);
            core.setLogHook(LogLevel.ERROR, (level, message) -> System.out.printf("[DISCORD %s] %s\n", level, message));
            logger.info("Initialized the Discord RPC");
        } catch (Exception e) {
            logger.error(String.format("Failed to initialize the Discord RPC: %s", e.toString()));
        }
    }

    @Override
    public void updateActivity() {
        if (core == null || !core.isDiscordRunning()) return;

        try (Activity activity = new Activity()) {
            MaxonGame game = MaxonGame.getInstance();
            activity.timestamps().setStart(startTime);

            Screen screen = game.getScreen();

            Savegame savegame = Savegame.getInstance();
            String savegameInfo = String.format(
                    "%s💵 | %sⅹ | %s 🐱",
                    NumberFormatter.format((long) savegame.getMoney()),
                    NumberFormatter.format((long) savegame.getMultiplier()),
                    NumberFormatter.format(savegame.getAllPetAmount())
            );

            String details, largeImage;
            String smallImage = null, smallText = null, largeText = null;

            if (screen instanceof GameScreen) {
                details = "Petting Maxon";
                largeImage = "maxon";
                largeText = savegameInfo;
            } else if (screen instanceof SlotsMinigameScreen) {
                details = "Spinning the slots";
                largeImage = "slots";
                largeText = String.format("Total spins: %s | Total wins: %s", savegame.getSlotsTotalSpins(), savegame.getSlotsWins());
                smallImage = "maxon";
                smallText = savegameInfo;
            } else {
                details = "Sitting in Main Menu";
                largeImage = "maxon";
            }

            activity.setDetails(details);
            activity.assets().setLargeImage(largeImage);

            if (smallImage != null) activity.assets().setSmallImage(smallImage);
            if (smallText != null) activity.assets().setSmallText(smallText);
            if (largeText != null) activity.assets().setLargeText(largeText);

            core.activityManager().updateActivity(activity);
        } catch (Exception e) {
            logger.error(String.format("Failed to set the activity: %s", e.toString()));
        }
    }

    @Override
    public void runThread() {
        if (task != null && !task.isScheduled()) Timer.schedule(task, 0.25f, 0.25f);
    }

    @Override
    public void dispose() {
        if (task != null) task.cancel();
        if (core != null) core.close();
    }
}

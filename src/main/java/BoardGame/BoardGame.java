package BoardGame;

import BoardGame.characters.BGColorless;
import BoardGame.characters.BGCurse;
import BoardGame.monsters.bgexordium.*;
import BoardGame.monsters.bgcity.*;
import BoardGame.monsters.bgbeyond.*;
import BoardGame.monsters.bgending.*;
import BoardGame.dungeons.*;
import BoardGame.relics.*;
import BoardGame.screen.TargetSelectScreen;
import basemod.*;
import basemod.eventUtil.AddEventParams;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import BoardGame.cards.*;
import BoardGame.characters.BGIronclad;
import BoardGame.events.*;
import BoardGame.potions.PlaceholderPotion;
import BoardGame.util.IDCheckDontTouchPls;
import BoardGame.util.TextureLoader;
import BoardGame.variables.DefaultCustomVariable;
import BoardGame.variables.DefaultSecondMagicNumber;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Target;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static basemod.BaseMod.addMonster;


/*

 * https://github.com/daviscook477/BaseMod/wiki

 */

@SpireInitializer
public class BoardGame implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        EditCharactersSubscriber,
        PostInitializeSubscriber {
    // Make sure to implement the subscribers *you* are using (read basemod wiki). Editing cards? EditCardsSubscriber.
    // Making relics? EditRelicsSubscriber. etc., etc., for a full list and how to make your own, visit the basemod wiki.
    public static final Logger logger = LogManager.getLogger(BoardGame.class.getName());
    private static String modID;

    // Mod-settings settings. This is if you want an on/off savable button
    public static Properties theDefaultDefaultSettings = new Properties();
    public static final String ENABLE_PLACEHOLDER_SETTINGS = "enablePlaceholder";
    public static boolean enablePlaceholder = true; // The boolean we'll be setting on/off (true/false)

    //This is for the in-game mod settings panel.
    private static final String MODNAME = "Board Game";
    private static final String AUTHOR = "Lua Viper";
    private static final String DESCRIPTION = "[Pithy board game description goes here]";

    // =============== INPUT TEXTURE LOCATION =================

    // Colors (RGB)
    // Character Color
    public static final Color BG_IRONCLAD_RED = CardHelper.getColor(128.0f, 25.6f, 25.6f);
    public static final Color BG_CURSE_BLACK = CardHelper.getColor(29, 29, 29);
    public static final Color BG_COLORLESS_GRAY = CardHelper.getColor(0.15F, 0.15F, 0.15F);
    // Potion Colors in RGB
    public static final Color PLACEHOLDER_POTION_LIQUID = CardHelper.getColor(209.0f, 53.0f, 18.0f); // Orange-ish Red
    public static final Color PLACEHOLDER_POTION_HYBRID = CardHelper.getColor(255.0f, 230.0f, 230.0f); // Near White
    public static final Color PLACEHOLDER_POTION_SPOTS = CardHelper.getColor(100.0f, 25.0f, 10.0f); // Super Dark Red/Brown



    // Card backgrounds - The actual rectangular card.
    private static final String BGATTACK = "BoardGameResources/images/512/bg_attack_default_gray.png";
    private static final String BGSKILL = "BoardGameResources/images/512/bg_skill_default_gray.png";
    private static final String BGPOWER = "BoardGameResources/images/512/bg_power_default_gray.png";

    private static final String ENERGY_ORB = "BoardGameResources/images/512/card_default_gray_orb.png";
    private static final String SMALL_ORB = "BoardGameResources/images/512/card_small_orb.png";

    private static final String BGATTACK_P = "BoardGameResources/images/1024/bg_attack_default_gray.png";
    private static final String BGSKILL_P = "BoardGameResources/images/1024/bg_skill_default_gray.png";
    private static final String BGPOWER_P = "BoardGameResources/images/1024/bg_power_default_gray.png";
    private static final String ENERGY_ORB_P = "BoardGameResources/images/1024/card_default_gray_orb.png";

    // Character assets
    private static final String CHAR_SELECT_BUTTON = "images/ui/charSelect/ironcladButton.png";
    private static final String CHAR_SELECT_PORTRAIT = "images/ui/charSelect/ironcladPortrait.jpg";
//    public static final String SHOULDER_1 = "BoardGameResources/images/char/defaultCharacter/shoulder.png";
//    public static final String SHOULDER_2 = "BoardGameResources/images/char/defaultCharacter/shoulder2.png";
//    public static final String CORPSE = "BoardGameResources/images/char/defaultCharacter/corpse.png";

    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    //TODO: still using placeholder TheDefaultMod badge
    public static final String BADGE_IMAGE = "BoardGameResources/images/Badge.png";

    // Atlas and JSON files for the Animations
    public static final String BGIRONCLAD_SKELETON_ATLAS = "images/characters/ironclad/idle/skeleton.atlas";
    public static final String BGIRONCLAD_SKELETON_JSON = "images/characters/ironclad/idle/skeleton.json";



    private static final String ATTACK_COLORLESS = "BoardGameResources/images/512/colorless_bg_attack.png";
    private static final String SKILL_COLORLESS = "BoardGameResources/images/512/colorless_bg_skill.png";
    private static final String POWER_COLORLESS = "BoardGameResources/images/512/colorless_bg_power.png";




    // =============== MAKE IMAGE PATHS =================

    public static String makeCardPath(String resourcePath) {
        return getModID() + "Resources/images/cards/" + resourcePath;
    }

    public static String makeRelicPath(String resourcePath) {
        return getModID() + "Resources/images/relics/" + resourcePath;
    }

    public static String makeRelicOutlinePath(String resourcePath) {
        return getModID() + "Resources/images/relics/outline/" + resourcePath;
    }

    public static String makeOrbPath(String resourcePath) {
        return getModID() + "Resources/images/orbs/" + resourcePath;
    }

    public static String makePowerPath(String resourcePath) {
        return getModID() + "Resources/images/powers/" + resourcePath;
    }

    public static String makeEventPath(String resourcePath) {
        return getModID() + "Resources/images/events/" + resourcePath;
    }

    // =============== /MAKE IMAGE PATHS/ =================

    // =============== /INPUT TEXTURE LOCATION/ =================


    // =============== SUBSCRIBE, CREATE THE COLOR_GRAY, INITIALIZE =================

    public BoardGame() {
        logger.info("Subscribe to BaseMod hooks");

        BaseMod.subscribe(this);

        setModID("BoardGame");

        logger.info("Done subscribing");

        logger.info("Creating the color " + BGIronclad.Enums.BG_RED.toString());

        BaseMod.addColor(BGIronclad.Enums.BG_RED, BG_IRONCLAD_RED,
                BGATTACK, BGSKILL, BGPOWER, ENERGY_ORB,
                BGATTACK_P, BGSKILL_P, BGPOWER_P,
                ENERGY_ORB_P, SMALL_ORB);

        logger.info("Done creating the color");

        logger.info("Creating the color " + BGCurse.Enums.BG_CURSE.toString());
        BaseMod.addColor(BGCurse.Enums.BG_CURSE, BG_CURSE_BLACK,
                BGATTACK, BGSKILL, BGPOWER, ENERGY_ORB,
                BGATTACK_P, BGSKILL_P, BGPOWER_P,
                ENERGY_ORB_P, SMALL_ORB);

        logger.info("Creating the color " + BGColorless.Enums.CARD_COLOR.toString());
        BaseMod.addColor(BGColorless.Enums.CARD_COLOR, BG_COLORLESS_GRAY,
                ATTACK_COLORLESS, SKILL_COLORLESS, POWER_COLORLESS, ENERGY_ORB,
                BGATTACK_P, BGSKILL_P, BGPOWER_P,
                ENERGY_ORB_P, SMALL_ORB);

        logger.info("Done creating the color");


        logger.info("Adding mod settings");
        // This loads the mod settings.
        // The actual mod Button is added below in receivePostInitialize()
        theDefaultDefaultSettings.setProperty(ENABLE_PLACEHOLDER_SETTINGS, "FALSE"); // This is the default setting. It's actually set...
        try {
            SpireConfig config = new SpireConfig("defaultMod", "theDefaultConfig", theDefaultDefaultSettings); // ...right here
            // the "fileName" parameter is the name of the file MTS will create where it will save our setting.
            config.load(); // Load the setting and set the boolean to equal it
            enablePlaceholder = config.getBool(ENABLE_PLACEHOLDER_SETTINGS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Done adding mod settings");

    }

    // ====== NO EDIT AREA ======
    // DON'T TOUCH THIS STUFF. IT IS HERE FOR STANDARDIZATION BETWEEN MODS AND TO ENSURE GOOD CODE PRACTICES.
    // IF YOU MODIFY THIS I WILL HUNT YOU DOWN AND DOWNVOTE YOUR MOD ON WORKSHOP

    public static void setModID(String ID) { // DON'T EDIT
        Gson coolG = new Gson(); // EY DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i hate u Gdx.files
        InputStream in = BoardGame.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THIS ETHER
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // OR THIS, DON'T EDIT IT
        logger.info("You are attempting to set your mod ID as: " + ID); // NO WHY
        if (ID.equals(EXCEPTION_STRINGS.DEFAULTID)) { // DO *NOT* CHANGE THIS ESPECIALLY, TO EDIT YOUR MOD ID, SCROLL UP JUST A LITTLE, IT'S JUST ABOVE
            throw new RuntimeException(EXCEPTION_STRINGS.EXCEPTION); // THIS ALSO DON'T EDIT
        } else if (ID.equals(EXCEPTION_STRINGS.DEVID)) { // NO
            modID = EXCEPTION_STRINGS.DEFAULTID; // DON'T
        } else { // NO EDIT AREA
            modID = ID; // DON'T WRITE OR CHANGE THINGS HERE NOT EVEN A LITTLE
        } // NO
        logger.info("Success! ID is " + modID); // WHY WOULD U WANT IT NOT TO LOG?? DON'T EDIT THIS.
    } // NO

    public static String getModID() { // NO
        return modID; // DOUBLE NO
    } // NU-UH

    private static void pathCheck() { // ALSO NO
        Gson coolG = new Gson(); // NOPE DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i still hate u btw Gdx.files
        InputStream in = BoardGame.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THISSSSS
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // NAH, NO EDIT
        String packageName = BoardGame.class.getPackage().getName(); // STILL NO EDIT ZONE
        FileHandle resourcePathExists = Gdx.files.internal(getModID() + "Resources"); // PLEASE DON'T EDIT THINGS HERE, THANKS
        if (!modID.equals(EXCEPTION_STRINGS.DEVID)) { // LEAVE THIS EDIT-LESS
            if (!packageName.equals(getModID())) { // NOT HERE ETHER
                throw new RuntimeException(EXCEPTION_STRINGS.PACKAGE_EXCEPTION + getModID()); // THIS IS A NO-NO
            } // WHY WOULD U EDIT THIS
            if (!resourcePathExists.exists()) { // DON'T CHANGE THIS
                throw new RuntimeException(EXCEPTION_STRINGS.RESOURCE_FOLDER_EXCEPTION + getModID() + "Resources"); // NOT THIS
            }// NO
        }// NO
    }// NO

    // ====== YOU CAN EDIT AGAIN ======


    public static void initialize() {
        logger.info("========================= Initializing <s>Default</s> BoardGame Mod. Hi. =========================");
        BoardGame defaultmod = new BoardGame();
        logger.info("========================= /<s>Default</s> BoardGame Mod Initialized. Hello World./ =========================");
    }

    // ============== /SUBSCRIBE, CREATE THE COLOR_GRAY, INITIALIZE/ =================


    // =============== LOAD THE CHARACTER =================

    @Override
    public void receiveEditCharacters() {
        logger.info("Beginning to edit characters. " + "Add " + BGIronclad.Enums.BG_IRONCLAD.toString());

        BaseMod.addCharacter(new BGIronclad("the Ironclad", BGIronclad.Enums.BG_IRONCLAD),
                CHAR_SELECT_BUTTON, CHAR_SELECT_PORTRAIT, BGIronclad.Enums.BG_IRONCLAD);

        receiveEditPotions();
        logger.info("Added " + BGIronclad.Enums.BG_IRONCLAD.toString());




    }

    // =============== /LOAD THE CHARACTER/ =================


    // =============== POST-INITIALIZE =================

    @Override
    public void receivePostInitialize() {
        logger.info("Loading badge image and mod options");

        // Load the Mod Badge
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);

        // Create the Mod Menu
//        ModPanel settingsPanel = new ModPanel();

//        // Create the on/off button:
//        ModLabeledToggleButton enableNormalsButton = new ModLabeledToggleButton("This is the text which goes next to the checkbox.",
//                350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont, // Position (trial and error it), color, font
//                enablePlaceholder, // Boolean it uses
//                settingsPanel, // The mod panel in which this button will be in
//                (label) -> {
//                }, // thing??????? idk
//                (button) -> { // The actual button:
//
//                    enablePlaceholder = button.enabled; // The boolean true/false will be whether the button is enabled or not
//                    try {
//                        // And based on that boolean, set the settings and save them
//                        SpireConfig config = new SpireConfig("defaultMod", "theDefaultConfig", theDefaultDefaultSettings);
//                        config.setBool(ENABLE_PLACEHOLDER_SETTINGS, enablePlaceholder);
//                        config.save();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//
//        settingsPanel.addUIElement(enableNormalsButton); // Add the button to the settings panel. Button is a go.

//        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        BaseMod.addCustomScreen(new TargetSelectScreen());



        //TODO: non-repeating "monster deck" -- it's still possible to get the same encounter twice, especially with elites

        addMonster(BGCultist.ID, ()->new BGCultist(0,0));
        addMonster("BoardGame:Jaw Worm (Easy)", ()->new BGJawWorm(0,0, 0, ""));
        addMonster("BoardGame:Easy Small Slimes",()->new MonsterGroup(new AbstractMonster[]{
                new BGSpikeSlime_S(-200.0F, 0.0F),
                new BGAcidSlime_M(80.0F, -15.0F)
        }));
        addMonster("BoardGame:2 Louse",()->new MonsterGroup(new AbstractMonster[]{
                new BGRedLouse(-200.0F, 10.0F,false, "S21"),
                new BGGreenLouse(80.0F, 30.0F)
        }));
        addMonster("BoardGame:Cultist and SpikeSlime",()->new MonsterGroup(new AbstractMonster[]{
                new BGCultist(-200.0F, 10.0F),
                new BGSpikeSlime_M(80.0F, 30.0F)
        }));
        addMonster("BoardGame:Cultist and Louse",()->new MonsterGroup(new AbstractMonster[]{
                new BGCultist(-200.0F, 10.0F),
                new BGGreenLouse(80.0F, 30.0F)
        }));
        addMonster("BoardGame:Fungi Beasts",()->new MonsterGroup(new AbstractMonster[]{
                new BGFungiBeast(-200.0F, 10.0F, "21S"),
                new BGFungiBeast(80.0F, 30.0F,"2S1")
        }));
        addMonster("BoardGame:Slime Trio",()->new MonsterGroup(new AbstractMonster[]{
                new BGSpikeSlime_S(-350.0F,0.0F),
                new BGAcidSlime_M(-125.0F,-15.0F),
                new BGSpikeSlime_M(80.0F,0.0F)
        }));
        addMonster("BoardGame:3 Louse (Hard)",()->new MonsterGroup(new AbstractMonster[]{
                new BGRedLouse(-350.0F, 10.0F,true, "S22"),
                new BGGreenLouse(-125.0F, 30.0F),
                new BGRedLouse(80.0F, 20.0F,false, "21S")
        }));
        addMonster("BoardGame:Large Slime", ()->new BGAcidSlime_L(-480.0F,0.0F,false));
        addMonster("BoardGame:Sneaky Gremlin Team", ()->new BGGremlinSneaky(-220.0F,0.0F,true));
        addMonster("BoardGame:Angry Gremlin Team", ()->new BGGremlinAngry(-220.0F,0.0F,true));
        addMonster("BoardGame:Blue Slaver", ()->new BGBlueSlaver(0.0F, 0.0F,"W2D"));
        addMonster("BoardGame:Red Slaver", ()->new BGRedSlaver(0.0F, 0.0F,"DV3"));
        addMonster("BoardGame:Looter", ()->new BGLooter(0.0F, 0.0F, false));
        addMonster("BoardGame:Jaw Worm (Medium)", ()->new BGJawWorm(0,0, 1, ""));
        addMonster("BoardGame:Lagavulin", ()->new BGLagavulin());
        addMonster("BoardGame:Gremlin Nob", ()->new BGGremlinNob(0,0));
        addMonster("BoardGame:3 Sentries",()->new MonsterGroup(new AbstractMonster[]
                    { new BGSentry(-330.0F, 25.0F,"3D"),
                        new BGSentry(-85.0F, 10.0F,"D3"),
                        new BGSentry(140.0F, 30.0F,"2D")
        }));
        //note that we create boss encounters on-demand in AbstractBGDungeon; these are just here to register to console
        addMonster("BoardGame:Hexaghost", ()->new BGHexaghost());
        addMonster("BoardGame:SlimeBoss", ()->new BGSlimeBoss());
        addMonster("BoardGame:TheGuardian", ()->new BGTheGuardian());

        addMonster("BoardGame:Centurion A",()->new MonsterGroup(new AbstractMonster[]{
                new BGCenturion(-200.0F,-15.0F,"B3"),
                new BGHealer(120.0F,0.0F)
        }));
        addMonster("BoardGame:Centurion B",()->new MonsterGroup(new AbstractMonster[]{
                new BGCenturion(-240.0F,0.0F,"3B"),
                new BGHealer(80.0F,15.0F)
        }));
        addMonster("BoardGame:Looter (Hard)",()->new MonsterGroup(new AbstractMonster[]{
                new BGLooter(-200.0F,-15.0F,true),
                new BGMugger(120.0F,0.0F)
        }));
        addMonster("BoardGame:Another Looter (Hard)",()->new MonsterGroup(new AbstractMonster[]{
                new BGLooter(-240,-0.0F,true),
                new BGMugger(80.0F,15.0F)
        }));
        addMonster("BoardGame:Chosen and Cultist",()->new MonsterGroup(new AbstractMonster[]{
                new BGChosen(-230.0F, 15.0F,14),
                new BGCultist(80.0F,0.0F)
        }));
        addMonster("BoardGame:Chosen and Byrd",()->new MonsterGroup(new AbstractMonster[]{
                new BGChosen(-170.0F, 0.0F,16),
                new BGByrd(80.0F,70.0F)
        }));
        addMonster("BoardGame:Shelled Parasite",()->new MonsterGroup(new AbstractMonster[]{
                new BGShelledParasite()
        }));
        addMonster("BoardGame:3 Cultists",()->new MonsterGroup(new AbstractMonster[]{
                new BGCultist(-465.0F, -20.0F, false),
                new BGCultist(-130.0F, 15.0F, false),
                new BGCultist(200.0F, -5.0F)
        }));
        addMonster("BoardGame:3 Byrds",()->new MonsterGroup(new AbstractMonster[]{
                new BGByrd(-360.0F,MathUtils.random(25.0F, 70.0F), "1S3", 5),
                new BGByrd(-80.0F,MathUtils.random(25.0F, 70.0F)),
                new BGByrd(200.0F, MathUtils.random(25.0F, 70.0F))
        }));
        addMonster("BoardGame:SphericGuardian",()->new MonsterGroup(new AbstractMonster[]{
                new BGSphericGuardian()
        }));
        addMonster("BoardGame:SnakePlant",()->new MonsterGroup(new AbstractMonster[]{
                new BGSnakePlant(-30.0F, -30.0F)
        }));
        addMonster("BoardGame:Snecko",()->new MonsterGroup(new AbstractMonster[]{
                new BGSnecko()
        }));
        addMonster("BoardGame:Book of Stabbing",()->new MonsterGroup((AbstractMonster)new BGBookOfStabbing()));
        addMonster("BoardGame:Gremlin Leader",()->new MonsterGroup(new AbstractMonster[] {
            new BGGremlinPlaceholder(),
            new BGGremlinPlaceholder(),
            new BGGremlinLeader()
        }));
        addMonster("BoardGame:Taskmaster",()->new MonsterGroup(new AbstractMonster[]{
                new BGBlueSlaver(-385.0F, -15.0F),
                new BGTaskmaster(-133.0F, 0.0F), (AbstractMonster)
                new BGRedSlaver(125.0F, -30.0F)
        }));

        addMonster("BoardGame:Automaton", ()->new BGBronzeAutomaton());
        addMonster("BoardGame:Collector", ()->new MonsterGroup(new AbstractMonster[] {
                new BGTheCollector()
        }));
        addMonster("BoardGame:Champ", ()->new BGChamp());

        addMonster("BoardGame:Jaw Worms (Hard)", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGJawWorm(-490.0F, -5.0F, 2, "SDA"),
                (AbstractMonster)new BGJawWorm(-150.0F, 20.0F, 2, "DAS"),
                (AbstractMonster)new BGJawWorm(175.0F, 5.0F, 2, "ASD")
        }));
        addMonster("BoardGame:Spire Growth", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGSpireGrowth()
        }));
        addMonster("BoardGame:Exploder and Friends", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGExploder(-480.0F, 6.0F),
                (AbstractMonster)new BGRepulsor(-240.0F, -6.0F, "3D"),
                (AbstractMonster)new BGSpiker(0.0F, -12.0F)
        }));
        addMonster("BoardGame:Repulsor and Friends", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGRepulsor(-480.0F, 6.0F, "D3"),
                (AbstractMonster)new BGExploder(-240.0F, -6.0F),
                (AbstractMonster)new BGSpiker(0.0F, -12.0F)
        }));
        addMonster("BoardGame:Orb Walker v2.3", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGOrbWalker(-30.0F, 20.0F, "23")
        }));
        addMonster("BoardGame:Orb Walker v3.2", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGOrbWalker(0.0F, 26.0F, "32")
        }));
        addMonster("BoardGame:Transient", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGTransient()
        }));
        addMonster("BoardGame:Maw", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGMaw(-70.0F,20.0F)
        }));
        addMonster("BoardGame:Writhing Mass", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGWrithingMass()
        }));
        addMonster("BoardGame:3 Darklings", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGDarkling(-440.0F, 10.0F,"CUBE"),
                (AbstractMonster)new BGDarkling(-140.0F, 30.0F,"32S"),
                (AbstractMonster)new BGDarkling(180.0F, -5.0F,"S32")
        }));
        addMonster("BoardGame:Giant Head", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGGiantHead()
        }));
        addMonster("BoardGame:Nemesis", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGNemesis()
        }));
        addMonster("BoardGame:Reptomancer", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGReptomancer()
        }));
        addMonster("BoardGame:Time Eater", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGTimeEater()
        }));
        addMonster("BoardGame:Awakened One", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGCultist(-590.0F, 10.0F, false),
                (AbstractMonster)new BGCultist(-298.0F, -10.0F, false),
                (AbstractMonster)new BGAwakenedOne(100.0F, 15.0F)
        }));
        addMonster("BoardGame:Donu and Deca", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGDeca(), (AbstractMonster)new BGDonu()
        }));
        addMonster("BoardGame:The Heart", ()->new MonsterGroup(new AbstractMonster[] {
                (AbstractMonster)new BGCorruptHeart()
        }));

        // =============== EVENTS =================
        // https://github.com/daviscook477/BaseMod/wiki/Custom-Events

        // You can add the event like so:
        // BaseMod.addEvent(IdentityCrisisEvent.ID, IdentityCrisisEvent.class, TheCity.ID);
        // Then, this event will be exclusive to the City (act 2), and will show up for all characters.
        // If you want an event that's present at any part of the game, simply don't include the dungeon ID

        // If you want to have more specific event spawning (e.g. character-specific or so)
        // deffo take a look at that basemod wiki link as well, as it explains things very in-depth
        // btw if you don't provide event type, normal is assumed by default


        BaseMod.addEvent(new AddEventParams.Builder(BGLivingWall.ID, BGLivingWall.class).dungeonID(BGExordium.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGTheLibrary.ID, BGTheLibrary.class).dungeonID(BGExordium.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGGoldenIdolEvent.ID, BGGoldenIdolEvent.class).dungeonID(BGExordium.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGGoldenWing.ID, BGGoldenWing.class).dungeonID(BGExordium.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGBonfire.ID, BGBonfire.class).dungeonID(BGExordium.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGTransmogrifier.ID, BGTransmogrifier.class).dungeonID(BGExordium.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGCleric.ID, BGCleric.class).dungeonID(BGExordium.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGUpgradeShrine.ID, BGUpgradeShrine.class).dungeonID(BGExordium.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGGremlinWheelGame.ID, BGGremlinWheelGame.class).dungeonID(BGExordium.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGScrapOoze.ID, BGScrapOoze.class).dungeonID(BGExordium.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGAccursedBlacksmith.ID, BGAccursedBlacksmith.class).dungeonID(BGExordium.ID).dungeonID(BGTheCity.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGLab.ID, BGLab.class).dungeonID(BGExordium.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGBigFish.ID, BGBigFish.class).dungeonID(BGExordium.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());

        BaseMod.addEvent(new AddEventParams.Builder(BGGremlinWheelGame.ID, BGGremlinWheelGame.class).dungeonID(BGTheCity.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGGoldShrine.ID, BGGoldShrine.class).dungeonID(BGTheCity.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGCursedTome.ID, BGCursedTome.class).dungeonID(BGTheCity.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGBackToBasics.ID, BGBackToBasics.class).dungeonID(BGTheCity.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGDrugDealer.ID, BGDrugDealer.class).dungeonID(BGTheCity.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGDesigner.ID, BGDesigner.class).dungeonID(BGTheCity.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGWomanInBlue.ID, BGWomanInBlue.class).dungeonID(BGTheCity.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGBeggar.ID, BGBeggar.class).dungeonID(BGTheCity.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGKnowingSkull.ID, BGKnowingSkull.class).dungeonID(BGTheCity.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());

        BaseMod.addEvent(new AddEventParams.Builder(BGGremlinWheelGame.ID, BGGremlinWheelGame.class).dungeonID(BGTheBeyond.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGBonfire.ID, BGBonfire.class).dungeonID(BGTheBeyond.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGPurificationShrine.ID, BGPurificationShrine.class).dungeonID(BGTheBeyond.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGMindBloom.ID, BGMindBloom.class).dungeonID(BGTheBeyond.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGTombRedMask.ID, BGTombRedMask.class).dungeonID(BGTheBeyond.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGFalling.ID, BGFalling.class).dungeonID(BGTheBeyond.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGSecretPortal.ID, BGSecretPortal.class).dungeonID(BGTheBeyond.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGWindingHalls.ID, BGWindingHalls.class).dungeonID(BGTheBeyond.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
        BaseMod.addEvent(new AddEventParams.Builder(BGSensoryStone.ID, BGSensoryStone.class).dungeonID(BGTheBeyond.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());

        BaseMod.addEvent(new AddEventParams.Builder(FakeMonsterRoomEvent.ID, FakeMonsterRoomEvent.class).dungeonID(BGTheEnding.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
//        BaseMod.addEvent(new AddEventParams.Builder(BGMindBloom.ID, BGMindBloom.class).dungeonID(BGTheEnding.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
//        BaseMod.addEvent(new AddEventParams.Builder(BGTombRedMask.ID, BGTombRedMask.class).dungeonID(BGTheEnding.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
//        BaseMod.addEvent(new AddEventParams.Builder(BGFalling.ID, BGFalling.class).dungeonID(BGTheEnding.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());
//        BaseMod.addEvent(new AddEventParams.Builder(BGSecretPortal.ID, BGSecretPortal.class).dungeonID(BGTheEnding.ID).playerClass(BGIronclad.Enums.BG_IRONCLAD).create());


        // =============== /EVENTS/ =================


        logger.info("Done loading badge Image and mod options");
    }

    // =============== / POST-INITIALIZE/ =================

    // ================ ADD POTIONS ===================

    public void receiveEditPotions() {
        logger.info("Beginning to edit potions");

        // Class Specific Potion. If you want your potion to not be class-specific,
        // just remove the player class at the end (in this case the "TheDefaultEnum.THE_DEFAULT".
        // Remember, you can press ctrl+P inside parentheses like addPotions)
        BaseMod.addPotion(PlaceholderPotion.class, PLACEHOLDER_POTION_LIQUID, PLACEHOLDER_POTION_HYBRID, PLACEHOLDER_POTION_SPOTS, PlaceholderPotion.POTION_ID, BGIronclad.Enums.BG_IRONCLAD);


        logger.info("Done editing potions");
    }

    // ================ /ADD POTIONS/ ===================


    // ================ ADD RELICS ===================

    @Override
    public void receiveEditRelics() {
        logger.info("Adding relics");

        new AutoAdd("BoardGame")
                .packageFilter(AbstractBGRelic.class)
                .any(AbstractBGRelic.class, (info, relic) -> {
                    BaseMod.addRelicToCustomPool(relic, BGIronclad.Enums.BG_RED);
                    if (!info.seen) {
                        UnlockTracker.markRelicAsSeen(relic.relicId);
                    }
                });

        BaseMod.addRelicToCustomPool(new BGBurningBlood(), BGIronclad.Enums.BG_RED);
        BaseMod.addRelicToCustomPool(new BGTheDieRelic(),BGIronclad.Enums.BG_RED);


//        // This adds a relic to the Shared pool. Every character can find this relic.
//        BaseMod.addRelic(new PlaceholderRelic2(), RelicType.SHARED);


        // Mark relics as seen - makes it visible in the compendium immediately
        // If you don't have this it won't be visible in the compendium until you see them in game
        // (the others are all starters so they're marked as seen in the character file)
        UnlockTracker.markRelicAsSeen(BottledPlaceholderRelic.ID);
        logger.info("Done adding relics!");
    }

    // ================ /ADD RELICS/ ===================


    // ================ ADD CARDS ===================

    @Override
    public void receiveEditCards() {
        logger.info("Adding variables");
        //Ignore this
        pathCheck();
        // Add the Custom Dynamic Variables
        logger.info("Add variables");
        // Add the Custom Dynamic variables
        BaseMod.addDynamicVariable(new DefaultCustomVariable());
        BaseMod.addDynamicVariable(new DefaultSecondMagicNumber());

        logger.info("Adding cards");
        // Add the cards

        // The ID for this function isn't actually your modid as used for prefixes/by the getModID() method.
        // It's the mod id you give MTS in ModTheSpire.json - by default your artifact ID in your pom.xml

        //DONE: Rename the "DefaultMod" with the modid in your ModTheSpire.json file
        //DONE: The artifact mentioned in ModTheSpire.json is the artifactId in pom.xml you should've edited earlier
        new AutoAdd("BoardGame") // ${project.artifactId}
                .packageFilter(AbstractDefaultCard.class) // filters to any class in the same package as AbstractDefaultCard, nested packages included
                .setDefaultSeen(true)
                .cards();

        // .setDefaultSeen(true) unlocks the cards
        // This is so that they are all "seen" in the library,
        // for people who like to look at the card list before playing your mod

        logger.info("Done adding cards!");
    }

    // ================ /ADD CARDS/ ===================


    // ================ LOAD THE TEXT ===================

    @Override
    public void receiveEditStrings() {
        logger.info("You seeing this?");
        logger.info("Beginning to edit strings for mod with ID: " + getModID());

        // CardStrings
        BaseMod.loadCustomStringsFile(CardStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Card-Strings.json");

        // PowerStrings
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Power-Strings.json");

        // RelicStrings
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Relic-Strings.json");

        // Event Strings
        BaseMod.loadCustomStringsFile(EventStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Event-Strings.json");

        // PotionStrings
        BaseMod.loadCustomStringsFile(PotionStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Potion-Strings.json");

        // CharacterStrings
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Character-Strings.json");

        // OrbStrings
        BaseMod.loadCustomStringsFile(OrbStrings.class,
                getModID() + "Resources/localization/eng/DefaultMod-Orb-Strings.json");

        logger.info("Done editing strings");
    }

    // ================ /LOAD THE TEXT/ ===================

    // ================ LOAD THE KEYWORDS ===================

    @Override
    public void receiveEditKeywords() {
        // Keywords on cards are supposed to be Capitalized, while in Keyword-String.json they're lowercase
        //
        // Multiword keywords on cards are done With_Underscores
        //
        // If you're using multiword keywords, the first element in your NAMES array in your keywords-strings.json has to be the same as the PROPER_NAME.
        // That is, in Card-Strings.json you would have #yA_Long_Keyword (#y highlights the keyword in yellow).
        // In Keyword-Strings.json you would have PROPER_NAME as A Long Keyword and the first element in NAMES be a long keyword, and the second element be a_long_keyword

        Gson gson = new Gson();
        String json = Gdx.files.internal(getModID() + "Resources/localization/eng/DefaultMod-Keyword-Strings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(getModID().toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
                //  getModID().toLowerCase() makes your keyword mod specific (it won't show up in other cards that use that word)
            }
        }
    }

    // ================ /LOAD THE KEYWORDS/ ===================


//    @SpirePatch(
//            clz= AbstractCard.class,
//            method=SpirePatch.CLASS
//    )
//    public static class ExampleField
//    {
//        public static SpireField<String> example = new SpireField<>(()->"default value");
//    }










    // this adds "ModName:" before the ID of any card/relic/power etc.
    // in order to avoid conflicts if any other mod uses the same ID.
    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }
}

package kz.ilotterytea.maxon.pets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import kz.ilotterytea.maxon.assets.loaders.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class PetManager {
    private final ArrayList<Pet> pets;
    private final AssetManager assetManager;
    private final Logger logger = LoggerFactory.getLogger(PetManager.class);

    public PetManager(final AssetManager assetManager) {
        this.pets = new ArrayList<>();
        this.assetManager = assetManager;
    }

    public void load() {
        pets.clear();

        String data = assetManager.get("data/pets.json", Text.class).getString();
        JsonValue root = new JsonReader().parse(data);

        List<Pet> pets = new ArrayList<>();

        for (JsonValue child : root.iterator()) {
            String id = child.getString("id");
            double price = child.getDouble("price");
            double multiplier = child.getDouble("multiplier");

            JsonValue iconData = child.get("icon_data");
            int iconColumns = iconData.getInt("columns");
            int iconRows = iconData.getInt("rows");

            Pet pet = Pet.create(id, price, multiplier, iconColumns, iconRows);
            pets.add(pet);
        }

        Collections.sort(pets, (pet1, pet2) -> Double.compare(pet1.getPrice(), pet2.getPrice()));

        this.pets.addAll(pets);
        logger.info("Loaded {} pets", pets.size());
    }

    public Pet getPet(String id) {
        for (Pet pet : pets) {
            if (pet.getId().equals(id)) {
                return pet;
            }
        }
        return null;
    }


    public ArrayList<Pet> getPets() {
        return pets;
    }
}

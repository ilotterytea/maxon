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

        pets = pets.stream().sorted((pet, t1) -> {
            if (pet.getPrice() > t1.getPrice()) {
                return 1;
            } else if (pet.getPrice() < t1.getPrice()) {
                return -1;
            }
            return 0;
        }).collect(Collectors.toList());

        this.pets.addAll(pets);
        logger.info("Loaded {} pets", pets.size());
    }

    public Optional<Pet> getPet(String id) {
        return pets.stream().filter(x -> x.getId().equals(id)).findFirst();
    }

    public ArrayList<Pet> getPets() {
        return pets;
    }
}

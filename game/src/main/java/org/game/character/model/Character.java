package org.game.character.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.game.utils.Pair;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora cualquier campo que no esté definido en la clase
public class Character {

    private Long id;

    //private User user;

    private Gender gender;

    private String name;

    @JsonProperty("last_name")
    private String lastName;

    private Map<Long, Pair<Boolean, byte[]>> image;

    private Pair<Long, String> imageActive;

    private Long level;

    private Long experience;

    @JsonProperty("to_next_level")
    private Long toNextLevel;

    private BasicType type;

    private Map<Long, Qualification> skills;

    private Map<Attribute, Long> attributes;

    @JsonProperty("max_attributes")
    private Map<Attribute, Long> maxAttributes;

    @JsonProperty("sub_type")
    private SubType subType;

    @JsonProperty("profession")
    private Map<Profession, Qualification> profession;

   // @JsonProperty("inventory_character")
   // @JsonIgnoreProperties("character")
   // private InventoryCharacter inventoryCharacter;
//
   // private Card card;

    private boolean dead;

    private boolean favorite;

    private String description;

    public enum Qualification {
        F,
        E,
        D,
        C,
        B,
        A,
        S,
        X
    }

    public enum Attribute {
        HEALTH_POINTS,
        MANA,
        AGILITY,
        STAMINA,
        ATTACK,
        SPEED,
        STRENGTH,
        MAGIC_POWER,
        MAGIC_RESISTANCE,
        ARMOR,
        LEADERSHIP
    }


    public enum BasicType {
        PIRATE(List.of(SubType.GUNNER, SubType.PIRATE_KNIGHT)),
        SOLDIER(List.of(SubType.KNIGHT, SubType.HEAVY_KNIGHT)),
        RANGE(List.of(SubType.SHOOTER, SubType.ARCHER)),
        MAGICIAN(List.of(SubType.ELEMENTAL_MAGE, Character.SubType.HEALER, SubType.SUMMONER)),
        DRUID(List.of(SubType.DARK_DRUID, SubType.LIGHT_DRUID)),
        STEALER(List.of(SubType.KILLER)),
        HALF_BEAST(List.of(SubType.BEAST_TAMER, SubType.TURNED)),
        SUMMONED(List.of(SubType.ALIVE_SUMMONED));


        private List<Character.SubType> subTypes;

        BasicType(List<Character.SubType> subTypes) {
            this.subTypes = subTypes;
        }

        public List<SubType> getSubTypes() {
            return subTypes;
        }

        public void setSubTypes(List<SubType> subTypes) {
            this.subTypes = subTypes;
        }
    }

    public enum Profession {
        CHEF,
        BLACKSMITH,
        BARD,
        HUNTER,
        EXPLORER,
        ENCHANTER,
        ALCHEMIST,
        BUILDER,
        INSTRUCTOR
    }

    public enum SubType {
        HEALER,
        ARCHER,
        SUMMONER,
        KILLER,
        SHOOTER,
        GUNNER,
        PIRATE_KNIGHT,
        ELEMENTAL_MAGE,
        LIGHT_DRUID,
        DARK_DRUID,
        KNIGHT,
        HEAVY_KNIGHT,
        BEAST_TAMER,
        TURNED,
        ALIVE_SUMMONED;
    }

    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }

}

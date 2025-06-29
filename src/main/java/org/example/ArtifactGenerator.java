package org.example;

import java.util.*;

public class ArtifactGenerator {
    private static final String[] ARTIFACT_TYPES = {"Sands of Eon", "Goblet of Eonothem", "Circlet of Logos"};

    // Main stat chances
    private static final Map<String, Double> MAIN_STATS = new HashMap<>() {{
        put("HP%", 26.68);
        put("ATK%", 26.66);
        put("DEF%", 26.66);
        put("Energy Recharge%", 10.00);
        put("Elemental Mastery", 10.00);
        put("Pyro DMG Bonus%", 5.00);
        put("Electro DMG Bonus%", 5.00);
        put("Cryo DMG Bonus%", 5.00);
        put("Hydro DMG Bonus%", 5.00);
        put("Dendro DMG Bonus%", 5.00);
        put("Anemo DMG Bonus%", 5.00);
        put("Geo DMG Bonus%", 5.00);
        put("Physical DMG Bonus%", 5.00);
        put("CRIT Rate%", 10.00);
        put("CRIT DMG%", 10.00);
        put("Healing Bonus%", 10.00);
    }};

    // Substat weights
    private static final Map<String, Double> SUB_STATS = new HashMap<>() {{
        put("HP", 6.0);
        put("ATK", 6.0);
        put("DEF", 6.0);
        put("HP%", 4.0);
        put("ATK%", 4.0);
        put("DEF%", 4.0);
        put("Energy Recharge%", 4.0);
        put("Elemental Mastery", 4.0);
        put("CRIT Rate%", 3.0);
        put("CRIT DMG%", 3.0);
    }};

    // Define substat values for each tier (5-star values)
    private static final Map<String, List<Double>> SUBSTAT_TIERS = new HashMap<>() {{
        put("HP", List.of(209.0, 239.0, 269.0, 299.0));
        put("ATK", List.of(14.0, 16.0, 18.0, 19.0));
        put("DEF", List.of(16.0, 19.0, 21.0, 23.0));
        put("HP%", List.of(4.1, 4.7, 5.3, 5.8));
        put("ATK%", List.of(4.1, 4.7, 5.3, 5.8));
        put("DEF%", List.of(5.1, 5.8, 6.6, 7.3));
        put("Energy Recharge%", List.of(4.5, 5.2, 5.8, 6.5));
        put("Elemental Mastery", List.of(16.0, 19.0, 21.0, 23.0));
        put("CRIT Rate%", List.of(2.7, 3.1, 3.5, 3.9));
        put("CRIT DMG%", List.of(5.4, 6.2, 7.0, 7.8));
    }};

    private static final Random RANDOM = new Random();

    // Add getters for the static fields so Artifact class can access them
    public static Map<String, Double> getSubStats() {
        return SUB_STATS;
    }

    public static Map<String, List<Double>> getSubstatTiers() {
        return SUBSTAT_TIERS;
    }

    public static Artifact generateRandomArtifact() {
        String type = ARTIFACT_TYPES[RANDOM.nextInt(ARTIFACT_TYPES.length)];
        String mainStat = selectMainStat(type);
        Artifact artifact = new Artifact("Random Artifact", "Example Set", type, mainStat);

        // Determine number of initial substats (3 or 4)
        int numberOfSubstats = 3 + RANDOM.nextInt(2);
        List<String> availableSubstats = new ArrayList<>(SUB_STATS.keySet());
        List<String> selectedSubstats = new ArrayList<>();

        // Remove main stat from available substats if it exists there
        availableSubstats.remove(mainStat);

        // Select substats based on weights and generate their values
        while (selectedSubstats.size() < numberOfSubstats) {
            // Calculate total weight of remaining available substats
            double totalWeight = 0;
            Map<String, Double> currentWeights = new HashMap<>();

            for (String stat : availableSubstats) {
                double weight = SUB_STATS.get(stat);
                currentWeights.put(stat, weight);
                totalWeight += weight;
            }

            // Select substat based on weights
            double randomValue = RANDOM.nextDouble() * totalWeight;
            double cumulativeWeight = 0;
            String selectedStat = null;

            for (Map.Entry<String, Double> entry : currentWeights.entrySet()) {
                cumulativeWeight += entry.getValue();
                if (randomValue <= cumulativeWeight) {
                    selectedStat = entry.getKey();
                    break;
                }
            }

            // Select a random tier for the substat
            if (selectedStat != null) {
                List<Double> tierValues = SUBSTAT_TIERS.get(selectedStat);
                double value = tierValues.get(RANDOM.nextInt(tierValues.size()));

                artifact.addSubstat(selectedStat, value);
                selectedSubstats.add(selectedStat);
                availableSubstats.remove(selectedStat);
            }
        }

        return artifact;
    }

    private static String selectMainStat(String type) {
        List<String> validMainStats = new ArrayList<>();

        switch (type) {
            case "Sands of Eon":
                validMainStats.add("HP%");
                validMainStats.add("ATK%");
                validMainStats.add("DEF%");
                validMainStats.add("Energy Recharge%");
                validMainStats.add("Elemental Mastery");
                break;
            case "Goblet of Eonothem":
                validMainStats.add("HP%");
                validMainStats.add("ATK%");
                validMainStats.add("DEF%");
                validMainStats.add("Pyro DMG Bonus%");
                validMainStats.add("Electro DMG Bonus%");
                validMainStats.add("Cryo DMG Bonus%");
                validMainStats.add("Hydro DMG Bonus%");
                validMainStats.add("Dendro DMG Bonus%");
                validMainStats.add("Anemo DMG Bonus%");
                validMainStats.add("Geo DMG Bonus%");
                validMainStats.add("Physical DMG Bonus%");
                validMainStats.add("Elemental Mastery");
                break;
            case "Circlet of Logos":
                validMainStats.add("HP%");
                validMainStats.add("ATK%");
                validMainStats.add("DEF%");
                validMainStats.add("CRIT Rate%");
                validMainStats.add("CRIT DMG%");
                validMainStats.add("Healing Bonus%");
                validMainStats.add("Elemental Mastery");
                break;
        }

        return selectStatBasedOnChance(MAIN_STATS, validMainStats);
    }

    private static String selectStatBasedOnChance(Map<String, Double> statChances, List<String> validStats) {
        double totalChance = 0;
        for (String stat : validStats) {
            totalChance += statChances.get(stat);
        }

        double randomValue = RANDOM.nextDouble() * totalChance;
        double cumulativeChance = 0;

        for (String stat : validStats) {
            cumulativeChance += statChances.get(stat);
            if (randomValue < cumulativeChance) {
                return stat;
            }
        }
        return validStats.get(0);
    }
}
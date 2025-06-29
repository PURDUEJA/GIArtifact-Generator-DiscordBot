package org.example;

import java.util.*;

public class Artifact {
    private String name;
    private String set;
    private String type;
    private String mainStat;
    private double mainStatChance;
    private Map<String, List<Double>> substats;
    private int level;

    public Artifact(String name, String set, String type, String mainStat) {
        this.name = name;
        this.set = set;
        this.type = type;
        this.mainStat = mainStat;
        this.substats = new HashMap<>();
        this.level = 0;
    }

    public void addSubstat(String substat, double value) {
        List<Double> values = new ArrayList<>();
        values.add(value);
        substats.put(substat, values);
    }

    public void levelUp(Random random) {
        if (level >= 20) {
            throw new IllegalStateException("Artifact is already at maximum level");
        }

        level += 4;

        if (substats.size() == 3 && level == 4) {
            addNewSubstat(random);
        } else if (substats.size() == 4) {
            enhanceRandomSubstat(random);
        }
    }

    private void addNewSubstat(Random random) {
        List<String> availableSubstats = new ArrayList<>(ArtifactGenerator.getSubStats().keySet());
        availableSubstats.removeAll(substats.keySet());
        availableSubstats.remove(mainStat);

        double totalWeight = 0;
        Map<String, Double> currentWeights = new HashMap<>();

        for (String stat : availableSubstats) {
            double weight = ArtifactGenerator.getSubStats().get(stat);
            currentWeights.put(stat, weight);
            totalWeight += weight;
        }

        double randomValue = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0;
        String selectedStat = null;

        for (Map.Entry<String, Double> entry : currentWeights.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue <= cumulativeWeight) {
                selectedStat = entry.getKey();
                break;
            }
        }

        if (selectedStat != null) {
            List<Double> tierValues = ArtifactGenerator.getSubstatTiers().get(selectedStat);
            double value = tierValues.get(random.nextInt(tierValues.size()));
            addSubstat(selectedStat, value);
        }
    }

    private void enhanceRandomSubstat(Random random) {
        List<String> existingSubstats = new ArrayList<>(substats.keySet());
        String selectedStat = existingSubstats.get(random.nextInt(existingSubstats.size()));
        List<Double> tierValues = ArtifactGenerator.getSubstatTiers().get(selectedStat);
        double additionalValue = tierValues.get(random.nextInt(tierValues.size()));
        substats.get(selectedStat).add(additionalValue);
    }

    // Getters
    public String getName() { return name; }
    public String getSet() { return set; }
    public String getType() { return type; }
    public String getMainStat() { return mainStat; }
    public double getMainStatChance() { return mainStatChance; }
    public Map<String, List<Double>> getSubstats() { return substats; }
    public int getLevel() { return level; }

    @Override
    public String toString() {
        return "Artifact Details:\n" +
                "-------------------\n" +
                "Name: " + name + "\n" +
                "Set: " + set + "\n" +
                "Type: " + type + "\n" +
                "Level: " + level + "\n" +
                "Main Stat: " + mainStat + "\n" +
                "Substats: \n" + formatSubstats(substats);
    }

    private String formatSubstats(Map<String, List<Double>> substats) {
        if (substats == null || substats.isEmpty()) {
            return "None";
        }

        StringBuilder formatted = new StringBuilder();
        for (Map.Entry<String, List<Double>> entry : substats.entrySet()) {
            formatted.append("* ");

            // Sum up all values for this substat
            double total = entry.getValue().stream().mapToDouble(Double::doubleValue).sum();

            // Format the stat name and value
            String statName = entry.getKey();
            if (statName.endsWith("%")) {
                // Remove the % from the stat name and add it after the value
                formatted.append(statName.substring(0, statName.length() - 1))
                        .append(": ")
                        .append(String.format("%.1f", total))
                        .append("%");
            } else {
                formatted.append(statName)
                        .append(": ")
                        .append(String.format("%.1f", total));
            }
            formatted.append("\n");
        }
        return formatted.toString();
    }
}
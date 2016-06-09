package de.symeda.sormas.backend.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.symeda.sormas.api.caze.CaseDto;

public class MockDataGenerator {
    private static int nextCaseId = 1;
    private static final Random random = new Random(1);

    private static String[] word1 = new String[] { "The art of", "Mastering",
            "The secrets of", "Avoiding", "For fun and profit: ",
            "How to fail at", "10 important facts about",
            "The ultimate guide to", "Book of", "Surviving", "Encyclopedia of",
            "Very much", "Learning the basics of", "The cheap way to",
            "Being awesome at", "The life changer:", "The Vaadin way:",
            "Becoming one with", "Beginners guide to",
            "The complete visual guide to", "The mother of all references:" };

    private static String[] word2 = new String[] { "gardening",
            "living a healthy life", "designing tree houses", "home security",
            "intergalaxy travel", "meditation", "ice hockey",
            "children's education", "computer programming", "Vaadin TreeTable",
            "winter bathing", "playing the cello", "dummies", "rubber bands",
            "feeling down", "debugging", "running barefoot",
            "speaking to a big audience", "creating software", "giant needles",
            "elephants", "keeping your wife happy" };

    public static List<CaseDto> createCases() {
        List<CaseDto> cases = new ArrayList<CaseDto>();
        for (int i = 0; i < 10; i++) {
            CaseDto p = createCase();
            cases.add(p);
        }
        return cases;
    }

    private static CaseDto createCase() {
        CaseDto c = new CaseDto();
        c.setUuid(java.util.UUID.randomUUID().toString());
        c.setDescription(generateName());
        return c;
    }


    private static String generateName() {
        return word1[random.nextInt(word1.length)] + " "
                + word2[random.nextInt(word2.length)];
    }}

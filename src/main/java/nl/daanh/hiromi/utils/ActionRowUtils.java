package nl.daanh.hiromi.utils;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Arrays;

public class ActionRowUtils {

    public static ActionRow[] splitButtons(Button[] buttonsToSplit) {
        return splitButtons(buttonsToSplit, 5);
    }

    /**
     * Splits the buttons so they fit into multiple action rows
     *
     * @param buttonsToSplit the buttons that should be split into rows
     * @param chunkSize      amount of buttons allowed per row
     * @return array with one or more rows of buttons
     */
    public static ActionRow[] splitButtons(Button[] buttonsToSplit, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size can not be lower then or equal to 0");
        }

        if (chunkSize > 5) {
            throw new IllegalArgumentException("Chunk size can not be lager then 5");
        }

        // first we have to check if the array can be split in multiple
        // arrays of equal 'chunk' size
        int rest = buttonsToSplit.length % chunkSize;  // if rest>0 then our last array will have less elements than the others
        // then we check in how many arrays we can split our input array
        int chunks = buttonsToSplit.length / chunkSize + (rest > 0 ? 1 : 0); // we may have to add an additional array for the 'rest'
        // now we know how many arrays we need and create our result array
        ActionRow[] arrays = new ActionRow[chunks];
        // we create our resulting arrays by copying the corresponding
        // part from the input array. If we have a rest (rest>0), then
        // the last array will have less elements than the others. This
        // needs to be handled separately, so we iterate 1 times less.
        for (int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++) {
            // this copies 'chunk' times 'chunkSize' elements into a new array
            arrays[i] = ActionRow.of(Arrays.copyOfRange(buttonsToSplit, i * chunkSize, i * chunkSize + chunkSize));
        }
        if (rest > 0) { // only when we have a rest
            // we copy the remaining elements into the last chunk
            arrays[chunks - 1] = ActionRow.of(Arrays.copyOfRange(buttonsToSplit, (chunks - 1) * chunkSize, (chunks - 1) * chunkSize + rest));
        }
        return arrays; // that's it
    }

    /**
     * Source https://stackoverflow.com/questions/27857011/how-to-split-a-string-array-into-small-chunk-arrays-in-java
     *
     * @param arrayToSplit array to split
     * @param chunkSize    how large single arrays are allowed to be
     * @return create nested array
     */
    public static Object[][] splitArray(Object[] arrayToSplit, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size can not be lower then or equal to 0");
        }
        // first we have to check if the array can be split in multiple
        // arrays of equal 'chunk' size
        int rest = arrayToSplit.length % chunkSize;  // if rest>0 then our last array will have less elements than the others
        // then we check in how many arrays we can split our input array
        int chunks = arrayToSplit.length / chunkSize + (rest > 0 ? 1 : 0); // we may have to add an additional array for the 'rest'
        // now we know how many arrays we need and create our result array
        Object[][] arrays = new Object[chunks][];
        // we create our resulting arrays by copying the corresponding
        // part from the input array. If we have a rest (rest>0), then
        // the last array will have less elements than the others. This
        // needs to be handled separately, so we iterate 1 times less.
        for (int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++) {
            // this copies 'chunk' times 'chunkSize' elements into a new array
            arrays[i] = Arrays.copyOfRange(arrayToSplit, i * chunkSize, i * chunkSize + chunkSize);
        }
        if (rest > 0) { // only when we have a rest
            // we copy the remaining elements into the last chunk
            arrays[chunks - 1] = Arrays.copyOfRange(arrayToSplit, (chunks - 1) * chunkSize, (chunks - 1) * chunkSize + rest);
        }
        return arrays; // that's it
    }
}

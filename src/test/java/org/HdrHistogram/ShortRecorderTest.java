/**
 * HistogramTest.java
 * Written by Gil Tene of Azul Systems, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 *
 * @author Gil Tene
 */

package org.HdrHistogram;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test for {@link Histogram}
 */
public class ShortRecorderTest {
    static final long highestTrackableValue = 3600L * 1000 * 1000; // e.g. for 1 hr in usec units

    @Test
    public void testIntervalRecording() throws Exception {

        ShortCountsHistogram histogram = new ShortCountsHistogram(highestTrackableValue, 3);
        ShortRecorder recorder1 =
                new ShortRecorder(highestTrackableValue, 3);
        ShortRecorder recorder2 =
                new ShortRecorder(highestTrackableValue, 3);


        for (int i = 0; i < 10000; i++) {
            histogram.recordValue(3000 * i);
            recorder1.recordValue(3000 * i);
            recorder2.recordValue(3000 * i);
        }

        ShortCountsHistogram histogram2 = recorder1.getIntervalHistogram();
        Assert.assertEquals(histogram, histogram2);

        recorder2.getIntervalHistogramInto(histogram2);
        Assert.assertEquals(histogram, histogram2);

        for (int i = 0; i < 5000; i++) {
            histogram.recordValue(3000 * i);
            recorder1.recordValue(3000 * i);
            recorder2.recordValue(3000 * i);
        }

        ShortCountsHistogram histogram3 = recorder1.getIntervalHistogram();

        ShortCountsHistogram sumHistogram = histogram2.copy();
        sumHistogram.add(histogram3);
        Assert.assertEquals(histogram, sumHistogram);

        recorder2.getIntervalHistogram();

        for (int i = 5000; i < 10000; i++) {
            histogram.recordValue(3000 * i);
            recorder1.recordValue(3000 * i);
            recorder2.recordValue(3000 * i);
        }

        ShortCountsHistogram histogram4 = recorder1.getIntervalHistogram();
        histogram4.add(histogram3);
        Assert.assertEquals(histogram4, histogram2);

        recorder2.getIntervalHistogramInto(histogram4);
        histogram4.add(histogram3);
        Assert.assertEquals(histogram4, histogram2);
    }

    //@Test
    //public void testSimpleAutosizingRecorder() throws Exception {
    //    ShortRecorder recorder = new ShortRecorder(3);
    //    ShortCountsHistogram histogram = recorder.getIntervalHistogram();
    //}

    // Recorder Recycling tests:

    @Test
    public void testRecycling() throws Exception {
        ShortRecorder recorder = new ShortRecorder(highestTrackableValue, 3);
        ShortCountsHistogram histogramA = recorder.getIntervalHistogram();
        ShortCountsHistogram histogramB = recorder.getIntervalHistogram(histogramA);
        ShortCountsHistogram histogramC = recorder.getIntervalHistogram(histogramA, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRecyclingContainingClassEnforcement() throws Exception {
        ShortCountsHistogram histToRecycle = new ShortCountsHistogram(3);
        ShortRecorder recorder = new ShortRecorder(highestTrackableValue, 3);
        ShortCountsHistogram histogramA = recorder.getIntervalHistogram(histToRecycle);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRecyclingContainingInstanceEnforcement() throws Exception {
        ShortRecorder recorder1 = new ShortRecorder(highestTrackableValue, 3);
        ShortRecorder recorder2 = new ShortRecorder(highestTrackableValue, 3);
        ShortCountsHistogram histToRecycle = recorder1.getIntervalHistogram();
        ShortCountsHistogram histToRecycle2 = recorder2.getIntervalHistogram(histToRecycle);
    }

    @Test
    public void testRecyclingContainingInstanceNonEnforcement() throws Exception {
        ShortRecorder recorder1 = new ShortRecorder(highestTrackableValue, 3);
        ShortRecorder recorder2 = new ShortRecorder(highestTrackableValue, 3);
        ShortCountsHistogram histToRecycle = recorder1.getIntervalHistogram();
        ShortCountsHistogram histToRecycle2 = recorder2.getIntervalHistogram(histToRecycle, false);
    }
}

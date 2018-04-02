package org.hwyl.sexytopo.control.io.thirdparty.survex;

import junit.framework.Assert;

import org.hwyl.sexytopo.model.survey.Station;
import org.hwyl.sexytopo.model.survey.Survey;
import org.hwyl.sexytopo.testhelpers.BasicTestSurveyCreator;
import org.junit.Test;


public class SurvexExporterTest {

    @Test
    public void testBasicExport() {
        SurvexExporter survexExporter = new SurvexExporter();
        Survey oneNorth = BasicTestSurveyCreator.createStraightNorth();
        String content = survexExporter.getContent(oneNorth);
        Assert.assertTrue(content.contains("1\t2\t5.00\t0.00\t0.00"));
        Assert.assertTrue(content.contains("2\t3\t5.00\t0.00\t0.00"));
        Assert.assertTrue(content.contains("3\t4\t5.00\t0.00\t0.00"));

    }

    @Test
    public void testBasicExportWithPromotedLegs() {
        SurvexExporter survexExporter = new SurvexExporter();
        Survey oneNorth = BasicTestSurveyCreator.createStraightNorthThroughRepeats();
        String content = survexExporter.getContent(oneNorth);
        Assert.assertTrue(content.contains("1\t2\t5.00\t0.00\t0.00"));
        Assert.assertTrue(content.contains(
                "{from: 5.00 0.00 0.00, 5.00 0.00 0.00, 5.00 0.00 0.00}"));

    }

    @Test
    public void testCommentsAreIncluded() {
        SurvexExporter survexExporter = new SurvexExporter();
        Survey oneNorth = BasicTestSurveyCreator.createStraightNorthThroughRepeats();
        Station latest = oneNorth.getActiveStation();
        String testComment = "Comment McComment Face";
        latest.setComment(testComment);
        String content = survexExporter.getContent(oneNorth);
        Assert.assertTrue(content.contains(testComment));
    }


}




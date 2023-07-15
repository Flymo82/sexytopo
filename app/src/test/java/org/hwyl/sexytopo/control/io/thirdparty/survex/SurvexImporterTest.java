package org.hwyl.sexytopo.control.io.thirdparty.survex;

import org.hwyl.sexytopo.SexyTopoConstants;
import org.hwyl.sexytopo.model.survey.Leg;
import org.hwyl.sexytopo.model.survey.Station;
import org.hwyl.sexytopo.model.survey.Survey;
import org.junit.Assert;
import org.junit.Test;


public class SurvexImporterTest {


    @Test
    public void testBasicImport() throws Exception {
        final String testContent =
                "1\t2\t5.0\t0.0\t0.0";
        Survey survey = new Survey();
        SurvexImporter.parse(testContent, survey);
        Assert.assertEquals(survey.getAllStations().size(), 2);
    }

    @Test
    public void testBasicImportRecordsPromotions() throws Exception {
        final String testContent =
                "1\t2\t5.0\t0.0\t0.0\t; {from: 5.0 0.0 0.0, 5.0 0.0 0.0, 5.0 0.0 0.0}";
        Survey survey = new Survey();
        SurvexImporter.parse(testContent, survey);
        Leg leg = survey.getOrigin().getConnectedOnwardLegs().get(0);
        Assert.assertEquals(3, leg.getPromotedFrom().length);
    }

    @Test
    public void testBasicImportHandlesComments() throws Exception {
        final String testContent =
                "1\t2\t5.0\t0.0\t0.0\t; {from: 5.0 0.0 0.0, 5.0 0.0 0.0, 5.0 0.0 0.0} testComment";
        Survey survey = new Survey();
        SurvexImporter.parse(testContent, survey);
        Station created = survey.getStationByName("2");
        Assert.assertEquals(created.getComment(), "testComment");
    }


    @Test
    public void testCommentInstructionParsing() {
        Leg[] legs = SurvexImporter.parseAnyPromotedLegs(
                "{from: 1 0 0, 1.0 0.0 0.0, 1.0 0.0 0.0}");
        Assert.assertEquals(3, legs.length);
        for (Leg leg : legs) {
            Assert.assertEquals(leg.getDistance(), 1.0, SexyTopoConstants.ALLOWED_DOUBLE_DELTA);
            Assert.assertEquals(leg.getAzimuth(), 0.0, SexyTopoConstants.ALLOWED_DOUBLE_DELTA);
            Assert.assertEquals(leg.getInclination(), 0.0, SexyTopoConstants.ALLOWED_DOUBLE_DELTA);
        }
    }
}




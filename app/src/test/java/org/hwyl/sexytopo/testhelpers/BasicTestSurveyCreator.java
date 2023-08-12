package org.hwyl.sexytopo.testhelpers;

import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import org.hwyl.sexytopo.control.util.SurveyUpdater;
import org.hwyl.sexytopo.model.survey.Leg;
import org.hwyl.sexytopo.model.survey.Survey;
import org.hwyl.sexytopo.model.survey.Trip;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BasicTestSurveyCreator {


    public static Survey createStraightNorthThroughRepeats() {
        Survey survey = new Survey();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Leg leg = new Leg(5, 0, 0);
                SurveyUpdater.update(survey, leg);
            }
        }
        return survey;
    }


    public static Survey createStraightNorth() {
        Survey survey = new Survey();

        Leg leg0 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg0);

        Leg leg1 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg1);

        Leg leg2 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg2);

        return survey;
    }


    public static Survey createStraightSouth() {
        Survey survey = new Survey();

        Leg leg0 = new Leg(5, 180, 0);
        SurveyUpdater.updateWithNewStation(survey, leg0);

        Leg leg1 = new Leg(5, 180, 0);
        SurveyUpdater.updateWithNewStation(survey, leg1);

        Leg leg2 = new Leg(5, 180, 0);
        SurveyUpdater.updateWithNewStation(survey, leg2);

        return survey;

    }


    public static Survey createRightRight() {
        Survey survey = new Survey();

        Leg leg0 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg0);

        Leg leg1 = new Leg(5, 90, 0);
        SurveyUpdater.updateWithNewStation(survey, leg1);

        Leg leg2 = new Leg(5, 180, 0);
        SurveyUpdater.updateWithNewStation(survey, leg2);

        return survey;

    }


    public static Survey create5MDown() {
        Survey survey = new Survey();

        Leg leg0 = new Leg(5, 0, -90);
        SurveyUpdater.updateWithNewStation(survey, leg0);

        return survey;
    }


    public static Survey create5MEast() {
        Survey survey = new Survey();

        Leg leg0 = new Leg(5, 90, 0);
        SurveyUpdater.updateWithNewStation(survey, leg0);

        Leg splay0Left = new Leg(1, 0, 0);
        SurveyUpdater.update(survey, splay0Left);
        Leg splay0Right = new Leg(1, 180, 0);
        SurveyUpdater.update(survey, splay0Right);

        return survey;
    }


    public static Survey createStraightNorthWith1EBranch() {
        Survey survey = new Survey();

        Leg leg0 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg0);

        Leg leg1 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg1);

        Leg leg2 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg2);

        survey.setActiveStation(survey.getStationByName("1"));
        Leg legBranch = new Leg(5, 90, 0);
        SurveyUpdater.updateWithNewStation(survey, legBranch);

        return survey;
    }


    public static Survey createStraightNorthWith2EBranch() {
        Survey survey = new Survey();

        Leg leg0 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg0);

        Leg leg1 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg1);

        Leg leg2 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg2);

        survey.setActiveStation(survey.getStationByName("1"));
        Leg legBranch = new Leg(5, 90, 0);
        SurveyUpdater.updateWithNewStation(survey, legBranch);

        Leg legBranch2 = new Leg(5, 90, 0);
        SurveyUpdater.updateWithNewStation(survey, legBranch2);

        return survey;
    }


    public static Survey createStraightNorthWith2EBranchFromS2() {
        Survey survey = new Survey();

        Leg leg0 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg0);

        Leg leg1 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg1);

        Leg leg2 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg2);

        survey.setActiveStation(survey.getStationByName("2"));
        Leg legBranch = new Leg(5, 90, 0);
        SurveyUpdater.updateWithNewStation(survey, legBranch);

        Leg legBranch2 = new Leg(5, 90, 0);
        SurveyUpdater.updateWithNewStation(survey, legBranch2);

        return survey;
    }


    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    public static Survey createStraightNorthWithTrip() {
        Survey survey = new Survey();

        List<Trip.TeamEntry> team = new ArrayList<>();
        team.add(new Trip.TeamEntry(
                "Alice", Arrays.asList(Trip.Role.BOOK)));
        team.add(new Trip.TeamEntry("Bob",
                Arrays.asList(Trip.Role.INSTRUMENTS, Trip.Role.DOG)));
        Trip trip = new Trip();
        trip.setTeam(team);
        survey.setTrip(trip);

        Leg leg0 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg0);

        Leg leg1 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg1);

        Leg leg2 = new Leg(5, 0, 0);
        SurveyUpdater.updateWithNewStation(survey, leg2);

        return survey;
    }

    public static void mockSurveyUri(Survey survey, String uri) {
        Uri mockUri = Mockito.mock(Uri.class);
        Mockito.when(mockUri.toString()).thenReturn(uri);
        DocumentFile mockDocumentFile = Mockito.mock(DocumentFile.class);
        Mockito.when(mockDocumentFile.getUri()).thenReturn(mockUri);
        Mockito.when(mockDocumentFile.getName()).thenReturn(uri);
        survey.setDirectory(mockDocumentFile);
    }
}

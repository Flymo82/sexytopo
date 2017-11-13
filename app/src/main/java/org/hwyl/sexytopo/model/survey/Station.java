package org.hwyl.sexytopo.model.survey;

import java.util.ArrayList;
import java.util.List;


public class Station extends SurveyComponent {

    private final String name;
    private List<Leg> onwardLegs = new ArrayList<>();
    private String comment = "";

    public Station(String name) {
        this.name = name;
    }

    public Station(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }

    public Station(Station station, String name) {
        this.name = name;
        this.onwardLegs = station.onwardLegs;
        this.comment = station.comment;
    }

    public String getName() {
        return name;
    }

    public List<Leg> getOnwardLegs() {
        return onwardLegs;
    }

    public void addOnwardLeg(Leg leg) {
        onwardLegs.add(leg);
    }

    public List<Leg> getUnconnectedOnwardLegs() {
        List<Leg> unconnectedOnwardLegs = new ArrayList<>();
        for (Leg leg : onwardLegs) {
            if (!leg.hasDestination()) {
                unconnectedOnwardLegs.add(leg);
            }
        }
        return unconnectedOnwardLegs;
    }

    public List<Leg> getConnectedOnwardLegs() {
        List<Leg> connectedOnwardLegs = new ArrayList<>();
        for (Leg leg : onwardLegs) {
            if (leg.hasDestination()) {
                connectedOnwardLegs.add(leg);
            }
        }
        return connectedOnwardLegs;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean hasComment() {
        return comment.length() > 0;
    }

    public String toString() {
        return name;
    }
}

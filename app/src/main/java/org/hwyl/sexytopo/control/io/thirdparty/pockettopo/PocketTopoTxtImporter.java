package org.hwyl.sexytopo.control.io.thirdparty.pockettopo;

import org.hwyl.sexytopo.control.io.basic.Loader;
import org.hwyl.sexytopo.control.io.translation.Importer;
import org.hwyl.sexytopo.control.util.SurveyUpdater;
import org.hwyl.sexytopo.control.util.TextTools;
import org.hwyl.sexytopo.model.graph.Coord2D;
import org.hwyl.sexytopo.model.graph.Projection2D;
import org.hwyl.sexytopo.model.graph.Space;
import org.hwyl.sexytopo.model.sketch.BrushColour;
import org.hwyl.sexytopo.model.sketch.Colour;
import org.hwyl.sexytopo.model.sketch.PathDetail;
import org.hwyl.sexytopo.model.sketch.Sketch;
import org.hwyl.sexytopo.model.survey.Leg;
import org.hwyl.sexytopo.model.survey.Station;
import org.hwyl.sexytopo.model.survey.Survey;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic import for the .txt file that is exported by PocketTopo.
 */
public class PocketTopoTxtImporter extends Importer {


    public Survey toSurvey(File file) {

        String text = Loader.slurpFile(file.getAbsolutePath());

        // FIXME we're ignoring the metadata for now

        Survey survey = new Survey(getDefaultName(file));

        parseDataAndUpdateSurvey(survey, text);

        Sketch elevation = getElevation(survey, text);
        survey.setElevationSketch(elevation);

        Sketch plan = getPlan(survey, text);
        survey.setPlanSketch(plan);

        survey.setSaved(true);

        return survey;
    }


    public boolean canHandleFile(File file) {
        return file.getName().endsWith("txt");
    }


    private static void parseDataAndUpdateSurvey(Survey survey, String fullText) {

        String text = getSection(fullText, "DATA");

        boolean firstStation = true;
        for (String line : TextTools.toArrayOfLines(text)) {
            String[] fields = line.split("\\t");

            if (fields.length < 3) {
                continue;
            }

            String fromStationName = fields[0];
            String toStationName = fields[1];

            double azimuth = Double.parseDouble(fields[2]);
            double inclination = Double.parseDouble(fields[3]);
            double distance = Double.parseDouble(fields[4]);

            if (firstStation) {
                survey.getOrigin().setName(fromStationName);
                firstStation = false;
            }

            Station fromStation = survey.getStationByName(fromStationName);

            survey.setActiveStation(fromStation);

            if (toStationName.equals("")) {
                Leg leg = new Leg(distance, azimuth, inclination);
                SurveyUpdater.update(survey, leg);
            } else {
                Station toStation = new Station(toStationName);
                Leg leg = new Leg(distance, azimuth, inclination, toStation, new Leg[]{});
                SurveyUpdater.updateWithNewStation(survey, leg);
            }
        }
    }

    private static Sketch getPlan(Survey survey, String fullText) {
        String text = getSection(fullText, "PLAN");
        Space<Coord2D> projection = Projection2D.PLAN.project(survey);
        Sketch plan = parseSketch(survey, text, projection);
        return plan;
    }

    private static Sketch getElevation(Survey survey, String fullText) {
        String text = getSection(fullText, "ELEVATION");
        Space<Coord2D> projection = Projection2D.EXTENDED_ELEVATION.project(survey);
        Sketch elevation = parseSketch(survey, text, projection);
        return elevation;
    }

    private static Sketch parseSketch(Survey survey, String text, Space<Coord2D> projection) {
        Sketch sketch = new Sketch();
        Coord2D offset = extractOffset(survey, text, projection);
        Set<PathDetail> pathDetails = parsePolylines(text, offset);
        sketch.setPathDetails(pathDetails);
        return sketch;
    }

    public static String getSection(String text, String header) {
        // a section in the file format appears to be made up of a header, followed by the content,
        // followed by a blank line. There are no intermediate blank lines.

        text = text + "\n\n"; // hack (ensure last section is terminated by newline)

        Pattern pattern = Pattern.compile(
            "(?sm)" +
            "^" + header.toUpperCase() + "\\n" +
            "(.*?)\\n" +
            "^\\n");
        Matcher matcher = pattern.matcher(text);
        matcher.find();
        return matcher.group(1);
    }


    public static Coord2D extractOffset(
            Survey survey, String text, Space<Coord2D> projection) {

        String stationsSection = getNamedSubSection(text, "STATIONS");
        String[] lines = TextTools.toArrayOfLines(stationsSection);

        Station guessedAnchorStation = survey.getOrigin();
        Coord2D offset = getOffsetForNamedStation(lines, guessedAnchorStation);

        if (offset == null) {
            if (guessedAnchorStation.getConnectedOnwardLegs().size() > 0) {
                Leg onward = guessedAnchorStation.getConnectedOnwardLegs().get(0);
                guessedAnchorStation = onward.getDestination();
                offset = getOffsetForNamedStation(lines, guessedAnchorStation);
                Coord2D position = projection.getStationMap().get(guessedAnchorStation);
                offset = offset.minus(position.flipVertically());
            }
        }

        if (offset == null) { // ¯\_(ツ)_/¯
            offset = Coord2D.ORIGIN;
        }

        return offset;
    }

    public static Coord2D getOffsetForNamedStation(String[] lines, Station station) {
        for (String line : lines) {
            String[] tokens = line.split("\t");
            String stationName = tokens[2];
            if (stationName.equals(station.getName())) {
                double x = Double.parseDouble(tokens[0]);
                double y = Double.parseDouble(tokens[1]);
                return new Coord2D(x, y);
            }
        }

        return null;
    }


    public static String getNamedSubSection(String text, String header, String sectionHeader) {
        String section = getSection(text, sectionHeader);
        return getNamedSubSection(section, header);
    }


    public static String getNamedSubSection(String text, String header) {
        // a section in the file format appears to be made up of a header, followed by the content,
        // followed by either another header (one line of uppercase text) or end of text

        List<String> subSection = new LinkedList<>();
        boolean inSubSection = false;
        for (String line : TextTools.toArrayOfLines(text)) {
            if (line.trim().equals(header)) {
                inSubSection = true;
                continue;
            }
            if (inSubSection) {
                if (line.matches("[A-Z]+")) {
                    break; // we've come to the next sub-section
                }
                subSection.add(line);
            }
        }

        return TextTools.join("\n", subSection);
    }

    public static Set<PathDetail> parsePolylines(String text, Coord2D offset) {
        Set<PathDetail> paths = new HashSet<>();
        boolean inPolyline = false;
        Colour currentPathColour = null;
        PathDetail currentPathDetail = null;
        for (String line : TextTools.toArrayOfLines(text)) {
            if (line.startsWith("POLYLINE")) {
                if (inPolyline) {
                    paths.add(currentPathDetail);
                    currentPathDetail = null;
                }
                String colourText = line.substring("POLYLINE ".length());
                currentPathColour = interpretColour(colourText);
                inPolyline = true;
                continue;
            }
            if (!inPolyline) {
                continue;
            }

            String[] coords = line.split("\t");
            double x = Double.parseDouble(coords[0]) - offset.x;
            double y = Double.parseDouble(coords[1]) - offset.y;
            Coord2D coord = new Coord2D(x, -y);

            if (currentPathDetail == null) {
                currentPathDetail = new PathDetail(coord, currentPathColour);
            } else {
                currentPathDetail.lineTo(coord);
            }
        }

        if (currentPathDetail != null) {
            paths.add(currentPathDetail);
        }

        return paths;

    }

    public static Colour interpretColour(String colourText) {
        if (colourText.equals("GRAY")) {
            colourText = "GREY";
        }
        try {
            BrushColour brushColour = BrushColour.valueOf(colourText);
            return brushColour.getColour();

        } catch(IllegalArgumentException exception) {
            return Colour.BLACK;
        }
    }

}

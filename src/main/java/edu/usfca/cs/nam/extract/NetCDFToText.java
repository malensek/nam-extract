package edu.usfca.cs.nam.extract;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;
import ucar.unidata.geoloc.LatLonPoint;

public class NetCDFToText {
    public static void main(String[] args)
    throws Exception {
        File f = new File(args[0]);

        System.err.println("Reading netcdf...");
        Map<String, Map<String, Float>> metaMap = readFile(f.getAbsolutePath());

        for (String geohash : metaMap.keySet()) {
            System.out.print(geohash + "\t");
            Map<String, Float> featureEntires = metaMap.get(geohash);
            for (String featureName : featureEntires.keySet()) {
                System.out.print(featureEntires.get(featureName) + "\t");
            }
            System.out.println();
        }
    }

    public static Map<String, Map<String, Float>> readFile(String file)
    throws Exception {
        NetcdfFile n = NetcdfFile.open(file);
        System.err.println("Opened: " + file);

        /* Determine the size of our grid */
        int xLen = n.findDimension("x").getLength();
        int yLen = n.findDimension("y").getLength();
        System.err.println("Grid size: " + xLen + "x" + yLen);

        /* What time is this set of readings for? */
        Variable timeVar = n.findVariable("time");
        String timeStr = timeVar.getUnitsString().toUpperCase();
        timeStr = timeStr.replace("HOURS SINCE ", "");
        timeStr = timeStr.replace("HOUR SINCE ", "");

        /* Find the base date (the day) the reading was taken */
        Date baseDate
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(timeStr);

        /* Get the number of hours since the base date this reading was taken */
        int offset = timeVar.read().getInt(0);

        /* Generate the actual date for this reading */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseDate);
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + offset);
        System.err.println("Time of collection: " + calendar.getTime());

        /* We'll keep a mapping of geolocations -> Galileo Metadata */
        Map<String, Map<String, Float>> metaMap = new HashMap<>();

        /* Determine the lat, lon coordinates for the grid points, and get each
         * reading at each grid point. */
            NetcdfDataset dataset = new NetcdfDataset(n);
            GridDataset gridData = new GridDataset(dataset);
            for (GridDatatype g : gridData.getGrids()) {
                /* Let's look at 3D variables: these have WxH dimensions, plus a
                 * single plane.  A 4D variable would contain elevation
                 * and multiple planes as a result */
                if (g.getShape().length == 3) {
                    convert3DVariable(g, calendar.getTime(), metaMap);
                }
            }
            gridData.close();
            dataset.close();
            n.close();
            return metaMap;
    }

    private static void convert3DVariable(
            GridDatatype g, Date date, Map<String, Map<String, Float>> metaMap)
    throws IOException {

        Variable v = g.getVariable();
        System.err.println("Reading: " + v.getFullName());
        Array values = v.read();

        int h = v.getShape(1);
        int w = v.getShape(2);

        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                LatLonPoint pt = g.getCoordinateSystem().getLatLon(j, i);
                String hash = Geohash.encode(
                        (float) pt.getLatitude(),
                        (float) pt.getLongitude(), 10).toLowerCase();

                Map<String, Float> metaEntries = metaMap.get(hash);
                if (metaEntries == null) {
                    /* We need to create Metadata for this location */
                    metaEntries = new HashMap<String, Float>();

                    long timestamp = date.getTime();
                    metaEntries.put("time", (float) timestamp);

                    metaMap.put(hash, metaEntries);
                }

                String featureName = v.getFullName().toLowerCase();
                float featureValue = values.getFloat(i * w + j);
                metaEntries.put(featureName, featureValue);
            }
        }
    }
}

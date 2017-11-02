# NAM Extractor

This package will extract data from NOAA NAM GRIB files via the NetCDF library.

Usage:
```
mvn clean package
java -jar ./target/nam.extract-1.0.jar /path/to/a/nam/file.grb
```

The default behavior is to print the geolocation of the reading, its time stamp, followed by feature values (delimited by tab characters). This can be extended to filter out specific features, enforce an ordering, or convert to other formats.

# Calibration frames

Calibration frames are exposures that record the unwanted signal of the camera and the optical system, so that it can be removed from the light frames that contain the target. The usual set consists of bias, dark, flat and dark-flat frames. They are taken with the same camera and, where it matters, with the same settings as the light frames.

## Bias frames

A bias frame is the shortest possible exposure with the shutter closed or the telescope covered. It records the offset that the camera adds to every pixel and the pattern of the readout electronics. Many bias frames are averaged into a master bias. With modern CMOS cameras bias frames are sometimes replaced by dark-flat frames, because very short exposures can behave differently from longer ones.

## Dark frames

A dark frame is an exposure with the same exposure time, gain and sensor temperature as the light frames, but without light. It records the thermal signal (dark current), hot pixels and amplifier glow. Subtracting a master dark removes this signal from the light frames. Cooled cameras make darks reusable, because the sensor temperature can be held constant; uncooled cameras need darks taken under similar conditions.

## Flat frames

A flat frame is an exposure of an evenly illuminated surface, for example a light panel or the twilight sky, through the same optical train, focus position and filter as the light frames. It records vignetting, dust shadows (so-called dust donuts) and differences in pixel sensitivity. Dividing the light frames by a normalized master flat corrects these effects. Flats must be taken for every filter and whenever the camera rotation or focus changes. A good flat exposure reaches roughly a third to a half of the full well.

## Dark-flat frames

Dark-flat frames have the same exposure time as the flats but no light. They remove the offset and dark signal from the flats before the flats are used, and they replace bias frames with many CMOS cameras.

## Order of calibration

A calibrated light frame is calculated as (light − master dark) / (master flat − master dark-flat), with the flat normalized to an average of one. Stacking software applies these steps automatically before registration and integration. Calibration does not add signal; it removes systematic errors, so that the noise in the final image is mainly the random noise of the light frames.

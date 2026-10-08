# Dithering

Dithering means moving the telescope by a small, random amount between individual exposures, so that the target falls on slightly different pixels in every frame. Typical shifts are a few to a few dozen pixels. The images are registered on the stars before stacking, so the target lines up again, while every defect of the sensor ends up at a different position on the sky.

## Why dithering helps

Camera sensors have fixed patterns: hot pixels, cold pixels, column defects and a faint pattern of amplifier glow and read noise. Calibration frames remove most of these, but not all. Without dithering the residual pattern stays at the same place in every frame and survives stacking; it often shows up as coloured streaks, so-called walking noise, when the guiding drifts slowly in one direction. With dithering the residual pattern is spread over different positions, and statistical rejection during stacking, for example sigma clipping, removes it like any other outlier.

## How much and how often

A shift of a few times the size of a star image is usually enough. Many imagers dither after every frame or every few frames. Each dither costs some seconds of settling time for the mount and the guiding, so very short exposures make frequent dithering expensive; then dithering every few frames is a reasonable compromise. Dithering needs a guided mount or a mount control program that can move the telescope between frames.

## Dithering and drizzle

Drizzle integration combines many slightly shifted frames onto a finer output grid. It can recover resolution from undersampled data, but only if the frames are shifted by fractions of a pixel. Dithering provides exactly these shifts, so drizzle and dithering are normally used together.

## Planetary imaging

Lucky imaging of the planets and the Moon uses thousands of very short frames in a video. Natural seeing and small tracking errors already shift the image, so explicit dithering is not used there.

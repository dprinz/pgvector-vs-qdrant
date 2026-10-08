# Mono and one-shot colour cameras

Astronomy cameras come in two kinds: monochrome cameras and one-shot colour (OSC) cameras. Both use the same kinds of sensors; the difference is a colour filter array on the sensor of the OSC camera.

## One-shot colour cameras

An OSC camera has a Bayer matrix of tiny red, green and blue filters in front of its pixels, usually in a pattern with two green pixels for every red and blue one. Each pixel records only one colour, and software reconstructs a full colour image by debayering. An OSC camera produces a colour image in a single exposure, needs no filter wheel and keeps the imaging session simple. Because every pixel loses about two thirds of the light to its colour filter, an OSC camera collects colour information more slowly than a monochrome camera with a full set of filters, and its effective resolution per colour is lower.

## Monochrome cameras

A monochrome camera records all light that reaches each pixel. Colour comes from separate exposures through filters in a filter wheel: L, R, G and B for broadband images, or Ha, OIII and SII for narrowband images. Every pixel is used for every filter, so mono cameras reach a given signal-to-noise ratio faster, especially in narrowband, where each line can be imaged through a dedicated filter. The cost is more equipment, more calibration frames (flats for each filter) and longer projects across several nights.

## Narrowband with colour cameras

Dual-band and tri-band filters pass several emission lines at once and make narrowband imaging possible with OSC cameras. Ha falls on the red pixels and OIII mainly on the green and blue pixels, so a HOO image can be extracted from one set of exposures. This is efficient but cannot match the separation and depth of dedicated narrowband filters on a monochrome camera.

## Choosing a camera

OSC cameras suit beginners, short sessions, broadband targets and imaging from dark sites. Monochrome cameras suit narrowband imaging from light-polluted locations and imagers who want the highest efficiency and flexibility. For planetary imaging both are used; colour cameras are common because they record all colours in one video while the planet rotates.

# Sub-exposures and integration time

A deep-sky image is not taken as one long exposure. It is built from many single exposures, called sub-exposures or subs, which are calibrated, aligned and stacked. The sum of all sub-exposure times is the integration time. Integration time decides how deep and clean the final image is; the length of a single sub is a separate choice.

## Why integration time matters

Noise in astronomical images is mostly random: photon shot noise of the target and the sky, read noise of the camera and thermal noise. When frames are stacked, the signal grows with the number of frames while random noise grows only with its square root. The signal-to-noise ratio therefore improves with the square root of the integration time: four times the integration time halves the relative noise. Faint nebulae and the outer regions of galaxies need many hours, while bright objects need far less.

## Choosing the sub-exposure length

A single sub should be long enough that the camera's read noise becomes small compared with the shot noise of the sky background; beyond that point longer subs add little. Under bright, light-polluted skies or with broadband filters this point is reached after short exposures, often well under a minute with modern CMOS cameras. Narrowband filters block most of the sky background, so narrowband subs are usually several minutes long. Very long subs carry risks: saturated stars, lost frames through clouds, satellites or wind, and tracking errors.

## Gain and offset

CMOS cameras let the user choose gain and offset. A higher gain lowers read noise but reduces the full well, so bright stars saturate earlier. Many imagers use the gain at which the camera switches to its low-noise mode. The offset must be high enough that no pixel is clipped to zero in a bias or dark frame.

## Planning the integration

An imaging plan distributes the available time of a night over the filters. Broadband LRGB often spends more time on luminance; narrowband plans give the faint OIII or SII channels more time than the bright Ha channel. The usable time is limited by the period in which the target is high enough and the sky dark enough. Splitting a project over several nights is common, and every night adds to the integration of the same target.

## Planetary imaging

Planetary and lunar imaging work differently: thousands of very short frames are recorded as a video within a few minutes, before the rotation of the planet blurs the details. Only the sharpest frames are kept and stacked.

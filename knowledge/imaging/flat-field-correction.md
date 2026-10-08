# Flat-field correction

Flat-field correction (FFC) is a digital imaging technique to mitigate pixel-to-pixel differences in the photodetector sensitivity and distortions in the optical path. It is a standard calibration procedure in everything from personal digital cameras to large telescopes.

## Overview

Flat fielding refers to the process of compensating for different gains and dark currents in a detector. Once a detector has been appropriately flat-fielded, a uniform signal will create a uniform output (hence flat-field). This then means any further signal is due to the phenomenon being detected and not a systematic error.

A flat-field image is acquired by imaging a uniformly-illuminated screen, thus producing an image of uniform color and brightness across the frame. For handheld cameras, the screen could be a piece of paper at arm's length, but a telescope will frequently image a clear patch of sky at twilight, when the illumination is uniform and there are few, if any, stars visible. Once the images are acquired, processing can begin.

A flat-field consists of two numbers for each pixel, the pixel's gain and its dark current (or dark frame). The pixel's gain is how the amount of signal given by the detector varies as a function of the amount of light (or equivalent). The gain is almost always a linear variable, as such the gain is given simply as the ratio of the input and output signals. The dark-current is the amount of signal given out by the detector when there is no incident light (hence dark frame). In many detectors this can also be a function of time, for example in astronomical telescopes it is common to take a dark-frame of the same time as the planned light exposure. The gain and dark-frame for optical systems can also be established by using a series of neutral density filters to give input/output signal information and applying a least squares fit to obtain the values for the dark current and gain. {\displaystyle C={\frac {(R-D)\times m}{(F-D)}}=(R-D)\times G} where:

- C = corrected image
- R = raw image
- F = flat field image
- D = dark frame image
- m = image-averaged value of (F−D)
- G = Gain = {\displaystyle m \over (F-D)}

In this equation, capital letters are 2D matrices, and lowercase letters are scalars. All matrix operations are performed element-by-element.

In order for an astrophotographer to capture a light frame, they must place a light source over the imaging instrument's objective lens such that the light source emanates evenly through the users optics. The photographer must then adjust the exposure of their imaging device (charge-coupled device (CCD) or digital single-lens reflex camera (DSLR) ) so that when the histogram of the image is viewed, a peak reaching about 40–70% of the dynamic range (maximum range of pixel values) of the imaging device is seen. The photographer typically takes 15–20 light frames and performs median stacking. Once the desired light frames are acquired, the objective lens is covered so that no light is allowed in, then 15–20 dark frames are taken, each of equal exposure time as a light frame. These are called Dark-Flat frames.

## In X-ray imaging

In X-ray imaging, the acquired projection images generally suffer from fixed-pattern noise, which is one of the limiting factors of image quality. It may stem from beam inhomogeneity, gain variations of the detector response due to inhomogeneities in the photon conversion yield, losses in charge transport, charge trapping, or variations in the performance of the readout. Also, the scintillator screen may accumulate dust and/or scratches on its surface, resulting in systematic patterns in every acquired X-ray projection image. In X-ray computed tomography (CT), fixed-pattern noise is known to significantly degrade the achievable spatial resolution and generally leads to ring or band artifacts in the reconstructed images. Fixed pattern noise can be easily removed using flat field correction. In conventional flat field correction, projection images without sample are acquired with and without the X-ray beam turned on, which are referred to as flat fields (F) and dark fields (D). Based on the acquired flat and dark fields, the measured projection images (P) with sample are then normalized to new images (N) according to: {\displaystyle N={\frac {(P-D)}{(F-D)}}}

## Dynamic flat field correction

While conventional flat field correction is an elegant and easy procedure that largely reduces fixed-pattern noise, it heavily relies on the stationarity of the X-ray beam, scintillator response and CCD sensitivity. In practice, however, this assumption is only approximately met. Indeed, detector elements are characterized by intensity dependent, nonlinear response functions and the incident beam often shows time dependent non-uniformities, which render conventional FFC inadequate. In synchrotron X-ray tomography, many factors may cause flat field variations: instability of the bending magnets of the synchrotron, temperature variations due to the water cooling in mirrors and the monochromator, or vibrations of the scintillator and other beamline components. The latter is responsible for the biggest variations in the flat fields. To deal with such variations, a dynamic flat field correction procedure can be employed that estimates a flat field for each individual projection. Through principal component analysis of a set of flat fields, which are acquired prior and/or posterior to the actual scan, eigen flat fields can be computed. A linear combination of the most important eigen flat fields can then be used to individually normalize each X-ray projection: {\displaystyle N_{j}={\frac {P_{j}-{\bar {D}}}{{\bar {F}}+\sum _{k}w_{jk}u_{k}-{\bar {D}}}}} where

- {\displaystyle N_{j}} = intensity normalized X-ray projection
- {\displaystyle P_{j}} = raw X-ray projection
- {\displaystyle {\bar {F}}} = mean flat field image (average of flat fields)
- {\displaystyle u_{k}} = k-th eigen flat field
- {\displaystyle w_{jk}} = weight of the eigen flat field {\displaystyle u_{k}}
- {\displaystyle {\bar {D}}} = mean dark field (average of dark fields)

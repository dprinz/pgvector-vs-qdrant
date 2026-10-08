# Speckle imaging

Speckle imaging comprises a range of high-resolution astronomical imaging techniques based on the analysis of large numbers of short exposures that freeze the variation of atmospheric turbulence. They can be divided into the shift-and-add (image stacking) method and the speckle interferometry methods. These techniques can dramatically increase the resolution of ground-based telescopes, but are limited to bright targets.

## Explanation

The principle of all the techniques is to take very short exposure images of astronomical targets, and then process those so as to remove the effects of astronomical seeing. Use of these techniques led to a number of discoveries, including thousands of binary stars that would otherwise appear as a single star to a visual observer working with a similar-sized telescope, and the first images of sunspot-like phenomena on other stars. Many of the techniques remain in wide use today, notably when imaging relatively bright targets.

The resolution of a telescope is limited by the size of the main mirror, due to the effects of Fraunhofer diffraction. This results in images of distant objects being spread out to a small spot known as the Airy disk. A group of objects whose images are closer together than this limit appear as a single object. Thus larger telescopes can image not only dimmer objects (because they collect more light), but resolve objects that are closer together as well.

This improvement of resolution breaks down due to the practical limits imposed by the atmosphere, whose random nature disrupts the single spot of the Airy disk into a pattern of similarly-sized spots scattered over a much larger area (see the adjacent image of a binary). For typical seeing, the practical resolution limits are at mirror sizes much less than the mechanical limits for the size of mirrors, namely at a mirror diameter equal to the astronomical seeing parameter r0 – about 20 cm in diameter for observations with visible light under good conditions. For many years, telescope performance was limited by this effect, until the introduction of speckle interferometry and adaptive optics provided a means of removing this limitation.

Speckle imaging recreates the original image through image processing techniques. The key to the technique, found by the American astronomer David L. Fried in 1966, was to take very fast images in which case the atmosphere is effectively "frozen" in place. At infrared wavelengths, coherence times τ0 are on the order of 100 ms, but for the visible region they drop to as little as 10 ms. When exposure times are shorter than τ0, the movement of the atmosphere is too sluggish to have an effect; the speckles recorded in the image are a snapshot of the atmospheric seeing at that instant. Coherence time τ0 = r0/v is a function of wavelength, because r0 is a function of wavelength.

The downside of the technique is that taking images at this short an exposure is difficult, and if the object is too dim, not enough light will be captured to make analysis possible. Early uses of the technique in the early 1970s were made on a limited scale using photographic techniques, but since photographic film captures only about 7% of the incoming light, only the brightest of objects could be viewed in this way. The introduction of the CCD into astronomy, which captures more than 70% of the light, lowered the bar on practical applications by an order of magnitude, and today the technique is widely used on bright astronomical objects (e.g. stars and star systems).

Many of the simpler speckle imaging methods have multiple names, largely from amateur astronomers re-inventing existing speckle imaging techniques and giving them new names.

Another use of the technique is in industry. By shining a laser (whose smooth wavefront is an excellent simulation of the light from a distant star) on a surface, the resulting speckle pattern can be processed to give detailed images of flaws in the material.

## Types

### Shift-and-add method

The shift-and-add method (more recently image-stacking method) is a form of speckle imaging commonly used for obtaining high quality images from a number of short exposures with varying image shifts. It has been used in astronomy for several decades, and is the basis for the image stabilisation feature on some cameras. The short exposure images are aligned by using the brightest speckle and averaged to give a single output image.

The method involves calculation of the differential shifts of the images. This is easily accomplished in astronomical images since they can be aligned with the stars. Once the images are aligned they are averaged together. It is a basic principle of statistics that variation in a sample can be reduced by averaging together the individual values. In fact, when using an average, the signal-to-noise ratio should be increased by a factor of the square root of the number of images. A number of software packages exist for performing this, including IRAF, RegiStax, Autostakkert, Keiths Image Stacker, Hugin, and Iris.

In the lucky imaging approach, only the best short exposures are selected for averaging. Early shift-and-add techniques aligned images according to the image centroid, giving a lower overall Strehl ratio.

### Speckle interferometry

In 1970, the French astronomer Antoine Labeyrie showed that Fourier analysis (speckle interferometry) can obtain information about the high-resolution structure of the object from the statistical properties of the speckle patterns. This technique was first implemented in 1971 at Palomar Observatory (200-inch telescope) by Daniel Y. Gezari, Antoine Labeyrie and Robert V. Stachnick. Methods developed in the 1980s allowed simple images to be reconstructed from this power spectrum information.

One more recent type of speckle interferometry called speckle masking involves calculation of the bispectrum or closure phases from each of the short exposures. The "average bispectrum" can then be calculated and then inverted to obtain an image. This works particularly well using aperture masks. In this arrangement the telescope aperture is blocked except for a few holes which allow light through, creating a small optical interferometer with better resolving power than the telescope would otherwise have. This aperture masking technique was pioneered by the Cavendish Astrophysics Group.

One limitation of the technique is that it requires extensive computer processing of the image, which was hard to come by when the technique was first developed. This limitation has faded away over the years as computing power has increased, and nowadays desktop computers have more than enough power to make such processing a trivial task.

## Biology

Speckle imaging in biology refers to the underlabeling of periodic cellular components (such as filaments and fibers) so that instead of appearing as a continuous and uniform structure, it appears as a discrete set of speckles. This is due to statistical distribution of the labeled component within unlabeled components. The technique, also known as dynamic speckle enables real-time monitoring of dynamical systems and video image analysis to understand biological processes.

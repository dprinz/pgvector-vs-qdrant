# Pixel scale and sampling

Pixel scale describes how much of the sky falls on a single pixel of the camera sensor. It is given in arcseconds per pixel and follows from two values: the physical pixel size of the camera and the effective focal length of the telescope, including any reducer or Barlow lens. The usual formula is pixel scale = 206.265 × pixel size in micrometres / focal length in millimetres. A camera with 3.76 µm pixels behind a telescope with 550 mm focal length, for example, records about 1.4 arcseconds per pixel. The constant 206.265 converts the ratio of micrometres to millimetres into arcseconds.

## Field of view

The field of view of a camera follows from the same quantities: the sensor size in millimetres divided by the focal length gives the angle the sensor covers on the sky. A short focal length and a large sensor give a wide field for large nebulae and Milky Way regions; a long focal length gives a narrow field for small galaxies and planetary nebulae. Pixel scale and field of view are therefore two views of the same choice of telescope and camera.

## Sampling and seeing

Sampling compares the pixel scale with the finest detail the sky allows. Under typical seeing the image of a star is blurred to a full width at half maximum (FWHM) of about 2 to 4 arcseconds. A common rule of thumb, derived from the Nyquist criterion, is to place two to three pixels across the FWHM of a star. With a FWHM of 3 arcseconds this suggests a pixel scale between about 1 and 1.5 arcseconds per pixel.

## Undersampling

An image is undersampled when the pixels are too large compared with the seeing, for example 3 to 4 arcseconds per pixel under good seeing. Stars become small and blocky, sometimes square, and fine detail is lost. Undersampling is common with short focal lengths and is acceptable for wide fields. Dithering and drizzle integration can recover part of the lost resolution when many frames are stacked.

## Oversampling

An image is oversampled when the pixels are much smaller than the seeing disc, for example 0.3 arcseconds per pixel under 3 arcseconds of seeing. The image shows no more detail, but the light of a star or nebula is spread over more pixels, so every pixel collects less signal and the signal-to-noise ratio per pixel drops. Oversampled data can be binned, either in the camera or in software, to combine neighbouring pixels and bring the pixel scale closer to the seeing.

## Planetary imaging

For the planets and the Moon the rules differ. Lucky imaging uses very short exposures to freeze moments of good seeing, so the useful resolution is limited by the aperture of the telescope rather than by the average seeing. Planetary imagers therefore choose a much finer pixel scale and often a focal ratio of about five times the pixel size in micrometres, reached with a Barlow lens.

## In practice

Pixel scale is fixed by the equipment, while seeing changes from night to night. A setup that samples well on an average night is a good compromise. Tools that plan an imaging session should calculate pixel scale and field of view from the actual telescope and camera instead of relying on rules of thumb, and should report whether a target fits the field.

# Angular resolution

Angular resolution describes the ability of any image-forming device such as an optical or radio telescope, a microscope, a camera, or an eye, to distinguish small details of an object, thereby making it a major determinant of image resolution. It is used in optics applied to light waves, in antenna theory applied to radio waves, and in acoustics applied to sound waves. The colloquial use of the term "resolution" sometimes causes confusion; when an optical system is said to have a high resolution or high angular resolution, it means that the perceived distance, or actual angular distance, between resolved neighboring objects is small. The value that quantifies this property, θ, which is given by the Rayleigh criterion, is low for a system with a high resolution. The closely related term spatial resolution refers to the precision of a measurement with respect to space, which is directly connected to angular resolution in imaging instruments. The Rayleigh criterion shows that the minimum angular spread that can be resolved by an image-forming system is limited by diffraction to the ratio of the wavelength of the waves to the aperture width. For this reason, high-resolution imaging systems such as astronomical telescopes, long distance telephoto camera lenses and radio telescopes have large apertures.

## Definition of terms

Resolving power is the ability of an imaging device to separate (i.e., to see as distinct) points of an object that are located at a small angular distance or it is the power of an optical instrument to separate far away objects, that are close together, into individual images. The term resolution or minimum resolvable distance is the minimum distance between distinguishable objects in an image, although the term is loosely used by many users of microscopes and telescopes to describe resolving power. As explained below, diffraction-limited resolution is defined by the Rayleigh criterion as the angular separation of two point sources when the maximum of each source lies in the first minimum of the diffraction pattern (Airy disk) of the other. In scientific analysis, in general, the term "resolution" is used to describe the precision with which any instrument measures and records (in an image or spectrum) any variable in the specimen or sample under study.

## The Rayleigh criterion

The imaging system's resolution can be limited either by aberration or by diffraction causing blurring of the image. These two phenomena have different origins and are unrelated. Aberrations can be explained by geometrical optics and can in principle be solved by increasing the optical quality of the system. On the other hand, diffraction comes from the wave nature of light and is determined by the finite aperture of the optical elements. The lens' circular aperture is analogous to a two-dimensional version of the single-slit experiment. Light passing through the lens interferes with itself creating a ring-shape diffraction pattern, known as the Airy pattern, if the wavefront of the transmitted light is taken to be spherical or plane over the exit aperture.

The interplay between diffraction and aberration can be characterised by the point spread function (PSF). The narrower the aperture of a lens the more likely the PSF is dominated by diffraction. In that case, the angular resolution of an optical system can be estimated (from the diameter of the aperture and the wavelength of the light) by the Rayleigh criterion defined by Lord Rayleigh: two point sources are regarded as just resolved when the principal diffraction maximum (center) of the Airy disk of one image coincides with the first minimum of the Airy disk of the other, as shown in the accompanying photos. (In the bottom photo on the right that shows the Rayleigh criterion limit, the central maximum of one point source might look as though it lies outside the first minimum of the other, but examination with a ruler verifies that the two do intersect.) If the distance is greater, the two points are well resolved and if it is smaller, they are regarded as not resolved. Rayleigh defended this criterion on sources of equal strength.

Considering diffraction through a circular aperture, this translates into:

{\displaystyle \theta \approx 1.22{\frac {\lambda }{D}}\quad ({\text{considering that}}\,\sin \theta \approx \theta )}

where θ is the angular resolution (radians), λ is the wavelength of light, and D is the diameter of the lens' aperture. The factor 1.22 is derived from a calculation of the position of the first dark circular ring surrounding the central Airy disc of the diffraction pattern. This number is more precisely 1.21966989... (OEIS: A245461), the first zero of the order-one Bessel function of the first kind {\displaystyle J_{1}(x)} divided by π.

The formal Rayleigh criterion is close to the empirical resolution limit found earlier by the English astronomer W. R. Dawes, who tested human observers on close binary stars of equal brightness. The result, θ = 4.56/D, with D in inches and θ in arcseconds, is slightly narrower than calculated with the Rayleigh criterion. A calculation using Airy discs as point spread function shows that at Dawes' limit there is a 5% dip between the two maxima, whereas at Rayleigh's criterion there is a 26.3% dip. Modern image processing techniques including deconvolution of the point spread function allow resolution of binaries with even less angular separation.

Using a small-angle approximation, the angular resolution may be converted into a spatial resolution, Δℓ, by multiplication of the angle (in radians) with the distance to the object. For a microscope, that distance is close to the focal length f of the objective. For this case, the Rayleigh criterion reads:

{\displaystyle \Delta \ell \approx 1.22{\frac {f\lambda }{D}}}.

This is the radius, in the imaging plane, of the smallest spot to which a collimated beam of light can be focused, which also corresponds to the size of the smallest object that the lens can resolve. The size is proportional to wavelength, λ, and thus, for example, blue light can be focused to a smaller spot than red light. If the lens is focusing a beam of light with a finite extent (e.g., a laser beam), the value of D corresponds to the diameter of the light beam, not the lens. Since the spatial resolution is inversely proportional to D, this leads to the slightly surprising result that a wide beam of light may be focused on a smaller spot than a narrow one. This result is related to the Fourier properties of a lens.

A similar result holds for a small sensor imaging a subject at infinity: The angular resolution can be converted to a spatial resolution on the sensor by using f as the distance to the image sensor; this relates the spatial resolution of the image to the f-number, f/#:

{\displaystyle \Delta \ell \approx 1.22{\frac {f\lambda }{D}}=1.22\lambda \cdot (f/\#)}.

Since this is the radius of the Airy disk, the resolution is better estimated by the diameter, {\displaystyle 2.44\lambda \cdot (f/\#)}

## Specific cases

### Single telescope

Point-like sources separated by an angle smaller than the angular resolution cannot be resolved. A single optical telescope may have an angular resolution less than one arcsecond, but astronomical seeing and other atmospheric effects make attaining this very hard.

The angular resolution R of a telescope can usually be approximated by

{\displaystyle R={\frac {\lambda }{D}}}

where λ is the wavelength of the observed radiation, and D is the diameter of the telescope's objective. The resulting R is in radians. For example, in the case of yellow light with a wavelength of 580 nm, for a resolution of 0.1 arc second, we need D=1.2 m. Sources larger than the angular resolution are called extended sources or diffuse sources, and smaller sources are called point sources.

This formula, for light with a wavelength of about 562 nm, is also called the Dawes' limit.

One unit for angular resolution in this case of Gaussian dispersion from astronomical seeing is the half-power diameter, which is the angular diameter in which half of the power from a telescope is centered.

### Telescope array

The highest angular resolutions for telescopes can be achieved by arrays of telescopes called astronomical interferometers: These instruments can achieve angular resolutions of 0.001 arcsecond at optical wavelengths, and much higher resolutions at x-ray wavelengths. In order to perform aperture synthesis imaging, a large number of telescopes are required laid out in a 2-dimensional arrangement with a dimensional precision better than a fraction (0.25x) of the required image resolution.

The angular resolution R of an interferometer array can usually be approximated by

{\displaystyle R={\frac {\lambda }{B}}}

where λ is the wavelength of the observed radiation, and B is the length of the maximum physical separation of the telescopes in the array, called the baseline. The resulting R is in radians. Sources larger than the angular resolution are called extended sources or diffuse sources, and smaller sources are called point sources.

For example, in order to form an image in yellow light with a wavelength of 580 nm, for a resolution of 1 milli-arcsecond, we need telescopes laid out in an array that is 120 m × 120 m with a dimensional precision better than 145 nm.

### Microscope

The resolution R (here measured as a distance, not to be confused with the angular resolution of a previous subsection) depends on the angular aperture {\displaystyle \alpha }:

{\displaystyle R={\frac {1.22\lambda }{\mathrm {NA} _{\text{condenser}}+\mathrm {NA} _{\text{objective}}}}} where {\displaystyle \mathrm {NA} =n\sin \theta }.

Here NA is the numerical aperture, {\displaystyle \theta } is half the included angle {\displaystyle \alpha } of the lens, which depends on the diameter of the lens and its focal length, {\displaystyle n} is the refractive index of the medium between the lens and the specimen, and {\displaystyle \lambda } is the wavelength of light illuminating or emanating from (in the case of fluorescence microscopy) the sample.

It follows that the NAs of both the objective and the condenser should be as high as possible for maximum resolution. In the case that both NAs are the same, the equation may be reduced to:

{\displaystyle R={\frac {0.61\lambda }{\mathrm {NA} }}\approx {\frac {\lambda }{2\mathrm {NA} }}}

The practical limit for {\displaystyle \theta } is about 70°. In a dry objective or condenser, this gives a maximum NA of 0.95. In a high-resolution oil immersion lens, the maximum NA is typically 1.45, when using immersion oil with a refractive index of 1.52. Due to these limitations, the resolution limit of a light microscope using visible light is about 200 nm. Given that the shortest wavelength of visible light is violet ({\displaystyle \lambda \approx 400\,\mathrm {nm} }),

{\displaystyle R={\frac {1.22\times 400\,{\mbox{nm}}}{1.45\ +\ 0.95}}=203\,{\mbox{nm}}}

which is near 200 nm.

Oil immersion objectives can have practical difficulties due to their shallow depth of field and extremely short working distance, which calls for the use of very thin (0.17 mm) cover slips, or, in an inverted microscope, thin glass-bottomed Petri dishes.

However, resolution below this theoretical limit can be achieved using super-resolution microscopy. These include optical near-fields (Near-field scanning optical microscope) or a diffraction technique called 4Pi STED microscopy. Objects as small as 30 nm have been resolved with both techniques. In addition to this Photoactivated localization microscopy can resolve structures of that size, but is also able to give information in z-direction (3D).

## List of telescopes and arrays by angular resolution

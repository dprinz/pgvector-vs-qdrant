# Astronomical seeing

In astronomy, seeing is the degradation of the image of an astronomical object due to turbulence in the atmosphere of Earth that may become visible as blurring, twinkling or variable distortion. The origin of this effect is rapidly changing variations of the optical refractive index along the light path from the object to the detector. Seeing is a major limitation to the angular resolution in astronomical observations with telescopes that would otherwise be limited through diffraction by the size of the telescope aperture. Today, many large scientific ground-based optical telescopes include adaptive optics to overcome seeing.

The strength of seeing is often characterized by the angular diameter of the long-exposure image of a star (seeing disk) or by the Fried parameter r0. The diameter of the seeing disk is the full width at half maximum of its optical intensity. An exposure time of several tens of milliseconds can be considered long in this context. The Fried parameter describes the size of an imaginary telescope aperture for which the diffraction limited angular resolution is equal to the resolution limited by seeing. Both the size of the seeing disc and the Fried parameter depend on the optical wavelength, but it is common to specify them for 500 nanometers. A seeing disk smaller than 0.4 arcseconds or a Fried parameter larger than 30 centimeters can be considered excellent seeing. The best conditions are typically found at high-altitude observatories on small islands, such as those at Mauna Kea or La Palma.

## Effects

Astronomical seeing has several effects:

- It causes the images of point sources (such as stars), which in the absence of atmospheric turbulence would be steady Airy patterns produced by diffraction, to break up into speckle patterns, which change very rapidly with time (the resulting speckled images can be processed using speckle imaging)
- Long exposure images of these changing speckle patterns result in a blurred image of the point source, called a seeing disc
- The brightness of stars appears to fluctuate in a process known as scintillation or twinkling
- Atmospheric seeing causes the fringes in an astronomical interferometer to move rapidly
- The distribution of atmospheric seeing through the atmosphere (the CN2 profile described below) causes the image quality in adaptive optics systems to degrade the further you look from the location of reference star

The effects of atmospheric seeing were indirectly responsible for the belief that there were canals on Mars. In viewing a bright object such as Mars, occasionally a still patch of air will come in front of the planet, resulting in a brief moment of clarity. Before the use of charge-coupled devices, there was no way of recording the image of the planet in the brief moment other than having the observer remember the image and draw it later. This had the effect of having the image of the planet be dependent on the observer's memory and preconceptions which led to the belief that Mars had linear features.

The effects of atmospheric seeing are qualitatively similar throughout the visible and near infrared wavebands. At large telescopes the long exposure image resolution is generally slightly higher at longer wavelengths, and the timescale (t0 – see below) for the changes in the dancing speckle patterns is substantially lower.

## Measures

There are three common descriptions of the astronomical seeing conditions at an observatory:

- The full width at half maximum (FWHM) of the seeing disc
- r0 (the size of a typical "lump" of uniform air within the turbulent atmosphere) and t0 (the time-scale over which the changes in the turbulence become significant)
- The CN2 profile

These are described in the sub-sections below:

### The full width at half maximum (FWHM) of the seeing disc

Without an atmosphere, a small star would have an apparent size, an "Airy disk", in a telescope image determined by diffraction and would be inversely proportional to the diameter of the telescope. However, when light enters the Earth's atmosphere, the different temperature layers and different wind speeds distort the light waves, leading to distortions in the image of a star. The effects of the atmosphere can be modeled as rotating cells of air moving turbulently. At most observatories, the turbulence is only significant on scales larger than r0 (see below—the seeing parameter r0 is 10–20 cm at visible wavelengths under the best conditions) and this limits the resolution of telescopes to be about the same as given by a space-based 10–20 cm telescope.

The distortion changes at a high rate, typically more frequently than 100 times a second. In a typical astronomical image of a star with an exposure time of seconds or even minutes, the different distortions average out as a filled disc called the "seeing disc". The diameter of the seeing disk, most often defined as the full width at half maximum (FWHM), is a measure of the astronomical seeing conditions.

It follows from this definition that seeing is always a variable quantity, different from place to place, from night to night, and even variable on a scale of minutes. Astronomers often talk about "good" nights with a low average seeing disc diameter, and "bad" nights where the seeing diameter was so high that all observations were worthless.

The FWHM of the seeing disc (or just "seeing") is usually measured in arcseconds, abbreviated with the symbol (″). A 1.0″ seeing is a good one for average astronomical sites. The seeing of an urban environment is usually much worse. Good seeing nights tend to be clear, cold nights without wind gusts. Warm air rises (convection), degrading the seeing, as do wind and clouds. At the best high-altitude mountaintop observatories, the wind brings in stable air which has not previously been in contact with the ground, sometimes providing seeing as good as 0.4".

### r0 and t0

The astronomical seeing conditions at an observatory can be conveniently described by the parameters r0 and t0.

For telescopes with diameters smaller than r0, the resolution of long-exposure images is determined primarily by diffraction and the size of the Airy pattern and thus is inversely proportional to the telescope diameter.

For telescopes with diameters larger than r0, the image resolution is determined primarily by the atmosphere and is independent of telescope diameter, remaining constant at the value given by a telescope of diameter equal to r0. r0 also corresponds to the length-scale over which the turbulence becomes significant (10–20 cm at visible wavelengths at good observatories), and t0 corresponds to the time-scale over which the changes in the turbulence become significant. r0 determines the spacing of the actuators needed in an adaptive optics system, and t0 determines the correction speed required to compensate for the effects of the atmosphere.

The parameters r0 and t0 vary with the wavelength used for the astronomical imaging, allowing slightly higher resolution imaging at longer wavelengths using large telescopes.

The seeing parameter r0 is often known as the Fried parameter, named after David L. Fried. The atmospheric time constant t0 is often referred to as the Greenwood time constant, after Darryl Greenwood.

#### Mathematical description of r0 and t0

Mathematical models can give an accurate model of the effects of astronomical seeing on images taken through ground-based telescopes. Three simulated short-exposure images are shown at the right through three different telescope diameters (as negative images to highlight the fainter features more clearly—a common astronomical convention). The telescope diameters are quoted in terms of the Fried parameter {\displaystyle r_{0}} (defined below). {\displaystyle r_{0}} is a commonly used measurement of the astronomical seeing at observatories. At visible wavelengths, {\displaystyle r_{0}} varies from 20 cm at the best locations to 5 cm at typical sea-level sites.

In reality, the pattern of blobs (speckles) in the images changes very rapidly, so that long-exposure photographs would just show a single large blurred blob in the center for each telescope diameter. The diameter (FWHM) of the large blurred blob in long-exposure images is called the seeing disc diameter, and is independent of the telescope diameter used (as long as adaptive optics correction is not applied).

In the standard classical theory, light is treated as an oscillation in a field {\displaystyle \psi }. For monochromatic plane waves arriving from a distant point source with wave-vector {\displaystyle \mathbf {k} }: {\displaystyle \psi _{0}\left(\mathbf {r} ,t\right)=A_{u}e^{i\left(\phi _{u}+2\pi \nu t+\mathbf {k} \cdot \mathbf {r} \right)}} where {\displaystyle \psi _{0}} is the complex field at position {\displaystyle \mathbf {r} } and time {\displaystyle t}, with real and imaginary parts corresponding to the electric and magnetic field components, {\displaystyle \phi _{u}} represents a phase offset, {\displaystyle \nu } is the frequency of the light determined by {\textstyle \nu ={\frac {1}{2\pi }}c\left|\mathbf {k} \right|}, and {\displaystyle A_{u}} is the amplitude of the light.

The photon flux in this case is proportional to the square of the amplitude {\displaystyle A_{u}}, and the optical phase corresponds to the complex argument of {\displaystyle \psi _{0}}. As wavefronts pass through the Earth's atmosphere they may be perturbed by refractive index variations in the atmosphere. The diagram at the top-right of this page shows schematically a turbulent layer in the Earth's atmosphere perturbing planar wavefronts before they enter a telescope. The perturbed wavefront {\displaystyle \psi _{p}} may be related at any given instant to the original planar wavefront {\displaystyle \psi _{0}\left(\mathbf {r} \right)} in the following way: {\displaystyle \psi _{p}\left(\mathbf {r} \right)=\left(\chi _{a}\left(\mathbf {r} \right)e^{i\phi _{a}\left(\mathbf {r} \right)}\right)\psi _{0}\left(\mathbf {r} \right)} where {\displaystyle \chi _{a}\left(\mathbf {r} \right)} represents the fractional change in wavefront amplitude and {\displaystyle \phi _{a}\left(\mathbf {r} \right)} is the change in wavefront phase introduced by the atmosphere. Note that {\displaystyle \chi _{a}\left(\mathbf {r} \right)} and {\displaystyle \phi _{a}\left(\mathbf {r} \right)} describe the effect of the Earth's atmosphere, and the timescales for any changes in these functions will be set by the speed of refractive index fluctuations in the atmosphere.

#### The Kolmogorov model of turbulence

A description of the nature of the wavefront perturbations introduced by the atmosphere is provided by the Kolmogorov model developed by Tatarski, based partly on the studies of turbulence by the Russian mathematician Andrey Kolmogorov. This model is supported by a variety of experimental measurements and is widely used in simulations of astronomical imaging. The model assumes that the wavefront perturbations are brought about by variations in the refractive index of the atmosphere. These refractive index variations lead directly to phase fluctuations described by {\displaystyle \phi _{a}\left(\mathbf {r} \right)}, but any amplitude fluctuations are only brought about as a second-order effect while the perturbed wavefronts propagate from the perturbing atmospheric layer to the telescope. For all reasonable models of the Earth's atmosphere at optical and infrared wavelengths the instantaneous imaging performance is dominated by the phase fluctuations {\displaystyle \phi _{a}\left(\mathbf {r} \right)}. The amplitude fluctuations described by {\displaystyle \chi _{a}\left(\mathbf {r} \right)} have negligible effect on the structure of the images seen in the focus of a large telescope.

For simplicity, the phase fluctuations in Tatarski's model are often assumed to have a Gaussian random distribution with the following second-order structure function: {\displaystyle D_{\phi _{a}}\left(\mathbf {\rho } \right)=\left\langle \left|\phi _{a}\left(\mathbf {r} \right)-\phi _{a}\left(\mathbf {r} +\mathbf {\rho } \right)\right|^{2}\right\rangle _{\mathbf {r} }} where {\displaystyle D_{\phi _{a}}\left({\mathbf {\rho } }\right)} is the atmospherically induced variance between the phase at two parts of the wavefront separated by a distance {\displaystyle {\boldsymbol {\rho }}} in the aperture plane, and {\displaystyle \langle \cdot \rangle } represents the ensemble average.

For the Gaussian random approximation, the structure function of Tatarski (1961) can be described in terms of a single parameter {\displaystyle r_{0}}: {\displaystyle D_{\phi _{a}}\left({\mathbf {\rho } }\right)=6.88\left({\frac {\left|\mathbf {\rho } \right|}{r_{0}}}\right)^{5/3}} {\displaystyle r_{0}} indicates the strength of the phase fluctuations as it corresponds to the diameter of a circular telescope aperture at which atmospheric phase perturbations begin to seriously limit the image resolution. Typical {\displaystyle r_{0}} values for I band (900 nm wavelength) observations at good sites are 20–40 cm. {\displaystyle r_{0}} also corresponds to the aperture diameter for which the variance {\displaystyle \sigma ^{2}} of the wavefront phase averaged over the aperture comes approximately to unity: {\displaystyle \sigma ^{2}=1.0299\left({\frac {d}{r_{0}}}\right)^{5/3}}This equation represents a commonly used definition for {\displaystyle r_{0}}, a parameter frequently used to describe the atmospheric conditions at astronomical observatories.

{\displaystyle r_{0}} can be determined from a measured CN2 profile (described below) as follows: {\displaystyle r_{0}=\left(16.7\lambda ^{-2}(\cos \gamma )^{-1}\int _{0}^{\infty }C_{N}^{2}(h)dh\right)^{-3/5}} where the turbulence strength {\displaystyle C_{N}^{2}(h)} varies as a function of height {\displaystyle h} above the telescope, and {\displaystyle \gamma } is the angular distance of the astronomical source from the zenith (from directly overhead).

If turbulent evolution is assumed to occur on slow timescales, then the timescale t0 is simply proportional to r0 divided by the mean wind speed.

The refractive index fluctuations caused by Gaussian random turbulence can be simulated using the following algorithm: {\displaystyle \phi _{a}(\mathbf {r} )={\mbox{Re}}[{\mbox{FT}}[R(\mathbf {k} )K(\mathbf {k} )]]} where {\displaystyle \phi _{a}(\mathbf {r} )} is the optical phase error introduced by atmospheric turbulence, R(k) is a two-dimensional square array of independent random complex numbers which have a Gaussian distribution about zero and white noise spectrum, K(k) is the (real) Fourier amplitude expected from the Kolmogorov (or Von Karman) spectrum, Re[...] represents taking the real part, and FT[...] represents a discrete Fourier transform of the resulting two-dimensional square array (typically an FFT).

#### Turbulent intermittency

The assumption that the phase fluctuations in Tatarski's model have a Gaussian random distribution is usually unrealistic. In reality, turbulence exhibits intermittency.

These fluctuations in the turbulence strength can be straightforwardly simulated as follows: {\displaystyle \phi _{a}(\mathbf {r} )=\operatorname {Re} [{\mbox{FT}}[(R(\mathbf {k} )\otimes I(\mathbf {k} ))K(\mathbf {k} )]]} where I(k) is a two-dimensional array which represents the spectrum of intermittency, with the same dimensions as R(k), and where {\displaystyle \otimes } represents convolution. The intermittency is described in terms of fluctuations in the turbulence strength {\displaystyle C_{n}^{2}}. It can be seen that the equation for the Gaussian random case above is just the special case from this equation with: {\displaystyle I(k)=\delta (|k|)} where {\displaystyle \delta ()} is the Dirac delta function.

### The CN2 profile

A more thorough description of the astronomical seeing at an observatory is given by producing a profile of the turbulence strength as a function of altitude, called a CN2 profile. CN2 profiles are generally performed when deciding on the type of adaptive optics system which will be needed at a particular telescope, or in deciding whether or not a particular location would be a good site for setting up a new astronomical observatory. Typically, several methods are used simultaneously for measuring the CN2 profile and then compared. Some of the most common methods include:

- SCIDAR (imaging the shadow patterns in the scintillation of starlight)
- LOLAS (a small-aperture variant of SCIDAR designed for low-altitude profiling)
- SLODAR
- MASS
- MooSci (11-channel lunar scintillometer for ground level profiling)
- RADAR mapping of turbulence
- Balloon-borne thermometers to measure how quickly the air temperature is fluctuating with time due to turbulence
- V2 Precision Data Collection Hub (PDCH) with differential temperature sensors use to measure atmospheric turbulence

There are also mathematical functions describing the CN2 profile. Some are empirical fits from measured data and others attempt to incorporate elements of theory. One common model for continental land masses is known as Hufnagel-Valley after two workers in this subject.

## Mitigation

The first answer to this problem was speckle imaging, which allowed bright objects with simple morphology to be observed with diffraction-limited angular resolution. Later came space telescopes, such as NASA's Hubble Space Telescope, working outside the atmosphere and thus not having any seeing problems and allowing observations of faint targets for the first time (although with poorer resolution than speckle observations of bright sources from ground-based telescopes because of Hubble's smaller telescope diameter). The highest resolution visible and infrared images currently come from imaging optical interferometers such as the Navy Prototype Optical Interferometer or Cambridge Optical Aperture Synthesis Telescope, but those can only be used on very bright stars.

Starting in the 1990s, many telescopes have developed adaptive optics systems that partially solve the seeing problem. The best systems so far built, such as SPHERE on the ESO VLT and GPI on the Gemini telescope, achieve a Strehl ratio of 90% at a wavelength of 2.2 micrometers, but only within a very small region of the sky at a time.

A wider field of view can be obtained by using multiple deformable mirrors conjugated to several atmospheric heights and measuring the vertical structure of the turbulence, in a technique known as Multiconjugate Adaptive Optics.

Another cheaper technique, lucky imaging, has had good results on smaller telescopes. This idea dates back to pre-war naked-eye observations of moments of good seeing, which were followed by observations of the planets on cine film after World War II. The technique relies on the fact that every so often the effects of the atmosphere will be negligible, and hence by recording large numbers of images in real-time, a 'lucky' excellent image can be picked out. This happens more often when the number of r0-size patches over the telescope pupil is not too large, and the technique consequently breaks down for very large telescopes. It can nonetheless outperform adaptive optics in some cases and is accessible to amateurs. It does require very much longer observation times than adaptive optics for imaging faint targets, and is limited in its maximum resolution.

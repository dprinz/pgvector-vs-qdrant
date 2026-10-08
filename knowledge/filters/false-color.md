# False color

False colors and pseudo colors respectively refers to a group of color rendering methods used to display images in colors which were recorded in the visible or non-visible parts of the electromagnetic spectrum. A false-color image is an image that depicts an object in colors that differ from those a photograph (a true-color image) would show. In this image, colors have been assigned to three different wavelengths that human eyes cannot normally see.

In addition, variants of false colors such as pseudocolors, density slicing, and choropleths are used for information visualization of either data gathered by a single grayscale channel or data not depicting parts of the electromagnetic spectrum (e.g. elevation in relief maps or tissue types in magnetic resonance imaging).

## Types of color renderings

### True color

The concept behind true color can help in understanding false color. An image is called a true-color image when it offers a natural color rendition, or when it comes close to it. This means that the colors of an object in an image appear to a human observer the same way as if this same observer were to directly view the object: A green tree appears green in the image, a red apple red, a blue sky blue, and so on.

Absolute true-color rendering is impossible. There are three major sources of color error (metameric failure):

- Different spectral sensitivities of the human eye and of an image capture device (e.g. a camera).
- Different spectral emissions / reflections of the object and of the image render process (e.g. a printer or monitor).
- Differences in spectral irradiance in the case of reflective images (e.g. photo prints) or reflective objects – see color rendering index (CRI) for details.

The result of a metameric failure would be for example an image of a green tree which shows a different shade of green than the tree itself, a different shade of red for a red apple, a different shade of blue for the blue sky, and so on. Color management (e.g. with ICC profiles) can be used to mitigate this problem within the physical constraints.

Approximate true-color images gathered by spacecraft are an example where images have a certain amount of metameric failure, as the spectral bands of a spacecraft's camera are chosen to gather information on the physical properties of the object under investigation, and are not chosen to capture true-color images.

### False color

In contrast to a true-color image, a false-color image sacrifices natural color rendition in order to ease the detection of features that are not readily discernible otherwise – for example the use of near infrared for the detection of vegetation in satellite images. While a false-color image can be created using solely the visual spectrum (e.g. to accentuate color differences), typically some or all data used is from electromagnetic radiation (EM) outside the visual spectrum (e.g. infrared, ultraviolet or X-ray). The choice of spectral bands is governed by the physical properties of the object under investigation.

As the human eye uses three spectral bands (see trichromacy for details), three spectral bands are commonly combined into a false-color image. At least two spectral bands are needed for a false-color encoding, and it is possible to combine more bands into the three visual RGB bands – with the eye's ability to discern three channels being the limiting factor. In contrast, a "color" image made from one spectral band, or an image made from data consisting of non-EM data (e.g. elevation, temperature, tissue type) is a pseudocolor image (see below).

For true color, the RGB channels (red "R", green "G" and blue "B") from the camera are mapped to the corresponding RGB channels of the image, yielding a "RGB→RGB" mapping. For false color this relationship is changed. The simplest false-color encoding is to take an RGB image in the visible spectrum, but map it differently, e.g. "GBR→RGB". For traditional false-color satellite images of Earth a "NRG→RGB" mapping is used, with "N" being the near-infrared spectral band (and the blue spectral band being unused) – this yields the typical "vegetation in red" false-color images.

False color is used (among others) for satellite and space images: Examples are remote sensing satellites (e.g. Landsat, see example above), space telescopes (e.g. the Hubble Space Telescope) or space probes (e.g. Cassini-Huygens). Some spacecraft, with rovers (e.g. the Mars Science Laboratory Curiosity) being the most prominent examples, have the ability to capture approximate true-color images as well. Weather satellites produce, in contrast to the spacecraft mentioned previously, grayscale images from the visible or infrared spectrum.

False color has a range of scientific applications. Spacecraft often employ false-color methods to help understand the composition of structures in the universe such as nebula and galaxies. The frequency of light emitted by different ions in space are assigned contrasting colors, allowing the chemical composition of complex structures to be better separated and visualised. The image of the Eagle Nebula above is a typical example of this; the Hydrogen and Oxygen ions have been assigned green and blue respectively. The large amounts of green and blue in the image show that there is a large amount of Hydrogen and Oxygen in the nebula.

On 26 October 2004, the NASA/ESA Cassini-Huygens spacecraft captured a false-color image of Titan, Saturn's largest moon. The image was captured in Ultraviolet and Infrared wavelengths, both invisible to the human eye. In order to provide a visual representation, false color techniques were used. The infrared data was mapped to red and green colors, and ultraviolet mapped to blue.

### Pseudocolor

A pseudocolor image (sometimes styled pseudo-color or pseudo color) is derived from a grayscale image by mapping each intensity value to a color according to a table or function. Pseudo color is typically used when a single channel of data is available (e.g. temperature, elevation, soil composition, tissue type, and so on), in contrast to false color which is commonly used to display three channels of data.

Pseudocoloring can make some details more visible, as the perceived difference in color space is bigger than between successive gray levels alone. On the other hand, the color mapping function should be chosen to make sure the lightness of the color is still monotonic, or the uneven change would make it hard to interpret levels, for both normal and colorblind viewers. One offender is the commonly used "rainbow" palette, with a back-and-forth change in lightness. (See also Choropleth map § Color progression.)

A typical example for the use of pseudo color is thermography (thermal imaging), where infrared cameras feature only one spectral band and show their grayscale images in pseudo color.

Another familiar example of pseudo color is the encoding of elevation using hypsometric tints in physical relief maps, where negative values (below sea level) are usually represented by shades of blue, and positive values by greens and browns.

Depending on the table or function used and the choice of data sources, pseudocoloring may increase the information contents of the original image, for example adding geographic information, combining information obtained from infrared or ultra-violet light, or other sources like MRI scans.

A further application of pseudocoloring is to store the results of image elaboration; that is, changing the colors in order to ease understanding an image.

### Density slicing

Density slicing, a variation of pseudo color, divides an image into a few colored bands and is (among others) used in the analysis of remote sensing images. For density slicing the range of grayscale levels is divided into intervals, with each interval assigned to one of a few discrete colors – this is in contrast to pseudo color, which uses a continuous color scale. For example, in a grayscale thermal image the temperature values in the image can be split into bands of 2 °C, and each band represented by one color – as a result the temperature of one spot in the thermograph can be easier acquired by the user, because the discernible differences between the discrete colors are greater than those of images with continuous grayscale or continuous pseudo color.

### Choropleth

A choropleth is an image or map in which areas are colored or patterned proportionally to the category or value of one or more variables being represented. The variables are mapped to a few colors; each area contributes one data point and receives one color from these selected colors. Basically, it is density slicing applied to a pseudocolor overlay. A choropleth map of a geographic area is thus an extreme form of false color.

## False color in the arts

While artistic rendition lends to subjective expression of color, Andy Warhol (1928–1987) has become a culturally significant figure of the modern art movement by creating false-color paintings with screen printing techniques. Some of Warhol's most recognizable prints include a replication of Marilyn Monroe, her image based on a film frame from the movie Niagara. The subject was a sex symbol and film noir starlet whose death in 1962 influenced the artist. A series of prints were made with endearment but expose her persona as an illusion through his assembly line style of art production which are non-erotic and slightly grotesque. Using various ink color palettes, Warhol immersed himself in a process of repetition that serves to compare personas and everyday objects to the qualities of mass production and consumerism. The colors of ink were selected through experimentation of aesthetics and do not correlate to false-color rendering of the electromagnetic spectrum employed in remote sensing image processing. For years the artist continued screen printing false-color images of Marilyn Monroe, perhaps his most referenced work being Turquoise Marilyn which was bought in May 2007 by a private collector for 80 million US dollars.

# Bayer filter

A Bayer filter mosaic is a color filter array (CFA) for arranging RGB color filters on a square grid of photosensors. Its particular arrangement of color filters is used in most single-chip digital image sensors used in digital cameras, and camcorders to create a color image. The filter pattern is half green, one quarter red and one quarter blue, hence is also called BGGR, RGBG, GRBG, or RGGB.

It is named after its inventor, Bryce Bayer of Eastman Kodak. Bayer is also known for his recursively defined matrix used in ordered dithering.

Alternatives to the Bayer filter include both various modifications of colors and arrangement and completely different technologies, such as color co-site sampling, the Foveon X3 sensor, the dichroic mirrors or a transparent diffractive-filter array.

## Explanation

Bryce Bayer's patent (U.S. Patent No. 3,971,065) in 1976 called the green photosensors luminance-sensitive elements and the red and blue ones chrominance-sensitive elements. He used twice as many green elements as red or blue to mimic the physiology of the human eye. The luminance perception of the human retina uses M and L cone cells combined, during daylight vision, which are most sensitive to green light. These elements are referred to as sensor elements, sensels, pixel sensors, or simply pixels; sample values sensed by them, after interpolation, become image pixels. At the time Bayer registered his patent, he also proposed to use a cyan-magenta-yellow combination, that is another set of opposite colors. This arrangement was impractical at the time because the necessary dyes did not exist. Although digital CMY sensors have appeared in some older digital cameras, all modern consumer digital cameras use RGB sensitive sensors. The advantage of CMY dyes is that they have an improved light absorption characteristic, but this has been at the cost of lower fidelity conversion to the necessary RGB wavelengths necessary in light emitting displays.

The raw output of Bayer-filter cameras is referred to as a Bayer pattern image. Since each pixel is filtered to record only one of three colors, the data from each pixel cannot fully specify each of the red, green, and blue values on its own. To obtain a full-color image, various demosaicing algorithms can be used to interpolate a set of complete red, green, and blue values for each pixel. These algorithms make use of the surrounding pixels of the corresponding colors to estimate the values for a particular pixel.

Different algorithms requiring various amounts of computing power result in varying-quality final images. This can be done in-camera, producing a JPEG or TIFF image, or outside the camera using the raw data directly from the sensor. Since the processing power of the camera processor is limited, many photographers prefer to do these operations manually on a personal computer. The cheaper the camera, the fewer opportunities to influence these functions. In professional cameras, image correction functions are completely absent, or they can be turned off. Recording in Raw-format provides the ability to manually select demosaicing algorithm and control the transformation parameters, which is used not only in consumer photography but also in solving various technical and photometric problems.

## Demosaicing

Demosaicing can be performed in different ways. Simple methods interpolate the color value of the pixels of the same color in the neighborhood. For example, once the chip has been exposed to an image, each pixel can be read. A pixel with a green filter provides an exact measurement of the green component. The red and blue components for this pixel are obtained from the neighbors. For a green pixel, two red neighbors can be interpolated to yield the red value, also two blue pixels can be interpolated to yield the blue value.

This simple approach works well in areas with constant color or smooth gradients, but it can cause artifacts such as color bleeding in areas where there are abrupt changes in color or brightness especially noticeable along sharp edges in the image. Because of this, other demosaicing methods attempt to identify high-contrast edges and only interpolate along these edges, but not across them.

Other algorithms are based on the assumption that the color of an area in the image is relatively constant even under changing light conditions, so that the color channels are highly correlated with each other. Therefore, the green channel is interpolated at first then the red and afterwards the blue channel, so that the color ratio red-green respective blue-green are constant. There are other methods that make different assumptions about the image content and starting from this attempt to calculate the missing color values.

## Artifacts

Images with small-scale detail close to the resolution limit of the digital sensor can be a problem to the demosaicing algorithm, producing a result which does not look like the model. The most frequent artifact is Moiré, which may appear as repeating patterns, color artifacts or pixels arranged in an unrealistic maze-like pattern.

### False color artifact

A common and unfortunate artifact of Color Filter Array (CFA) interpolation or demosaicing is what is known and seen as false coloring. Typically this artifact manifests itself along edges, where abrupt or unnatural shifts in color occur as a result of misinterpolating across, rather than along, an edge. Various methods exist for preventing and removing this false coloring. Smooth hue transition interpolation is used during the demosaicing to prevent false colors from manifesting themselves in the final image. However, there are other algorithms that can remove false colors after demosaicing. These have the benefit of removing false coloring artifacts from the image while using a more robust demosaicing algorithm for interpolating the red and blue color planes.

### Zippering artifact

The zippering artifact is another side effect of CFA demosaicing, which also occurs primarily along edges, is known as the zipper effect. Simply put, zippering is another name for edge blurring that occurs in an on/off pattern along an edge. This effect occurs when the demosaicing algorithm averages pixel values over an edge, especially in the red and blue planes, resulting in its characteristic blur. As mentioned before, the best methods for preventing this effect are the various algorithms which interpolate along, rather than across image edges. Pattern recognition interpolation, adaptive color plane interpolation, and directionally weighted interpolation all attempt to prevent zippering by interpolating along edges detected in the image.

However, even with a theoretically perfect sensor that could capture and distinguish all colors at each photosite, Moiré and other artifacts could still appear. This is an unavoidable consequence of any system that samples an otherwise continuous signal at discrete intervals or locations. For this reason, most photographic digital sensors incorporate something called an optical low-pass filter (OLPF) or an anti-aliasing (AA) filter. This is typically a thin layer directly in front of the sensor, and works by effectively blurring any potentially problematic details that are finer than the resolution of the sensor.

## Modifications

The Bayer filter is almost universal on consumer digital cameras. Alternatives include the CYGM filter (cyan, yellow, green, magenta) and RGBE filter (red, green, blue, emerald), which require similar demosaicing. The Foveon X3 sensor (which layers red, green, and blue sensors vertically rather than using a mosaic) and arrangements of three separate CCDs (one for each color) doesn't need demosaicing.

### Panchromatic cells

On June 14, 2007, Eastman Kodak announced an alternative to the Bayer filter: a colour-filter pattern that increases the sensitivity to light of the image sensor in a digital camera by using some panchromatic cells that are sensitive to all wavelengths of visible light and collect a larger amount of light striking the sensor. They present several patterns, but none with a repeating unit as small as the Bayer pattern's 2×2 unit.

Another 2007 U.S. patent filing, by Edward T. Chang, claims a sensor where "the color filter has a pattern comprising 2×2 blocks of pixels composed of one red, one blue, one green and one transparent pixel," in a configuration intended to include infrared sensitivity for higher overall sensitivity. The Kodak patent filing was earlier.

Such cells have previously been used in "CMYW" (cyan, magenta, yellow, and white) "RGBW" (red, green, blue, white) sensors, but Kodak has not compared the new filter pattern to them yet.

### Fujifilm "EXR" color filter array

Fujifilm's EXR color filter array are manufactured in both CCD (SuperCCD) and CMOS (BSI CMOS). As with the SuperCCD, the filter itself is rotated 45 degrees. Unlike conventional Bayer filter designs, there are always two adjacent photosites detecting the same color. The main reason for this type of array is to contribute to pixel "binning", where two adjacent photosites can be merged, making the sensor itself more "sensitive" to light. Another reason is for the sensor to record two different exposures, which is then merged to produce an image with greater dynamic range. The underlying circuitry has two read-out channels that take their information from alternate rows of the sensor. The result is that it can act like two interleaved sensors, with different exposure times for each half of the photosites. Half of the photosites can be intentionally underexposed so that they fully capture the brighter areas of the scene. This retained highlight information can then be blended in with the output from the other half of the sensor that is recording a 'full' exposure, again making use of the close spacing of similarly colored photosites.

### Fujifilm "X-Trans" filter

The Fujifilm X-Trans CMOS sensor used in many Fujifilm X-series cameras is claimed to provide better resistance to color moiré than the Bayer filter, and as such they can be made without an anti-aliasing filter. This in turn allows cameras using the sensor to achieve a higher resolution with the same megapixel count. Also, the new design is claimed to reduce the incidence of false colors, by having red, blue and green pixels in each line. The arrangement of these pixels is also said to provide grain more like film.

One of main drawbacks for custom patterns is that they may lack full support in third party raw processing software like Adobe Photoshop Lightroom where adding improvements took multiple years.

### Quad Bayer

Sony introduced Quad Bayer color filter array, which first featured in the iPhone 6's front camera released in 2014. Quad Bayer is similar to Bayer filter, however adjacent 2×2 pixels are the same color, the 4×4 pattern features 4× blue, 4× red, and 8× green. For darker scenes, signal processing can combine data from each 2×2 group, essentially like a larger pixel. For brighter scenes, signal processing can convert the Quad Bayer into a conventional Bayer filter to achieve higher resolution. The pixels in Quad Bayer can be operated in long-time integration and short-time integration to achieve single shot HDR, reducing blending issues. Quad Bayer is also known as Tetracell by Samsung, 4-cell by OmniVision, and Quad CFA (QCFA) by Qualcomm.

On March 26, 2019, the Huawei P30 series were announced featuring RYYB Quad Bayer, with the 4×4 pattern featuring 4× blue, 4× red, and 8× yellow.

### Nonacell

On February 12, 2020, the Samsung Galaxy S20 Ultra was announced featuring Nonacell CFA. Nonacell CFA is similar to Bayer filter, however adjacent 3×3 pixels are the same color, the 6×6 pattern features 9× blue, 9× red, and 18× green.

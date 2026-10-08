# Autoguider

An autoguider is an automatic electronic guidance tool used in astronomy to keep a telescope pointed precisely at an object being observed. This prevents the object from drifting across the field of view during long-exposures which would create a blurred or elongated image.

## Usage

Imaging of dim celestial targets, usually deep sky objects, requires exposure times of many minutes, particularly when narrowband images are being taken. In order for the resulting image to maintain usable clarity and sharpness during these exposures, the target must be held at the same position within the telescope's field of view during the whole exposure; any apparent motion would cause point sources of light (such as stars) to appear as streaks, or the object being photographed to appear blurry. Even computer-tracked mounts and GoTo telescopes do not eliminate the need for tracking adjustments for exposures beyond a few minutes, as astrophotography demands an extremely high level of precision that these devices typically cannot achieve, especially if the mount is not properly polar aligned.

To accomplish this automatically an autoguider is usually attached to either a guidescope or finderscope, which is a smaller telescope oriented in the same direction as the main telescope, or an off-axis guider, which uses a prism to divert some of the light originally headed towards the eyepiece.

The device has a CCD or CMOS sensor that regularly takes short exposures of an area of sky near the object. After each image is captured, a computer measures the apparent motion of one or more stars within the imaged area and issues the appropriate corrections to the telescope's computerized mount.

Some computer controlled telescope mounts have an autoguiding port that connects directly to the autoguider (usually referred to as an ST-4 port, which works with analog signals). In this configuration, a guide camera will detect any apparent drift in the field of view. It will then send this signal to a computer which can calculate the required correction. This correction is then sent back to the camera which relays it back to the mount.

An autoguider need not be an independent unit; some high-end CCD imaging units (such as those offered by SBIG) have a second, integrated CCD sensor on the same plane as the main imaging chip that is dedicated to autoguiding. Astronomical video cameras or modified webcams can also serve as an autoguiding unit when used with guiding software such as Guidedog or PHD2, or general-purpose astronomical programs such as MaxDSLR. However, these setups are generally not as sensitive as specialized units.

Since an image of a star can take up more than one pixel on an image sensor due to lens imperfections and other effects, autoguiders use the amount of light falling on each pixel to calculate where the star should actually be located. As a result, most autoguiders have subpixel accuracy. In other words, the star can be tracked to an accuracy better than the angular size represented by one CCD pixel. However, atmospheric effects (astronomical seeing) typically limit accuracy to one arcsecond in most situations. To prevent the telescope from moving in response to changes in the guide star's apparent position caused by seeing, the user can usually adjust a setting called "aggressiveness".

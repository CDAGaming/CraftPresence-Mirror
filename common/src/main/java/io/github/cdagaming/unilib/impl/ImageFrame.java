/*
 * MIT License
 *
 * Copyright (c) 2018 - 2024 CDAGaming (cstack2011@yahoo.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.cdagaming.unilib.impl;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unicore.utils.TimeUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Image Conversion Layers and Utilities used to translate other Image Types
 * <p>Reference: <a href="https://stackoverflow.com/a/17269591">Click Here</a>
 *
 * @author CDAGaming
 */
public class ImageFrame {
    /**
     * The delay between image transitions
     */
    private final int delay;
    /**
     * The buffered image instance being stored
     */
    private final BufferedImage image;
    /**
     * The disposal method flag being used for this frame
     */
    private final String disposal;
    /**
     * The width of the current frame
     */
    private final int width;
    /**
     * The height of the current frame
     */
    private final int height;
    /**
     * The time, in milliseconds, for the frame to render until from current
     */
    private long renderTime = 0;

    /**
     * Initializes an Image Frame, with the specified arguments
     *
     * @param image    The buffered image, if any, to be stored for this frame
     * @param delay    The delay between now and the next image transition
     * @param disposal The disposal method flag to use for this frame
     * @param width    The width of this image
     * @param height   The height of this image
     */
    public ImageFrame(final BufferedImage image, final int delay, final String disposal, final int width, final int height) {
        this.image = deepCopy(image);
        this.delay = delay;
        this.disposal = disposal;
        this.width = width;
        this.height = height;
    }

    /**
     * Initializes an Image Frame, with the specified arguments
     *
     * @param image The buffered image, if any, to be stored for this frame
     */
    public ImageFrame(final BufferedImage image) {
        this(image, -1, null, -1, -1);
    }

    /**
     * Perform a deep-copy on the specified {@link BufferedImage}
     *
     * @param bi the target {@link BufferedImage}
     * @return the copied {@link BufferedImage}
     */
    public static BufferedImage deepCopy(final BufferedImage bi) {
        final ColorModel cm = bi.getColorModel();
        final boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        final WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null).getSubimage(0, 0, bi.getWidth(), bi.getHeight());
    }

    /**
     * Returns Whether the inputted string matches the format of an external image type
     *
     * @param input The original string to parse
     * @return Whether the inputted string matches the format of an external image type
     */
    public static boolean isExternalImage(final String input) {
        return !StringUtils.isNullOrEmpty(input) &&
                (input.toLowerCase().startsWith("http") || StringUtils.isBase64(input).getFirst() || input.toLowerCase().startsWith("file://"));
    }

    /**
     * Decodes the inputted string into valid Base64 data if possible
     *
     * @param input             The string to parse data
     * @param encoding          The encoding to parse data in
     * @param useDecodingMethod Whether we're using the alternative decoding method
     * @param repeatCycle       Whether this is a repeat run with the same input, should be false except for internal usage
     * @return Valid Base64 data, if possible to convert string data
     */
    public static byte[] decodeBase64(final String input, final String encoding, final boolean useDecodingMethod, final boolean repeatCycle) {
        try {
            return Base64.getDecoder().decode(useDecodingMethod ? URLDecoder.decode(input, encoding) : input);
        } catch (Throwable ex) {
            Constants.LOG.debugError(ex);

            if (!repeatCycle) {
                return decodeBase64(input, encoding, !useDecodingMethod, true);
            } else {
                return null;
            }
        }
    }

    /**
     * Reads an array of Image Frames from an InputStream
     *
     * @param stream The stream of data to be interpreted
     * @return The resulting array of Image Frames, if successful
     * @throws IOException If an error occurs during operation
     */
    public static ImageFrame[] readGif(final InputStream stream) throws IOException {
        final ArrayList<ImageFrame> frames = new ArrayList<>(2);

        final ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        reader.setInput(ImageIO.createImageInputStream(stream));

        int lastX = 0;
        int lastY = 0;

        int width = -1;
        int height = -1;

        final IIOMetadata metadata = reader.getStreamMetadata();

        Color backgroundColor = null;

        if (metadata != null) {
            final IIOMetadataNode globalRoot = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

            final NodeList globalColorTable = globalRoot.getElementsByTagName("GlobalColorTable");
            final NodeList globalScreeDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");

            if (globalScreeDescriptor.getLength() > 0) {
                final IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreeDescriptor.item(0);

                if (screenDescriptor != null) {
                    width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
                    height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
                }
            }

            if (globalColorTable.getLength() > 0) {
                final IIOMetadataNode colorTable = (IIOMetadataNode) globalColorTable.item(0);

                if (colorTable != null) {
                    final String backgroundIndex = colorTable.getAttribute("backgroundColorIndex");

                    IIOMetadataNode colorEntry = (IIOMetadataNode) colorTable.getFirstChild();
                    while (colorEntry != null) {
                        if (colorEntry.getAttribute("index").equals(backgroundIndex)) {
                            final int red = Integer.parseInt(colorEntry.getAttribute("red"));
                            final int green = Integer.parseInt(colorEntry.getAttribute("green"));
                            final int blue = Integer.parseInt(colorEntry.getAttribute("blue"));

                            backgroundColor = StringUtils.getColorFrom(red, green, blue);
                            break;
                        }

                        colorEntry = (IIOMetadataNode) colorEntry.getNextSibling();
                    }
                }
            }
        }

        BufferedImage master = null;
        boolean hasBackground = false;

        for (int frameIndex = 0; ; frameIndex++) {
            final BufferedImage image;
            try {
                image = reader.read(frameIndex);
            } catch (IndexOutOfBoundsException io) {
                break;
            }

            if (width == -1 || height == -1) {
                width = image.getWidth();
                height = image.getHeight();
            }

            final IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
            final IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
            final NodeList children = root.getChildNodes();

            final int delay = Integer.parseInt(gce.getAttribute("delayTime"));

            final String disposal = gce.getAttribute("disposalMethod");

            if (master == null) {
                master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                master.createGraphics().setColor(backgroundColor);
                master.createGraphics().fillRect(0, 0, master.getWidth(), master.getHeight());

                hasBackground = image.getWidth() == width && image.getHeight() == height;

                master.createGraphics().drawImage(image, 0, 0, null);
            } else {
                int x = 0;
                int y = 0;

                for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++) {
                    final Node nodeItem = children.item(nodeIndex);

                    if (nodeItem.getNodeName().equals("ImageDescriptor")) {
                        final NamedNodeMap map = nodeItem.getAttributes();

                        x = Integer.parseInt(map.getNamedItem("imageLeftPosition").getNodeValue());
                        y = Integer.parseInt(map.getNamedItem("imageTopPosition").getNodeValue());
                    }
                }

                if (disposal.equals("restoreToPrevious")) {
                    BufferedImage from = null;
                    for (int i = frameIndex - 1; i >= 0; i--) {
                        if (!frames.get(i).getDisposal().equals("restoreToPrevious") || frameIndex == 0) {
                            from = frames.get(i).getImage();
                            break;
                        }
                    }

                    if (from != null) {
                        master = deepCopy(from);
                    }
                } else if (disposal.equals("restoreToBackgroundColor") && backgroundColor != null && (!hasBackground || frameIndex > 1)) {
                    master.createGraphics().fillRect(lastX, lastY, frames.get(frameIndex - 1).getWidth(), frames.get(frameIndex - 1).getHeight());
                }
                master.createGraphics().drawImage(image, x, y, null);

                lastX = x;
                lastY = y;
            }

            final BufferedImage copy = deepCopy(master);
            frames.add(new ImageFrame(copy, delay, disposal, image.getWidth(), image.getHeight()));

            master.flush();
        }
        reader.dispose();

        return frames.toArray(new ImageFrame[0]);
    }

    /**
     * Retrieves the current buffered image being stored
     *
     * @return The current buffered image being stored
     */
    public BufferedImage getImage() {
        return deepCopy(image);
    }

    /**
     * Retrieves the delay between image transitions
     *
     * @return The delay between image transitions
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Retrieves the disposal method flag being used for this frame
     *
     * @return The disposal method flag being used for this frame
     */
    public String getDisposal() {
        return disposal;
    }

    /**
     * Retrieves the width of the current frame
     *
     * @return The width of the current frame
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves the height of the current frame
     *
     * @return The height of the current frame
     */
    public int getHeight() {
        return height;
    }

    /**
     * Retrieves the time, in milliseconds, for the frame to render until from current
     *
     * @return The time, in milliseconds, for the frame to render until from current
     */
    public long getRenderTime() {
        return renderTime;
    }

    /**
     * Sets the time, in milliseconds, for the frame to render until from current
     *
     * @param renderTime The new timestamp to render until
     */
    public void setRenderTime(final long renderTime) {
        this.renderTime = renderTime;
    }

    /**
     * Sets the time, in milliseconds, for the frame to render until from current
     */
    public void setRenderTime() {
        setRenderTime(TimeUtils.toEpochMilli());
    }

    /**
     * Determine whether this frame has rendered up to or past the delay
     *
     * @return Whether this frame has rendered up to or past the delay
     */
    public boolean shouldRenderNext() {
        return TimeUtils.toEpochMilli() - getRenderTime() > getDelay() * 10L;
    }
}

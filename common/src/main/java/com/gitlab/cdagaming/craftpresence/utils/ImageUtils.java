/*
 * MIT License
 *
 * Copyright (c) 2018 - 2023 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.craftpresence.utils;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.ImageFrame;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Image Utilities used to Parse External Image Data and rendering tasks
 *
 * @author CDAGaming, wagyourtail
 */
public class ImageUtils {
    /**
     * The Blocking Queue for URL Requests
     * <p>
     * Format: textureName;[textureInputType, textureObj]
     */
    private static final BlockingQueue<Pair<String, Pair<InputType, Object>>> urlRequests = Queues.newLinkedBlockingQueue();
    /**
     * Cached Images retrieved from URL Texture Retrieval
     * <p>
     * Format: textureName;[[textureInputType, textureObj], [textureIndex, imageData], textureData]
     */
    private static final Map<String, Tuple<Pair<InputType, Object>, Pair<Integer, List<ImageFrame>>, List<ResourceLocation>>> cachedImages = Maps.newHashMap();
    /**
     * The thread used for Url Image Events to take place within
     */
    private static final Thread urlQueue;

    static {
        urlQueue = new Thread("Url Queue") {
            @SuppressFBWarnings("DM_DEFAULT_ENCODING")
            @Override
            public void run() {
                try {
                    while (!CraftPresence.SYSTEM.IS_GAME_CLOSING) {
                        final Pair<String, Pair<InputType, Object>> request = urlRequests.take();
                        boolean isGif = request.getFirst().endsWith(".gif");

                        final Pair<Integer, List<ImageFrame>> bufferData = cachedImages.get(request.getFirst()).getSecond();
                        if (bufferData != null) {
                            // Retrieve Data from external source
                            try {
                                final InputStream streamData;
                                final Object originData = request.getSecond().getSecond();
                                switch (request.getSecond().getFirst()) {
                                    case FileData:
                                        streamData = Files.newInputStream(((File) originData).toPath());
                                        break;
                                    case FileStream:
                                        streamData = Files.newInputStream(Paths.get(originData.toString()));
                                        break;
                                    case ByteStream:
                                        final Tuple<Boolean, String, String> base64Data = StringUtils.isBase64(originData.toString());
                                        final byte[] dataSet = base64Data.getFirst() ?
                                                decodeBase64(base64Data.getThird(), "UTF-8", false, false) : (originData instanceof byte[] ? (byte[]) originData : originData.toString().getBytes());
                                        streamData = dataSet != null ? new ByteArrayInputStream(dataSet) : null;
                                        isGif = base64Data.getSecond().contains("gif");
                                        break;
                                    case Url:
                                        streamData = UrlUtils.getURLStream(originData instanceof URL ? (URL) originData : new URL(originData.toString()));
                                        isGif = originData.toString().endsWith(".gif");
                                        break;
                                    default:
                                        streamData = null;
                                        break;
                                }

                                if (streamData != null) {
                                    if (isGif) {
                                        final ImageFrame[] frames = ImageFrame.readGif(streamData);

                                        for (ImageFrame frame : frames) {
                                            try {
                                                bufferData.getSecond().add(frame);
                                            } catch (Exception ex) {
                                                if (ModUtils.IS_VERBOSE) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                        }
                                    } else {
                                        bufferData.getSecond().add(new ImageFrame(ImageIO.read(streamData)));
                                    }
                                    cachedImages.get(request.getFirst()).setSecond(bufferData);
                                    cachedImages.get(request.getFirst()).setThird(new ArrayList<>(bufferData.getSecond().size()));
                                }
                            } catch (Exception ex) {
                                if (ModUtils.IS_VERBOSE) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    if (ModUtils.IS_VERBOSE) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        urlQueue.start();
    }

    /**
     * Retrieves a Texture from an external Url, and caching it for further usage
     *
     * @param textureName The texture name to Identify this as
     * @param url         The url to retrieve the texture
     * @return The Resulting Texture Data
     */
    public static ResourceLocation getTextureFromUrl(final String textureName, final String url) {
        try {
            return getTextureFromUrl(textureName, new URL(url));
        } catch (Exception ex) {
            if (ModUtils.IS_VERBOSE) {
                ex.printStackTrace();
            }
            return new ResourceLocation("");
        }
    }

    /**
     * Retrieves a Texture from an external Url, and caching it for further usage
     *
     * @param textureName The texture name to Identify this as
     * @param url         The url to retrieve the texture
     * @return The Resulting Texture Data
     */
    public static ResourceLocation getTextureFromUrl(final String textureName, final URL url) {
        try {
            return getTextureFromUrl(textureName, new Pair<>(InputType.Url, url));
        } catch (Exception ex) {
            if (ModUtils.IS_VERBOSE) {
                ex.printStackTrace();
            }
            return new ResourceLocation("");
        }
    }

    /**
     * Retrieves a Texture from an external Url, and caching it for further usage
     *
     * @param textureName The texture name to Identify this as
     * @param url         The url to retrieve the texture
     * @return The Resulting Texture Data
     */
    public static ResourceLocation getTextureFromUrl(final String textureName, final File url) {
        try {
            return getTextureFromUrl(textureName, new Pair<>(InputType.FileData, url));
        } catch (Exception ex) {
            if (ModUtils.IS_VERBOSE) {
                ex.printStackTrace();
            }
            return new ResourceLocation("");
        }
    }

    /**
     * Retrieves a Texture from an external Url, and caching it for further usage
     *
     * @param textureName The texture name to Identify this as
     * @param url         The url to retrieve the texture
     * @return The Resulting Texture Data
     */
    public static ResourceLocation getTextureFromUrl(final String textureName, final Object url) {
        if (url instanceof File) {
            return getTextureFromUrl(textureName, (File) url);
        } else if (url instanceof URL) {
            return getTextureFromUrl(textureName, (URL) url);
        } else {
            if (url.toString().toLowerCase().startsWith("http")) {
                return getTextureFromUrl(textureName, url.toString());
            } else {
                return getTextureFromUrl(
                        textureName,
                        new Pair<>(StringUtils.isBase64(url.toString()).getFirst() ? InputType.ByteStream : InputType.FileStream, url.toString())
                );
            }
        }
    }

    /**
     * Retrieves a Texture from an external Url, and caching it for further usage
     *
     * @param textureName The texture name to Identify this as
     * @param stream      Streaming Data containing data to read later
     * @return The Resulting Texture Data
     */
    public static ResourceLocation getTextureFromUrl(final String textureName, final Pair<InputType, Object> stream) {
        synchronized (cachedImages) {
            if (!cachedImages.containsKey(textureName) || !cachedImages.get(textureName).getFirst().equals(stream)) {
                // Setup Initial data if not present (Or reset if the stream has changed)
                //
                // Note that the ResourceLocation needs to be
                // initially null here for compatibility reasons
                cachedImages.put(textureName, new Tuple<>(stream, new Pair<>(0, Lists.newArrayList()), null));
                try {
                    urlRequests.put(new Pair<>(textureName, stream));
                } catch (Exception ex) {
                    if (ModUtils.IS_VERBOSE) {
                        ex.printStackTrace();
                    }
                }
            }

            final Pair<Integer, List<ImageFrame>> bufferData = cachedImages.get(textureName).getSecond();

            if (bufferData == null || bufferData.getSecond() == null || bufferData.getSecond().isEmpty()) {
                return new ResourceLocation("");
            } else if (textureName != null) {
                final boolean shouldRepeat = textureName.endsWith(".gif") || stream.getSecond().toString().contains("gif");
                final boolean doesContinue = bufferData.getFirst() < bufferData.getSecond().size() - 1;

                final List<ResourceLocation> resources = cachedImages.get(textureName).getThird();
                if (bufferData.getFirst() < resources.size()) {
                    final ResourceLocation texLocation = resources.get(bufferData.getFirst());
                    if (bufferData.getSecond().get(bufferData.getFirst()).shouldRenderNext()) {
                        if (doesContinue) {
                            bufferData.getSecond().get(bufferData.setFirst(bufferData.getFirst() + 1)).setRenderTime(System.currentTimeMillis());
                        } else if (shouldRepeat) {
                            bufferData.getSecond().get(bufferData.setFirst(0)).setRenderTime(System.currentTimeMillis());
                        }
                    }
                    return texLocation;
                }
                try {
                    final DynamicTexture dynTexture = new DynamicTexture(bufferData.getSecond().get(bufferData.getFirst()).getImage());
                    final ResourceLocation cachedTexture = CraftPresence.instance.getTextureManager().getDynamicTextureLocation(textureName.toLowerCase() + (shouldRepeat ? "_" + cachedImages.get(textureName).getSecond().getFirst() : ""), dynTexture);
                    if (bufferData.getSecond().get(bufferData.getFirst()).shouldRenderNext()) {
                        if (doesContinue) {
                            bufferData.getSecond().get(bufferData.setFirst(bufferData.getFirst() + 1)).setRenderTime(System.currentTimeMillis());
                        } else if (shouldRepeat) {
                            bufferData.setFirst(0);
                        }
                    }
                    if (!resources.contains(cachedTexture)) {
                        resources.add(cachedTexture);
                    }
                    return cachedTexture;
                } catch (Exception ex) {
                    if (ModUtils.IS_VERBOSE) {
                        ex.printStackTrace();
                    }
                    return new ResourceLocation("");
                }
            } else {
                return new ResourceLocation("");
            }
        }
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
            return Base64.decodeBase64(useDecodingMethod ? URLDecoder.decode(input, encoding) : input);
        } catch (Exception ex) {
            if (ModUtils.IS_VERBOSE) {
                ex.printStackTrace();
            }

            if (!repeatCycle) {
                return decodeBase64(input, encoding, !useDecodingMethod, true);
            } else {
                return null;
            }
        }
    }

    /**
     * Detects whether the specified Texture lacks critical information
     *
     * @param location The texture to parse
     * @return Whether the specified Texture lacks critical information
     */
    public static boolean isTextureNull(final ResourceLocation location) {
        return location == null || (StringUtils.isNullOrEmpty(location.getNamespace()) || StringUtils.isNullOrEmpty(location.getPath()));
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
     * A Mapping storing the available Input Types for External Image Parsing
     *
     * <p>
     * FileData: Parsing with Raw File Data (IE a File Object Type), to be put into
     * a FileInputStream
     * <p>
     * FileStream: Parsing with the String representation of a file path, to be put
     * into a FileInputStream
     * <p>
     * ByteStream: Parsing with a direct or String representation of a Byte array, to be put
     * into an ByteArrayInputStream. (Byte Buffer can be used with Base64 representation or direct byte conversion)
     * <p>
     * Url: Parsing with a direct or string representation of a {@link URL}, to be converted
     * to an InputStream
     * <p>
     * Unknown: Unknown property, experience can be iffy using this
     */
    public enum InputType {
        /**
         * Constant for the "File (Raw)" Input Type.
         */
        FileData,
        /**
         * Constant for the "File (Stream)" Input Mode.
         */
        FileStream,
        /**
         * Constant for the "Byte Stream" Input Mode.
         */
        ByteStream,
        /**
         * Constant for the "URL" Input Mode.
         */
        Url,
        /**
         * Constant for the "Unknown" Input Mode.
         */
        Unknown
    }
}

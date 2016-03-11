package org.lyj.gui.components;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.lyj.commons.image.gif.GifDecoder;

import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 *
 */
public class AnimatedGif
        extends Animation {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AnimatedGif(final String filename,
                       final double durationMs) {
        this.init(filename, durationMs);
    }

    public AnimatedGif(final InputStream is,
                       final double durationMs) {
        this.init(is, durationMs);
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final InputStream is,
                      final double durationMs) {
        final GifDecoder d = new GifDecoder();
        d.read(is);

        this.init(d, durationMs);
    }

    private void init(final String filename,
                      final double durationMs) {
        final GifDecoder d = new GifDecoder();
        d.read(filename);

        this.init(d, durationMs);
    }

    private void init(final GifDecoder d,
                      final double durationMs) {

        final Image[] sequence = new Image[d.getFrameCount()];
        for (int i = 0; i < d.getFrameCount(); i++) {

            WritableImage wimg = null;
            BufferedImage bimg = d.getFrame(i);
            sequence[i] = SwingFXUtils.toFXImage(bimg, wimg);

        }

        super.initSequence(sequence, durationMs);
    }

}



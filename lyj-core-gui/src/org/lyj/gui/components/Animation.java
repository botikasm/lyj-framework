package org.lyj.gui.components;


import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Animation
        extends Transition {

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private ImageView _imageView;
    private int _count;

    private int _lastIndex;

    private Image[] _sequence;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public Animation() {
    }

    public Animation(final Image[] sequence, final double durationMs) {
        this.init(sequence, durationMs);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public ImageView getView() {
        return _imageView;
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected void interpolate(final double k) {

        final int index = Math.min((int) Math.floor(k * _count), _count - 1);
        if (index != _lastIndex) {
            _imageView.setImage(_sequence[index]);
            _lastIndex = index;
        }

    }

    protected void initSequence(final Image[] sequence, final double durationMs) {
        this.init(sequence, durationMs);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final Image[] sequence, final double durationMs) {

        _imageView = new ImageView(sequence[0]);
        _sequence = sequence;
        _count = sequence.length;

        super.setCycleCount(1);
        super.setCycleDuration(Duration.millis(durationMs));
        super.setInterpolator(Interpolator.LINEAR);
    }

}
/**
 * Helper class for animate css framework.
 *
 * https://github.com/daneden/animate.css
 *
 */
import ElementWrapper from "../../../lyts_core/view/components/ElementWrapper";
import {StyleManager, StyleModule} from "../../StyleManager";
import lang from "../../../lyts_core/commons/lang";
import animate_css from "./animate_css";

/**
 * Effects
 *
 */
enum AnimateEffect {
    // ATTENTION SEEKER
    bouce = 'bounce',
    flash = 'flash',
    pulse = 'pulse',
    rubberBand = 'rubberBand',
    shake = 'shake',
    headShake = 'headShake',
    swing = 'swing',
    tada = 'tada',
    wobble = 'wobble',
    jello = 'jello',

    // BOUNCING ENTRANCES
    bounceIn = 'bounceIn',
    bounceInDown = 'bounceInDown',
    bounceInLeft = 'bounceInLeft',
    bounceInRight = 'bounceInRight',
    bounceInUp = 'bounceInUp',

    // BOUNCING EXIT
    bounceOut = 'bounceOut',
    bounceOutDown = 'bounceOutDown',
    bounceOutLeft = 'bounceOutLeft',
    bounceOutRight = 'bounceOutRight',
    bounceOutUp = 'bounceOutUp',

    // FADE ENTRANCES
    fadeIn = 'fadeIn',
    fadeInDown = 'fadeInDown',
    fadeInDownBig = 'fadeInDownBig',
    fadeInLeft = 'fadeInLeft',
    fadeInLeftBig = 'fadeInLeftBig',
    fadeInRight = 'fadeInRight',
    fadeInRightBig = 'fadeInRightBig',
    fadeInUp = 'fadeInUp',
    fadeInUpBig = 'fadeInUpBig',

    // FADE EXIT
    fadeOut = 'fadeOut',
    fadeOutDown = 'fadeOutDown',
    fadeOutDownBig = 'fadeOutDownBig',
    fadeOutLeft = 'fadeOutLeft',
    fadeOutLeftBig = 'fadeOutLeftBig',
    fadeOutRight = 'fadeOutRight',
    fadeOutRightBig = 'fadeOutRightBig',
    fadeOutUp = 'fadeOutUp',
    fadeOutUpBig = 'fadeOutUpBig',

    // FLIPPERS
    flipInX = 'flipInX',
    flipInY = 'flipInY',
    flipOutX = 'flipOutX',
    flipOutY = 'flipOutY',

    // LIGHTSPEED
    lightSpeedIn = 'lightSpeedIn',
    lightSpeedOut = 'lightSpeedOut',

    // ROTATING ENTRANCES
    rotateIn = 'rotateIn',
    rotateInDownLeft = 'rotateInDownLeft',
    rotateInDownRight = 'rotateInDownRight',
    rotateInUpLeft = 'rotateInUpLeft',
    rotateInUpRight = 'rotateInUpRight',

    // ROTATING EXIT
    rotateOut = 'rotateOut',
    rotateOutDownLeft = 'rotateOutDownLeft',
    rotateOutDownRight = 'rotateOutDownRight',
    rotateOutUpLeft = 'rotateOutUpLeft',
    rotateOutUpRight = 'rotateOutUpRight',

    // SLIDING ENTRANCES
    slideInDown = 'slideInDown',
    slideInLeft = 'slideInLeft',
    slideInRight = 'slideInRight',
    slideInUp = 'slideInUp',

    // SLIDING EXITS
    slideOutDown = 'slideOutDown',
    slideOutLeft = 'slideOutLeft',
    slideOutRight = 'slideOutRight',
    slideOutUp = 'slideOutUp',

    // ZOOM ENTRANCES
    zoomIn = 'zoomIn',
    zoomInDown = 'zoomInDown',
    zoomInLeft = 'zoomInLeft',
    zoomInRight = 'zoomInRight',
    zoomInUp = 'zoomInUp',

    // ZOOM EXIT
    zoomOut = 'zoomOut',
    zoomOutDown = 'zoomOutDown',
    zoomOutLeft = 'zoomOutLeft',
    zoomOutRight = 'zoomOutRight',
    zoomOutUp = 'zoomOutUp',

    // SPECIAL
    hinge = 'hinge',
    jackInTheBox = 'jackInTheBox',
    rollIn = 'rollIn',
    rollOut = 'rollOut',


}

class AnimateClass {

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    constructor() {
        StyleManager
            .register(StyleModule.animate, animate_css)
            .inject({}, StyleModule.animate);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public apply(effect: AnimateEffect, elem: ElementWrapper, callback?: Function, is_infinite?: boolean) {
        this.applyEffect(effect, elem, callback, is_infinite);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private applyEffect(effect: AnimateEffect, elem: ElementWrapper, callback?: Function, is_infinite?: boolean) {
        this.animate(elem, () => {
            elem.classRemove(effect);
            if (!!is_infinite) {
                elem.classRemove('infinite');
            }
            if (!!callback && lang.isFunction(callback)) {
                lang.funcInvoke(callback);
            }
        });
        elem.classAdd(effect);
        if (!!is_infinite) {
            elem.classAdd('infinite');
        }
    }

    private animate(elem: ElementWrapper, callback: Function) {
        this.animationend(elem, () => {
            elem.classRemove('animated');
            lang.funcInvoke(callback);
        });
        elem.classAdd('animated');
    }

    private animationend(elem: ElementWrapper, callback: Function) {
        elem.addEventListener('animationend', (e: Event) => {
            e.preventDefault();
            // remove listeners
            elem.removeEventListener('animationend');

            lang.funcInvoke(callback);
        });
    }

    // ------------------------------------------------------------------------
    //                      S I N G L E T O N
    // ------------------------------------------------------------------------

    private static __instance: AnimateClass;

    public static instance(): AnimateClass {
        if (null == AnimateClass.__instance) {
            AnimateClass.__instance = new AnimateClass();
        }
        return AnimateClass.__instance;
    }

}

// ------------------------------------------------------------------------
//                      e x p o r t s
// ------------------------------------------------------------------------

const Animate: AnimateClass = AnimateClass.instance();

export {Animate, AnimateEffect};
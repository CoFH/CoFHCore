package cofh.core.client.gui.element.listbox;

import cofh.core.client.gui.element.ElementSlider;
import cofh.lib.client.gui.IGuiAccess;

public class SliderVertical extends ElementSlider {

    public SliderVertical(IGuiAccess containerScreen, int x, int y, int width, int height, int maxValue) {

        this(containerScreen, x, y, width, height, maxValue, 0);
    }

    public SliderVertical(IGuiAccess containerScreen, int x, int y, int width, int height, int maxValue, int minValue) {

        super(containerScreen, x, y, width, height, maxValue, minValue);
        int dist = maxValue - minValue;
        setSliderSize(width, dist <= 0 ? height : Math.max(height / ++dist, 9));
    }

    @Override
    public ElementSlider setLimits(int min, int max) {

        int dist = max - min;
        setSliderSize(width, dist <= 0 ? height : Math.max(height / ++dist, 9));
        return super.setLimits(min, max);
    }

    @Override
    public int getSliderY() {

        int dist = _valueMax - _valueMin;
        int maxPos = height - _sliderHeight;
        return Math.min(dist == 0 ? 0 : maxPos * (_value - _valueMin) / dist, maxPos);
    }

    @Override
    public void dragSlider(int x, int v) {

        v += Math.round(_sliderHeight * (v / (float) height) + (_sliderHeight * 0.25f));
        setValue(_valueMin + ((_valueMax - _valueMin) * v / height));
    }

}

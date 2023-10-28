package cofh.core.client.gui.element.listbox;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.client.gui.element.ElementSlider;

public class SliderHorizontal extends ElementSlider {

    public SliderHorizontal(IGuiAccess containerScreen, int x, int y, int width, int height, int maxValue) {

        this(containerScreen, x, y, width, height, maxValue, 0);
    }

    public SliderHorizontal(IGuiAccess containerScreen, int x, int y, int width, int height, int maxValue, int minValue) {

        super(containerScreen, x, y, width, height, maxValue, minValue);
        int dist = maxValue - minValue;
        setSliderSize(dist <= 0 ? width : Math.max(width / ++dist, 9), height);
    }

    @Override
    public ElementSlider setLimits(int min, int max) {

        int dist = max - min;
        setSliderSize(dist <= 0 ? width : Math.max(width / ++dist, 9), height);
        return super.setLimits(min, max);
    }

    @Override
    public int getSliderX() {

        int dist = valueMax - valueMin;
        int maxPos = width - sliderWidth;
        return Math.min(dist == 0 ? 0 : maxPos * (value - valueMin) / dist, maxPos);
    }

    @Override
    public void dragSlider(int v, int y) {

        v += Math.round(sliderWidth * (v / (float) width - 0.25f));
        setValue(valueMin + ((valueMax - valueMin) * v / width));
    }

}

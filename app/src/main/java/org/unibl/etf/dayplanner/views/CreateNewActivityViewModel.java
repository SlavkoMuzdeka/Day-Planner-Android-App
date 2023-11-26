package org.unibl.etf.dayplanner.views;

import androidx.lifecycle.ViewModel;

import com.aemerse.slider.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;

public class CreateNewActivityViewModel extends ViewModel {
    private List<CarouselItem> images = new ArrayList<>();

    public List<CarouselItem> getImages() {
        return images;
    }

    public void setImages(List<CarouselItem> images) {
        this.images = images;
    }

    public void addImage(CarouselItem image) {
        this.images.add(image);
    }
}

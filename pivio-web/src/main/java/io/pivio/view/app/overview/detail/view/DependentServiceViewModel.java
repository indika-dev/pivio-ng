package io.pivio.view.app.overview.detail.view;

public class DependentServiceViewModel implements Comparable {

    public String id;
    public String short_name;

    public DependentServiceViewModel() {
    }

    public DependentServiceViewModel(String id, String short_name) {
        this.id = id;
        this.short_name = short_name;
    }

    @Override
    public int compareTo(Object o) {
        return this.short_name.compareTo(((DependentServiceViewModel) o).short_name);
    }
}

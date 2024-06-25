package io.pivio.view.app.overview.detail.view;

import java.util.ArrayList;
import java.util.List;

public class ServiceViewModel {

    public List<ProvidesModel> provides = new ArrayList<>();
    public List<ServiceDependencyViewModel> internalServiceDependencies = new ArrayList<>();
}

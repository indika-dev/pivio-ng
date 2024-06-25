package io.pivio.view.app.overview.list;

import io.pivio.view.PivioServerConnector;
import io.pivio.view.app.overview.list.ModelMapper;
import io.pivio.view.app.overview.list.serverresponse.Overview;
import io.pivio.view.app.overview.list.OverviewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ListService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PivioServerConnector pivioServerConnector;

    public List<OverviewModel> getShortInfo() throws IOException {
        List<Overview> overviews = pivioServerConnector.getOverviews();
        List<OverviewModel> overviewModels = new ArrayList<>();
        for (Overview overview : overviews) {
            overviewModels.add(modelMapper.map(overview));
        }
        return overviewModels;
    }
}

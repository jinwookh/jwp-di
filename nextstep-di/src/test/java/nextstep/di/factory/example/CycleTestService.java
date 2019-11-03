package nextstep.di.factory.example;

import nextstep.stereotype.Service;

@Service
public class CycleTestService {
    private CycleTestController cycleTestController;

    public CycleTestService(CycleTestController cycleTestController) {
        this.cycleTestController = cycleTestController;
    }

    public CycleTestController getCycleTestController() {
        return cycleTestController;
    }
}

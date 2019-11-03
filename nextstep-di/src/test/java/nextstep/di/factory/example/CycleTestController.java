package nextstep.di.factory.example;

import nextstep.stereotype.Controller;

@Controller
public class CycleTestController {
    private CycleTestService cycleTestService;

    public CycleTestController(CycleTestService cycleTestService) {
        this.cycleTestService = cycleTestService;
    }

    public CycleTestService getCycleTestService() {
        return cycleTestService;
    }

}

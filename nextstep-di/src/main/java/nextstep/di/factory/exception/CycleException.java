package nextstep.di.factory.exception;

public class CycleException extends RuntimeException {
    public CycleException() {
        super("사이클이 감지되었습니다.");
    }
}

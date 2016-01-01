package io.uppercase.unicorn.handler;

/**
 * 서버와의 접속이 실패했을 때 실행되는 핸들러를 작성하기 위한 인터페이스
 */
public interface ConnectionFailedHandler {

    public void handle();
}

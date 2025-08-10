package com.example.companycoreserver.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SessionManager {
    
    // 사용자 ID를 키로 하는 활성 세션 관리
    private final Map<Long, String> activeSessions = new ConcurrentHashMap<>();
    
    // 세션 ID를 키로 하는 세션 정보 관리
    private final Map<String, SessionInfo> sessionInfo = new ConcurrentHashMap<>();
    
    /**
     * 새로운 로그인 시도 시 기존 세션을 종료하고 새 세션 생성
     * @param userId 사용자 ID
     * @param newToken 새로운 JWT 토큰
     * @return 이전 세션이 있었는지 여부
     */
    public boolean handleNewLogin(Long userId, String newToken) {
        String sessionId = UUID.randomUUID().toString();
        
        // 기존 세션이 있는지 확인
        String existingToken = activeSessions.get(userId);
        boolean hadExistingSession = existingToken != null;
        
        if (hadExistingSession) {
            // 기존 세션 정보 제거
            sessionInfo.remove(existingToken);
            System.out.println("기존 세션 종료 - 사용자 ID: " + userId + ", 토큰: " + existingToken.substring(0, 20) + "...");
        }
        
        // 새 세션 등록
        activeSessions.put(userId, newToken);
        sessionInfo.put(newToken, new SessionInfo(userId, LocalDateTime.now()));
        
        System.out.println("새 세션 등록 - 사용자 ID: " + userId + ", 토큰: " + newToken.substring(0, 20) + "...");
        
        return hadExistingSession;
    }
    
    /**
     * 토큰이 유효한 세션인지 확인
     * @param token JWT 토큰
     * @return 유효한 세션인지 여부
     */
    public boolean isValidSession(String token) {
        return sessionInfo.containsKey(token);
    }
    
    /**
     * 사용자 ID로 현재 활성 세션 토큰 조회
     * @param userId 사용자 ID
     * @return 현재 활성 세션 토큰 (없으면 null)
     */
    public String getActiveSessionToken(Long userId) {
        return activeSessions.get(userId);
    }
    
    /**
     * 로그아웃 시 세션 제거
     * @param token JWT 토큰
     */
    public void logout(String token) {
        SessionInfo info = sessionInfo.remove(token);
        if (info != null) {
            activeSessions.remove(info.getUserId());
            System.out.println("로그아웃 - 사용자 ID: " + info.getUserId() + ", 토큰: " + token.substring(0, 20) + "...");
        }
    }
    
    /**
     * 사용자 ID로 로그아웃
     * @param userId 사용자 ID
     */
    public void logoutByUserId(Long userId) {
        String token = activeSessions.remove(userId);
        if (token != null) {
            sessionInfo.remove(token);
            System.out.println("사용자 ID로 로그아웃 - 사용자 ID: " + userId);
        }
    }
    
    /**
     * 현재 활성 세션 수 조회
     * @return 활성 세션 수
     */
    public int getActiveSessionCount() {
        return activeSessions.size();
    }
    
    /**
     * 세션 정보를 담는 내부 클래스
     */
    private static class SessionInfo {
        private final Long userId;
        private final LocalDateTime loginTime;
        
        public SessionInfo(Long userId, LocalDateTime loginTime) {
            this.userId = userId;
            this.loginTime = loginTime;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public LocalDateTime getLoginTime() {
            return loginTime;
        }
    }
}

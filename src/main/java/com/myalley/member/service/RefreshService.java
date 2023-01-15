package com.myalley.member.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.myalley.exception.CustomException;
import com.myalley.exception.MemberExceptionType;
import com.myalley.member.domain.Member;
import com.myalley.member.domain.RefreshToken;
import com.myalley.member.jwt.JwtSecret;
import com.myalley.member.jwt.JwtUtils;
import com.myalley.member.repository.MemberRepository;
import com.myalley.member.repository.TokenRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshService {

    private final PasswordEncoder passwordEncoder;
    private final TokenRedisRepository tokenRedisRepository;

    private final MemberRepository memberRepository;

    public Map<String, String> good(String refreshToken) {

        RefreshToken rt = new RefreshToken("han@100", refreshToken);
        tokenRedisRepository.save(rt);
        HashMap<String, String> token = new HashMap<>();
//        Optional<RefreshToken> rts=tokenRedisRepository.findById("han@100");
//        rts.ifPresent(rtss->{
//            token.put("email", rtss.getEmail());
//
//        });

        return token;
    }

    public Map<String, String> refresh(String refreshToken) {

        // === Refresh Token 유효성 검사 === //
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JwtSecret.JWT_SECRET_KEY)).build();
            DecodedJWT decodedJWT = verifier.verify(refreshToken);

            // === Access Token 재발급 === //
            long now = System.currentTimeMillis();
            String email = decodedJWT.getSubject();

            RefreshToken redisData = tokenRedisRepository.findById(email).orElseThrow(() -> new CustomException(MemberExceptionType.TOKEN_FORBIDDEN));
            String redisRefreshToken = redisData.getToken();


            if (!redisRefreshToken.equals(refreshToken)) {//리프레시토큰 다름
                throw new CustomException(MemberExceptionType.TOKEN_FORBIDDEN);
            }

            Map<String, String> accessTokenResponseMap = new HashMap<>();

            // === 현재시간과 Refresh Token 만료날짜를 통해 남은 만료기간 계산 === //
            // === Refresh Token 만료시간 계산해 1개월 미만일 시 refresh token도 발급 === //
            ///만료시간에 따라 refresh토큰 갱신해서 보내줌 만료 아니라면 그냥 원래refresh보내줌
            //JwtUtile.createToken or createTokenSet
            long refreshExpireTime = decodedJWT.getClaim("exp").asLong() * 1000;
            long diffDays = (refreshExpireTime - now) / 1000 / (24 * 3600);
            long diffMin = (refreshExpireTime - now) / 1000 / 60;
            Member member = memberRepository.findByEmail(email);
            Map<String, String> token = new HashMap<>();
            if (diffMin < 5) {
                log.info("refresh 토큰이 재발급 되었습니다");
                token = JwtUtils.createTokenSet(member);
                tokenRedisRepository.save(new RefreshToken(email, token.get("refreshToken")));

            } else {

                token.put("acessToken", JwtUtils.createToken(member));
                token.put("refreshToken", refreshToken);

            }


            return token;
        } catch (JWTDecodeException e) {
           throw new CustomException(MemberExceptionType.TOKEN_FORBIDDEN);
        }
    }

}


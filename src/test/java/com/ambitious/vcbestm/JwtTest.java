package com.ambitious.vcbestm;

import com.ambitious.vcbestm.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.security.PublicKey;

/**
 * @author Ambitious
 * @date 2022/6/18 10:29
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtTest {

    @Resource
    private PublicKey jwtPublicKey;

    @Test
    void testParse() {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiI2NTZmODQ2ZTRiNjE0NGUxOWNlYmVlZTBkMWViOTc2MSIsImV4cCI6MTY1NTYwNTcwNiwidXNlcklkIjoxfQ.Po7qU7K9t4d8KIoo0O3DutILPfRKMbTvfc3EN5FBhLK-gAxbUlWJVML1uTYoFrURguLShpk1yBadO6lHCfHOkUXokEIicy3i0piIiwA4Pn4HZx-AwPjzwIcB5P-rE0ah0hueA4WkA9gAkBVOeCfI_uDz-VVUEkgCxhdFryBiyQ2ZkBXAJ7FcQwQ7uAJYFAtY-ZYRDdB0Q15o_aHSgq40EwovgpLX6BfL5blhzMWGYMGqSAvZbyTPT_PMPU5ZbOo85c8j9gtiQnfDjj-yq5l-OGzZucuDbbqfM3bsnj-Ri-dqPAzp9_gmIoCaz9a8sKTcBZxxdWFuFE8BQ3fqbJLytg";
        Long userId = JwtUtils.getUserIdFromToken(token, jwtPublicKey);
        System.out.println(userId);
    }
}
